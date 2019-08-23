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

package com.arangodb.graphql.query.result.resolver;

import com.arangodb.entity.BaseDocument;
import com.arangodb.graphql.query.result.ArangoResultContext;
import graphql.schema.SelectedField;

import java.util.Map;

/**
 * Class responsible for converting an Arango Query Result in the form of a collection of paths and resolving it back to the
 * structure requested by the GraphQL query
 *
 * @author Colin Findlay
 */
public class ResultResolver {

    /**
     * Converting an Arango Query Result in the form of a collection of paths and resolving it back to the
     * structure requested by the GraphQL query
     * @param context The Arango Result Context
     * @param rootVertex The root vertex to start from
     * @return A Map matching the structure of the incoming GraphQL Query
     */
    public Map resolve(ArangoResultContext context, BaseDocument rootVertex) {

        ResultResolverNodeHierarchy root = new ResultResolverNodeHierarchy(context);

        for (SelectedField field : context.getGraphQLContext().getSelectedFields()) {
            ResultResolverNode node = new ResultResolverNode(field);
            root.insert(node);
        }

        return root.process(rootVertex);

    }

}
