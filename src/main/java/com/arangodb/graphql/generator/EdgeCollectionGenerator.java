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

import com.arangodb.graphql.context.ArangoEdgeTraversalDirection;
import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.model.EdgeCollection;

import java.util.stream.Collectors;

/**
 * AQL Generator that generates a list of Edge Collections to include in the query
 *
 * @author Colin Findlay
 */
public class EdgeCollectionGenerator implements ArangoQueryGenerator {

    @Override
    public String generateAql(ArangoGraphQLContext ctx) {
        return ctx.getEdgeCollections().stream()
                .map(x -> edgeCollection(x, ctx.getRootTraversalDirection()))
                .collect(Collectors.joining(","));
    }

    private String edgeCollection(EdgeCollection edgeCollection, ArangoEdgeTraversalDirection rootTraversalDirection) {
        if (edgeCollection.getDirection().equals(rootTraversalDirection.direction())) {
            return edgeCollection.getName();
        }
        DirectionValue directionValue = new DirectionValue(edgeCollection.getDirection());
        return directionValue.get() + " " + edgeCollection.getName();
    }

    @Override
    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
        return ctx.hasTraversal();
    }
}
