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
public class CopyMetadataTest {

    @Test
    public void getActions() throws Exception {
        String fromField = "dc.identifier";
        String toField = "vlaanderen.indentifier";

        String value1 = "id1";
        String value2 = "id2";

        MetadatumExtended description = new MetadatumExtended("dc.description", "description");
        MetadatumExtended title = new MetadatumExtended("dc.title", "title");
        MetadatumExtended id1 = new MetadatumExtended(fromField, value1);
        MetadatumExtended id2 = new MetadatumExtended(fromField, value2);

        MetadatumExtended vid1 = new MetadatumExtended(toField, value1);
        MetadatumExtended vid2 = new MetadatumExtended(toField, value2);

        List<DCValue> values = new ArrayList<DCValue>();
        values.add(description);
        values.add(id1);
        values.add(title);
        values.add(id2);

        CopyMetadata copyMetadata = new CopyMetadata(fromField, toField);
        Map<ManipulationAction, List<DCValue>> actions = copyMetadata.getActions(null, null, values);

        List<DCValue> toRemove = actions.get(ManipulationAction.REMOVE);
        List<DCValue> toAdd = actions.get(ManipulationAction.ADD);

        assertNull(toRemove);
        assertNotNull(toAdd);

        assertTrue(toAdd.contains(vid1));
        assertTrue(toAdd.contains(vid2));

        assertFalse(toAdd.contains(id1));
        assertFalse(toAdd.contains(id2));
        assertFalse(toAdd.contains(title));
        assertFalse(toAdd.contains(description));
    }

}