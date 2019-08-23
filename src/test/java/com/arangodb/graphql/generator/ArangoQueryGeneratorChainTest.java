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

package com.arangodb.graphql.generator;

import com.arangodb.graphql.context.ArangoGraphQLContext;
import com.arangodb.graphql.query.ArangoTraversalQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ArangoQueryGeneratorChainTest {

    @Mock
    private ArangoGraphQLContext context;

    @Test
    public void execute() {

        ArangoQueryGeneratorChain chain = new ArangoQueryGeneratorChain().with(new ArangoQueryGenerator() {
            @Override
            public String generateAql(ArangoGraphQLContext ctx) {
                return "RETURN 1";
            }

            @Override
            public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                return true;
            }
        });

        ArangoTraversalQuery result = chain.execute(context);
        assertThat(result.getContext(), equalTo(context));
        assertThat(result.getAql(), equalTo("RETURN 1"));

    }

    @Test
    public void executeNoGeneration() {

        ArangoQueryGeneratorChain chain = new ArangoQueryGeneratorChain().with(new ArangoQueryGenerator() {
            @Override
            public String generateAql(ArangoGraphQLContext ctx) {
                return "RETURN 1";
            }

            @Override
            public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                return false;
            }
        });

        ArangoTraversalQuery result = chain.execute(context);
        assertThat(result.getContext(), equalTo(context));
        assertThat(result.getAql(), equalTo(""));

    }

    @Test
    public void executeNullGeneration() {

        ArangoQueryGeneratorChain chain = new ArangoQueryGeneratorChain().with(new ArangoQueryGenerator() {
            @Override
            public String generateAql(ArangoGraphQLContext ctx) {
                return null;
            }

            @Override
            public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                return true;
            }
        });

        ArangoTraversalQuery result = chain.execute(context);
        assertThat(result.getContext(), equalTo(context));
        assertThat(result.getAql(), equalTo(""));

    }

    @Test
    public void executeMultiLineGeneration() {

        ArangoQueryGeneratorChain chain = new ArangoQueryGeneratorChain().with(new ArangoQueryGenerator() {
            @Override
            public String generateAql(ArangoGraphQLContext ctx) {
                return "FOR x IN [1,2,3]";
            }

            @Override
            public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                return true;
            }
        })
                .with(new ArangoQueryGenerator() {
                    @Override
                    public String generateAql(ArangoGraphQLContext ctx) {
                        return "RETURN x";
                    }

                    @Override
                    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                        return true;
                    }
                });

        ArangoTraversalQuery result = chain.execute(context);
        assertThat(result.getContext(), equalTo(context));
        assertThat(result.getAql(), equalTo("FOR x IN [1,2,3]\nRETURN x"));

    }

    @Test
    public void executeMultiLineGenerationWithNull() {

        ArangoQueryGeneratorChain chain = new ArangoQueryGeneratorChain().with(new ArangoQueryGenerator() {
            @Override
            public String generateAql(ArangoGraphQLContext ctx) {
                return null;
            }

            @Override
            public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                return true;
            }
        })
                .with(new ArangoQueryGenerator() {
                    @Override
                    public String generateAql(ArangoGraphQLContext ctx) {
                        return "RETURN x";
                    }

                    @Override
                    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                        return true;
                    }
                });

        ArangoTraversalQuery result = chain.execute(context);
        assertThat(result.getContext(), equalTo(context));
        assertThat(result.getAql(), equalTo("RETURN x"));

    }

    @Test
    public void executeMultiLineGenerationWithEmptyString() {

        ArangoQueryGeneratorChain chain = new ArangoQueryGeneratorChain().with(new ArangoQueryGenerator() {
            @Override
            public String generateAql(ArangoGraphQLContext ctx) {
                return "";
            }

            @Override
            public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                return true;
            }
        })
                .with(new ArangoQueryGenerator() {
                    @Override
                    public String generateAql(ArangoGraphQLContext ctx) {
                        return "RETURN x";
                    }

                    @Override
                    public boolean shouldGenerateFor(ArangoGraphQLContext ctx) {
                        return true;
                    }
                });

        ArangoTraversalQuery result = chain.execute(context);
        assertThat(result.getContext(), equalTo(context));
        assertThat(result.getAql(), equalTo("RETURN x"));

    }


}