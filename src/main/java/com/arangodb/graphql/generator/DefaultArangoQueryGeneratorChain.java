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

/**
 * Default implementation of an ArangoQueryGeneratorChain that will support generating both traversal and non-traversal queries.
 *
 * This generator supports queries of the following forms
 *
 * Non-traversal Queries
 *
 * FOR rootVertex IN @@rootCollection
 * FILTER {filter criteria for root vertex}
 * RETURN {edges: [], vertices: [rootVertex]}
 *
 * Traveral Queries
 *
 * WITH {list of document collections}
 * FOR rootVertex IN @@rootCollection
 * FILTER {filter criteria for root vertex}
 * FOR v, e, p IN @min..@max OUTBOUND rootVertex
 * {list of edge collections}
 * RETURN p
 *
 * @author Colin Findlay
 */
public class DefaultArangoQueryGeneratorChain extends ArangoQueryGeneratorChain {

    public DefaultArangoQueryGeneratorChain() {
        with(new WithGenerator());
        with(new RootForGenerator());
        with(new SortGenerator());
        with(new LimitGenerator());
        with(new RootFilterGenerator());
        with(new TraversalForGenerator());
        with(new EdgeCollectionGenerator());
        with(new TraversalFilterGenerator());
        with(new ReturnStatementGenerator());

    }
}
