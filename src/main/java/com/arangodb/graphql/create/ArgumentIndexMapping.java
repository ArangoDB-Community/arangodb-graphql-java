package com.arangodb.graphql.create;

import com.arangodb.graphql.schema.ArangoVertexDirective;
import graphql.schema.*;

import java.util.ArrayList;
import java.util.List;

public class ArgumentIndexMapping {

    private GraphQLFieldDefinition fieldDefinition;

    private List<String> fields;

    private String collection;

    public ArgumentIndexMapping(GraphQLFieldDefinition fieldDefinition){
        this.fieldDefinition = fieldDefinition;
        init();
    }


    private void init(){

        fields = new ArrayList<>();

        List<GraphQLArgument> arguments = fieldDefinition.getArguments();

        boolean firstMandatoryArgFound = false;
        for(GraphQLArgument argument : arguments){
            if(firstMandatoryArgFound){
                fields.add(argument.getName());
            }
            else {
                boolean nonNull = GraphQLTypeUtil.isNonNull(argument.getType());
                if (nonNull) {
                    firstMandatoryArgFound = true;
                    fields.add(0, argument.getName());
                } else {
                    fields.add(argument.getName());
                }
            }

        }

        GraphQLUnmodifiedType type = GraphQLTypeUtil.unwrapAll(fieldDefinition.getType());
        if(type instanceof GraphQLDirectiveContainer) {
            ArangoVertexDirective vertexDirective = new ArangoVertexDirective((GraphQLDirectiveContainer)type);
            this.collection = vertexDirective.getCollection();
        }
    }

    public String getCollection() {
        return collection;
    }

    public List<String> getFields() {
        return fields;
    }
}
