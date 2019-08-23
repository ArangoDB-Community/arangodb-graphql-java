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
 * Interface for a class that is capable of generating a snippet of AQL from the specified context
 *
 * @author Colin Findlay
 */
public interface ArangoQueryGenerator {

    /**
     * Generate a snippet of AQL for the supplied context
     * @param ctx The GraphQL Context
     * @return A snippet of AQL for the supplied context
     */
    String generateAql(ArangoGraphQLContext ctx);

    /**
     * Determine if this query generator should execute for the current context
     * @param ctx The GraphQL Context
     * @return True if this query generator should be executed, false if it should be skipped
     */
    boolean shouldGenerateFor(ArangoGraphQLContext ctx);

}
