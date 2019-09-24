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

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.query.result.ArangoTraversalQueryResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLOutputType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArangoTraversalQueryExecutorTest {

    @Mock
    private ArangoDB arango;

    @Mock
    private ArangoTraversalQuery query;

    @Mock
    private ArangoDatabase database;

    @Mock
    private ArangoCursor cursor;

    @Mock
    private ArangoGraphQLContext context;

    @Mock
    private DataFetchingEnvironment dataFetchingEnvironment;

    @Mock
    private GraphQLOutputType outputType;

    private ArangoTraversalQueryExecutor executor;

    @Before
    public void setUp() throws Exception {
        executor = new ArangoTraversalQueryExecutor(arango, "my_db");
    }

    @Test
    public void execute() {


        when(query.getContext()).thenReturn(context);
        when(query.getAql()).thenReturn("RETURN 1");

        when(arango.db("my_db")).thenReturn(database);
        when(database.query(any(), any(), any(), any())).thenReturn(cursor);
        when(cursor.asListRemaining()).thenReturn(Collections.EMPTY_LIST);
        when(context.getEnvironment()).thenReturn(dataFetchingEnvironment);
        when(dataFetchingEnvironment.getFieldType()).thenReturn(outputType);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        ArangoTraversalQueryResult result = executor.execute(query);


        assertThat(result.getResult(), nullValue());

        verify(database).query(captor.capture(), any(), any(), any());
        assertThat(captor.getValue(), equalTo("RETURN 1"));

    }
}