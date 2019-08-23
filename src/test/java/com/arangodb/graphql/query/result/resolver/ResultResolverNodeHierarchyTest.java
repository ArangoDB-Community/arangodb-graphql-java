/*
 * DISCLAIMER
 * Copyright 2019 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 *
 */

package com.arangodb.graphql.query.result.resolver;

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.query.result.ArangoResultContext;
import com.arangodb.graphql.query.result.PathEdge;
import com.arangodb.graphql.query.result.ResultEdges;
import com.arangodb.graphql.query.result.ResultVertices;
import com.arangodb.model.TraversalOptions;
import graphql.schema.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResultResolverNodeHierarchyTest {

    @Mock
    private SelectedField topLevelField;

    @Mock
    private SelectedField secondLevelField;

    @Mock
    private ArangoResultContext context;

    @Mock
    private GraphQLFieldDefinition topLevelFieldDefinition;

    @Mock
    private GraphQLFieldDefinition secondLevelFieldDefinition;

    @Mock
    private GraphQLDirective edgeDirective;

    @Mock
    private GraphQLArgument collectionArgument;

    @Mock
    private ResultEdges resultEdges;

    @Mock
    private ResultVertices resultVertices;

    @Mock
    private GraphQLArgument directionArgument;

    private ResultResolverNodeHierarchy nodes;

    @Before
    public void setUp(){
        nodes = new ResultResolverNodeHierarchy(context);
        when(topLevelField.getQualifiedName()).thenReturn("topLevelField");
        when(topLevelField.getName()).thenReturn("topLevelField");
        when(topLevelField.getFieldDefinition()).thenReturn(topLevelFieldDefinition);

        when(secondLevelField.getQualifiedName()).thenReturn("topLevelField/secondLevelField");
        when(secondLevelField.getName()).thenReturn("secondLevelField");
        when(secondLevelField.getFieldDefinition()).thenReturn(secondLevelFieldDefinition);

    }

    @Test
    public void insertTopLevel() {

        ResultResolverNode node = new ResultResolverNode(topLevelField);
        nodes.insert(node);
        assertThat(nodes.getChildren().get("topLevelField"), equalTo(node));

    }

    @Test
    public void insertSecondLevel() {

        ResultResolverNode parentNode = new ResultResolverNode(topLevelField);
        nodes.insert(parentNode);

        ResultResolverNode childNode = new ResultResolverNode(secondLevelField);
        nodes.insert(childNode);

        assertThat(nodes.getChildren().get("topLevelField"), equalTo(parentNode));
        assertThat(nodes.getChildren().get("topLevelField").getChildren().get("secondLevelField"), equalTo(childNode));

    }
    @Test(expected = NoNodeInContextException.class)
    public void insertSecondLevelNoNodeInContext() {
        ResultResolverNode node = new ResultResolverNode(secondLevelField);
        nodes.insert(node);
    }

    @Test
    public void process() {

        Map<String, Object> topLevelFieldValues = new HashMap<>();
        topLevelFieldValues.put("secondLevelField", 41);
        BaseDocument baseDocument = new BaseDocument();
        baseDocument.setId("Document/1");
        baseDocument.addAttribute("topLevelField", topLevelFieldValues);

        ResultResolverNode parentNode = new ResultResolverNode(topLevelField);
        nodes.insert(parentNode);

        ResultResolverNode childNode = new ResultResolverNode(secondLevelField);
        nodes.insert(childNode);

        Map result = nodes.process(baseDocument);
        assertThat(result.get("topLevelField"), equalTo(topLevelFieldValues));


    }

    @Test
    public void processWithTraversal() {

        BaseDocument source = new BaseDocument("1");
        source.setId("Document/1");
        BaseDocument target = new BaseDocument("2");
        target.setId("Document/2");
        target.addAttribute("secondLevelField", 41);

        BaseEdgeDocument baseEdgeDocument = new BaseEdgeDocument();
        baseEdgeDocument.setId("Edge/1");
        baseEdgeDocument.setFrom("Document/1");
        baseEdgeDocument.setTo("Document/2");

        PathEdge pathEdge = new PathEdge(baseEdgeDocument, TraversalOptions.Direction.outbound);

        Map<String, Object> topLevelFieldValues = new HashMap<>();
        topLevelFieldValues.put("__collection", "Document");
        topLevelFieldValues.put("secondLevelField", 41);

        //Have an Edge directive
        when(topLevelFieldDefinition.getDirective("edge")).thenReturn(edgeDirective);
        when(edgeDirective.getArgument("collection")).thenReturn(collectionArgument);
        when(edgeDirective.getArgument("direction")).thenReturn(directionArgument);
        when(directionArgument.getValue()).thenReturn("outbound");
        when(collectionArgument.getValue()).thenReturn("mycollection");

        //Have Edges between src and target
        when(context.getVertices()).thenReturn(resultVertices);
        when(context.getEdges()).thenReturn(resultEdges);
        when(resultEdges.edgesFor("mycollection", "Document/1", TraversalOptions.Direction.outbound)).thenReturn(Sets.newSet(pathEdge));
        when(resultVertices.get("Document/2")).thenReturn(target);

        ResultResolverNode parentNode = new ResultResolverNode(topLevelField);
        nodes.insert(parentNode);

        ResultResolverNode childNode = new ResultResolverNode(secondLevelField);
        nodes.insert(childNode);

        Map result = nodes.process(source);
        assertThat(result.get("topLevelField"), equalTo(topLevelFieldValues));


    }
}