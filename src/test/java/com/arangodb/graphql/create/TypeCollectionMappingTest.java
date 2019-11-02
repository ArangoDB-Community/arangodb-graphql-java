package com.arangodb.graphql.create;

import org.junit.Test;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class TypeCollectionMappingTest {

    @Test
    public void addEdgeCollection() {
        final String testVertexCollection = "banana";
        final String testEdgeCollection = "apple";
        TypeCollectionMapping mapping = new TypeCollectionMapping(testVertexCollection);
        mapping.addEdgeCollection(testEdgeCollection);
        assertThat(mapping.getEdgeCollections(), contains(testEdgeCollection));
        assertThat(mapping.getEdgeCollections().size(), equalTo(1));
    }

    @Test
    public void vertexCollection() {
        final String testVertexCollection = "banana";
        TypeCollectionMapping mapping = new TypeCollectionMapping(testVertexCollection);
        assertThat(mapping.getVertexCollection(), equalTo(testVertexCollection));
    }
}