package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;
import org.junit.*;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class DossierServiceTest {

    private Context readContext;
    private Context writeContext;
    private DocumentService documentService;
    private DossierService dossierService;
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

        documentService = new DSpace().getServiceManager().getServicesByType(DocumentService.class).get(0);
        dossierService = new DSpace().getServiceManager().getServicesByType(DossierService.class).get(0);

        relationships = TestUtils.loadTestDossiersAndDocuments(readContext);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.tearDownContext(relationships);

        // unload test data sql
        TestUtils.unloadTestSQL();
    }


    @Test
    public void testFindByRelationshipUnique() throws Exception {

        // the dos-dos relationship
        Item itemD = Item.find(readContext, Integer.MAX_VALUE - 3);
        Relationship relationship = new Relationship(null, itemD, itemD, null);
        Dossier found = dossierService.findByRelationshipUnique(readContext, relationship);

        TestUtils.compareDossierA(found);

        // the dos-doc relationship
        Item itemA = Item.find(readContext, Integer.MAX_VALUE);
        Relationship relationship2 = new Relationship(null, itemD, itemA, null);
        found = dossierService.findByRelationshipUnique(readContext, relationship2);

        TestUtils.compareDossierA(found);

        // a call with no expected results
        Item itemC = Item.find(readContext, Integer.MAX_VALUE - 2);
        Relationship relationship3 = new Relationship(null, null, itemC, null);
        found = dossierService.findByRelationshipUnique(readContext, relationship3);
        assertNull(found);
    }

    @Test
    public void testFindById() throws Exception {
        Relationship relationship = relationships.get(2); // dossier A
        Integer id = relationship.getId();
        Dossier dossier = dossierService.findById(readContext, id);

        TestUtils.compareDossierA(dossier);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() throws Exception {
        Dossier dossier = dossierService.findById(readContext, relationships.get(2).getId());
        dossierService.update(writeContext, dossier);
    }

    @Test
    public void testDelete() throws Exception {
        readContext.turnOffAuthorisationSystem();
        writeContext.turnOffAuthorisationSystem();

        Relationship dossierRelationship = relationships.get(2);
        Dossier dossier = dossierService.findById(readContext, dossierRelationship.getId());

        boolean deleted = dossierService.delete(writeContext, dossier);
        assertFalse("The documents should be deleted before the dossier can be deleted", deleted);

        for (Document document : dossier.getDocuments()) {
            documentService.delete(writeContext, document);
        }
        writeContext.commit();

        // dossier instances don't get updated automatically, they have to be re-retrieved (or manually updated)
        dossier = dossierService.findById(readContext, dossierRelationship.getId());
        deleted = dossierService.delete(writeContext, dossier);
        writeContext.commit();
        assertTrue(deleted);

        Relationship dossierRelationshipAfter = relationshipService.findById(readContext, dossierRelationship.getId());
        assertNull(dossierRelationshipAfter);

        writeContext.restoreAuthSystemState();
        readContext.restoreAuthSystemState();
    }

    @Test
    public void testCreate() throws Exception {
        readContext.turnOffAuthorisationSystem();
        writeContext.turnOffAuthorisationSystem();

        Item itemA = Item.find(writeContext, Integer.MAX_VALUE);
        Item itemC = Item.find(writeContext, Integer.MAX_VALUE - 2);

        // I can't use an existing document since it's linked to an existing dossier,
        // so first I'll free up an item by deleting a document. (I only have so many available test items...)
        Document document = documentService.findById(writeContext, relationships.get(0).getId());
        documentService.delete(writeContext, document);
        writeContext.commit();

        List<Document> documents = new LinkedList<Document>();
        documents.add(new Document(null, null, null, itemA)); // the new document will be created along with the dossier
        Dossier dossier = new Dossier(null, null, null, itemC, documents);
        Dossier dossierCreated = dossierService.create(writeContext, dossier); //using the readContext in the find methods above cause the table to be locked, causing an endless timeout
        writeContext.commit();

        // asserts

        assertNotNull(dossierCreated);
        assertEquals(itemC, dossierCreated.getItem());
        assertEquals("The dossier has 1 document", 1, dossierCreated.getDocuments().size());
        assertEquals("The dossier has 1 outgoing relationship", 1, dossierCreated.getOutgoing().size());
        for (Document doc : dossierCreated.getDocuments()) {
            List<Relationship> incoming = doc.getIncoming();
            assertEquals(1, incoming.size());
            Relationship incomingRelationship = incoming.get(0);
            assertEquals(itemC, incomingRelationship.getLeft());
        }

        //clean up

        for (Document doc : dossierCreated.getDocuments()) {
            documentService.delete(writeContext, doc);
        }
        writeContext.commit();
        dossierService.delete(writeContext, dossierCreated);
        writeContext.commit();
        writeContext.restoreAuthSystemState();
        readContext.restoreAuthSystemState();

    }
}