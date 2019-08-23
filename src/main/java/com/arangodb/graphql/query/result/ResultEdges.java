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

import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.graphql.query.BaseDocumentPathEntity;
import com.arangodb.model.TraversalOptions;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * Class representing all the Edges in a GraphQL result. Provides the capability to look up edges from ot to a particular document
 *
 * @author Colin Findlay
 */
public class ResultEdges {

    private Map<String, Map<String, Set<PathEdge>>> edges;

    private Map<String, Map<String, Set<PathEdge>>> inboundEdges;

    /**
     * Default Constructor
     * @param paths The paths returned from the Arango Query
     */
    public ResultEdges(List<BaseDocumentPathEntity> paths) {
        edges = paths.stream()
                .flatMap(x -> x.getEdges().stream())
                .collect(groupingBy(y -> y.getId().split("/")[0],
                        groupingBy(BaseEdgeDocument::getFrom,
                                mapping(baseEdgeDocument -> new PathEdge(baseEdgeDocument, TraversalOptions.Direction.outbound), toSet()))));

        inboundEdges = paths.stream()
                .flatMap(x -> x.getEdges().stream())
                .collect(groupingBy(y -> y.getId().split("/")[0],
                        groupingBy(BaseEdgeDocument::getTo,
                                mapping(baseEdgeDocument -> new PathEdge(baseEdgeDocument, TraversalOptions.Direction.inbound), toSet()))));
    }

    /**
     * Get the edges that match the supplied critera
     * @param collection The Edge Collection
     * @param documentId The document the edge is linked to
     * @param direction The direction of the edge
     * @return A list of edges linked to the supplied document in the specified direction from the specified edge collection
     */
    public Set<PathEdge> edgesFor(String collection, String documentId, TraversalOptions.Direction direction) {

        switch (direction){
            case outbound:
                return edgesFor(collection, documentId, edges);
            case inbound:
                return edgesFor(collection, documentId, inboundEdges);
            default:
                Set<PathEdge> anyEdgeDocuments = new HashSet<>();
                Set<PathEdge> outboundEdgeDocuments = edgesFor(collection, documentId, edges);
                Set<PathEdge> inboundEdgeDocuments = edgesFor(collection, documentId, inboundEdges);
                if(inboundEdgeDocuments != null) {
                    anyEdgeDocuments.addAll(inboundEdgeDocuments);
                }
                if(outboundEdgeDocuments != null) {
                    anyEdgeDocuments.addAll(outboundEdgeDocuments);
                }
                return anyEdgeDocuments;
        }

    }

    private Set<PathEdge> edgesFor(String collection, String documentId,  Map<String, Map<String, Set<PathEdge>>> edgesMap) {
        Map<String, Set<PathEdge>> collectionMap = edgesMap.get(collection);
        if (collectionMap != null) {
            return collectionMap.get(documentId);
        }
        return null;
    }
}
