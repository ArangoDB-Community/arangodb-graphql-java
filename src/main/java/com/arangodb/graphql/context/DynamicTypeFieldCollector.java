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


import graphql.Internal;
import graphql.execution.ConditionalNodes;
import graphql.execution.FieldCollectorParameters;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLUnionType;
import graphql.schema.SchemaUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static graphql.execution.TypeFromAST.getTypeFromAST;


/**
 * A field collector can iterate over field selection sets and build out the sub fields that have been selected,
 * expanding named and inline fragments as it goes.
 *
 * This is required to replace the GraphQL field collector because we need to collect fields before their type is known
 *
 */
public class DynamicTypeFieldCollector {

        private final ConditionalNodes conditionalNodes;

        private final SchemaUtil schemaUtil = new SchemaUtil();

        public DynamicTypeFieldCollector() {
            conditionalNodes = new ConditionalNodes();
        }


        /**
         * Given a list of fields this will collect the sub-field selections and return it as a map
         *
         * @param parameters the parameters to this method
         * @param fields     the list of fields to collect for
         *
         * @return a map of the sub field selections
         */
        public Map<String, List<CollectedField>> collectFields(FieldCollectorParameters parameters, List<Field> fields) {
            Map<String, List<CollectedField>> subFields = new LinkedHashMap<>();
            List<String> visitedFragments = new ArrayList<>();
            for (Field field : fields) {
                if (field.getSelectionSet() == null) {
                    continue;
                }
                this.collectFields(parameters, field.getSelectionSet(), visitedFragments, subFields, parameters.getObjectType());
            }
            return subFields;
        }

        private void collectFields(FieldCollectorParameters parameters, SelectionSet selectionSet, List<String> visitedFragments, Map<String, List<CollectedField>> fields, GraphQLType type) {

            for (Selection selection : selectionSet.getSelections()) {
                if (selection instanceof Field) {
                    collectField(parameters, fields, (Field) selection, type);
                } else if (selection instanceof InlineFragment) {
                    collectInlineFragment(parameters, visitedFragments, fields, (InlineFragment) selection);
                } else if (selection instanceof FragmentSpread) {
                    collectFragmentSpread(parameters, visitedFragments, fields, (FragmentSpread) selection);
                }
            }
        }

        private void collectFragmentSpread(FieldCollectorParameters parameters, List<String> visitedFragments, Map<String, List<CollectedField>> fields, FragmentSpread fragmentSpread) {
            if (visitedFragments.contains(fragmentSpread.getName())) {
                return;
            }
            if (!conditionalNodes.shouldInclude(parameters.getVariables(), fragmentSpread.getDirectives())) {
                return;
            }
            visitedFragments.add(fragmentSpread.getName());
            FragmentDefinition fragmentDefinition = parameters.getFragmentsByName().get(fragmentSpread.getName());

            if (!conditionalNodes.shouldInclude(parameters.getVariables(), fragmentDefinition.getDirectives())) {
                return;
            }
            if (!doesFragmentConditionMatch(parameters, fragmentDefinition)) {
                return;
            }

            GraphQLType conditionType = getTypeFromAST(parameters.getGraphQLSchema(), fragmentDefinition.getTypeCondition());
            collectFields(parameters, fragmentDefinition.getSelectionSet(), visitedFragments, fields, conditionType);
        }

        private void collectInlineFragment(FieldCollectorParameters parameters, List<String> visitedFragments, Map<String, List<CollectedField>> fields, InlineFragment inlineFragment) {
            if (!conditionalNodes.shouldInclude(parameters.getVariables(), inlineFragment.getDirectives()) ||
                    !doesFragmentConditionMatch(parameters, inlineFragment)) {
                return;
            }

            GraphQLType conditionType = getTypeFromAST(parameters.getGraphQLSchema(), inlineFragment.getTypeCondition());
            collectFields(parameters, inlineFragment.getSelectionSet(), visitedFragments, fields, conditionType);
        }

        private void collectField(FieldCollectorParameters parameters, Map<String, List<CollectedField>> fields, Field field, GraphQLType type) {
            if (!conditionalNodes.shouldInclude(parameters.getVariables(), field.getDirectives())) {
                return;
            }
            String name = getFieldEntryKey(field);
            fields.putIfAbsent(name, new ArrayList<>());
            fields.get(name).add(new CollectedField(field, type));
        }

        private String getFieldEntryKey(Field field) {
            if (field.getAlias() != null) return field.getAlias();
            else return field.getName();
        }


        private boolean doesFragmentConditionMatch(FieldCollectorParameters parameters, InlineFragment inlineFragment) {
            return true;
        }

        private boolean doesFragmentConditionMatch(FieldCollectorParameters parameters, FragmentDefinition fragmentDefinition) {
            return true;
        }


}
