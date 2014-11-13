package com.atmire.dspace.content;

import org.apache.commons.io.FileUtils;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.servicemanager.DSpaceKernelImpl;
import org.dspace.servicemanager.DSpaceKernelInit;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.utils.DSpace;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeNotNull;

/**
 * // -Ddspace.dir=/Users/antoine/Development/dspaces/dspace42 -Ddspace.configuration=/Users/antoine/Development/dspaces/dspace42/config/dspace.cfg
 */
public class RelationshipServiceImplTest {

    private static DSpaceKernelImpl kernelImpl;
    private static String sqlFilesDir;
    private Context readContext;
    private Context writeContext;
    private RelationshipService relationshipService;

    @BeforeClass
    public static void setUpBF() throws Exception {
        kernelImpl = DSpaceKernelInit.getKernel(null);
        if (!kernelImpl.isRunning()) {
            kernelImpl.start(ConfigurationManager.getProperty("dspace.dir"));
			sqlFilesDir = System.getProperty("root.basedir") + File.separator + "dspace"+ File.separator + "etc" + File.separator + "postgres" + File.separator + "lne" + File.separator + "test_data" + File.separator;
        }
    }


    @Before
    public void setUp() throws Exception {
        readContext = new Context();
        writeContext = new Context();

        // load test data sql
        String sql = FileUtils.readFileToString(new File(sqlFilesDir + "load-relationship-test-data.sql"), "UTF-8");
        DatabaseManager.loadSql(sql);

        relationshipService = new DSpace().getServiceManager().getServicesByType(RelationshipService.class).get(0);

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
        String sql = FileUtils.readFileToString(new File(sqlFilesDir + "unload-relationship-test-data.sql"), "UTF-8");
        DatabaseManager.loadSql(sql);


    }

    @Test
    public void testFindById() throws Exception {
        Integer id = Integer.MAX_VALUE;
        Relationship relationship = relationshipService.findById(readContext, id);

        assertEquals(id, relationship.getId());
        assertEquals(2147483646, relationship.getLeft().getID());
        assertEquals(2147483647, relationship.getRight().getID());
        assertEquals(2147483647, relationship.getType().getId());

    }

    @Test
    public void testFindByExampleUnique() throws Exception {
        Relationship relationship = new Relationship();
        relationship.setLeft(Item.find(readContext, 2147483646));
        relationship.setRight(Item.find(readContext, 2147483647));
        //it's only unique because I know the test data though

        Relationship result = relationshipService.findByExampleUnique(readContext, relationship);
        compareTestSubject(result);
    }

    @Test
    public void testFindByExample() throws Exception {
        Relationship relationship = new Relationship();
        Item leftItem = Item.find(readContext, 2147483646);
        relationship.setLeft(leftItem);

        Collection<Relationship> results = relationshipService.findByExample(readContext, relationship);
        assertEquals(2, results.size());
        for (Relationship result : results) {
            assertEquals(leftItem, result.getLeft());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        Relationship test = testSubject();
        Item rightItem = Item.find(readContext, 2147483644);
        test.setRight(rightItem);

        relationshipService.update(writeContext, test);
        writeContext.complete();
        Relationship result = relationshipService.findById(readContext, test.getId());

        compare(test, result);
    }

    @Test
    public void testDelete() throws Exception {
        Relationship test = testSubject();
        relationshipService.delete(writeContext, test);
        writeContext.complete();
        Relationship result = relationshipService.findById(readContext, test.getId());
        assertNull(result);
    }

    @Test
    public void testCreate() throws Exception {
        Relationship test = testSubject();

        // change left item or else -> duplicate key value violates unique constraint "relationship_left_type_right_unique" / "relationship_left_and_type_unique"
        Item leftItem = Item.find(readContext, 2147483644);
        test.setLeft(leftItem);

        Relationship result = relationshipService.create(writeContext, test);
        relationshipService.delete(writeContext, result); // cleaning up, but of course delete() needs to work
        writeContext.complete();

        test.setId(result.getId()); // not comparing id
        compare(test, result);

    }

	@Test
	public void testCreateItemItem() throws Exception {
		Relationship rt=testSubject();
		rt.setLeft(Item.find(readContext,2147483644));
		rt.setRight(Item.find(readContext,2147483646));
		Relationship result = relationshipService.create(writeContext, rt);

		writeContext.commit();

		result=relationshipService.findById(readContext,result.getId());
		rt.setId(result.getId()); // not comparing id
		compare(rt,result);


	}


	private void compareTestSubject(Relationship result) {
        compare(testSubject(), result);
    }

    private void compare(Relationship expected, Relationship actual) {
        if(expected==null){
            assertNull(actual);
        }else {
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getLeft().getID(), actual.getLeft().getID());
            assertEquals(expected.getRight().getID(), actual.getRight().getID());
            assertEquals(expected.getType().getId(), actual.getType().getId());
        }
    }

    private Relationship testSubject(){
        Relationship testSubject = relationshipService.findById(readContext, Integer.MAX_VALUE);
        assumeNotNull(testSubject);
        return testSubject;
    }
}