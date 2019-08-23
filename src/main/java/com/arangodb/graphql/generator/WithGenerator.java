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

package com.arangodb.graphql.generator;

import com.arangodb.graphql.context.ArangoGraphQLContext;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * AQL Generator that generates a WITH statement with a list of Document Collections to include in the query
 *
 * @author Colin Findlay
 */
public class WithGenerator implements ArangoQueryGenerator {
    @Override
    public String generateAql(ArangoGraphQLContext ctx) {
        Set<String> vertexCollections = ctx.getVertexCollections();
        if (vertexCollections.size() > 0) {
            return "WITH " + vertexCollections.stream().collect(Collectors.joining(","));
        }
        return null;
    }

    @Override
    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
        Set<String> vertexCollections = ctx.getVertexCollections();
        return vertexCollections.size() > 0;
    }
}
