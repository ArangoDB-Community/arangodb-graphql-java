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

package com.arangodb.graphql.schema.runtime;

import com.arangodb.graphql.ArangoDataFetcher;
import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class ArangoRuntimeWiringBuilder {

    public static RuntimeWiring newArangoRuntimeWiring(ArangoDataFetcher fetcher, TypeDefinitionRegistry typeRegistry, TypeDiscriminatorRegistry discriminatorRegistry){

        Optional<TypeDefinition> query = typeRegistry.getType("Query");
        ObjectTypeDefinition typeDefinition = (ObjectTypeDefinition) query.get();
        Map<String, DataFetcher> dataFetcherMap = typeDefinition.getFieldDefinitions().stream()
                .collect(Collectors.toMap(FieldDefinition::getName, x -> fetcher));

        ArangoTypeResolver t = new ArangoTypeResolver(discriminatorRegistry);

        RuntimeWiring.Builder wiringBuilder = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetchers(dataFetcherMap));

        for(TypeDefinition typeDef : typeRegistry.types().values()){
            if(typeDef instanceof InterfaceTypeDefinition || typeDef instanceof UnionTypeDefinition) {
                String name = typeDef.getName();
                wiringBuilder = wiringBuilder.type(TypeRuntimeWiring.newTypeWiring(name).typeResolver(t));
            }
        }

        return wiringBuilder.build();
    }
}
