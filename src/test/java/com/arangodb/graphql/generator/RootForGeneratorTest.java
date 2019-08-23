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
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RootForGeneratorTest {

    @Mock
    private ArangoGraphQLContext context;

    private RootForGenerator generator;

    @Before
    public void setUp(){
        generator = new RootForGenerator();
    }

    @Test
    public void generateAql() {
        assertThat(generator.generateAql(context), equalTo("FOR rootVertex IN @@rootCollection"));
    }

    @Test
    public void shouldGenerateFor() {
        assertTrue(generator.shouldGenerateFor(context));
    }
}