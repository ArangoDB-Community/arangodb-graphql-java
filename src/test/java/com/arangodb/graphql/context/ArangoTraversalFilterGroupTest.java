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

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ArangoTraversalFilterGroupTest {
    
    @Test
    public void testDepth(){
        Map<String, Object> args = new LinkedHashMap<>();
        ArangoFilterGroup arangoRootFilterGroup = new ArangoFilterGroup(args, 2);
        assertThat(arangoRootFilterGroup.getDepth(), equalTo(2));
    }


    @Test
    public void testArgs(){
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("jersey", 29);
        ArangoFilterGroup arangoRootFilterGroup = new ArangoFilterGroup(args, 2);
        assertThat(arangoRootFilterGroup.getFilters().get(0).getKey(), equalTo("jersey"));
        assertThat(arangoRootFilterGroup.getFilters().get(0).getValues().get(0), equalTo(29));
    }

}