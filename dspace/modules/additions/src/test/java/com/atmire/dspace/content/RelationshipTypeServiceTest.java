package com.atmire.dspace.content;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.servicemanager.DSpaceKernelImpl;
import org.dspace.servicemanager.DSpaceKernelInit;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.utils.DSpace;
import org.junit.*;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeNotNull;

/**
 * // -Ddspace.dir=/Users/antoine/Development/dspaces/dspace42 -Ddspace.configuration=/Users/antoine/Development/dspaces/dspace42/config/dspace.cfg
 */
public class RelationshipTypeServiceTest {
    private static DSpaceKernelImpl kernelImpl;
    private static String sqlFilesDir;
    private Context readContext;
    private Context writeContext;
    private RelationshipTypeService relationshipTypeService;

    @BeforeClass
    public static void setUpBF() throws Exception {
        kernelImpl = DSpaceKernelInit.getKernel(null);
        if (!kernelImpl.isRunning()) {
            kernelImpl.start(ConfigurationManager.getProperty("dspace.dir"));
            sqlFilesDir = ConfigurationManager.getProperty("dspace.dir") + File.separator + "etc" + File.separator + "postgres" + File.separator + "lne" + File.separator + "test_data" + File.separator;
        }
    }

    @AfterClass
    public static void tearDownAS() throws Exception {
        kernelImpl.destroy();
    }

    @Before
    public void setUp() throws Exception {
        readContext = new Context();
        writeContext = new Context();

        // load test data sql
        String sql = FileUtils.readFileToString(new File(sqlFilesDir + "relationship-type.sql"), "UTF-8");
        DatabaseManager.loadSql(sql);

        relationshipTypeService = new DSpace().getServiceManager().getServiceByName("relationshipTypeService", RelationshipTypeService.class);
    }

    @After
    public void tearDown() throws Exception {
        if (readContext.getDBConnection() != null) {
            readContext.abort();
        }
        if (writeContext.getDBConnection() != null) {
            writeContext.abort();
        }

        // unload test data sql
        String sql = FileUtils.readFileToString(new File(sqlFilesDir + "relationship-type_unload.sql"), "UTF-8");
        DatabaseManager.loadSql(sql);
    }

    @Test
    public void testFindById() throws Exception {
        int id = Integer.MAX_VALUE - 2;
        RelationshipType type = relationshipTypeService.findById(readContext, id);

        compareFirstSubject(type);
    }

    @Test
    public void testFindByExampleUnique() throws Exception {
        RelationshipType type = new RelationshipType();
        type.setSemanticRuleset("Rules I");

        RelationshipType result = relationshipTypeService.findByExampleUnique(readContext, type);
        compareFirstSubject(result);
    }

    @Test
    public void testFindByExample() throws Exception {
        RelationshipType type = new RelationshipType();
        type.setSemanticRuleset("Rules II");

        Collection<RelationshipType> results = relationshipTypeService.findByExample(readContext, type);
        assertEquals(2, results.size());
        for (RelationshipType result : results) {
            assertEquals("Rules II", result.getSemanticRuleset());
        }
    }

    @Test(timeout = 20000)
    public void testUpdate() throws Exception {
        RelationshipType type = firstTestSubject();
        type.setRightLabel("updated right label");

        relationshipTypeService.update(writeContext, type);
        writeContext.complete();
        RelationshipType result = relationshipTypeService.findById(readContext, type.getId());

        compare(type, result);
    }

    @Test
    public void testDelete() throws Exception {
        RelationshipType type = firstTestSubject();
        relationshipTypeService.delete(writeContext, type);
        writeContext.complete();
        RelationshipType result = relationshipTypeService.findById(readContext, type.getId());
        assertNull(result);
    }

    @Test
    public void testCreate() throws Exception {
        RelationshipType type = firstTestSubject();
        RelationshipType result = relationshipTypeService.create(writeContext, type);
        relationshipTypeService.delete(writeContext, result);
        writeContext.complete();

        type.setId(result.getId()); // not comparing id
        compare(type, result);
    }

    private void compareFirstSubject(RelationshipType type) {
        compare(firstTestSubject(), type);
//        assertEquals(Integer.MAX_VALUE - 2, type.getId());
//        assertEquals("test type a", type.getLeftType());
//        assertEquals("type b", type.getRightType());
//        assertEquals("label a", type.getLeftLabel());
//        assertEquals("label b", type.getRightLabel());
//        assertEquals(new ImmutablePair<Integer, Integer>(1, 1), type.getLeftCardinality());
//        assertEquals(new ImmutablePair<Integer, Integer>(1, 5), type.getRightCardinality());
//        assertEquals("Rules I", type.getSemanticRuleset());
    }

    private void compare(RelationshipType expected, RelationshipType actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getLeftType(), actual.getLeftType());
        assertEquals(expected.getRightType(), actual.getRightType());
        assertEquals(expected.getLeftLabel(), actual.getLeftLabel());
        assertEquals(expected.getRightLabel(), actual.getRightLabel());
        assertEquals(expected.getLeftCardinality(), actual.getLeftCardinality());
        assertEquals(expected.getRightCardinality(), actual.getRightCardinality());
        assertEquals(expected.getSemanticRuleset(), actual.getSemanticRuleset());
    }

    private RelationshipType firstTestSubject() {
        RelationshipType type = relationshipTypeService.findById(readContext, Integer.MAX_VALUE - 2);
        assumeNotNull(type);
        return type;

//        RelationshipType relationshipType = new RelationshipType();
//        relationshipType.setId(Integer.MAX_VALUE - 2);
//        relationshipType.setLeftType("test type a");
//        relationshipType.setRightType("type b");
//        relationshipType.setLeftLabel("label a");
//        relationshipType.setRightLabel("label b");
//        relationshipType.setLeftCardinality(new ImmutablePair<Integer, Integer>(1, 1));
//        relationshipType.setRightCardinality(new ImmutablePair<Integer, Integer>(1, 5));
//        relationshipType.setSemanticRuleset("Rules I");
//        return relationshipType;
    }

}