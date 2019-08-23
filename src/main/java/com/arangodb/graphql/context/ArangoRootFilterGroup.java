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

import graphql.schema.GraphQLType;

import java.util.Map;

/**
 * This class represents a group of filters to be applied to the root collection
 * @author Colin Findlay
 * @see ArangoFilter
 * @see ArangoFilter
 */
public class ArangoRootFilterGroup extends ArangoFilterGroup {
    public ArangoRootFilterGroup(Map<String, Object> arguments, GraphQLType type) {
        super(arguments, -1);
        addTypeFilter(type);
    }

    private void addTypeFilter(GraphQLType type){

        ArangoTypeDiscriminationFilterFactory filterFactory = new ArangoTypeDiscriminationFilterFactory();
        ArangoFilter arangoFilter = filterFactory.typeFilterFor(type);
        if(arangoFilter != null){
            this.getFilters().add(arangoFilter);
        }

    }
}
