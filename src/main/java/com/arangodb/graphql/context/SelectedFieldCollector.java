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


import graphql.execution.FieldCollectorParameters;
import graphql.execution.ValuesResolver;
import graphql.introspection.Introspection;
import graphql.language.*;
import graphql.schema.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class SelectedFieldCollector {

    private final static String SEP = "/";

    private List<Field> parentFields;
    private GraphQLSchema graphQLSchema;
    private GraphQLFieldsContainer parentFieldType;
    private Map<String, Object> variables;
    private Map<String, FragmentDefinition> fragmentsByName;


    private Map<String, List<Field>> selectionSetFields;
    private Map<String, GraphQLFieldDefinition> selectionSetFieldDefinitions;
    private Map<String, Map<String, Object>> selectionSetFieldArgs;
    private Set<String> flattenedFields;


    public SelectedFieldCollector(DataFetchingEnvironment environment) {
        this.graphQLSchema = environment.getGraphQLSchema();
        this.parentFields = environment.getFields();
        this.parentFieldType = (GraphQLFieldsContainer) GraphQLTypeUtil.unwrapAll(environment.getFieldType());
        this.variables = environment.getExecutionContext().getVariables();
        this.fragmentsByName = environment.getFragmentsByName();
    }

    private void computeValuesLazily() {
        synchronized (this) {
            if (selectionSetFields != null) {
                return;
            }

            selectionSetFields = new LinkedHashMap<>();
            selectionSetFieldDefinitions = new LinkedHashMap<>();
            selectionSetFieldArgs = new LinkedHashMap<>();
            flattenedFields = new LinkedHashSet<>();

            List<CollectedField> fields = parentFields.stream().map(x -> new CollectedField(x, parentFieldType)).collect(Collectors.toList());
            traverseFields(fields, parentFieldType, "");
        }
    }



    private void traverseFields(List<CollectedField> fieldList, GraphQLFieldsContainer parentFieldType, String fieldPrefix) {
        DynamicTypeFieldCollector fieldCollector = new DynamicTypeFieldCollector();
        ValuesResolver valuesResolver = new ValuesResolver();

        FieldCollectorParameters parameters = FieldCollectorParameters.newParameters()
                .schema(graphQLSchema)
                .objectType(asObjectTypeOrNull(parentFieldType))
                .fragments(fragmentsByName)
                .variables(variables)
                .build();


        Map<String, List<CollectedField>> collectedFields = fieldCollector.collectFields(parameters, fieldList.stream().map(x -> x.getField()).collect(Collectors.toList()));
        for (Map.Entry<String, List<CollectedField>> entry : collectedFields.entrySet()) {
            String fieldName = mkFieldName(fieldPrefix, entry.getKey());
            List<CollectedField> collectedFieldList = entry.getValue();
            selectionSetFields.put(fieldName, collectedFieldList.stream().map(x -> x.getField()).collect(Collectors.toList()));

            Field field = collectedFieldList.get(0).getField();
            GraphQLType typeOverride = collectedFieldList.get(0).getType();
            GraphQLType introspectionType = typeOverride == null ? parentFieldType : typeOverride;
            GraphQLFieldDefinition fieldDef = Introspection.getFieldDef(graphQLSchema, (GraphQLCompositeType) introspectionType, field.getName());
            GraphQLType unwrappedType = GraphQLTypeUtil.unwrapAll(fieldDef.getType());
            Map<String, Object> argumentValues = valuesResolver.getArgumentValues(fieldDef.getArguments(), field.getArguments(), variables);

            selectionSetFieldArgs.put(fieldName, argumentValues);
            selectionSetFieldDefinitions.put(fieldName, fieldDef);
            flattenedFields.add(fieldName);

            if (unwrappedType instanceof GraphQLFieldsContainer) {
                traverseFields(collectedFieldList, (GraphQLFieldsContainer) unwrappedType, fieldName);
            }
        }
    }


    private String mkFieldName(String fieldPrefix, String fieldName) {
        return (!fieldPrefix.isEmpty() ? fieldPrefix + SEP : "") + fieldName;
    }

    private static GraphQLObjectType asObjectTypeOrNull(GraphQLType unwrappedType) {
        return unwrappedType instanceof GraphQLObjectType ? (GraphQLObjectType) unwrappedType : null;
    }

    public SelectedField getField(String fqFieldName) {
        computeValuesLazily();

        List<Field> fields = selectionSetFields.get(fqFieldName);
        if (fields == null) {
            return null;
        }
        GraphQLFieldDefinition fieldDefinition = selectionSetFieldDefinitions.get(fqFieldName);
        Map<String, Object> arguments = selectionSetFieldArgs.get(fqFieldName);
        arguments = arguments == null ? emptyMap() : arguments;
        return new SelectedFieldImpl(fqFieldName, fields, fieldDefinition, arguments);
    }


    public List<SelectedField> getFields() {
        computeValuesLazily();

        return flattenedFields.stream()
                .map(this::getField)
                .collect(Collectors.toList());
    }


    private class SelectedFieldImpl implements SelectedField {
        private final String qualifiedName;
        private final String name;
        private final GraphQLFieldDefinition fieldDefinition;
        private final DataFetchingFieldSelectionSet selectionSet;
        private final Map<String, Object> arguments;

        private SelectedFieldImpl(String qualifiedName, List<Field> parentFields, GraphQLFieldDefinition fieldDefinition, Map<String, Object> arguments) {
            this.qualifiedName = qualifiedName;
            this.name = parentFields.get(0).getName();
            this.fieldDefinition = fieldDefinition;
            this.arguments = arguments;
            GraphQLType unwrappedType = GraphQLTypeUtil.unwrapAll(fieldDefinition.getType());
            if (unwrappedType instanceof GraphQLFieldsContainer) {
                this.selectionSet = new DataFetchingFieldSelectionSetImpl(parentFields, (GraphQLFieldsContainer) unwrappedType, graphQLSchema, variables, fragmentsByName);
            } else {
                this.selectionSet = NOOP;
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getQualifiedName() {
            return qualifiedName;
        }

        @Override
        public GraphQLFieldDefinition getFieldDefinition() {
            return fieldDefinition;
        }

        @Override
        public Map<String, Object> getArguments() {
            return arguments;
        }

        @Override
        public DataFetchingFieldSelectionSet getSelectionSet() {
            return selectionSet;
        }
    }

    private final static DataFetchingFieldSelectionSet NOOP = new DataFetchingFieldSelectionSet() {
        @Override
        public Map<String, List<Field>> get() {
            return emptyMap();
        }

        @Override
        public Map<String, Map<String, Object>> getArguments() {
            return emptyMap();
        }

        @Override
        public Map<String, GraphQLFieldDefinition> getDefinitions() {
            return emptyMap();
        }

        @Override
        public boolean contains(String fieldGlobPattern) {
            return false;
        }

        @Override
        public SelectedField getField(String fieldName) {
            return null;
        }

        @Override
        public List<SelectedField> getFields() {
            return emptyList();
        }

        @Override
        public List<SelectedField> getFields(String fieldGlobPattern) {
            return emptyList();
        }
    };

}
