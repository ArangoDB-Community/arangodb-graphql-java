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

package com.arangodb.graphql.context;

import com.arangodb.model.TraversalOptions;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class ArangoEdgeTraversalDirectionTest {

    @Test
    public void inbound() {
        ArangoEdgeTraversalDirection traversalDirection = new ArangoEdgeTraversalDirection();
        traversalDirection.push(TraversalOptions.Direction.inbound);
        assertThat(traversalDirection.direction(), equalTo(TraversalOptions.Direction.inbound));
    }

    @Test
    public void outbound() {
        ArangoEdgeTraversalDirection traversalDirection = new ArangoEdgeTraversalDirection();
        traversalDirection.push(TraversalOptions.Direction.outbound);
        assertThat(traversalDirection.direction(), equalTo(TraversalOptions.Direction.outbound));
    }

    @Test
    public void any() {
        ArangoEdgeTraversalDirection traversalDirection = new ArangoEdgeTraversalDirection();
        traversalDirection.push(TraversalOptions.Direction.inbound);
        traversalDirection.push(TraversalOptions.Direction.outbound);
        assertThat(traversalDirection.direction(), equalTo(TraversalOptions.Direction.any));
    }

    @Test
    public void anyExplicit() {
        ArangoEdgeTraversalDirection traversalDirection = new ArangoEdgeTraversalDirection();
        traversalDirection.push(TraversalOptions.Direction.any);
        assertThat(traversalDirection.direction(), equalTo(TraversalOptions.Direction.any));
    }

    @Test
    public void anyExplicitWithInbound() {
        ArangoEdgeTraversalDirection traversalDirection = new ArangoEdgeTraversalDirection();
        traversalDirection.push(TraversalOptions.Direction.any);
        traversalDirection.push(TraversalOptions.Direction.inbound);
        assertThat(traversalDirection.direction(), equalTo(TraversalOptions.Direction.any));
    }

    @Test
    public void anyExplicitWithOutbound() {
        ArangoEdgeTraversalDirection traversalDirection = new ArangoEdgeTraversalDirection();
        traversalDirection.push(TraversalOptions.Direction.any);
        traversalDirection.push(TraversalOptions.Direction.outbound);
        assertThat(traversalDirection.direction(), equalTo(TraversalOptions.Direction.any));
    }





}