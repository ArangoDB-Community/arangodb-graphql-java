package com.arangodb.graphql.create;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.model.HashIndexOptions;
import graphql.schema.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static graphql.Scalars.GraphQLString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseObjectCreatorTest {

    @Mock
    private ArangoDB arangoDB;

    @Mock
    private GraphQLSchema graphQLSchema;

    @Mock
    private ArangoDatabase arangoDatabase;

    @Mock
    private GraphQLObjectType mockType;

    @Mock
    private GraphQLDirective mockDirective;

    @Mock
    private GraphQLDirective mockEdgeDirective;

    @Mock
    private GraphQLArgument mockArgument;

    @Mock
    private ArangoCollection mockCollection;

    @Mock
    private ArangoCollection mockEdgeCollection;

    @Mock
    private GraphQLFieldDefinition mockFieldDefinition;

    @Mock
    private GraphQLArgument mockEdgeArgument;

    @Mock
    private GraphQLObjectType mockQueryType;

    @Mock
    private GraphQLFieldDefinition mockQueryFieldDefinition;

    @Mock
    private GraphQLArgument mockMandatoryArgument;

    @Mock
    private GraphQLArgument mockOptionalArgument;

    private String databaseName = "myDatabase";

    private String collectionName = "myCollection";

    private String edgeCollectionName = "myEdgeCollection";

    private DatabaseObjectCreator databaseObjectCreator;
    private String mandatoryArgumentName = "mandatoryArg";
    private String optionalArgumentName = "optionalArg";

    private void attachVertexDirectiveToMockType() {
        when(mockType.getDirective("vertex")).thenReturn(mockDirective);
        when(mockDirective.getArgument("collection")).thenReturn(mockArgument);
        when(mockArgument.getValue()).thenReturn(collectionName);
    }

    private void attachEdgeDirectiveToMockType() {
        attachVertexDirectiveToMockType();

        when(mockFieldDefinition.getDirective("edge")).thenReturn(mockEdgeDirective);
        when(mockEdgeDirective.getArgument("collection")).thenReturn(mockEdgeArgument);
        when(mockEdgeArgument.getValue()).thenReturn(edgeCollectionName);
    }

    @Before
    public void setup() {
        //Arango
        when(arangoDB.db(databaseName)).thenReturn(arangoDatabase);
        when(arangoDatabase.collection(collectionName)).thenReturn(mockCollection);
        when(arangoDatabase.collection(edgeCollectionName)).thenReturn(mockEdgeCollection);

        //GraphQL Schema
        when(graphQLSchema.getAllTypesAsList()).thenReturn(Arrays.asList(mockType));
        when(graphQLSchema.getQueryType()).thenReturn(mockQueryType);
        when(mockQueryType.getFieldDefinitions()).thenReturn(Arrays.asList(mockQueryFieldDefinition));
        when(mockQueryFieldDefinition.getType()).thenReturn(mockType);
        when(mockMandatoryArgument.getName()).thenReturn(mandatoryArgumentName);
        when(mockMandatoryArgument.getType()).thenReturn(new GraphQLNonNull(GraphQLString));
        when(mockOptionalArgument.getName()).thenReturn(optionalArgumentName);


        //Type Setup
        when(mockType.getFieldDefinitions()).thenReturn(Arrays.asList(mockFieldDefinition));

        databaseObjectCreator = new DatabaseObjectCreator(arangoDB, databaseName, graphQLSchema);
    }

    @Test
    public void createDatabase() {
        when(arangoDatabase.exists()).thenReturn(false);
        databaseObjectCreator.createDatabase();
        verify(arangoDatabase).create();
    }

    @Test
    public void doNotCreateExistingDatabase() {
        when(arangoDatabase.exists()).thenReturn(true);
        databaseObjectCreator.createDatabase();
        verify(arangoDatabase, never()).create();
    }

    @Test
    public void createCollectionForVertexDirectiveType() {
        attachVertexDirectiveToMockType();
        when(mockCollection.exists()).thenReturn(false);

        databaseObjectCreator.createCollections();

        ArgumentCaptor<CollectionCreateOptions> captor = ArgumentCaptor.forClass(CollectionCreateOptions.class);
        verify(mockCollection).create(captor.capture());
        assertThat(captor.getValue().getType(), equalTo(CollectionType.DOCUMENT));

    }

    @Test
    public void doNotCreateExistingCollectionForVertexDirectiveType() {
        attachVertexDirectiveToMockType();
        when(mockCollection.exists()).thenReturn(true);

        databaseObjectCreator.createCollections();

        verify(mockCollection, never()).create(ArgumentMatchers.any(CollectionCreateOptions.class));

    }

    @Test
    public void doNotCreateCollectionForTypeWithNoDirective() {

        databaseObjectCreator.createCollections();
        verify(mockCollection, never()).create(ArgumentMatchers.any(CollectionCreateOptions.class));

    }

    @Test
    public void createCollectionForEdgeDirectiveType() {
        attachEdgeDirectiveToMockType();

        when(mockEdgeCollection.exists()).thenReturn(false);

        databaseObjectCreator.createCollections();

        ArgumentCaptor<CollectionCreateOptions> captor = ArgumentCaptor.forClass(CollectionCreateOptions.class);
        verify(mockEdgeCollection).create(captor.capture());
        assertThat(captor.getValue().getType(), equalTo(CollectionType.EDGES));

    }

    @Test
    public void doNotCreateExistingCollectionForEdgeDirectiveType() {
        attachEdgeDirectiveToMockType();

        when(mockEdgeCollection.exists()).thenReturn(true);

        databaseObjectCreator.createCollections();

        verify(mockEdgeCollection, never()).create(ArgumentMatchers.any(CollectionCreateOptions.class));

    }

    @Test
    public void doNotCreateCollectionForFieldWithNoEdgeDirectiveType() {
        attachVertexDirectiveToMockType();

        databaseObjectCreator.createCollections();

        verify(mockEdgeCollection, never()).create(ArgumentMatchers.any(CollectionCreateOptions.class));

    }

    @Test
    public void createIndexes() {
        attachVertexDirectiveToMockType();
        when(mockQueryFieldDefinition.getArguments()).thenReturn(Arrays.asList(mockMandatoryArgument, mockOptionalArgument));

        databaseObjectCreator.createIndexes();

        ArgumentCaptor<Iterable<String>> captor = ArgumentCaptor.forClass(Iterable.class);

        verify(mockCollection).ensureHashIndex(captor.capture(), ArgumentMatchers.any(HashIndexOptions.class));

        assertThat(captor.getValue(), contains(mandatoryArgumentName, optionalArgumentName));

    }

    @Test
    public void createIndexesWithOptionalFirstArg() {
        attachVertexDirectiveToMockType();
        when(mockQueryFieldDefinition.getArguments()).thenReturn(Arrays.asList(mockOptionalArgument, mockMandatoryArgument));

        databaseObjectCreator.createIndexes();

        ArgumentCaptor<Iterable<String>> captor = ArgumentCaptor.forClass(Iterable.class);

        verify(mockCollection).ensureHashIndex(captor.capture(), ArgumentMatchers.any(HashIndexOptions.class));

        assertThat(captor.getValue(), contains(mandatoryArgumentName, optionalArgumentName));

    }

    @Test
    public void createIndexesWithNoMandatoryArgs() {
        attachVertexDirectiveToMockType();
        when(mockQueryFieldDefinition.getArguments()).thenReturn(Arrays.asList(mockOptionalArgument));

        databaseObjectCreator.createIndexes();

        ArgumentCaptor<Iterable<String>> captor = ArgumentCaptor.forClass(Iterable.class);

        verify(mockCollection).ensureHashIndex(captor.capture(), ArgumentMatchers.any(HashIndexOptions.class));

        assertThat(captor.getValue(), hasItem(optionalArgumentName));

    }


}