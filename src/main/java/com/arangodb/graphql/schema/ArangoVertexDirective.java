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

package com.arangodb.graphql.schema;

import graphql.language.DirectivesContainer;
import graphql.schema.GraphQLDirectiveContainer;
import graphql.schema.GraphQLObjectType;

/**
 * Implementation of the Vertex directive used to indicate when an object in the schema is resolved via a document in a vertex collection
 *
 * @author Colin Findlay
 */
public class ArangoVertexDirective extends AbstractArangoCollectionDirective {

    /**
     * Default Constructor
     * @param type The type that this directive is applied to
     */
    public ArangoVertexDirective(GraphQLDirectiveContainer type) {
        super(type, "vertex");
    }

    /**
     * Default Constructor
     * @param directivesContainer The directives container that this directive is contained in
     *
     */
    public ArangoVertexDirective(DirectivesContainer directivesContainer) {
        super(directivesContainer, "vertex");
    }
}
