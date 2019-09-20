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

import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.query.BaseDocumentPathEntity;
import com.arangodb.graphql.query.result.resolver.ResultResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLTypeUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class representing the result of a GraphQL query executed on Arango
 *
 * @author Colin Findlay
 */
public class ArangoTraversalQueryResult {

    private ArangoGraphQLContext ctx;

    private List<BaseDocumentPathEntity> paths;

    private List<Map> result;

    /**
     * Default Constructor
     * @param ctx The context used to execute the query
     * @param paths The paths returned from Arango
     */
    public ArangoTraversalQueryResult(ArangoGraphQLContext ctx, List<BaseDocumentPathEntity> paths) {
        this.ctx = ctx;
        this.paths = paths;
        init();
    }

    private void init() {

        ArangoResultContext arangoResultContext = new ArangoResultContext(ctx, paths);

        ResultResolver resultResolver = new ResultResolver();

        result = arangoResultContext.getVertices().rootVertices().stream()
                .map(x -> resultResolver.resolve(arangoResultContext, x))
                .collect(Collectors.toList());
    }

    public int size(){
        return paths.size();
    }

    /**
     *
     * @return The result in the structure requested by the GraphQL Query
     */
    public Object getResult() {
        DataFetchingEnvironment environment = ctx.getEnvironment();
        boolean isList = GraphQLTypeUtil.isList(environment.getFieldType());
        if(isList){
            return result;
        }
        else{
            if(result.size() == 0){
                return null;
            }
            return result.get(0);
        }
    }

}
