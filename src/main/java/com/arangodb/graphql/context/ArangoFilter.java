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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class represents a filter, filtering a specific key for one of the supplied values
 *
 * @author Colin Findlay
 */
public class ArangoFilter {

    private final String key;

    private final List values;

    public ArangoFilter(String key, Object value) {
        this.key = key;
        if (value instanceof Collection) {
            this.values = new ArrayList((Collection) value);
        } else {
            this.values = Arrays.asList(value);
        }
    }

    public String getKey() {
        return key;
    }

    public List getValues() {
        return values;
    }

}
