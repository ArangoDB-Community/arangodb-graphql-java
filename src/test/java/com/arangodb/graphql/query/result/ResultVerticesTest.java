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
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class ResultVerticesTest {

    private ResultVertices resultVertices;
    private BaseDocument a;
    private BaseDocument b;
    private BaseDocument c;

    @Before
    public void setUp(){

        BaseDocumentPathEntity path = new BaseDocumentPathEntity();
        a = new BaseDocument();
        a.setId("A/1");

        b = new BaseDocument();
        b.setId("B/1");

        c = new BaseDocument();
        c.setId("C/1");

        BaseEdgeDocument a2b = new BaseEdgeDocument("A/1", "B/1");
        BaseEdgeDocument b2c = new BaseEdgeDocument("B/1", "C/1");


        path.setVertices(Arrays.asList(a, b, c));
        path.setEdges(Arrays.asList(a2b, b2c));

        resultVertices = new ResultVertices("A", Arrays.asList(path));

    }

    @Test
    public void get() {

        assertThat(resultVertices.get("A/1"), equalTo(a));
        assertThat(resultVertices.get("B/1"), equalTo(b));
        assertThat(resultVertices.get("C/1"), equalTo(c));

    }

    @Test
    public void rootVertices() {
        Set<BaseDocument> baseDocuments = resultVertices.rootVertices();
        assertThat(baseDocuments.size(), equalTo(1));
        assertThat(baseDocuments.stream().findFirst().get(), equalTo(a));
    }
}