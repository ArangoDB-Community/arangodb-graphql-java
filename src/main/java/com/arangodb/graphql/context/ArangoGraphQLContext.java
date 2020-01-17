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

import com.arangodb.graphql.model.EdgeCollection;
import com.arangodb.graphql.schema.ArangoEdgeDirective;
import com.arangodb.graphql.schema.ArangoEdgeTargetDirective;
import com.arangodb.graphql.schema.ArangoVertexDirective;
import graphql.schema.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The class represents contextual information from the incoming GraphQL Query and the GraphQL schema
 * that will be used to create an ArangoDB AQL Query
 *
 * @author Colin Findlay
 */
public class ArangoGraphQLContext {

    private final List<SelectedField> selectedFields;
    private final Set<String> vertexCollections;

    private final Map<String, ArangoEdgeTraversalDirection> edgeCollections;

    private ArangoEdgeTraversalDirection rootTraversalDirection;
    private String rootCollection;

    private Map<String, ArangoEdgeDirective> edgeDirectives;

    private int max;

    private Map sort;

    private Integer limit;

    private Integer skip;

    private ArangoFilterGroup rootFilters;
    private List<ArangoFilterGroup> traversalFilters;

    private DataFetchingEnvironment environment;

    /**
     * Constructor
     * @param dataFetchingEnvironment The GraphQL Data Fetching Environment to create a context for
     */
    public ArangoGraphQLContext(DataFetchingEnvironment dataFetchingEnvironment, SelectedFieldCollector selectedFieldCollector) {

        this.environment = dataFetchingEnvironment;
        vertexCollections = new LinkedHashSet<>();
        edgeCollections = new LinkedHashMap<>();
        edgeDirectives = new HashMap<>();
        rootTraversalDirection = new ArangoEdgeTraversalDirection();

        setupLimit(dataFetchingEnvironment);
        setupSort(dataFetchingEnvironment);

        setupRootCollection(dataFetchingEnvironment);
        Map<String, Object> filterArguments = filterArguments(dataFetchingEnvironment);
        rootFilters = new ArangoRootFilterGroup(filterArguments, dataFetchingEnvironment.getFieldType());
        traversalFilters = new ArrayList<>();

        selectedFields = selectedFieldCollector.getFields();
        selectedFields.forEach(x -> processField(x));

    }

    private Map<String, Object> filterArguments(DataFetchingEnvironment dataFetchingEnvironment){
        Map<String, Object> arguments = dataFetchingEnvironment.getArguments();
        arguments.remove("sort");
        arguments.remove("limit");
        arguments.remove("skip");
        return arguments;
    }

    private void setupSort(DataFetchingEnvironment dataFetchingEnvironment){
        Object sort = dataFetchingEnvironment.getArgument("sort");
        if(sort instanceof Map){
            this.sort = (Map) sort;
        }
    }

    private void setupLimit(DataFetchingEnvironment dataFetchingEnvironment){
        Object limit = dataFetchingEnvironment.getArgument("limit");
        if(limit instanceof Integer){
            this.limit = (Integer) limit;
            Object skip = dataFetchingEnvironment.getArgument("skip");
            if(skip instanceof Integer){
                this.skip = (Integer) skip;
            }
        }
    }

    private void setupRootCollection(DataFetchingEnvironment dataFetchingEnvironment) {
        GraphQLType rootType = GraphQLTypeUtil.unwrapAll(dataFetchingEnvironment.getFieldType());
        ArangoVertexDirective arangoVertexDirective = new ArangoVertexDirective((GraphQLDirectiveContainer) rootType);
        rootCollection = arangoVertexDirective.getCollection();
    }

    private void processField(SelectedField field) {
        SelectedFieldMetadata metadata = new SelectedFieldMetadata(field);
        updateDepth(metadata.depth());
        addTraversalFilterGroup(metadata.filterGroup());
        addEdgeDirective(field, metadata.edgeDirective());
        addVertexCollection(metadata);
    }

    private void updateDepth(int depth) {
        if (depth > max) {
            max = depth;
        }
    }

    private void addTraversalFilterGroup(ArangoFilterGroup filterGroup) {
        if (filterGroup != null) {
            traversalFilters.add(filterGroup);
        }
    }

