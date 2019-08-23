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

import graphql.TypeResolutionEnvironment;
import graphql.execution.FieldCollectorParameters;
import graphql.language.*;
import graphql.parser.Parser;
import graphql.parser.antlr.GraphqlParser;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static graphql.execution.FieldCollectorParameters.newParameters;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class DynamicTypeFieldCollectorTest {

    @Test
    public void fieldSelection(){

        String schemaString = "type Query { bar1: String bar2: String }";
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry parse = schemaParser.parse(schemaString);
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema schema = schemaGenerator.makeExecutableSchema(parse, RuntimeWiring.newRuntimeWiring().build());
        GraphQLObjectType object = schema.getObjectType("Query");


        Document document = new Parser().parseDocument("{foo {bar1 bar2 }}");
        Field field = (Field) ((OperationDefinition) document.getChildren().get(0)).getSelectionSet().getSelections().get(0);

        FieldCollectorParameters fieldCollectorParameters = newParameters()
                .schema(schema)
                .objectType(object)
                .build();

        Selection bar1 = field.getSelectionSet().getSelections().get(0);
        Selection bar2 = field.getSelectionSet().getSelections().get(1);


        DynamicTypeFieldCollector collector = new DynamicTypeFieldCollector();
        Map<String, List<CollectedField>> collectedFields = collector.collectFields(fieldCollectorParameters, Arrays.asList(field));


        assertThat(collectedFields.get("bar1").get(0).getField(), equalTo(bar1));
        assertThat(collectedFields.get("bar2").get(0).getField(), equalTo(bar2));

    }


    @Test
    public void fieldSelectionWithInlineFragment(){

        String schemaString = "type Query { bar1: String bar2: Test }" +
                "interface Test { fieldOnInterface: String }" +
                "type TestImpl { fieldOnInterface: String }";
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry parse = schemaParser.parse(schemaString);
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().
                type(TypeRuntimeWiring.newTypeWiring("Test")
                        .typeResolver(env -> env.getSchema().getObjectType("TestImpl"))
                        .build())
                .build();
        GraphQLSchema schema = schemaGenerator.makeExecutableSchema(parse, runtimeWiring);
        GraphQLObjectType object = schema.getObjectType("TestImpl");





        Document document = new Parser().parseDocument("{bar1 { ... on Test { fieldOnInterface}}}");
        Field field = (Field) ((OperationDefinition) document.getChildren().get(0)).getSelectionSet().getSelections().get(0);

        FieldCollectorParameters fieldCollectorParameters = newParameters()
                .schema(schema)
                .objectType(object)
                .build();

        InlineFragment inlineFragment = (InlineFragment) field.getSelectionSet().getSelections().get(0);
        Selection interfaceField = inlineFragment.getSelectionSet().getSelections().get(0);

        DynamicTypeFieldCollector collector = new DynamicTypeFieldCollector();
        Map<String, List<CollectedField>> collectedFields = collector.collectFields(fieldCollectorParameters, Arrays.asList(field));


        assertThat(collectedFields.get("fieldOnInterface").get(0).getField(), equalTo(interfaceField));
        assertThat(collectedFields.get("fieldOnInterface").get(0).getType().getName(), equalTo("Test"));

    }

    @Test
    public void fieldSelectionWithFragmentSpread(){

        String schemaString = "type Query { bar1: String bar2: Test }" +
                "interface Test { fieldOnInterface: String }" +
                "type TestImpl { fieldOnInterface: String }";
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry parse = schemaParser.parse(schemaString);
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().
                type(TypeRuntimeWiring.newTypeWiring("Test")
                        .typeResolver(env -> env.getSchema().getObjectType("TestImpl"))
                        .build())
                .build();
        GraphQLSchema schema = schemaGenerator.makeExecutableSchema(parse, runtimeWiring);
        GraphQLObjectType object = schema.getObjectType("TestImpl");





        Document document = new Parser().parseDocument("query {bar1 { ...interfaceFields }} fragment interfaceFields on Test { fieldOnInterface }");
        Field field = (Field) ((OperationDefinition) document.getChildren().get(0)).getSelectionSet().getSelections().get(0);

        Map<String, FragmentDefinition> fragments = document.getDefinitions().stream().filter(x -> x instanceof FragmentDefinition).map(x -> (FragmentDefinition) x).collect(Collectors.toMap(FragmentDefinition::getName, x -> x));
        FieldCollectorParameters fieldCollectorParameters = newParameters()
                .schema(schema)
                .objectType(object)
                .fragments(fragments)
                .build();

        FragmentSpread fragmentSpread = (FragmentSpread) field.getSelectionSet().getSelections().get(0);

        DynamicTypeFieldCollector collector = new DynamicTypeFieldCollector();
        Map<String, List<CollectedField>> collectedFields = collector.collectFields(fieldCollectorParameters, Arrays.asList(field));

        assertThat(collectedFields.get("fieldOnInterface").get(0).getType().getName(), equalTo("Test"));

    }

}