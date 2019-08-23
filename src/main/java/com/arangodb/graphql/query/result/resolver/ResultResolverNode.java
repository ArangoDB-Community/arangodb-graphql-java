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

import com.arangodb.graphql.schema.ArangoDiscriminatorDirective;
import com.arangodb.graphql.schema.ArangoEdgeTargetDirective;
import graphql.language.DirectivesContainer;
import graphql.schema.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A result resolver node is a node in a ResultResolverNodeHierarchy used to map a selected field into a tree structure that matches the incoming GraphQL Query
 *
 * @author Colin Findlay
 */
public class ResultResolverNode {

    private final SelectedField field;

    private final Map<String, ResultResolverNode> children;

    private ResultResolverNode parent;

    /**
     * Default Constructor
     * @param field The selected field in the GraphQL Query
     */
    public ResultResolverNode(SelectedField field) {
        this.field = field;
        children = new LinkedHashMap<>();
    }

    /**
     * Children of this node
     * @return A map of child properties
     */
    public Map<String, ResultResolverNode> getChildren() {
        return children;
    }

    /**
     * Add a child node to this node
     * @param node The node to add
     */
    public void addChild(ResultResolverNode node) {
        children.put(node.getField().getName(), node);
        node.setParent(this);
    }

    /**
     *
     * @return The selected field from the incoming GraphQL Query
     */
    public SelectedField getField() {
        return field;
    }

    public ResultResolverNode getParent() {
        return parent;
    }

    public void setParent(ResultResolverNode parent) {
        this.parent = parent;
    }

    /**
     *
     * @return True if this field is an edge target
     */
    public boolean isEdgeTarget() {
        ArangoEdgeTargetDirective edgeTargetDirective = new ArangoEdgeTargetDirective(getField().getFieldDefinition());
        return edgeTargetDirective.isEdgeTarget();
    }

    /**
     * If the node should auto merge property values between edges and targets
     * @return
     */
    public boolean isAutoMerge(){
        boolean childEdgeTargets = children.values().stream().anyMatch(x -> x.isEdgeTarget());
        return !childEdgeTargets;
    }

    /**
     *
     * @return The property to use for type discrimination
     */
    public String typeDiscriminator(){
        GraphQLType type = GraphQLTypeUtil.unwrapAll(getField().getFieldDefinition().getType());
        if(type instanceof GraphQLDirectiveContainer){
            ArangoDiscriminatorDirective discriminatorDirective = new ArangoDiscriminatorDirective((GraphQLDirectiveContainer) type);
            return discriminatorDirective.getProperty();
        }
        return null;

    }


}
