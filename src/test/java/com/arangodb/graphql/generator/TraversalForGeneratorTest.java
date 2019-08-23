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
import com.arangodb.model.TraversalOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TraversalForGeneratorTest {

    @Mock
    private ArangoGraphQLContext context;

    @Mock
    private ArangoEdgeTraversalDirection edgeTraversalDirection;

    private TraversalForGenerator generator;

    @Before
    public void setUp(){
        generator = new TraversalForGenerator();
        when(context.getRootTraversalDirection()).thenReturn(edgeTraversalDirection);
    }

    @Test
    public void generateAqlInbound() {
        when(edgeTraversalDirection.direction()).thenReturn(TraversalOptions.Direction.inbound);
        assertThat(generator.generateAql(context), equalTo("FOR v, e, p IN @min..@max INBOUND rootVertex"));
    }

    @Test
    public void generateAqlOutbound() {
        when(edgeTraversalDirection.direction()).thenReturn(TraversalOptions.Direction.outbound);
        assertThat(generator.generateAql(context), equalTo("FOR v, e, p IN @min..@max OUTBOUND rootVertex"));
    }

    @Test
    public void generateAqlAny() {
        when(edgeTraversalDirection.direction()).thenReturn(TraversalOptions.Direction.any);
        assertThat(generator.generateAql(context), equalTo("FOR v, e, p IN @min..@max ANY rootVertex"));
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