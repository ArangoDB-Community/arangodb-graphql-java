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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WithGeneratorTest {


    @Mock
    private ArangoGraphQLContext context;

    private WithGenerator generator;

    @Before
    public void setUp(){
        generator = new WithGenerator();
    }


    @Test
    public void generateAqlNoVertexCollections() {
        when(context.getVertexCollections()).thenReturn(Collections.EMPTY_SET);
        assertThat(generator.generateAql(context), nullValue());
    }

    @Test
    public void generateAqlOneVertexCollection() {
        when(context.getVertexCollections()).thenReturn(new HashSet<>(Arrays.asList(
                "a"
        )));
        assertThat(generator.generateAql(context), equalTo("WITH a"));
    }


    @Test
    public void generateAqlMultipleVertexCollection() {
        when(context.getVertexCollections()).thenReturn(new HashSet<>(Arrays.asList(
                "a",
                "b",
                "c"
        )));
        assertThat(generator.generateAql(context), equalTo("WITH a,b,c"));
    }

    @Test
    public void shouldGenerateFor() {
        when(context.getVertexCollections()).thenReturn(new HashSet<>(Arrays.asList(
                "a"
        )));
        assertTrue(generator.shouldGenerateFor(context));
    }

    @Test
    public void shouldNotGenerateFor() {
        when(context.getVertexCollections()).thenReturn(Collections.EMPTY_SET);
        assertFalse(generator.shouldGenerateFor(context));
    }
}