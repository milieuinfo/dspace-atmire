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
public class SkipBlankValuesTest {
    @Test
    public void getActions() throws Exception {

        MetadatumExtended description = new MetadatumExtended("dc.description", "description1");
        MetadatumExtended empty = new MetadatumExtended("dc.description", "");
        MetadatumExtended nullValue = new MetadatumExtended("dc.title", null);

        List<DCValue> values = new ArrayList<DCValue>();
        values.add(description);
        values.add(empty);
        values.add(nullValue);

        SkipBlankValues removeMetadata = new SkipBlankValues();
        Map<ManipulationAction, List<DCValue>> actions = removeMetadata.getActions(null, null, values);

        List<DCValue> toRemove = actions.get(ManipulationAction.REMOVE);
        List<DCValue> toAdd = actions.get(ManipulationAction.ADD);

        assertNotNull(toRemove);
        assertNull(toAdd);

        assertFalse(toRemove.contains(description));
        assertTrue(toRemove.contains(empty));
        assertTrue(toRemove.contains(nullValue));
    }

}