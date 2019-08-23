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

import com.arangodb.model.TraversalOptions;

/**
 * Class representing the string value of an Arango traversal direction for query generation
 */
public class DirectionValue {

    private final TraversalOptions.Direction direction;

    public DirectionValue(TraversalOptions.Direction direction) {
        this.direction = direction == null ? TraversalOptions.Direction.any : direction;
    }

    /**
     *
     * @return INBOUND, OUTBOUND or ANY
     */
    public String get() {
        return direction.toString().toUpperCase();
    }
}
