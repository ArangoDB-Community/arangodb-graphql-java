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

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;

import java.util.List;

/**
 * Model class representing a path entity in the Arango result
 *
 * @author Colin Findlay
 */
public class BaseDocumentPathEntity {

    private List<BaseDocument> vertices;

    private List<BaseEdgeDocument> edges;

    /**
     * @return The vertices in the path
     */
    public List<BaseDocument> getVertices() {
        return vertices;
    }

    /**
     * Set the vertices in the path
     * @param vertices the vertices in the path
     */
    public void setVertices(List<BaseDocument> vertices) {
        this.vertices = vertices;
    }

    /**
     * @return The edges in the path
     */
    public List<BaseEdgeDocument> getEdges() {
        return edges;
    }

    /**
     * Set the edges in the path
     * @param edges The edges in the path
     */
    public void setEdges(List<BaseEdgeDocument> edges) {
        this.edges = edges;
    }
}
