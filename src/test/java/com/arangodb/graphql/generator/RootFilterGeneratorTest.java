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

import com.arangodb.graphql.context.ArangoFilter;
import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.context.ArangoRootFilterGroup;
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
public class RootFilterGeneratorTest {


    @Mock
    private ArangoGraphQLContext context;

    @Mock
    private ArangoRootFilterGroup filterGroup;

    private RootFilterGenerator generator;


    @Before
    public void setUp(){
        generator = new RootFilterGenerator();
        when(context.getRootFilters()).thenReturn(filterGroup);
        when(filterGroup.getDepth()).thenReturn(-1);
    }


    @Test
    public void generateAqlSingleNumericFilter(){

        when(filterGroup.getFilters()).thenReturn(Arrays.asList(
                new ArangoFilter("jersey", 29)
        ));

        assertThat(generator.generateAql(context), equalTo("FILTER (rootVertex.jersey == 29)"));

    }

    @Test
    public void generateAqlSingleNonNumericFilter(){

        when(filterGroup.getFilters()).thenReturn(Arrays.asList(
                new ArangoFilter("name", "Felix Potvin")
        ));

        assertThat(generator.generateAql(context), equalTo("FILTER (rootVertex.name == \"Felix Potvin\")"));

    }



    @Test
    public void generateAqlMultipleNonNumericFilter(){

        when(filterGroup.getFilters()).thenReturn(Arrays.asList(
                new ArangoFilter("name", "Felix Potvin"),
                new ArangoFilter("jersey", 29)
        ));

        assertThat(generator.generateAql(context), equalTo("FILTER (rootVertex.name == \"Felix Potvin\")\nFILTER (rootVertex.jersey == 29)"));

    }

    @Test
    public void generateAqlMultiValueFilter(){

        when(filterGroup.getFilters()).thenReturn(Arrays.asList(
                new ArangoFilter("jersey", Arrays.asList(29, 93))
        ));

        assertThat(generator.generateAql(context), equalTo("FILTER (rootVertex.jersey == 29 || rootVertex.jersey == 93)"));

    }


    @Test
    public void shouldGenerateFor() {

        when(filterGroup.getFilters()).thenReturn(Arrays.asList(
                new ArangoFilter("jersey", 29)
        ));
        assertTrue(generator.shouldGenerateFor(context));

    }

    @Test
    public void shouldNotGenerateFor() {

        when(filterGroup.getFilters()).thenReturn(Collections.EMPTY_LIST);
        assertFalse(generator.shouldGenerateFor(context));

    }

}