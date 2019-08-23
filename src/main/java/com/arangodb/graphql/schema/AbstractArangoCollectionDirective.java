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

import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.StringValue;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLDirectiveContainer;

/**
 * Abstract base class representing custom directives in the GraphQL schema used to provide metadata to help us generate AQL queries
 *
 * @author Colin Findlay
 */
public abstract class AbstractArangoCollectionDirective extends AbstractArangoDirective {

    /**
     *  Constructor
     * @param fieldDefinition The GraphQL field definition
     * @param directiveName The directive name
     */
    protected AbstractArangoCollectionDirective(GraphQLDirectiveContainer fieldDefinition, String directiveName) {
        super(fieldDefinition, directiveName, "collection");
    }

    /**
     * Constructor
     * @param directivesContainer The GraphQL field definition
     * @param directiveName The directive name
     */
    protected AbstractArangoCollectionDirective(DirectivesContainer directivesContainer, String directiveName) {
        super(directivesContainer, directiveName, "collection");
    }

    /**
     * @return The collection value from the directive
     */
    public String getCollection() {
        return getValue();
    }

}
