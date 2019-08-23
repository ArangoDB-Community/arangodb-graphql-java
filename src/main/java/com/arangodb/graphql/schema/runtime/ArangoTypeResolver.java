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

import com.arangodb.graphql.schema.ArangoDiscriminatorDirective;
import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLDirectiveContainer;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.TypeResolver;

import java.util.Map;

public class ArangoTypeResolver implements TypeResolver {

    private TypeDiscriminatorRegistry typeDiscriminatorRegistry;

    public ArangoTypeResolver(TypeDiscriminatorRegistry typeDiscriminatorRegistry) {
        this.typeDiscriminatorRegistry = typeDiscriminatorRegistry;
    }

    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment env) {

        String discriminatorProperty = "__collection";
        GraphQLType fieldType = env.getFieldType();
        if(fieldType instanceof GraphQLDirectiveContainer) {
            ArangoDiscriminatorDirective discriminatorDirective = new ArangoDiscriminatorDirective((GraphQLDirectiveContainer) env.getFieldType());
            String property = discriminatorDirective.getProperty();
            if(property != null){
                discriminatorProperty = property;
            }
        }

        //1. Get Discriminator Value
        Map object = env.getObject();
        Object discriminatorValue = object.get(discriminatorProperty);

        //2. Read it from the registry
        String typeName = typeDiscriminatorRegistry.get(discriminatorValue);

        //3. If we don't have a type name use the default
        if(typeName == null && discriminatorValue != null){
            typeName = discriminatorValue.toString();
        }

        return env.getSchema().getObjectType(typeName);
    }
}
