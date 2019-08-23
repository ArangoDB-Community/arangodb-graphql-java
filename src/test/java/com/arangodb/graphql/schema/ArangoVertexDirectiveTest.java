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
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.StringValue;
import graphql.schema.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArangoVertexDirectiveTest {

    private ArangoVertexDirective directive;

    @Mock
    private GraphQLObjectType field;

    @Mock
    private DirectivesContainer directivesContainer;

    @Mock
    private Directive directiveContainerDirective;

    @Mock
    private GraphQLDirective schemaDirective;

    @Mock
    private GraphQLArgument argument;

    @Mock
    private Argument directiveArgument;

    @Test
    public void testCollection(){

        String expected = "collectionName";

        when(field.getDirective(eq("vertex"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(eq("collection"))).thenReturn(argument);
        when(argument.getValue()).thenReturn(expected);

        directive = new ArangoVertexDirective(field);
        assertThat(directive.getCollection(), equalTo(expected));

    }

    @Test
    public void testCollectionWithDirectivesContainer(){

        String expected = "collectionName";

        when(directivesContainer.getDirective(eq("vertex"))).thenReturn(directiveContainerDirective);
        when(directiveContainerDirective.getArgument(eq("collection"))).thenReturn(directiveArgument);
        when(directiveArgument.getValue()).thenReturn(new StringValue(expected));

        directive = new ArangoVertexDirective(directivesContainer);
        assertThat(directive.getCollection(), equalTo(expected));

    }

    @Test
    public void testNoDirective(){

        String expected = "collectionName";

        when(field.getDirective(eq("vertex"))).thenReturn(null);

        directive = new ArangoVertexDirective(field);
        assertThat(directive.getCollection(), nullValue());

    }

    @Test
    public void testNoCollection(){

        when(field.getDirective(eq("vertex"))).thenReturn(schemaDirective);
        when(schemaDirective.getArgument(eq("collection"))).thenReturn(null);

        directive = new ArangoVertexDirective(field);
        assertThat(directive.getCollection(), nullValue());

    }

}