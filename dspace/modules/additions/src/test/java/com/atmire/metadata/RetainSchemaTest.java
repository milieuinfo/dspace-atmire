package com.atmire.metadata;

import com.atmire.util.subclasses.MetadatumExtended;
import org.dspace.content.DCValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public class RetainSchemaTest {

    @Test
    public void getActions() throws Exception {

        MetadatumExtended dcValue = new MetadatumExtended("dc.description", "description");
        MetadatumExtended vlaanderenValue = new MetadatumExtended("vlaanderen.title", "title");

        List<DCValue> values = new ArrayList<DCValue>();
        values.add(dcValue);
        values.add(vlaanderenValue);

        RetainSchema retainSchema = new RetainSchema("vlaanderen");
        Map<ManipulationAction, List<DCValue>> actions = retainSchema.getActions(null, null, values);

        List<DCValue> toRemove = actions.get(ManipulationAction.REMOVE);
        List<DCValue> toAdd = actions.get(ManipulationAction.ADD);

        assertNotNull(toRemove);
        assertNull(toAdd);

        assertTrue(toRemove.contains(dcValue));
        assertFalse(toRemove.contains(vlaanderenValue));
    }

}