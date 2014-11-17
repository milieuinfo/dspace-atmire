package com.atmire.dspace.content;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;
import org.junit.*;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

/**
 * // -Ddspace.dir=/Users/antoine/Development/dspaces/dspace42 -Ddspace.configuration=/Users/antoine/Development/dspaces/dspace42/config/dspace.cfg -Droot.basedir=/Users/antoine/IdeaProjects/lne42/code
 */
public class RelationshipTypeServiceTest {

    private Context readContext;
    private Context writeContext;
    private RelationshipTypeService relationshipTypeService;

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
        readContext = new Context();
        writeContext = new Context();

        // load test data sql
        TestUtils.loadTestSQL();

        relationshipTypeService = new DSpace().getServiceManager().getServicesByType(RelationshipTypeService.class).get(0);
    }

    @After
    public void tearDown() throws Exception {
        if (writeContext.getDBConnection() != null) {
            writeContext.abort();
        }
        if (readContext.getDBConnection() != null) {
            readContext.abort();
        }

        // unload test data sql
        TestUtils.unloadTestSQL();
    }

    @Test
    public void testFindById() throws Exception {
        int id = Integer.MAX_VALUE - 2;
        RelationshipType type = relationshipTypeService.findById(readContext, id);
        assertNotNull(type);
//        compareFirstSubject(type); // this would be redundant because it uses findById

        assertEquals(Integer.MAX_VALUE - 2, type.getId());
        assertEquals("test type a", type.getLeftType());
        assertEquals("type b", type.getRightType());
        assertEquals("label a", type.getLeftLabel());
        assertEquals("label b", type.getRightLabel());
        assertEquals(new ImmutablePair<Integer, Integer>(1, 1), type.getLeftCardinality());
        assertEquals(new ImmutablePair<Integer, Integer>(1, 5), type.getRightCardinality());
        assertEquals("Rules I", type.getSemanticRuleset());
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

    @Test
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
        relationshipTypeService.delete(writeContext, result); // cleaning up, but of course delete() needs to work
        writeContext.complete();

        type.setId(result.getId()); // not comparing id
        compare(type, result);
    }

    private void compareFirstSubject(RelationshipType type) {
        compare(firstTestSubject(), type);
    }

    private void compare(RelationshipType expected, RelationshipType actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getLeftType(), actual.getLeftType());
            assertEquals(expected.getRightType(), actual.getRightType());
            assertEquals(expected.getLeftLabel(), actual.getLeftLabel());
            assertEquals(expected.getRightLabel(), actual.getRightLabel());
            assertEquals(expected.getLeftCardinality(), actual.getLeftCardinality());
            assertEquals(expected.getRightCardinality(), actual.getRightCardinality());
            assertEquals(expected.getSemanticRuleset(), actual.getSemanticRuleset());
        }
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