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

import com.arangodb.model.TraversalOptions;
import graphql.schema.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArangoEdgeDirectiveTest {

    private ArangoEdgeDirective directive;

    @Mock
    private GraphQLFieldDefinition field;

    @Mock
    private GraphQLDirective schemaDirective;

    @Mock
    private GraphQLArgument argument;

    @Test
    public void testCollection(){

        String expected = "collectionName";

        when(field.getDirective(eq("edge"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(eq("collection"))).thenReturn(argument);
        when(argument.getValue()).thenReturn(expected);

        directive = new ArangoEdgeDirective(field);
        assertThat(directive.getCollection(), equalTo(expected));

    }

    @Test
    public void testNoDirective(){

        String expected = "collectionName";

        when(field.getDirective(eq("edge"))).thenReturn(null);

        directive = new ArangoEdgeDirective(field);
        assertThat(directive.getCollection(), nullValue());

    }

    @Test
    public void testNoCollection(){

        when(field.getDirective(eq("edge"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(eq("collection"))).thenReturn(null);

        directive = new ArangoEdgeDirective(field);
        assertThat(directive.getCollection(), nullValue());

    }

    @Test
    public void testDirectionInbound(){

        TraversalOptions.Direction expected = TraversalOptions.Direction.inbound;

        when(field.getDirective(eq("edge"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(any(String.class))).thenReturn(argument);
        when(argument.getValue()).thenReturn(expected.toString());

        directive = new ArangoEdgeDirective(field);
        assertThat(directive.getDirection(), equalTo(TraversalOptions.Direction.inbound));

    }

    @Test
    public void testDirectionOutbound(){

        TraversalOptions.Direction expected = TraversalOptions.Direction.outbound;

        when(field.getDirective(eq("edge"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(any(String.class))).thenReturn(argument);
        when(argument.getValue()).thenReturn(expected.toString());

        directive = new ArangoEdgeDirective(field);
        assertThat(directive.getDirection(), equalTo(TraversalOptions.Direction.outbound));

    }


    @Test
    public void testDirectionAny(){

        TraversalOptions.Direction expected = TraversalOptions.Direction.any;

        when(field.getDirective(eq("edge"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(any(String.class))).thenReturn(argument);
        when(argument.getValue()).thenReturn(expected.toString());

        directive = new ArangoEdgeDirective(field);
        assertThat(directive.getDirection(), equalTo(TraversalOptions.Direction.any));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectionInvalid(){

        TraversalOptions.Direction expected = TraversalOptions.Direction.any;

        when(field.getDirective(eq("edge"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(any(String.class))).thenReturn(argument);
        when(argument.getValue()).thenReturn(expected.toString() + "__invalid");

        directive = new ArangoEdgeDirective(field);

    }
}