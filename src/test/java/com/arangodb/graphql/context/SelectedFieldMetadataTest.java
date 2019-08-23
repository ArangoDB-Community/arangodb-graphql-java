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
import graphql.Scalars;
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
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelectedFieldMetadataTest {

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
    private GraphQLUnionType unionType;

    @Before
    public void setUp(){
        when(selectedField.getFieldDefinition()).thenReturn(graphQLFieldDefinition);
        when(graphQLFieldDefinition.getDirective(eq("edge"))).thenReturn(directive);
        when(directive.getArgument(eq("collection"))).thenReturn(collectionArgument);
        when(directive.getArgument(eq("direction"))).thenReturn(directionArgument);
    }

    @Test
    public void depth() {
        when(selectedField.getQualifiedName()).thenReturn("a/b/c");
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        assertThat(metadata.depth(), equalTo(3));
    }

    @Test
    public void filterGroup() {
        Map<String, Object> args = new HashMap<>();
        args.put("a", 1);
        when(selectedField.getArguments()).thenReturn(args);
        when(selectedField.getQualifiedName()).thenReturn("a/b/c");

        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        ArangoFilterGroup filterGroup = metadata.filterGroup();
        assertThat(filterGroup.getFilters().size(), equalTo(1));
        assertThat(filterGroup.getFilters().get(0).getKey(), equalTo("a"));
        assertThat(filterGroup.getFilters().get(0).getValues().get(0), equalTo(1));
    }

    @Test
    public void filterGroupNoArgs() {
        Map<String, Object> args = new HashMap<>();
        when(selectedField.getArguments()).thenReturn(args);

        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        ArangoFilterGroup filterGroup = metadata.filterGroup();
        assertThat(filterGroup, nullValue());

    }

    @Test
    public void edgeDirective() {

        when(collectionArgument.getValue()).thenReturn("myCollection");
        when(directionArgument.getValue()).thenReturn("inbound");

        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        ArangoEdgeDirective arangoEdgeDirective = metadata.edgeDirective();
        assertThat(arangoEdgeDirective.getCollection(), equalTo("myCollection"));
        assertThat(arangoEdgeDirective.getDirection(), equalTo(TraversalOptions.Direction.inbound));
    }

    @Test
    public void noEdgeDirective() {

        when(graphQLFieldDefinition.getDirective(eq("edge"))).thenReturn(null);
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        ArangoEdgeDirective arangoEdgeDirective = metadata.edgeDirective();
        assertThat(arangoEdgeDirective, nullValue());

    }

    @Test
    public void isScalar() {
        when(graphQLFieldDefinition.getType()).thenReturn(Scalars.GraphQLString);
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        assertTrue(metadata.isScalar());
    }

    @Test
    public void isNotScalar() {
        when(graphQLFieldDefinition.getType()).thenReturn(GraphQLObjectType.newObject().name("CustomType").build());
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        assertFalse(metadata.isScalar());
    }

    @Test
    public void objectType() {
        when(graphQLFieldDefinition.getType()).thenReturn(GraphQLObjectType.newObject().name("CustomType").build());
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        List<GraphQLFieldsContainer> containers = metadata.fieldContainers();
        assertThat(containers.get(0).getName(), equalTo("CustomType"));
    }


    @Test
    public void objectTypeInList() {
        when(graphQLFieldDefinition.getType()).thenReturn(GraphQLList.list(GraphQLObjectType.newObject().name("CustomType").build()));
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        List<GraphQLFieldsContainer> containers = metadata.fieldContainers();
        assertThat(containers.get(0).getName(), equalTo("CustomType"));
    }

    @Test
    public void objectTypeForScalar() {
        when(graphQLFieldDefinition.getType()).thenReturn(Scalars.GraphQLString);
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        List<GraphQLFieldsContainer> containers = metadata.fieldContainers();
        assertThat(containers, nullValue());
    }

    @Test
    public void unionType(){
        GraphQLObjectType customTypeA = GraphQLObjectType.newObject().name("CustomType").build();
        GraphQLObjectType customTypeB = GraphQLObjectType.newObject().name("CustomType").build();
        when(unionType.getTypes()).thenReturn(Arrays.asList(customTypeA, customTypeB));
        when(graphQLFieldDefinition.getType()).thenReturn(unionType);

        SelectedFieldMetadata metadata = new SelectedFieldMetadata(selectedField);
        List<GraphQLFieldsContainer> containers = metadata.fieldContainers();

        assertThat(containers, hasItem(customTypeA));
        assertThat(containers, hasItem(customTypeB));
    }
}