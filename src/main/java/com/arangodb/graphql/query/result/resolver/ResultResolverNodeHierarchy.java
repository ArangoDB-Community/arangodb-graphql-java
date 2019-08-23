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
import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.query.result.ArangoResultContext;
import com.arangodb.graphql.query.result.PathEdge;
import com.arangodb.graphql.schema.ArangoDiscriminatorDirective;
import com.arangodb.graphql.schema.ArangoEdgeDirective;
import com.arangodb.model.TraversalOptions;
import graphql.schema.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a tree of ResultResolverNodes that are used to map the Arango path collections in the query response to a tree structure that mirrors the
 * incoming GraphQL Query
 *
 * @author Colin Findlay
 */
public class ResultResolverNodeHierarchy {

    private final Map<String, ResultResolverNode> children;

    private final ArangoResultContext context;

    private final PropertyMerger propertyMerger;

    /**
     * Default Constructor
     * @param context The result context from the query
     */
    public ResultResolverNodeHierarchy(ArangoResultContext context) {
        this.context = context;
        children = new LinkedHashMap<>();
        propertyMerger = new PropertyMerger();
    }

    /**
     * Children of this root node
     * @return A map of child properties
     */
    public Map<String, ResultResolverNode> getChildren() {
        return children;
    }

    /**
     * Insert a node in the correct position in this tree
     * @param node The node to insert
     */
    public void insert(ResultResolverNode node) {

        String qualifiedName = node.getField().getQualifiedName();
        String[] path = qualifiedName.split("\\/");

        if (path.length > 1) {

            ResultResolverNode contextNode = null;

            for (int i = 0; i < path.length - 1; i++) {
                if (i == 0) {
                    contextNode = children.get(path[i]);
                } else {
                    contextNode = contextNode.getChildren().get(path[i]);
                }
            }
            if (contextNode == null) {
                throw new NoNodeInContextException();
            }

            contextNode.addChild(node);

        } else {
            children.put(node.getField().getName(), node);
        }
    }


    private Object processNodeWithDocument(ResultResolverNode node, BaseDocument document) {
        return processNodeWithDocument(node, document, null);
    }

    private Map handleEdgeTarget(BaseDocument document, ResultResolverNode node) {
        Map target = new LinkedHashMap();
        Map<String, ResultResolverNode> children = node.getChildren();

        attachTypeDiscriminationMetadata(document, target, node);
        children.forEach((key, value) -> {
            target.put(key, processNodeWithDocument(value, document));
        });
        return target;
    }

    private ArangoEdgeDirective edgeDirective(ResultResolverNode node) {
        SelectedField field = node.getField();
        GraphQLFieldDefinition fieldDefinition = field.getFieldDefinition();
        return new ArangoEdgeDirective(fieldDefinition);
    }

    private Object traverse(ResultResolverNode node, ArangoEdgeDirective directive, BaseDocument document, Map<String, ResultResolverNode> children) {
        //Get list of base documents by traversing
        Set<PathEdge> edges = context.getEdges().edgesFor(directive.getCollection(), document.getId(), directive.getDirection());

        if(edges == null){
            return null;
        }

        List<ResultResolverTraversalDestination> destinations = edges.stream()
                .map(edge -> new ResultResolverTraversalDestination(context.getVertices().get(edge.getFrom()), context.getVertices().get(edge.getTo()), edge))
                .collect(Collectors.toList());

        List result = new ArrayList();
        for (ResultResolverTraversalDestination destination : destinations) {

            BaseDocument traversalTarget;

            if (destination.getEdge().getDirection() == TraversalOptions.Direction.outbound) {
                traversalTarget = destination.getTarget();
            } else {
                traversalTarget = destination.getSource();
            }

            if (traversalTarget != null) {
                Map target = new LinkedHashMap();
                children.forEach((key, value) -> {
                    target.put(key, processNodeWithDocument(value, traversalTarget, destination.getEdge()));
                });
                attachTypeDiscriminationMetadata(traversalTarget, target, node);
                result.add(target);
            }
        }

        //Make sure we send the results back as a list if the schema dictates we should
        GraphQLOutputType type = node.getField().getFieldDefinition().getType();
        boolean isList = GraphQLTypeUtil.isList(GraphQLTypeUtil.unwrapNonNull(type));
        if (isList) {
            return result;
        } else {
            if (result.size() > 1) {
                context.getGraphQLContext().getEnvironment().getExecutionContext().addError(new ResultSizeMismatch(node, document));
            }

            if (result.size() > 0) {
                return result.get(0);
            } else {
                return null;
            }

        }
    }

