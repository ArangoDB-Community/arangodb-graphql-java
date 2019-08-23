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

package com.arangodb.graphql.schema;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLObjectType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArangoTypeAliasDirectiveTest {

    private ArangoTypeAliasDirective directive;

    @Mock
    private GraphQLObjectType field;

    @Mock
    private GraphQLDirective schemaDirective;

    @Mock
    private GraphQLArgument argument;

    @Test
    public void testName(){

        String expected = "myAlias";

        when(field.getDirective(eq("alias"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(eq("name"))).thenReturn(argument);
        when(argument.getValue()).thenReturn(expected);

        directive = new ArangoTypeAliasDirective(field);
        assertThat(directive.getName(), equalTo(expected));

    }

    @Test
    public void testNoDirective(){

        when(field.getDirective(eq("alias"))).thenReturn(null);

        directive = new ArangoTypeAliasDirective(field);
        assertThat(directive.getName(), nullValue());

    }

    @Test
    public void testNoName(){

        when(field.getDirective(eq("alias"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(eq("name"))).thenReturn(null);

        directive = new ArangoTypeAliasDirective(field);

        assertThat(directive.getName(), nullValue());

    }

}