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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReturnStatementGeneratorTest {

    @Mock
    private ArangoGraphQLContext context;

    private ReturnStatementGenerator generator;

    @Before
    public void setUp(){
        generator = new ReturnStatementGenerator();
    }

    @Test
    public void generateAql() {
        when(context.hasTraversal()).thenReturn(true);
        String aql = generator.generateAql(context);
        assertThat(aql, equalTo("RETURN p"));
    }

    @Test
    public void generateAqlNoTraveral() {
        when(context.hasTraversal()).thenReturn(false);
        String aql = generator.generateAql(context);
        assertThat(aql, equalTo("RETURN {edges: [], vertices: [rootVertex]}"));
    }

    @Test
    public void shouldGenerateFor() {
        assertTrue(generator.shouldGenerateFor(context));
    }
}