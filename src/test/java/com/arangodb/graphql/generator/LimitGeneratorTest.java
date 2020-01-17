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

import com.arangodb.graphql.context.ArangoEdgeTraversalDirection;
import com.arangodb.graphql.context.ArangoGraphQLContext;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LimitGeneratorTest {


    @Mock
    private ArangoGraphQLContext context;

    private LimitGenerator generator;

    @Before
    public void setUp(){
        generator = new LimitGenerator();
    }

    @Test
    public void generateLimitNoSkip() {
        when(context.getLimit()).thenReturn(100);
        when(context.getSkip()).thenReturn(null);
        assertThat(generator.generateAql(context), equalTo("LIMIT 100"));
    }

    @Test
    public void generateLimitSkip() {
        when(context.getLimit()).thenReturn(100);
        when(context.getSkip()).thenReturn(200);
        assertThat(generator.generateAql(context), equalTo("LIMIT 200, 100"));
    }


    @Test
    public void shouldGenerateForLimit() {
        when(context.getLimit()).thenReturn(100);
        assertTrue(generator.shouldGenerateFor(context));
    }


    @Test
    public void shouldNotGenerateForSkipOnly() {
        when(context.getLimit()).thenReturn(null);
        assertFalse(generator.shouldGenerateFor(context));
    }
}