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

package com.arangodb.graphql.query.result.resolver;

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.graphql.query.result.PathEdge;

/**
 * Model class representing a traversal in the result resolver
 *
 * @author Colin Findlay
 */
public class ResultResolverTraversalDestination {

    private final BaseDocument source;

    private final BaseDocument target;

    private final PathEdge edge;

    /**
     * Default Consructor
     * @param source Source document
     * @param target Target document
     * @param edge Edge between source and target
     */
    public ResultResolverTraversalDestination(BaseDocument source, BaseDocument target, PathEdge edge) {
        this.source = source;
        this.target = target;
        this.edge = edge;
    }

    /**
     *
     * @return Source Document
     */
    public BaseDocument getSource() {
        return source;
    }

    /**
     *
     * @return Target document
     */
    public BaseDocument getTarget() {
        return target;
    }

    /**
     *
     * @return Edge document
     */
    public PathEdge getEdge() {
        return edge;
    }
}
