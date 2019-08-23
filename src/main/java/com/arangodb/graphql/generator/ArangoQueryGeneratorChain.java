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
import com.arangodb.graphql.query.ArangoTraversalQuery;

import java.util.LinkedList;
import java.util.List;

/**
 * A chain of responsibility pattern implementation that will pass a GraphQL context along a chain of AQL Generators.
 *
 * Upon receiving a request, each AQL Generator decides either to process the request and add to the AQL query string before passing to the next generator
 * in the chain, or to skip it and pass it to the next handler in the chain.
 *
 * The output of the chain will be an ArangoTraversalQuery object containing a fully formed AQL Query String
 *
 * @author Colin Findlay
 */
public class ArangoQueryGeneratorChain {

    private List<ArangoQueryGenerator> chain;

    /**
     * Default Constructor
     */
    public ArangoQueryGeneratorChain() {
        chain = new LinkedList<>();
    }

    /**
     *
     * @return The list of generators in the chain
     */
    public List<ArangoQueryGenerator> getChain() {
        return chain;
    }

    /**
     * Builder method allowing you to fluently build a chain
     * @param generator The generator to add to the chain
     * @return This chain with the newly added generator
     */
    public ArangoQueryGeneratorChain with(ArangoQueryGenerator generator) {
        chain.add(generator);
        return this;
    }

    /**
     * Execute the chain.
     *
     * Upon receiving a request, each AQL Generator decides either to process the request and add to the AQL query string before passing to the next generator
     * in the chain, or to skip it and pass it to the next handler in the chain.
     *
     *
     * @param ctx The GraphQL Context
     * @return An ArangoTraversalQuery object containing a fully formed AQL Query String
     */
    public ArangoTraversalQuery execute(ArangoGraphQLContext ctx) {
        StringBuilder buffer = new StringBuilder();
        chain.stream()
                .filter(generator -> generator.shouldGenerateFor(ctx))
                .map(generator -> generator.generateAql(ctx))
                .forEach(aql -> append(buffer, aql));

        return new ArangoTraversalQuery(buffer.toString(), ctx);
    }

    private void append(StringBuilder buffer, String queryPart) {
        if (queryPart != null) {
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(queryPart);
        }
    }


}
