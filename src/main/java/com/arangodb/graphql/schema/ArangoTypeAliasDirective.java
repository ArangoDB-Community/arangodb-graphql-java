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
 * Class representing custom directives in the GraphQL schema used to provide type aliases to the result
 * type resolvers
 *
 * @author Colin Findlay
 */
public class ArangoTypeAliasDirective extends AbstractArangoDirective {

    public ArangoTypeAliasDirective(GraphQLDirectiveContainer fieldDefinition) {
        super(fieldDefinition, "alias", "name");
    }

    /**
     * Default Constructor
     * @param directivesContainer The GraphQL directives container
     */
    public ArangoTypeAliasDirective(DirectivesContainer directivesContainer) {
        super(directivesContainer, "alias", "name");
    }

    /**
     * @return The name value from the directive
     */
    public String getName() {
        return getValue();
    }

}
