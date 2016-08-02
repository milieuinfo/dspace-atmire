package com.atmire.metadata;

import com.atmire.util.subclasses.MetadatumExtended;
import org.dspace.content.DCValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public class MetadataManipulatorTest {

    @Test
    public void manipulateMetadata() throws Exception {

        String fromField = "dc.identifier";
        String toField = "vlaanderen.indentifier";

        String value1 = "id1";
        String value2 = "id2";

        MetadatumExtended description = new MetadatumExtended("dc.description", "description");
        MetadatumExtended title = new MetadatumExtended("dc.title", "title");
        MetadatumExtended id1 = new MetadatumExtended(fromField, value1);
        MetadatumExtended id2 = new MetadatumExtended(fromField, value2);
        MetadatumExtended vlaanderenValue = new MetadatumExtended("vlaanderen.title", "title");

        MetadatumExtended vid1 = new MetadatumExtended(toField, value1);
        MetadatumExtended vid2 = new MetadatumExtended(toField, value2);

        List<MetadataManipulation> manipulations = new LinkedList<MetadataManipulation>();
        manipulations.add(new RetainSchema("vlaanderen"));
        manipulations.add(new CopyMetadata(fromField, toField));
        manipulations.add(new SkipBlankValues());

        List<DCValue> values = new ArrayList<DCValue>();
        values.add(description);
        values.add(id1);
        values.add(title);
        values.add(id2);
        values.add(vlaanderenValue);

        MetadataManipulator manipulator = new MetadataManipulator(manipulations);
        List<DCValue> dcValues = manipulator.manipulateMetadata(null, null, values);

        assertFalse(dcValues.contains(id1));
        assertFalse(dcValues.contains(id2));
        assertFalse(dcValues.contains(title));
        assertFalse(dcValues.contains(description));

        assertTrue(dcValues.contains(vid1));
        assertTrue(dcValues.contains(vid2));
        assertTrue(dcValues.contains(vlaanderenValue));

    }

}