package com.atmire.dspace.content;

import org.apache.commons.io.FileUtils;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.kernel.ServiceManager;
import org.dspace.servicemanager.DSpaceKernelImpl;
import org.dspace.servicemanager.DSpaceKernelInit;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.utils.DSpace;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 13 Nov 2014
 */
public class TestUtils {


    private static DSpaceKernelImpl kernelImpl;
    private static String sqlFilesDir = System.getProperty("root.basedir") + File.separator + "dspace" + File.separator + "etc" + File.separator + "postgres" + File.separator + "lne" + File.separator + "test_data" + File.separator;

    public static ServiceManager serviceManager;
    public static RelationshipTypeService relationshipTypeService;
    public static RelationshipService relationshipService;
    public static DocumentService documentService;
    public static DossierService dossierService;
    public static Context readContext;
    public static Context writeContext;

    public static void setUpBF(){
        kernelImpl = DSpaceKernelInit.getKernel(null);
        if (!kernelImpl.isRunning()) {
            kernelImpl.start(ConfigurationManager.getProperty("dspace.dir"));
            serviceManager = new DSpace().getServiceManager();
            relationshipTypeService = serviceManager.getServicesByType(RelationshipTypeService.class).get(0);
            relationshipService = serviceManager.getServicesByType(RelationshipService.class).get(0);
            documentService = new DSpace().getServiceManager().getServicesByType(DocumentService.class).get(0);
            dossierService = new DSpace().getServiceManager().getServicesByType(DossierService.class).get(0);
        }
    }

    public static void tearDownAS() throws Exception {
           kernelImpl.destroy();
       }

    public static void loadTestSQL() throws IOException, SQLException {
        String sql = FileUtils.readFileToString(new File(sqlFilesDir + "load-relationship-test-data.sql"), "UTF-8");
        DatabaseManager.loadSql(sql);
    }

    public static void unloadTestSQL() throws IOException, SQLException {
        String sql = FileUtils.readFileToString(new File(sqlFilesDir + "unload-relationship-test-data.sql"), "UTF-8");
        DatabaseManager.loadSql(sql);
    }

    /**
     * Requires that loadTestSQL() has been executed
     */
    public static List<Relationship> loadTestDossiersAndDocuments(Context context) throws SQLException {
        relationshipTypeService = new DSpace().getServiceManager().getServicesByType(RelationshipTypeService.class).get(0);
        relationshipService = new DSpace().getServiceManager().getServicesByType(RelationshipService.class).get(0);

        // setup RelationshipTypes
        RelationshipType dosDocType = new RelationshipType(Dossier.class.getCanonicalName(), Document.class.getCanonicalName());
        RelationshipType dosDosType = new RelationshipType(Dossier.class.getCanonicalName(), Dossier.class.getCanonicalName());
        RelationshipType docDocType = new RelationshipType(Document.class.getCanonicalName(), Document.class.getCanonicalName());

        List<RelationshipType> setupTypes = new LinkedList<RelationshipType>();
        setupTypes.add(dosDocType);
        setupTypes.add(dosDosType);
        setupTypes.add(docDocType);
        for (RelationshipType setupType : setupTypes) {
            if (relationshipTypeService.findByExampleUnique(context, setupType) == null) {
                relationshipTypeService.create(context, setupType);
            }
        }
        dosDocType = relationshipTypeService.findByExampleUnique(context, dosDocType);
        dosDosType = relationshipTypeService.findByExampleUnique(context, dosDosType);
        docDocType = relationshipTypeService.findByExampleUnique(context, docDocType);

        // setup Relationships
        Item itemA = Item.find(context, Integer.MAX_VALUE);
        Item itemB = Item.find(context, Integer.MAX_VALUE - 1);
        Item itemC = Item.find(context, Integer.MAX_VALUE - 2);
        Item itemD = Item.find(context, Integer.MAX_VALUE - 3);

        List<Relationship> relationships = new ArrayList<Relationship>();
        Relationship relationship1 = relationshipService.create(context, itemA, itemA, docDocType); // document A
        Relationship relationship2 = relationshipService.create(context, itemB, itemB, docDocType); // document B

        Relationship relationship3 = relationshipService.create(context, itemD, itemD, dosDosType); // dossier A

        Relationship relationship4 = relationshipService.create(context, itemD, itemA, dosDocType); // dossier A contains document A
        Relationship relationship5 = relationshipService.create(context, itemD, itemB, dosDocType); // dossier A contains document B

        relationships.add(relationship1);
        relationships.add(relationship2);
        relationships.add(relationship3);
        relationships.add(relationship4);
        relationships.add(relationship5);

        context.commit();

        return relationships;
    }

    public static void unloadRelationships(Context context, List<Relationship> relationships) throws SQLException {
        for (Relationship relationship : relationships) {
            if (relationshipService.findById(context, relationship.getId()) != null) {
                relationshipService.delete(context, relationship);
            }
        }
        context.commit();
    }


    public static void setupContext() throws SQLException {
        readContext = new Context();
        writeContext = new Context();

        EPerson ePerson = EPerson.find(readContext, Integer.MAX_VALUE);
        readContext.setCurrentUser(ePerson);

        ePerson = EPerson.find(writeContext, Integer.MAX_VALUE);
        writeContext.setCurrentUser(ePerson);
    }

    public static void tearDownContext(List<Relationship> relationships) throws SQLException {

        if (writeContext.getDBConnection() != null) {
            writeContext.abort();
        }

        if (readContext.getDBConnection() != null) {
            // make sure to abort the writeContext first
            // otherwise if it had a problem, this could end up getting stuck
            TestUtils.unloadRelationships(readContext, relationships);
            readContext.abort();
        }
    }

    public static void compareDossierA(Dossier found) throws SQLException {
        Item itemD = Item.find(readContext, Integer.MAX_VALUE - 3);
        assertNotNull(found);
        assertEquals(itemD, found.getItem());
        assertEquals("The dossier has 2 documents", 2, found.getDocuments().size());
        assertEquals("The dossier has 2 outgoing relationships", 2, found.getOutgoing().size());
        for (Document document : found.getDocuments()) {
            List<Relationship> incoming = document.getIncoming();
            assertEquals(1, incoming.size());
            Relationship incomingRelationship = incoming.get(0);
            assertEquals(itemD, incomingRelationship.getLeft());
        }
    }

    public static void compareDocumentA(Document found) throws SQLException {
        Item itemA = Item.find(readContext, Integer.MAX_VALUE);
        assertNotNull(found);
        assertEquals(itemA, found.getItem());
        List<Relationship> incoming = found.getIncoming();
        assertEquals("The document should have one incoming relationship", 1, incoming.size());
        Item dossierItem = incoming.get(0).getLeft();
        Dossier dossier = dossierService.findByItem(readContext, dossierItem);
        assertNotNull(dossier);
        assertTrue("The document's dossier should contain the document", dossier.getDocuments().contains(found));
    }
}
