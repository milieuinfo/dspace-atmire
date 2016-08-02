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
public class RemoveMetadataTest {
    @Test
    public void getActions() throws Exception {

        String fieldToRemove = "dc.description";
        MetadatumExtended description = new MetadatumExtended(fieldToRemove, "description1");
        MetadatumExtended description2 = new MetadatumExtended(fieldToRemove, "description2");
        MetadatumExtended title = new MetadatumExtended("dc.title", "title");

        List<DCValue> values = new ArrayList<DCValue>();
        values.add(description);
        values.add(description2);
        values.add(title);

        RemoveMetadata removeMetadata = new RemoveMetadata(fieldToRemove);
        Map<ManipulationAction, List<DCValue>> actions = removeMetadata.getActions(null, null, values);

        List<DCValue> toRemove = actions.get(ManipulationAction.REMOVE);
        List<DCValue> toAdd = actions.get(ManipulationAction.ADD);

        assertNotNull(toRemove);
        assertNull(toAdd);

        assertTrue(toRemove.contains(description));
        assertTrue(toRemove.contains(description2));
        assertFalse(toRemove.contains(title));

    }

}