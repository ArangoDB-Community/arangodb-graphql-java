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
import com.arangodb.model.TraversalOptions;

/**
 * AQL Generator that will generate a FOR statement for traversal
 *
 * This implementation simply generates "FOR v, e, p IN @min..@max INBOUND|OUTBOUND|ANY rootVertex" depending on the traversal direction in the GraphQL Context, and allowing
 * min and max traversal depth to be specified as query parameters
 *
 * @author Colin Findlay
 */
public class TraversalForGenerator implements ArangoQueryGenerator {
    @Override
    public String generateAql(ArangoGraphQLContext ctx) {
        TraversalOptions.Direction direction = ctx.getRootTraversalDirection().direction();
        DirectionValue directionValue = new DirectionValue(direction);
        return "FOR v, e, p IN @min..@max " + directionValue.get() + " rootVertex";
    }

    @Override
    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
        return ctx.hasTraversal();
    }
}
