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

import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionContextBuilder;
import graphql.execution.ExecutionId;
import graphql.language.Document;
import graphql.language.FragmentDefinition;
import graphql.language.NodeUtil;
import graphql.language.OperationDefinition;
import graphql.parser.Parser;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentBuilder;
import graphql.schema.GraphQLSchema;
import graphql.schema.SelectedField;
import graphql.schema.idl.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SelectedFieldCollectorTest {

    private Map<String, FragmentDefinition> getFragments(Document document) {
        NodeUtil.GetOperationResult getOperationResult = NodeUtil.getOperation(document, null);
        return getOperationResult.fragmentsByName;
    }


    @Test
    public void test() throws URISyntaxException, IOException {

        InputStream stream = SelectedFieldCollectorTest.class.getResourceAsStream("/starWarsSchema.graphqls");
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(new InputStreamReader(stream));
        SchemaGenerator.Options options = SchemaGenerator.Options.defaultOptions().enforceSchemaDirectives(false);

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Character")
                        .typeResolver(env -> env.getSchema().getObjectType("Droid")))
                .build();
        GraphQLSchema starWarsSchema = new SchemaGenerator().makeExecutableSchema(options, typeRegistry, wiring);

        String query = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("query.graphql").toURI())));
        Document document = new Parser().parseDocument(query);

        ExecutionContext executionContext = ExecutionContextBuilder.newExecutionContextBuilder()
                .executionId(ExecutionId.generate())
                .fragmentsByName(getFragments(document))
                .document(document)
                .operationDefinition((OperationDefinition)document.getDefinitions().get(0))
                .graphQLSchema(starWarsSchema).build();
        DataFetchingEnvironment environment = DataFetchingEnvironmentBuilder.newDataFetchingEnvironment(executionContext)
                .build();

        SelectedFieldCollector selectedFieldCollector = new SelectedFieldCollector(environment);
        List<SelectedField> fields = selectedFieldCollector.getFields();

        //TODO: Finish this test

    }

}