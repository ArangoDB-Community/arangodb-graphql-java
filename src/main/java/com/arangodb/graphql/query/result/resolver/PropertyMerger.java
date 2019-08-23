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

package com.arangodb.graphql.query.result.resolver;

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;


/**
 * Class responsible for merging properties between a target document and an edge document.
 *
 * This implementation will automatically prefer a property in a target document over an edge document.
 *
 * For example given this target document
 *
 * {
 *     a: 1
 *     b: 2
 * }
 *
 * and this edge document
 *
 * {
 *     b: 3
 *     c: 4
 * }
 *
 * A merge of the two documents would produce
 *
 * {
 *     a: 1
 *     b: 2
 *     c: 4
 * }
 *
 * @author Colin Findlay
 *
 */
public class PropertyMerger {

    /**
     * Merge property
     * @param first Target Document
     * @param second Edge Document
     * @param name The property to merge
     * @return The merged value for the property - see class description for details
     */
    public Object merge(BaseDocument first, BaseDocument second, String name) {

        switch(name){
            case "_key":
                return first.getKey();
            case "_id":
                return first.getId();
            case "_revision":
                return first.getRevision();
            default:
                Object left = first != null ? first.getProperties().get(name) : null;
                Object right = second != null ? second.getProperties().get(name) : null;
                return left != null ? left : right;
        }


    }
}
