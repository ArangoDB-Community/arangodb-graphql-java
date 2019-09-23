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

import com.arangodb.graphql.context.ArangoFilter;
import com.arangodb.graphql.context.ArangoFilterGroup;
import com.arangodb.graphql.context.ArangoGraphQLContext;

import java.util.Iterator;
import java.util.List;

/**
 * Abstract base class for generating AQL FILTER Statements
 *
 * @author Colin Findlay
 * @see RootFilterGenerator
 * @see TraversalFilterGenerator
 */
public abstract class AbstractFilterGenerator implements ArangoQueryGenerator {

    @Override
    public String generateAql(ArangoGraphQLContext ctx) {
        List<ArangoFilterGroup> filterGroups = filtersFor(ctx);
        StringBuilder sb = new StringBuilder();

        Iterator<ArangoFilterGroup> filterGroupIterator = filterGroups.iterator();
        while (filterGroupIterator.hasNext()){
            ArangoFilterGroup filterGroup = filterGroupIterator.next();
            List<ArangoFilter> filters = filterGroup.getFilters();

            Iterator<ArangoFilter> filterIterator = filters.iterator();
            while (filterIterator.hasNext()) {
                sb.append(filter(filterIterator.next(), filterGroup.getDepth()));

                if (filterIterator.hasNext()) {
                    sb.append("\n");
                }
            }

            if (filterGroupIterator.hasNext()) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Provide a list of filter groups for the specified GraphQL Context
     *
     * Implementations may return filters for the root collection, or filters on traversals
     *
     * @param ctx The GraphQL context
     * @return A list of filter groups for the specified GraphQL Context
     */
    public abstract List<ArangoFilterGroup> filtersFor(ArangoGraphQLContext ctx);


    private String filter(ArangoFilter filter, int depth) {

        boolean edgeFilter = depth >=0;
        String prefix = edgeFilter ? "p.vertices[" + depth + "]." : "rootVertex.";

        StringBuilder sb = new StringBuilder();
        StringBuilder isSameCollection = new StringBuilder();
        sb.append("FILTER (");

        if(edgeFilter){
            sb.append("(");
            isSameCollection.append("IS_SAME_COLLECTION(\"");
            isSameCollection.append(filter.getEdgeCollection());
            isSameCollection.append("\", p.edges[");
            isSameCollection.append(depth -1);
            isSameCollection.append("]._id)");
        }

        Iterator<ArangoFilter> iterator = filter.getValues().iterator();
        while (iterator.hasNext()) {
            Object value = iterator.next();

            sb.append(prefix);
            sb.append(filter.getKey());
            sb.append(" == ");

            FilterValue filterValue = new FilterValue(value);
            sb.append(filterValue.get());

            if (iterator.hasNext()) {
                sb.append(" || ");
            }
        }
        sb.append(")");

        if(edgeFilter) {
            sb.append(" AND ");
            sb.append(isSameCollection.toString());
            sb.append(")");

            sb.append(" OR (NOT ");
            sb.append(isSameCollection.toString());
            sb.append(")");
        }

        return sb.toString();

    }

}
