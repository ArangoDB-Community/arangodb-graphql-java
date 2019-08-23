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

package com.arangodb.graphql.context;

import com.arangodb.model.TraversalOptions;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents the traversal direction of an edge.
 *
 * @author Colin Findlay
 */
public class ArangoEdgeTraversalDirection {

    private Set<TraversalOptions.Direction> directions;

    public ArangoEdgeTraversalDirection() {
        directions = new HashSet<>();
    }

    /**
     *  Push a direction into a set of all directions for this edge
     *
     * @param direction The direction of this edge
     */
    public void push(TraversalOptions.Direction direction) {
        directions.add(direction);
    }

    /**
     * Compute the direction of traversals on this edge
     *
     * @return If the set contains both OUTBOUND and INBOUND directions the direction will be any, if it contains only
     * INBOUND the driection will be INBOUND, otherwise the direction will be OUTBOUND
     */
    public TraversalOptions.Direction direction() {

        if (isAny()) {
            return TraversalOptions.Direction.any;
        }

        if (isInbound()) {
            return TraversalOptions.Direction.inbound;
        }

        return TraversalOptions.Direction.outbound;

    }

    private boolean isAny() {
        return directions.contains(TraversalOptions.Direction.any) ||
                (directions.contains(TraversalOptions.Direction.inbound) &&
                        directions.contains(TraversalOptions.Direction.outbound));
    }

    private boolean isInbound() {
        return directions.contains(TraversalOptions.Direction.inbound);
    }
}
