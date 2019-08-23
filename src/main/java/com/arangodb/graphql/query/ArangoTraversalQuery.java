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

package com.arangodb.graphql.query;

import com.arangodb.graphql.context.ArangoGraphQLContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents a query that will be executed on ArangoDB
 *
 * @author Colin Findlay
 */
public class ArangoTraversalQuery {

    private String aql;

    private ArangoGraphQLContext context;

    /**
     * Default Constructor
     * @param aql An AQL Query String
     * @param context The GraphQL context used to create the query
     */
    public ArangoTraversalQuery(String aql, ArangoGraphQLContext context) {
        this.aql = aql;
        this.context = context;
    }

    /**
     *
     * @return The AQL Query String
     */
    public String getAql() {
        return aql;
    }

    /**
     *
     * @return The GraphQL context used to create the query
     */
    public ArangoGraphQLContext getContext() {
        return context;
    }

    /**
     * The query params that will be passed to Arango when the query is executed
     * @return Query params including the root collection, and min/max traversal depths if appropriate.
     */
    public Map<String, Object> params() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("@rootCollection", context.getRootCollection());
        if (context.hasTraversal()) {
            params.put("min", context.min());
            params.put("max", context.max());
        }
        return params;
    }
}
