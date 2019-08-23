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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class FilterValueTest {

    @Test
    public void getString() {
        FilterValue filterValue = new FilterValue("Felix Potvin");
        assertThat(filterValue.get(), equalTo("\"Felix Potvin\""));
    }

    @Test
    public void getNumber() {
        FilterValue filterValue = new FilterValue(29);
        assertThat(filterValue.get(), equalTo("29"));
    }

    @Test
    public void getObject() {

        final String toString = "#29 Felix Potvin";
        Object myObj = new Object(){
            @Override
            public String toString() {
                return toString;
            }
        };

        FilterValue filterValue = new FilterValue(myObj);
        assertThat(filterValue.get(), equalTo("\"" + toString + "\""));
    }
}