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

package com.arangodb.graphql.generator;

import com.arangodb.graphql.context.ArangoGraphQLContext;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * AQL Generator that will generate a SORT statement for the root query
 *
 * @author Colin Findlay
 */
public class SortGenerator implements ArangoQueryGenerator {

    @Override
    public String generateAql(ArangoGraphQLContext ctx) {
        Map<String, String> sort = ctx.getSort();

        String sortEntries = sort.entrySet()
                .stream()
                .map(e -> sortEntry(e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));

        String aql = "SORT " + sortEntries;
        return aql;
    }

    private String sortEntry(String key, String value){
        if("DESC".equals(value)) {
            return "rootVertex." + key + " DESC";
        }
        else{
            return "rootVertex." + key;
        }
    }

    @Override
    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
        return ctx.getSort() != null && ctx.getSort().size() > 0;
    }
}
