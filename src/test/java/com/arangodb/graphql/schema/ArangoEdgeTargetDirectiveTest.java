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

import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArangoEdgeTargetDirectiveTest {

    private ArangoEdgeTargetDirective directive;

    @Mock
    private GraphQLFieldDefinition field;

    @Mock
    private GraphQLDirective schemaDirective;

    @Test
    public void isEdgeTarget() {
        when(field.getDirective("edgeTarget")).thenReturn(schemaDirective);
        directive = new ArangoEdgeTargetDirective(field);
        assertTrue(directive.isEdgeTarget());
    }

    @Test
    public void isNotEdgeTarget() {
        when(field.getDirective("edgeTarget")).thenReturn(null);
        directive = new ArangoEdgeTargetDirective(field);
        assertFalse(directive.isEdgeTarget());
    }


}