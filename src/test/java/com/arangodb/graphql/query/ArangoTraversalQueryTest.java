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

package com.arangodb.graphql.query;

import com.arangodb.ArangoDatabase;
import com.arangodb.graphql.context.ArangoGraphQLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArangoTraversalQueryTest {

    @Mock
    private ArangoGraphQLContext context;

    private ArangoTraversalQuery query;

    @Before
    public void setUp(){
        query = new ArangoTraversalQuery("RETURN 1", context);
    }

    @Test
    public void getAql() {
        assertThat(query.getAql(), equalTo("RETURN 1"));
    }

    @Test
    public void getContext() {
        assertThat(query.getContext(), equalTo(context));
    }

    @Test
    public void params() {
        String rootCollection = "myRootCollection";
        int min = 1;
        int max = 3;

        when(context.min()).thenReturn(min);
        when(context.max()).thenReturn(max);
        when(context.getRootCollection()).thenReturn(rootCollection);
        when(context.hasTraversal()).thenReturn(true);

        assertThat(query.params().get("@rootCollection"), equalTo(rootCollection));
        assertThat(query.params().get("min"), equalTo(min));
        assertThat(query.params().get("max"), equalTo(max));

    }

    @Test
    public void paramsNoTraversal() {
        String rootCollection = "myRootCollection";

        when(context.getRootCollection()).thenReturn(rootCollection);
        when(context.hasTraversal()).thenReturn(false);

        assertThat(query.params().get("@rootCollection"), equalTo(rootCollection));
        assertThat(query.params().get("min"), nullValue());
        assertThat(query.params().get("max"), nullValue());

    }
}