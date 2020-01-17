package com.arangodb.graphql.create;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.CollectionType;
import com.arangodb.graphql.schema.ArangoEdgeDirective;
import com.arangodb.graphql.schema.ArangoVertexDirective;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.model.HashIndexOptions;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DatabaseObjectCreator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArangoDB arango;
    private final String databaseName;
    private final GraphQLSchema graphQLSchema;

    public DatabaseObjectCreator(ArangoDB arango, String databaseName, GraphQLSchema graphQLSchema) {
        this.arango = arango;
        this.databaseName = databaseName;
        this.graphQLSchema = graphQLSchema;
    }

    private List<TypeCollectionMapping> createTypeMappings(){

        List<GraphQLType> types = graphQLSchema.getAllTypesAsList();

        List<TypeCollectionMapping> mappings = new ArrayList<>();
        types.forEach(type -> {
            if(type instanceof GraphQLObjectType){
                GraphQLObjectType objectType = (GraphQLObjectType) type;
                ArangoVertexDirective vertexDirective = new ArangoVertexDirective(objectType);
                String collection = vertexDirective.getCollection();
                if(collection != null){
                    TypeCollectionMapping mapping = new TypeCollectionMapping(collection);

                    objectType.getFieldDefinitions().forEach(graphQLFieldDefinition -> {
                        ArangoEdgeDirective arangoEdgeDirective = new ArangoEdgeDirective(graphQLFieldDefinition);
                        String edgeCollectionName = arangoEdgeDirective.getCollection();
                        if(edgeCollectionName != null){
                            mapping.addEdgeCollection(edgeCollectionName);
                        }
                    });

                    mappings.add(mapping);

                }
            }
        });

        return mappings;

    }

    public void createDatabase(){
        ArangoDatabase db = arango.db(databaseName);
        if(!db.exists()){
            logger.info("Auto Create Database {}", databaseName);
            db.create();
        }
        else{
            logger.info("Database {} already exists", databaseName);
        }
    }

    public void createCollections(){

        List<TypeCollectionMapping> mappings = createTypeMappings();
        mappings.forEach(mapping -> {
            String vertex = mapping.getVertexCollection();
            createCollection(vertex, CollectionType.DOCUMENT);
            Set<String> edgeCollections = mapping.getEdgeCollections();
            edgeCollections.forEach(edgeCollection -> {
                createCollection(edgeCollection, CollectionType.EDGES);
            });
        });

    }

    public void createIndexes(){

        ArangoDatabase database = arango.db(databaseName);
        GraphQLObjectType queryType = graphQLSchema.getQueryType();
        queryType.getFieldDefinitions().forEach(graphQLFieldDefinition -> {
            ArgumentIndexMapping mapping = new ArgumentIndexMapping(graphQLFieldDefinition);

            String collection = mapping.getCollection();
            if(collection != null){
                logger.info("Auto Create Index on collection {} for fields {}", collection, mapping.getFields());
                database.collection(collection).ensureHashIndex(mapping.getFields(), new HashIndexOptions());
            }

        });
    }

    protected CollectionCreateOptions createCollectionCreateOptions(CollectionType collectionType){
        return new CollectionCreateOptions().type(collectionType);
    }

    private void createCollection(String name, CollectionType collectionType) {

        CollectionCreateOptions collectionCreateOptions = createCollectionCreateOptions(collectionType);
        ArangoDatabase database = arango.db(databaseName);

        if(!database.collection(name).exists()){
            logger.info("Auto Create Collection {}:{}", name, collectionType);
            database.collection(name).create(collectionCreateOptions);
        }
        else{
            logger.info("Collection already exists {}:{}", name, collectionType);
        }

    }

}
