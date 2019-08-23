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

import com.arangodb.model.TraversalOptions;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;

/**
 * Implementation of the Edge directive used to indicate when a traversal via edge collection is required when
 * resolving a field in the GraphQL schema
 *
 * @author Colin Findlay
 */
public class ArangoEdgeDirective extends AbstractArangoCollectionDirective {

    private TraversalOptions.Direction direction;

    /**
     * Default Constructor
     * @param graphQLFieldDefinition GraphQL Field Definition
     */
    public ArangoEdgeDirective(GraphQLFieldDefinition graphQLFieldDefinition) {
        super(graphQLFieldDefinition, "edge");
        GraphQLDirective directive = graphQLFieldDefinition.getDirective("edge");
        if (directive != null) {

            GraphQLArgument directionArg = directive.getArgument("direction");
            if (directionArg != null) {
                Object value = directionArg.getValue();
                if (value != null) {
                    this.direction = TraversalOptions.Direction.valueOf(value.toString());
                }
            }
        }
    }

    /**
     *
     * @return The direction value from the directive
     */
    public TraversalOptions.Direction getDirection() {
        return direction;
    }

}
