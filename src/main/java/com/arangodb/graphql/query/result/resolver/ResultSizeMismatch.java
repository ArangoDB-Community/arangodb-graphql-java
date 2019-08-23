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
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing a GraphQL Error when the data returned from a traversal query indicates that the property in the GraphQL schema should be an array
 * but it only supports a single value.
 *
 * @author Colin Findlay
 */
public class ResultSizeMismatch implements GraphQLError {

    private ResultResolverNode node;

    private BaseDocument document;

    /**
     * Default Constructor
     * @param node The Result Resolver Node that this error occurred on
     * @param document The document that caused the error
     */
    public ResultSizeMismatch(ResultResolverNode node, BaseDocument document) {
        this.node = node;
        this.document = document;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format("Error when processing {0}! The field {1} is not a collection in your GraphQL schema however multiple results have been returned for this traversal. The result has been truncated.", document.getId(), node.getField().getQualifiedName());
    }

    @Override
    public List<SourceLocation> getLocations() {
        return Arrays.asList(node.getField().getFieldDefinition().getDefinition().getSourceLocation());
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }
}
