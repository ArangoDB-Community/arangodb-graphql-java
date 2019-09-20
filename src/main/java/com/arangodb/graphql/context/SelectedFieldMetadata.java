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

import com.arangodb.graphql.schema.ArangoDiscriminatorDirective;
import com.arangodb.graphql.schema.ArangoEdgeDirective;
import com.arangodb.graphql.schema.ArangoTypeAliasDirective;
import graphql.schema.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class reads metadata from the GraphQL Schema of the provided selected field
 * @author Colin Findlay
 */
public class SelectedFieldMetadata {

    private SelectedField selectedField;

    /**
     * Constructor
     * @param selectedField The selected field to read metadata from
     */
    public SelectedFieldMetadata(SelectedField selectedField) {
        this.selectedField = selectedField;
    }

    /**
     * Create a filter group for this field if required
     * @return A Filter group for this field if it has arguments, otherwise null
     */
    public ArangoFilterGroup filterGroup() {

        List<ArangoFilter> filters = new ArrayList<>();

        if (selectedField.getArguments().size() > 0) {
            ArangoFilterGroup argumentGroup = new ArangoFilterGroup(selectedField, depth());
            filters.addAll(argumentGroup.getFilters());
        }

        ArangoFilter typeFilter = typeFilter();
        if(typeFilter != null){
            filters.add(typeFilter);
        }

        if(filters.size() > 0){
            return new ArangoFilterGroup(filters, depth());
        }

        return null;
    }

    /**
     *
     * @return The depth of this selected field in the query
     */
    public int depth() {
        return selectedField.getQualifiedName().split("\\/").length;
    }

    public ArangoEdgeDirective edgeDirective() {
        GraphQLFieldDefinition fieldDefinition = selectedField.getFieldDefinition();
        ArangoEdgeDirective arangoEdgeDirective = new ArangoEdgeDirective(fieldDefinition);
        boolean resolveViaEdge = !isScalar() && arangoEdgeDirective.getCollection() != null;
        if (resolveViaEdge) {
            return arangoEdgeDirective;
        }
        return null;
    }

    /**
     *
     * @return True if the field is a scalar
     */
    public boolean isScalar() {
        GraphQLFieldDefinition fieldDefinition = selectedField.getFieldDefinition();
        GraphQLType graphQLType = GraphQLTypeUtil.unwrapAll(fieldDefinition.getType());
        return GraphQLTypeUtil.isScalar(graphQLType);
    }

    private GraphQLFieldsContainer toFieldContainer(GraphQLOutputType type){
        if (type instanceof GraphQLFieldsContainer) {
            return (GraphQLFieldsContainer) type;
        }
        return null;
    }

    /**
     *
     * @return The object type of this field, or null if this field is not an object
     */
    public List<GraphQLFieldsContainer> fieldContainers() {
        GraphQLFieldDefinition fieldDefinition = selectedField.getFieldDefinition();
        GraphQLType graphQLType = GraphQLTypeUtil.unwrapAll(fieldDefinition.getType());
        if (graphQLType instanceof GraphQLFieldsContainer) {
            return Arrays.asList((GraphQLFieldsContainer) graphQLType);
        }

        if(graphQLType instanceof GraphQLUnionType){
            GraphQLUnionType unionType = (GraphQLUnionType) graphQLType;
            List<GraphQLOutputType> types = unionType.getTypes();
            return types.stream()
                    .map(this::toFieldContainer)
                    .collect(Collectors.toList());

        }

        return null;
    }

    private ArangoFilter typeFilter(){
        ArangoEdgeDirective edgeDirective = new ArangoEdgeDirective(selectedField.getFieldDefinition());
        GraphQLFieldDefinition fieldDefinition = selectedField.getFieldDefinition();
        GraphQLType graphQLType = GraphQLTypeUtil.unwrapAll(fieldDefinition.getType());
        ArangoTypeDiscriminationFilterFactory typeFilterFactory = new ArangoTypeDiscriminationFilterFactory();
        return typeFilterFactory.typeFilterFor(graphQLType, edgeDirective.getCollection());
    }
}
