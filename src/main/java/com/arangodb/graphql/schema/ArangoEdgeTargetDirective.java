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

import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLDirectiveContainer;

/**
 * Implementation of the EdgeTarget directive used to indicate when a traversal via edge collection is required when
 * resolving a field in the GraphQL schema and that the result of that traversal should be mapped to a specific property
 *
 * @author Colin Findlay
 */
public class ArangoEdgeTargetDirective {

    private boolean edgeTarget;

    /**
     * Default Constructor
     * @param fieldDefinition GraphQL Field Definition
     */
    public ArangoEdgeTargetDirective(GraphQLDirectiveContainer fieldDefinition) {
        GraphQLDirective directive = fieldDefinition.getDirective("edgeTarget");
        edgeTarget = directive != null;
    }

    /**
     *
     * @return True if this field has an edgeTarget directive on it
     */
    public boolean isEdgeTarget() {
        return edgeTarget;
    }
}
