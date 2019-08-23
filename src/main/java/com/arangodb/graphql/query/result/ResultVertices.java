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

import com.arangodb.entity.BaseDocument;
import com.arangodb.graphql.query.BaseDocumentPathEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Class representing all the Vertices in a GraphQL result. Provides the capability to look up vertices by ID, and locate root vertices
 *
 * @author Colin Findlay
 */
public class ResultVertices {

    private String rootCollection;

    private Map<String, BaseDocument> vertices;

    private Set<BaseDocument> rootVertices;

    /**
     * Default Constructor
     * @param rootCollection The name of the Root Collection
     * @param paths The paths returned from the Arango Query
     */
    public ResultVertices(String rootCollection, List<BaseDocumentPathEntity> paths) {
        this.rootCollection = rootCollection;
        vertices = paths.stream()
                .flatMap(x -> x.getVertices().stream())
                .filter(x -> x != null)
                .collect(toMap(BaseDocument::getId, x -> x, (x, y) -> x));

        rootVertices = paths.stream()
                .map(x -> x.getVertices())
                .map(x -> x.size() > 0 ? x.get(0) : null)
                .filter(x -> x != null)
                .collect(Collectors.toSet());
    }

    /**
     * Get a vertex by ID
     * @param id The ID of the vertex
     * @return The Vertex with the specified ID or null if not found
     */
    public BaseDocument get(String id) {
        return vertices.get(id);
    }

    /**
     * Get all the root vertices in the result
     * @return All the root vertices in the result
     */
    public Set<BaseDocument> rootVertices() {
        return rootVertices;
    }

}
