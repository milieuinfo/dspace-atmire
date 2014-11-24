package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;
import org.junit.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class DocumentServiceTest {


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

/*    @Test
    public void testFindByRelationshipUnique() throws Exception {

        // the doc-doc relationship
        Item itemA = Item.find(readContext, Integer.MAX_VALUE);
        Relationship relationship1 = new Relationship(null, itemA, itemA, null);
        Document found = documentService.findByRelationshipUnique(readContext, relationship1);
        assertNotNull(found);
        assertEquals(itemA, found.getItem());
        List<Relationship> incoming = found.getIncoming();
        assertEquals("The document should have one incoming relationship", 1, incoming.size());
        Item dossierItem = incoming.get(0).getLeft();
        Dossier dossier = dossierService.findByItem(readContext, dossierItem);
        assertNotNull(dossier);
        assertTrue("The document's dossier should contain the document", dossier.getDocuments().contains(found));

        // the dos-doc relationship
        Item itemD = Item.find(readContext, Integer.MAX_VALUE - 3);
        Relationship relationship2 = new Relationship(null, itemD, itemA, null);
        found = documentService.findByRelationshipUnique(readContext, relationship2);
        assertNotNull(found);
        assertEquals(itemA, found.getItem());

        // a call with no expected results
        Item itemC = Item.find(readContext, Integer.MAX_VALUE - 2);
        Relationship relationship3 = new Relationship(null, null, itemC, null);
        found = documentService.findByRelationshipUnique(readContext, relationship3);
        assertNull(found);
    }*/

    @Test
    public void testFindById() throws Exception {
        Relationship relationship = relationships.get(0); // document A
        Integer id = relationship.getId();
        Document document = documentService.findById(readContext, id);
        assertNotNull(document);
        assertEquals(id, Integer.valueOf(document.getID()));

        Item itemA = Item.find(readContext, Integer.MAX_VALUE);
        assertEquals(itemA, document.getItem());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() throws Exception {
        Document document = documentService.findById(readContext, relationships.get(0).getId()); // document A
        documentService.update(writeContext, document);
    }

    @Test
    public void testDelete() throws Exception {
        readContext.turnOffAuthorisationSystem();
        writeContext.turnOffAuthorisationSystem();

        List<Relationship> relationshipsToBeRemoved = Arrays.asList(relationships.get(0), relationships.get(3));

        Integer id = relationships.get(0).getId(); // document A
        Document documentBefore = documentService.findById(readContext, id);
        boolean deleted = documentService.delete(writeContext, documentBefore);
        writeContext.commit();
        assertTrue(deleted);
        Document documentAfter = documentService.findById(readContext, id);
        assertNull(documentAfter);

        for (Relationship relationship : relationshipsToBeRemoved) {
            Relationship byId = relationshipService.findById(readContext, relationship.getId());
            assertNull(byId);
        }

        writeContext.restoreAuthSystemState();
        readContext.restoreAuthSystemState();
    }

    @Test
    public void testCreate() throws Exception {

        Item itemC = Item.find(readContext, Integer.MAX_VALUE - 2);
        Document document = new Document(null, null, null, itemC);

        Item itemD = Item.find(readContext, Integer.MAX_VALUE - 3);
        Dossier dossier =  dossierService.findByItem(readContext, itemD); // dossier A
        document.setDossier(dossier);

        Document documentCreated = documentService.create(writeContext, document);
        writeContext.commit();

        // asserts

        assertNotNull(documentCreated);
        assertEquals(itemC, documentCreated.getItem());
        List<Relationship> incoming = documentCreated.getIncoming();
        assertEquals("The document should have one incoming relationship", 1, incoming.size());
        Item dossierItem = incoming.get(0).getLeft();
        Dossier dossierTest = dossierService.findByItem(readContext, dossierItem);
        assertEquals(dossier, dossierTest);

        //clean up

        readContext.turnOffAuthorisationSystem();
        writeContext.turnOffAuthorisationSystem();
        documentService.delete(writeContext, document);
        writeContext.commit();
        writeContext.restoreAuthSystemState();
        readContext.restoreAuthSystemState();

    }
}