    private void addEdgeDirective(SelectedField field, ArangoEdgeDirective arangoEdgeDirective) {
        if (arangoEdgeDirective != null) {
            edgeDirectives.put(field.getQualifiedName(), arangoEdgeDirective);

            ArangoEdgeTraversalDirection edgeTraversalDirection = edgeCollections.get(arangoEdgeDirective.getCollection());
            if (edgeTraversalDirection == null) {
                edgeTraversalDirection = new ArangoEdgeTraversalDirection();
            }
            edgeTraversalDirection.push(arangoEdgeDirective.getDirection());
            edgeCollections.put(arangoEdgeDirective.getCollection(), edgeTraversalDirection);

            if (isRootLevel(field)) {
                rootTraversalDirection.push(arangoEdgeDirective.getDirection());
            }
        }
    }

    private void addVertexCollection(SelectedFieldMetadata metadata) {

        if (metadata.isScalar()) {
            return;
        }

        List<GraphQLFieldsContainer> containers = metadata.fieldContainers();
        if (containers == null) {
            return;
        }

        for(GraphQLFieldsContainer container : containers) {
            addVertexCollection(container);
            container.getFieldDefinitions().stream()
                    .filter(fieldDefinition -> (new ArangoEdgeTargetDirective(fieldDefinition)).isEdgeTarget())
                    .findFirst()
                    .ifPresent(fieldDefinition -> addVertexCollection(fieldDefinition.getType()));
        }

    }

    private boolean isRootLevel(SelectedField field) {
        return !field.getQualifiedName().contains("/");
    }

    private void addVertexCollection(GraphQLType type) {
        if (type instanceof GraphQLObjectType) {
            GraphQLObjectType objectType = (GraphQLObjectType) type;

            ArangoVertexDirective arangoVertexDirective = new ArangoVertexDirective(objectType);
            if (arangoVertexDirective.getCollection() != null) {
                vertexCollections.add(arangoVertexDirective.getCollection());
            }
        }

        if(type instanceof GraphQLInterfaceType){
            List<GraphQLObjectType> implementations = environment.getGraphQLSchema().getImplementations((GraphQLInterfaceType) type);
            implementations.forEach(impl -> addVertexCollection(impl));
        }
    }

    /**
     *
     * @return The GraphQL Data Fetching Environment
     */
    public DataFetchingEnvironment getEnvironment() {
        return environment;
    }

    /**
     *
     * @return A group of filters to be applied to the root collection
     */
    public ArangoFilterGroup getRootFilters() {
        return rootFilters;
    }

    /**
     *
     * @return A list of filter groups to be applied to traversals
     */
    public List<ArangoFilterGroup> getTraversalFilters() {
        return traversalFilters;
    }

    /**
     *
     * @return The fields selected in the query
     */
    public List<SelectedField> getSelectedFields() {
        return selectedFields;
    }


    /**
     *
     * @return The root collection. This is the collection where the traversal will start from
     */
    public String getRootCollection() {
        return rootCollection;
    }

    /**
     *
     * @return The minimum traversal depth
     */
    public int min() {
        return 0;
    }

    /**
     *
     * @return The maximum traversal depth
     */
    public int max() {
        return max;
    }

    /**
     *
     * @return The direction of travel from edges starting from vertices in the root collection
     */
    public ArangoEdgeTraversalDirection getRootTraversalDirection() {
        return rootTraversalDirection;
    }

    /**
     *
     * @return A map of Edge Directives in the schema keyed by selected field qualified name
     */
    public Map<String, ArangoEdgeDirective> getEdgeDirectives() {
        return edgeDirectives;
    }

    /**
     *
     * @return True if the GraphQL query requires a traversal
     */
    public boolean hasTraversal() {
        return vertexCollections.size() > 0 && edgeCollections.size() > 0;
    }

    /**
     *
     * @return A set of Vertex collections required to satisfy the GraphQL Query
     */
    public Set<String> getVertexCollections() {
        return new LinkedHashSet<>(vertexCollections);
    }

    /**
     *
     * @return A list of Edge collections required to satisfy the GraphQL Query
     */
    public List<EdgeCollection> getEdgeCollections() {
        return edgeCollections.entrySet().stream()
                .map(x -> new EdgeCollection(x.getKey(), x.getValue().direction())).collect(Collectors.toList());
    }

    /**
     * Checks for an Edge Directive for the field with the specified qualified name.
     * @param qualifiedName The qualified name of the selected field we want to interrogate for an edge directive
     * @return The edge directive for the specified qualified name or null in none exists
     */
    public ArangoEdgeDirective edgeDirectiveFor(String qualifiedName) {
        return edgeDirectives.get(qualifiedName);
    }

    /**
     *
     * @return A sorted Map of top level attributes and their sort directions
     */
    public Map getSort() {
        return sort;
    }

    /**
     *
     * @return The maximum number of top level results
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     *
     * @return The number of top level records to skip (used for pagination)
     */
    public Integer getSkip() {
        return skip;
    }
}
