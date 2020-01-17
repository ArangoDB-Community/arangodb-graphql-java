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

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SortGeneratorTest {

    @Mock
    private ArangoGraphQLContext context;

    private SortGenerator generator;

    @Before
    public void setUp(){
        generator = new SortGenerator();
    }

    @Test
    public void generateSingleSortAsc() {
        Map sort = new LinkedHashMap();
        sort.put("propA", "ASC");
        when(context.getSort()).thenReturn(sort);
        assertThat(generator.generateAql(context), equalTo("SORT rootVertex.propA"));
    }

    @Test
    public void generateSingleSortDesc() {
        Map sort = new LinkedHashMap();
        sort.put("propA", "DESC");
        when(context.getSort()).thenReturn(sort);
        assertThat(generator.generateAql(context), equalTo("SORT rootVertex.propA DESC"));
    }

    @Test
    public void generateMultipleSort() {
        Map sort = new LinkedHashMap();
        sort.put("propA", "ASC");
        sort.put("propB", "DESC");
        when(context.getSort()).thenReturn(sort);
        assertThat(generator.generateAql(context), equalTo("SORT rootVertex.propA, rootVertex.propB DESC"));
    }


    @Test
    public void shouldGenerateForPopulatedSortMap() {
        Map sort = new LinkedHashMap();
        sort.put("propA", "ASC");
        when(context.getSort()).thenReturn(sort);
        assertTrue(generator.shouldGenerateFor(context));
    }


    @Test
    public void shouldNotGenerateForUnpopulatedSortMap() {
        Map sort = new LinkedHashMap();
        when(context.getSort()).thenReturn(sort);
        assertFalse(generator.shouldGenerateFor(context));
    }


    @Test
    public void shouldNotGenerateForNullSortMap() {
        when(context.getSort()).thenReturn(null);
        assertFalse(generator.shouldGenerateFor(context));
    }


}