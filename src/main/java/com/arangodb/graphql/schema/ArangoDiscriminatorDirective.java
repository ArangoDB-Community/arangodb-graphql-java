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
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLDirectiveContainer;

/**
 * Class representing custom directives in the GraphQL schema used to provide type discriminators to the result
 * type resolvers
 *
 * @author Colin Findlay
 */
public class ArangoDiscriminatorDirective extends AbstractArangoDirective {

    /**
     * Default Constructor
     * @param fieldDefinition The GraphQL field definition
     */
    public ArangoDiscriminatorDirective(GraphQLDirectiveContainer fieldDefinition) {
        super(fieldDefinition, "discriminator", "property");
    }

    public ArangoDiscriminatorDirective(DirectivesContainer directivesContainer) {
        super(directivesContainer, "discriminator", "property");
    }

    /**
     * @return The property value from the directive
     */
    public String getProperty() {
        return getValue();
    }

}
