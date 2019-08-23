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
import com.arangodb.graphql.context.SelectedFieldCollector;
import com.arangodb.graphql.query.result.ArangoTraversalQueryResult;
import com.arangodb.model.AqlQueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

/**
 * This class is responsible for executing a query on ArangoDB
 *
 * @author Colin Findlay
 */
public class ArangoTraversalQueryExecutor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArangoDB arango;
    private final String databaseName;

    /**
     * Default Constructor
     * @param arango An ArangoDB
     * @param databaseName The name of the database to execute the query on
     */
    public ArangoTraversalQueryExecutor(ArangoDB arango, String databaseName) {
        this.arango = arango;
        this.databaseName = databaseName;
    }

    private ArangoCursor<BaseDocumentPathEntity>  executeOnDb(ArangoTraversalQuery query){
        ArangoDatabase db = arango.db(databaseName);
        logger.debug("Begin Query Execution");
        Instant start = Instant.now();
        ArangoCursor<BaseDocumentPathEntity> cursor = db.query(query.getAql(), query.params(), new AqlQueryOptions(), BaseDocumentPathEntity.class);
        Instant finish = Instant.now();
        long duration = Duration.between(start, finish).toMillis();
        logger.debug("Query Execution Completed in {}ms", duration);
        return cursor;
    }

    private ArangoTraversalQueryResult processResult(ArangoTraversalQuery query, ArangoCursor<BaseDocumentPathEntity> cursor){
        logger.debug("Begin Result Processing");
        Instant start = Instant.now();
        ArangoTraversalQueryResult result = new ArangoTraversalQueryResult(query.getContext(), cursor.asListRemaining());
        Instant finish = Instant.now();
        long duration = Duration.between(start, finish).toMillis();
        logger.debug("Result Processing Completed in {}ms", duration);
        return result;
    }

    /**
     * Execute the supplied query
     * @param query The query to execute
     * @return The query result
     */
    public ArangoTraversalQueryResult execute(ArangoTraversalQuery query) {

        ArangoCursor<BaseDocumentPathEntity> cursor = executeOnDb(query);
        return processResult(query, cursor);

    }
}
