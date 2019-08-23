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

/**
 * AQL Generator that generates an AQL RETURN Statement.
 *
 * This generator will generate a RETURN statement that returns the path for traversal queries, and for non-traversal queries it will
 * synthesize a path entity with no edges and just the vertex returned from the query so that the result format is the same regardless if
 * the query performs a traversal or not.
 *
 * This allows the processing of the result to be the same even when a traversal did not occur in the query.
 *
 * @author Colin Findlay
 *
 */
public class ReturnStatementGenerator implements ArangoQueryGenerator {
    @Override
    public String generateAql(ArangoGraphQLContext ctx) {
        if (ctx.hasTraversal()) {
            return "RETURN p";
        } else {
            return "RETURN {edges: [], vertices: [rootVertex]}";
        }
    }

    @Override
    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
        return true;
    }
}
