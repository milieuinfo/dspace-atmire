package com.atmire.metadata;

import com.atmire.util.helper.MetadataFieldString;
import com.atmire.util.subclasses.MetadatumExtended;
import org.dspace.content.DCValue;
import org.dspace.content.Item;
import org.dspace.core.Context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public class CopyMetadata implements MetadataManipulation {
    private final String from;
    private final String to;

    public CopyMetadata(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Map<ManipulationAction, List<DCValue>> getActions(Context c, Item item, List<DCValue> originalValues) {
        Map<ManipulationAction, List<DCValue>> map = new HashMap<ManipulationAction, List<DCValue>>();
        List<DCValue> toAdd = new LinkedList<DCValue>();
        MetadatumExtended fromField = MetadataFieldString.encapsulate(from);
        for (DCValue originalValue : originalValues) {
            if (fromField.hasSameFieldAs(originalValue)) {
                MetadatumExtended newValue = MetadataFieldString.encapsulate(to);
                newValue = newValue.filledWithExceptField(originalValue);
                toAdd.add(newValue);
            }
        }
        map.put(ManipulationAction.ADD, toAdd);
        return map;
    }
}
