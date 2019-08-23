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

/**
 * Class that generates a string value to represent the value side of a Filter statement.
 *
 * This class wraps non-numeric/non-boolean values in double quotes, and leaves numeric/boolean values as is.
 *
 * i.e.
 *
 * FILTER doc.propertyA == true
 * FILTER doc.propertyB == 42
 * FILTER doc.propertyC == "Colin"
 *
 * @author Colin Findlay
 */
public class FilterValue {

    private final Object value;

    public FilterValue(Object value) {
        this.value = value;
    }

    /**
     *
     * @return A string representation of the filter value that can be appended to the query
     */
    public String get() {

        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        return "\"" + value.toString() + "\"";

    }
}
