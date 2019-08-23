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

import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.context.ArangoEdgeTraversalDirection;
import com.arangodb.graphql.model.EdgeCollection;
import com.arangodb.model.TraversalOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EdgeCollectionGeneratorTest {


    @Mock
    private ArangoGraphQLContext context;

    @Mock
    private ArangoEdgeTraversalDirection edgeTraversalDirection;

    private EdgeCollectionGenerator generator;

    @Before
    public void setUp(){
        generator = new EdgeCollectionGenerator();
        when(context.getRootTraversalDirection()).thenReturn(edgeTraversalDirection);
    }

    @Test
    public void generateAqlNoEdgeCollections() {
        when(context.getEdgeCollections()).thenReturn(Collections.EMPTY_LIST);
        assertThat(generator.generateAql(context), equalTo(""));
    }

    @Test
    public void generateAqlOneEdgeCollection() {

        when(edgeTraversalDirection.direction()).thenReturn(TraversalOptions.Direction.inbound);
        when(context.getEdgeCollections())
                .thenReturn(Arrays.asList(
                        new EdgeCollection("a", TraversalOptions.Direction.inbound)
                ));
        assertThat(generator.generateAql(context), equalTo("a"));
    }


    @Test
    public void generateAqlMultipleEdgeCollection() {

        when(edgeTraversalDirection.direction()).thenReturn(TraversalOptions.Direction.inbound);
        when(context.getEdgeCollections())
                .thenReturn(Arrays.asList(
                        new EdgeCollection("a", TraversalOptions.Direction.inbound),
                        new EdgeCollection("b", TraversalOptions.Direction.inbound)
                ));
        assertThat(generator.generateAql(context), equalTo("a,b"));
    }


    @Test
    public void generateAqlOneEdgeCollectionMixedDirection() {

        when(edgeTraversalDirection.direction()).thenReturn(TraversalOptions.Direction.inbound);
        when(context.getEdgeCollections())
                .thenReturn(Arrays.asList(
                        new EdgeCollection("a", TraversalOptions.Direction.outbound)
                ));
        assertThat(generator.generateAql(context), equalTo("OUTBOUND a"));
    }


    @Test
    public void generateAqlMultipleEdgeCollectionMixedDirection() {

        when(edgeTraversalDirection.direction()).thenReturn(TraversalOptions.Direction.inbound);
        when(context.getEdgeCollections())
                .thenReturn(Arrays.asList(
                        new EdgeCollection("a", TraversalOptions.Direction.inbound),
                        new EdgeCollection("b", TraversalOptions.Direction.outbound),
                        new EdgeCollection("c", TraversalOptions.Direction.inbound)
                ));
        assertThat(generator.generateAql(context), equalTo("a,OUTBOUND b,c"));
    }

    @Test
    public void shouldGenerateFor() {
        when(context.hasTraversal()).thenReturn(true);
        assertTrue(generator.shouldGenerateFor(context));
    }


    @Test
    public void shouldNotGenerateFor() {
        when(context.hasTraversal()).thenReturn(false);
        assertFalse(generator.shouldGenerateFor(context));
    }
}