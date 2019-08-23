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

import com.arangodb.graphql.schema.ArangoDiscriminatorDirective;
import com.arangodb.graphql.schema.ArangoTypeAliasDirective;
import graphql.schema.*;

import java.util.Optional;

public class ArangoTypeDiscriminationFilterFactory  {

    private String discriminator(GraphQLOutputType type){

        if(type instanceof GraphQLDirectiveContainer) {

            ArangoDiscriminatorDirective discriminatorDirective = new ArangoDiscriminatorDirective((GraphQLDirectiveContainer)type);
            return discriminatorDirective.getProperty();

        }
        return null;

    }

    public ArangoFilter typeFilterFor(GraphQLType type){
        GraphQLType graphQLType = GraphQLTypeUtil.unwrapAll(type);

        if(graphQLType instanceof GraphQLObjectType) {

            GraphQLObjectType objectType = (GraphQLObjectType) graphQLType;

            Optional<String> discriminator = objectType.getInterfaces().stream()
                    .map(x -> discriminator(x))
                    .filter(x -> x != null)
                    .findFirst();

            if(discriminator.isPresent()){
                ArangoTypeAliasDirective typeAliasDirective = new ArangoTypeAliasDirective(objectType);
                String alias = typeAliasDirective.getName();

                if(alias == null){
                    alias = graphQLType.getName();
                }

                return new ArangoFilter(discriminator.get(), alias);

            }

        }

        return null;
    }

}