    private void attachRootTypeDiscriminationMetadata(BaseDocument source, Map target){

        String typeDiscriminatorProperty = rootTypeDiscriminator();
        Object typeDiscriminator = source.getProperties().get(typeDiscriminatorProperty);
        if(typeDiscriminatorProperty != null && typeDiscriminator != null) {
            target.put(typeDiscriminatorProperty, typeDiscriminator);
        }
        target.put("__collection", source.getId().split("\\/")[0]);

    }

    private void attachTypeDiscriminationMetadata(BaseDocument source, Map target, ResultResolverNode node){

        String typeDiscriminatorProperty = node.typeDiscriminator();
        Object typeDiscriminator = source.getProperties().get(typeDiscriminatorProperty);
        if(typeDiscriminatorProperty != null && typeDiscriminator != null) {
            target.put(typeDiscriminatorProperty, typeDiscriminator);
        }
        target.put("__collection", source.getId().split("\\/")[0]);

    }

    private Object processNodeWithDocument(ResultResolverNode node, BaseDocument document, BaseEdgeDocument edgeDocument) {

        Map<String, ResultResolverNode> children = node.getChildren();
        if (children == null) {
            return propertyMerger.merge(document, edgeDocument, node.getField().getName());
        } else {

            boolean edgeTarget = node.isEdgeTarget();
            if (edgeTarget) {
                return handleEdgeTarget(document, node);
            }

            ArangoEdgeDirective directive = edgeDirective(node);
            boolean shouldTraverse = directive.getCollection() != null;

            if (shouldTraverse) {
                return traverse(node, directive, document, children);
            } else {
                //No need for traversal - so just return the values in the document
                if(node.getParent() == null || node.getParent().isAutoMerge()) {
                    return propertyMerger.merge(document, edgeDocument, node.getField().getName());
                }
                else{
                    return propertyMerger.merge(edgeDocument, null, node.getField().getName());
                }
            }


        }


    }

    /**
     * Process the supplied vertex and convert it into a tree that matches the GraphQL Query
     * @param rootVertex The vertex to start from
     * @return A map of maps representing a tree that matches the GraphQL query
     */
    public Map process(BaseDocument rootVertex) {

        Map target = new LinkedHashMap();

        children.forEach((key, value) -> {
            target.put(key, processNodeWithDocument(value, rootVertex));
        });


        attachRootTypeDiscriminationMetadata(rootVertex, target);

        return target;

    }


    /**
     *
     * @return The property to use for type discrimination
     */
    public String rootTypeDiscriminator(){

        ArangoGraphQLContext graphQLContext = context.getGraphQLContext();
        if(graphQLContext != null){
            DataFetchingEnvironment environment = graphQLContext.getEnvironment();
            if(environment != null){
                GraphQLOutputType fieldType = environment.getFieldType();
                GraphQLType type = GraphQLTypeUtil.unwrapAll(fieldType);
                if(type instanceof GraphQLDirectiveContainer){
                    ArangoDiscriminatorDirective discriminatorDirective = new ArangoDiscriminatorDirective((GraphQLDirectiveContainer) type);
                    return discriminatorDirective.getProperty();
                }
            }
        }

        return null;

    }

}
