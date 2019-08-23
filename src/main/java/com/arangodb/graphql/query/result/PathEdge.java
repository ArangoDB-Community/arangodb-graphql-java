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

package com.arangodb.graphql.query.result;

import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.model.TraversalOptions;

import java.util.Map;
import java.util.Objects;

public class PathEdge extends BaseEdgeDocument {

    private final TraversalOptions.Direction direction;

    private final BaseEdgeDocument baseEdgeDocument;

    public PathEdge(BaseEdgeDocument baseEdgeDocument, TraversalOptions.Direction direction) {
        this.baseEdgeDocument = baseEdgeDocument;
        this.direction = direction;
    }

    public TraversalOptions.Direction getDirection() {
        return direction;
    }

    @Override
    public String getFrom() {
        return baseEdgeDocument.getFrom();
    }

    @Override
    public void setFrom(String from) {
        baseEdgeDocument.setFrom(from);
    }

    @Override
    public String getTo() {
        return baseEdgeDocument.getTo();
    }

    @Override
    public void setTo(String to) {
        baseEdgeDocument.setTo(to);
    }

    @Override
    public String toString() {
        return baseEdgeDocument.toString();
    }

    @Override
    public int hashCode() {
        return baseEdgeDocument == null? 0 : baseEdgeDocument.getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PathEdge pathEdge = (PathEdge) o;
        return Objects.equals(baseEdgeDocument.getId(), pathEdge.baseEdgeDocument.getId());
    }

    @Override
    public String getId() {
        return baseEdgeDocument.getId();
    }

    @Override
    public void setId(String id) {
        baseEdgeDocument.setId(id);
    }

    @Override
    public String getKey() {
        return baseEdgeDocument.getKey();
    }

    @Override
    public void setKey(String key) {
        baseEdgeDocument.setKey(key);
    }

    @Override
    public String getRevision() {
        return baseEdgeDocument.getRevision();
    }

    @Override
    public void setRevision(String revision) {
        baseEdgeDocument.setRevision(revision);
    }

    @Override
    public Map<String, Object> getProperties() {
        return baseEdgeDocument.getProperties();
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        baseEdgeDocument.setProperties(properties);
    }

    @Override
    public void addAttribute(String key, Object value) {
        baseEdgeDocument.addAttribute(key, value);
    }

    @Override
    public void updateAttribute(String key, Object value) {
        baseEdgeDocument.updateAttribute(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return baseEdgeDocument.getAttribute(key);
    }
}
