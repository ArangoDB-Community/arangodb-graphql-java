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

package com.arangodb.graphql;

import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.context.SelectedFieldCollector;
import com.arangodb.graphql.generator.ArangoQueryGeneratorChain;
import com.arangodb.graphql.query.ArangoTraversalQuery;
import com.arangodb.graphql.query.ArangoTraversalQueryExecutor;
import com.arangodb.graphql.query.result.ArangoTraversalQueryResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A DataFetcher implementation that
 *
 * 1. Converts the incoming GraphQL query to a native AQL query
 * 2. Executes the query on Arango
 * 3. Marshals the result of a collection of paths from Arango back to the format prescribed by the GraphQL query
 *
 * @author Colin Findlay
 *
 */
public class ArangoDataFetcher implements DataFetcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ArangoQueryGeneratorChain queryGenerator;

    private ArangoTraversalQueryExecutor queryExecutor;

    /**
     * Default Constructor
     * @param queryGenerator A query generator chain
     * @param queryExecutor A query executor
     */
    public ArangoDataFetcher(ArangoQueryGeneratorChain queryGenerator, ArangoTraversalQueryExecutor queryExecutor) {
        this.queryGenerator = queryGenerator;
        this.queryExecutor = queryExecutor;
    }

    private ArangoTraversalQuery createQuery(DataFetchingEnvironment dataFetchingEnvironment){
        logger.debug("Converting incoming GraphQL Query to AQL Query");
        Instant start = Instant.now();
        SelectedFieldCollector collector = new SelectedFieldCollector(dataFetchingEnvironment);
        ArangoGraphQLContext ctx = new ArangoGraphQLContext(dataFetchingEnvironment, collector);
        ArangoTraversalQuery query = queryGenerator.execute(ctx);
        Instant finish = Instant.now();
        long duration = Duration.between(start, finish).toMillis();
        logger.debug("Query Generation Completed in {}ms", duration);
        logger.trace("Generated Query: \n{}", query.getAql());
        return query;
    }

    private ArangoTraversalQueryResult executeQuery(ArangoTraversalQuery query){
        logger.debug("Begin Query Execution and Result Processing");
        Instant start = Instant.now();
        ArangoTraversalQueryResult result = queryExecutor.execute(query);
        Instant finish = Instant.now();
        long duration = Duration.between(start, finish).toMillis();
        logger.debug("Query Execution and Result Processing Completed in {}ms", duration);
        logger.debug("Query returned {} paths", result.size());
        return result;
    }

    private Object processResult(ArangoTraversalQueryResult result){
        logger.debug("Converting paths to GraphQL Query Response");
        Instant start = Instant.now();
        Object convertedResult = result.getResult();
        Instant finish = Instant.now();
        long duration = Duration.between(start, finish).toMillis();
        logger.debug("Response conversion completed in {}ms", duration);
        return convertedResult;
    }

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

        ArangoTraversalQuery query = createQuery(dataFetchingEnvironment);
        ArangoTraversalQueryResult result = executeQuery(query);
        return processResult(result);
    }

}
