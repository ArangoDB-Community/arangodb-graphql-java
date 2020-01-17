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

package com.arangodb.graphql.context;

import com.arangodb.graphql.schema.ArangoEdgeDirective;
import com.arangodb.model.TraversalOptions;
import graphql.execution.ExecutionContext;
import graphql.schema.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArangoGraphQLContextTest {

    @Mock
    private DataFetchingEnvironment dataFetchingEnvironment;

    @Mock
    private DataFetchingFieldSelectionSet dataFetchingFieldSelectionSet;

    @Mock
    private GraphQLObjectType rootType;

    @Mock
    private GraphQLDirective vertexDirective;

    @Mock
    private GraphQLArgument vertexArgument;

    @Mock
    private SelectedField selectedField;

    @Mock
    private GraphQLFieldDefinition graphQLFieldDefinition;

    @Mock
    private GraphQLDirective directive;

    @Mock
    private GraphQLArgument collectionArgument;

    @Mock
    private GraphQLArgument directionArgument;

    @Mock
    private GraphQLObjectType subType;

    @Mock
    private ExecutionContext executionContext;

    @Mock
    private SelectedFieldCollector selectedFieldCollector;

    private ArangoGraphQLContext context;



    @Before
    public void setUp() throws Exception {

        Map<String, Object> args = new HashMap<>();
        args.put("id", 123);

        List<SelectedField> selectedFields = Arrays.asList(selectedField);

        when(dataFetchingEnvironment.getFieldType()).thenReturn(rootType);
        when(rootType.getDirective(eq("vertex"))).thenReturn(vertexDirective);
        when(vertexDirective.getArgument("collection")).thenReturn(vertexArgument);
        when(vertexArgument.getValue()).thenReturn("rootCollection");
        when(dataFetchingEnvironment.getArguments()).thenReturn(args);

        when(selectedFieldCollector.getFields()).thenReturn(selectedFields);

        when(selectedField.getFieldDefinition()).thenReturn(graphQLFieldDefinition);
        when(graphQLFieldDefinition.getDirective(eq("edge"))).thenReturn(directive);
        when(graphQLFieldDefinition.getType()).thenReturn(subType);
        when(subType.getDirective("vertex")).thenReturn(vertexDirective);

        when(directive.getArgument(eq("collection"))).thenReturn(collectionArgument);
        when(directive.getArgument(eq("direction"))).thenReturn(directionArgument);

        when(collectionArgument.getValue()).thenReturn("myEdgeCollection");
        when(directionArgument.getValue()).thenReturn("outbound");
        when(selectedField.getQualifiedName()).thenReturn("id");
        when(selectedField.getArguments()).thenReturn(args);

        when(dataFetchingEnvironment.getArgument("limit")).thenReturn(100);
        when(dataFetchingEnvironment.getArgument("skip")).thenReturn(200);

        Map sort = new HashMap();
        sort.put("property", "ASC");
        when(dataFetchingEnvironment.getArgument("sort")).thenReturn(sort);


        context = new ArangoGraphQLContext(dataFetchingEnvironment, selectedFieldCollector);
    }


    @Test
    public void getRootFilters() {
        ArangoFilterGroup rootFilters = context.getRootFilters();
        assertThat(rootFilters.getFilters().size(), equalTo(1));
        assertThat(rootFilters.getFilters().get(0).getKey(), equalTo("id"));
        assertThat(rootFilters.getFilters().get(0).getValues().get(0), equalTo(123));
        assertThat(rootFilters.getDepth(), equalTo(-1));
    }

    @Test
    public void getTraversalFilters() {
        List<ArangoFilterGroup> traversalFilters = context.getTraversalFilters();

        assertThat(traversalFilters.size(), equalTo(1));

        ArangoFilterGroup filterGroup = traversalFilters.get(0);
        assertThat(filterGroup.getFilters().size(), equalTo(1));
        assertThat(filterGroup.getFilters().get(0).getKey(), equalTo("id"));
        assertThat(filterGroup.getFilters().get(0).getValues().get(0), equalTo(123));
        assertThat(filterGroup.getDepth(), equalTo(1));
    }

    @Test
    public void getSelectedFields() {
        assertThat(context.getSelectedFields().size(), equalTo(1));
    }

    @Test
    public void getRootCollection() {
        assertThat(context.getRootCollection(), equalTo("rootCollection"));
    }

    @Test
    public void min() {
        assertThat(context.min(), equalTo(0));
    }

    @Test
    public void max() {
        assertThat(context.max(), equalTo(1));
    }

    @Test
    public void getRootTraversalDirection() {
        assertThat(context.getRootTraversalDirection().direction(), equalTo(TraversalOptions.Direction.outbound));
    }

    @Test
    public void getEdgeDirectives() {
        assertThat(context.getEdgeDirectives().size(), equalTo(1));

    }

    @Test
    public void hasTraversal() {
        assertTrue(context.hasTraversal());
    }

    @Test
    public void getVertexCollections() {
        assertThat(context.getVertexCollections().size(), equalTo(1));
    }

    @Test
    public void getEdgeCollections() {
        assertThat(context.getEdgeCollections().size(), equalTo(1));
        assertThat(context.getEdgeCollections().get(0).getName(), equalTo("myEdgeCollection"));
        assertThat(context.getEdgeCollections().get(0).getDirection(), equalTo(TraversalOptions.Direction.outbound));
    }

    @Test
    public void edgeDirectiveFor() {
        ArangoEdgeDirective arangoEdgeDirective = context.edgeDirectiveFor("id");
        assertThat(arangoEdgeDirective.getCollection(), equalTo("myEdgeCollection"));
        assertThat(arangoEdgeDirective.getDirection(), equalTo(TraversalOptions.Direction.outbound));
    }

    @Test
    public void limitAndSkip(){
        assertThat(context.getLimit(), equalTo(100));
        assertThat(context.getSkip(), equalTo(200));
    }

    @Test
    public void sort(){
        assertThat(context.getSort().get("property"), equalTo("ASC"));
    }
}