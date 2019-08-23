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

package com.arangodb.graphql.query.result;

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.graphql.query.BaseDocumentPathEntity;
import com.arangodb.model.TraversalOptions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResultEdgesTest {

    private ResultEdges resultEdges;

    @Before
    public void setUp(){

        BaseDocumentPathEntity path = new BaseDocumentPathEntity();
        BaseDocument a = new BaseDocument();
        a.setId("A/1");

        BaseDocument b = new BaseDocument();
        b.setId("B/1");

        BaseDocument c = new BaseDocument();
        c.setId("C/1");

        BaseEdgeDocument a2b = new BaseEdgeDocument("A/1", "B/1");
        a2b.setId("edges/1");
        BaseEdgeDocument b2c = new BaseEdgeDocument("B/1", "C/1");
        b2c.setId("edges/2");


        path.setVertices(Arrays.asList(a, b, c));
        path.setEdges(Arrays.asList(a2b, b2c));

        resultEdges = new ResultEdges(Arrays.asList(path));

    }

    @Test
    public void edgesForA1() {

        Set<PathEdge> edges = resultEdges.edgesFor("edges", "A/1", TraversalOptions.Direction.outbound);
        assertThat(edges.size(), equalTo(1));
        BaseEdgeDocument edgeDocument = edges.stream().findFirst().get();
        assertThat(edgeDocument.getFrom(), equalTo("A/1"));
        assertThat(edgeDocument.getTo(), equalTo("B/1"));

    }

    @Test
    public void edgesForB1() {

        Set<PathEdge> edges = resultEdges.edgesFor("edges", "B/1", TraversalOptions.Direction.outbound);
        BaseEdgeDocument edgeDocument = edges.stream().findFirst().get();
        assertThat(edges.size(), equalTo(1));
        assertThat(edgeDocument.getFrom(), equalTo("B/1"));
        assertThat(edgeDocument.getTo(), equalTo("C/1"));

    }

    @Test
    public void edgesForB1Inbound() {

        Set<PathEdge> edges = resultEdges.edgesFor("edges", "B/1", TraversalOptions.Direction.inbound);
        assertThat(edges.size(), equalTo(1));
        BaseEdgeDocument edgeDocument = edges.stream().findFirst().get();
        assertThat(edgeDocument.getFrom(), equalTo("A/1"));
        assertThat(edgeDocument.getTo(), equalTo("B/1"));

    }

    @Test
    public void edgesForC1Inbound() {

        Set<PathEdge> edges = resultEdges.edgesFor("edges", "C/1", TraversalOptions.Direction.inbound);
        BaseEdgeDocument edgeDocument = edges.stream().findFirst().get();
        assertThat(edges.size(), equalTo(1));
        assertThat(edgeDocument.getFrom(), equalTo("B/1"));
        assertThat(edgeDocument.getTo(), equalTo("C/1"));

    }

    @Test
    public void edgesForA1Any() {

        Set<PathEdge> edges = resultEdges.edgesFor("edges", "A/1", TraversalOptions.Direction.any);
        assertThat(edges.size(), equalTo(1));
        List<PathEdge> edgeList = edges.stream().collect(Collectors.toList());

        PathEdge edgeDocument = edgeList.get(0);
        assertThat(edgeDocument.getFrom(), equalTo("A/1"));
        assertThat(edgeDocument.getTo(), equalTo("B/1"));

    }

    @Test
    public void edgesForB1Any() {

        Set<PathEdge> edges = resultEdges.edgesFor("edges", "B/1", TraversalOptions.Direction.any);
        assertThat(edges.size(), equalTo(2));


        boolean firstEdgePresent = edges.stream().anyMatch(x -> x.getFrom().equals("A/1") && x.getTo().equals("B/1"));
        boolean secondEdgePresent = edges.stream().anyMatch(x -> x.getFrom().equals("B/1") && x.getTo().equals("C/1"));

        assertTrue(firstEdgePresent);
        assertTrue(secondEdgePresent);

    }

    @Test
    public void edgesForC1Any() {

        Set<PathEdge> edges = resultEdges.edgesFor("edges", "C/1", TraversalOptions.Direction.any);
        assertThat(edges.size(), equalTo(1));
        List<PathEdge> edgeList = edges.stream().collect(Collectors.toList());

        PathEdge edgeDocument = edgeList.get(0);
        assertThat(edgeDocument.getFrom(), equalTo("B/1"));
        assertThat(edgeDocument.getTo(), equalTo("C/1"));
    }
}