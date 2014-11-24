package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;
import org.junit.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractRelationshipObjectServiceImplTest {

    private Context readContext;
    private Context writeContext;
    private RelationshipObjectService<Document> documentService;
    private RelationshipObjectService<Dossier> dossierService;
    private RelationshipTypeService relationshipTypeService = TestUtils.relationshipTypeService;
    private RelationshipService relationshipService = TestUtils.relationshipService;
    protected List<Relationship> relationships;

    @BeforeClass
    public static void setUpBF() throws Exception {
        TestUtils.setUpBF();
    }

    @AfterClass
    public static void tearDownAS() throws Exception {
        TestUtils.tearDownAS();
    }

    @Before
    public void setUp() throws Exception {

        // load test data sql
        TestUtils.loadTestSQL();

        TestUtils.setupContext();
        readContext = TestUtils.readContext;
        writeContext = TestUtils.writeContext;

        documentService = RelationshipObjectServiceFactory.getInstance().getRelationshipObjectService(Document.class);
        dossierService = RelationshipObjectServiceFactory.getInstance().getRelationshipObjectService(Dossier.class);

        relationships = TestUtils.loadTestDossiersAndDocuments(readContext);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.tearDownContext(relationships);

        // unload test data sql
        TestUtils.unloadTestSQL();
    }

    @Test
    public void testFindByExample() throws Exception {
        // by item
        Item itemA = Item.find(readContext, Integer.MAX_VALUE);
        Document document = new Document(null, null, null, itemA);
        Document foundDocument = documentService.findByExample(readContext, document);
        TestUtils.compareDocumentA(foundDocument);

        Item itemD = Item.find(readContext, Integer.MAX_VALUE - 3);
        Dossier dossier = new Dossier(null, null, null, itemD, null);
        Dossier foundDossier = dossierService.findByExample(readContext, dossier);
        TestUtils.compareDossierA(foundDossier);

        // findByExample currently only works with the item

        /*
        // by outgoing
        List<Relationship> outgoing = new LinkedList<Relationship>();
        outgoing.add(relationships.get(4));
        Dossier dossier = new Dossier(null, null, outgoing, null, null);
        Dossier foundDossier = dossierService.findByExample(readContext, dossier);
        TestUtils.compareDossierA(foundDossier);

        // by document
        List<Document> documents = new LinkedList<Document>();
        documents.add(foundDocument);
        dossier = new Dossier(null, null, null, null, documents);
        foundDossier = dossierService.findByExample(readContext, dossier);
        TestUtils.compareDossierA(foundDossier);

        // by incoming
        List<Relationship> incoming = new LinkedList<Relationship>();
        incoming.add(relationships.get(4));
        document = new Document(null, incoming, null, null);
        foundDocument = documentService.findByExample(readContext, document);
        TestUtils.compareDocumentA(foundDocument);
        */

    }


    @Test
    public void testFindByItems() throws Exception {
        Item itemA = Item.find(readContext, Integer.MAX_VALUE);
        Item itemD = Item.find(readContext, Integer.MAX_VALUE - 3);
        Document document = documentService.findByItems(readContext, itemD, itemA);
        TestUtils.compareDocumentA(document);
        Dossier dossier = dossierService.findByItems(readContext, itemD, itemA);
        TestUtils.compareDossierA(dossier);
    }

    @Test
    public void testFindByItem() throws Exception {
        Item itemA = Item.find(readContext, Integer.MAX_VALUE);
        Document document = documentService.findByItem(readContext, itemA);
        TestUtils.compareDocumentA(document);

        Item itemD = Item.find(readContext, Integer.MAX_VALUE - 3);
        Dossier dossier = dossierService.findByItem(readContext, itemD);
        TestUtils.compareDossierA(dossier);
    }

    @Test
    public void testGetIncomingRelationshipObjects() throws Exception {
        Document document = documentService.findById(readContext, relationships.get(0).getId()); // document A
        Dossier dossier = dossierService.findById(readContext, relationships.get(2).getId()); // dossier A
        List<RelationShipObject> incomingRelationshipObjects = documentService.getIncomingRelationshipObjects(readContext, document);
        assertEquals(1, incomingRelationshipObjects.size());
        assertTrue(incomingRelationshipObjects.contains(dossier));

        incomingRelationshipObjects = dossierService.getIncomingRelationshipObjects(readContext, dossier);
        assertEquals((0), incomingRelationshipObjects.size());
    }

    @Test
    public void testGetOutgoingRelationshipObjects() throws Exception {
        Document documentA = documentService.findById(readContext, relationships.get(0).getId()); // document A
        Document documentB = documentService.findById(readContext, relationships.get(1).getId()); // document A
        Dossier dossier = dossierService.findById(readContext, relationships.get(2).getId()); // dossier A
        List<RelationShipObject> outgoingRelationshipObjects = dossierService.getOutgoingRelationshipObjects(readContext, dossier);
        assertEquals(2, outgoingRelationshipObjects.size());
        assertTrue(outgoingRelationshipObjects.contains(documentA));
        assertTrue(outgoingRelationshipObjects.contains(documentB));

        outgoingRelationshipObjects = documentService.getOutgoingRelationshipObjects(readContext, documentA);
        assertEquals(0, outgoingRelationshipObjects.size());
    }
}