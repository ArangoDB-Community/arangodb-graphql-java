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
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class DirectionValueTest {

    @Test
    public void getInbound() {
        DirectionValue directionValue = new DirectionValue(TraversalOptions.Direction.inbound);
        assertThat(directionValue.get(), equalTo("INBOUND"));
    }


    @Test
    public void getOutbound() {
        DirectionValue directionValue = new DirectionValue(TraversalOptions.Direction.outbound);
        assertThat(directionValue.get(), equalTo("OUTBOUND"));
    }

    @Test
    public void getAny() {
        DirectionValue directionValue = new DirectionValue(TraversalOptions.Direction.any);
        assertThat(directionValue.get(), equalTo("ANY"));
    }
}