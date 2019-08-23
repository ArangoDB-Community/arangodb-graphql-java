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

import com.arangodb.graphql.context.ArangoFilterGroup;
import com.arangodb.graphql.context.ArangoGraphQLContext;

import java.util.List;

/**
 * AQL Generator that will generate filters for the traversals in the query
 *
 * @author Colin Findlay
 */
public class TraversalFilterGenerator extends AbstractFilterGenerator {
    @Override
    public List<ArangoFilterGroup> filtersFor(ArangoGraphQLContext ctx) {
        return ctx.getTraversalFilters();
    }

    @Override
    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
        return ctx.hasTraversal();
    }
}
