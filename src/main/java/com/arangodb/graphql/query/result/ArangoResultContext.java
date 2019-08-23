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

package com.arangodb.graphql.query.result;

import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.query.BaseDocumentPathEntity;

import java.util.List;

/**
 * Context class reprsenting the result from an Arango Query, consisting of Vertices and Edges from the paths, and the context used to execute the query
 *
 * @author Colin Findlay
 */
public class ArangoResultContext {

    private ResultVertices vertices;

    private ResultEdges edges;

    private ArangoGraphQLContext graphQLContext;

    /**
     * Default Constructor
     * @param graphQLContext The context used to execute the query
     * @param paths The paths returned from the query
     */
    public ArangoResultContext(ArangoGraphQLContext graphQLContext, List<BaseDocumentPathEntity> paths) {
        this.graphQLContext = graphQLContext;
        vertices = new ResultVertices(graphQLContext.getRootCollection(), paths);
        edges = new ResultEdges(paths);
    }

    /**
     *
     * @return The vertices returned from the query
     */
    public ResultVertices getVertices() {
        return vertices;
    }

    /**
     *
     * @return The edges returned from the query
     */
    public ResultEdges getEdges() {
        return edges;
    }

    /**
     *
     * @return The context used to execute the query
     */
    public ArangoGraphQLContext getGraphQLContext() {
        return graphQLContext;
    }

}
