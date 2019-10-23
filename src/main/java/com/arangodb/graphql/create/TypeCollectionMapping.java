package com.arangodb.graphql.create;

import java.util.HashSet;
import java.util.Set;

public class TypeCollectionMapping {

    private final String vertexCollection;

    private final Set<String> edgeCollections;

    public TypeCollectionMapping(String vertexCollection){
        this.vertexCollection = vertexCollection;
        this.edgeCollections = new HashSet<>();
    }

    public void addEdgeCollection(String edgeCollection){
        this.edgeCollections.add(edgeCollection);
    }


    public String getVertexCollection() {
        return vertexCollection;
    }

    public Set<String> getEdgeCollections() {
        return edgeCollections;
    }
}
