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

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class DefaultArangoQueryGeneratorChainTest {

    @Test
    public void testDefaultChainOrder(){
        DefaultArangoQueryGeneratorChain chain = new DefaultArangoQueryGeneratorChain();
        List<ArangoQueryGenerator> chainItems = chain.getChain();

        assertThat(chainItems.get(0), instanceOf(WithGenerator.class));
        assertThat(chainItems.get(1), instanceOf(RootForGenerator.class));
        assertThat(chainItems.get(2), instanceOf(RootFilterGenerator.class));
        assertThat(chainItems.get(3), instanceOf(TraversalForGenerator.class));
        assertThat(chainItems.get(4), instanceOf(EdgeCollectionGenerator.class));
        assertThat(chainItems.get(5), instanceOf(TraversalFilterGenerator.class));
        assertThat(chainItems.get(6), instanceOf(ReturnStatementGenerator.class));

    }

}