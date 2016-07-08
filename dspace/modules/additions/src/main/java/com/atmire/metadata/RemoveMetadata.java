package com.atmire.metadata;

import com.atmire.util.helper.MetadataFieldString;
import com.atmire.util.subclasses.MetadatumExtended;
import org.dspace.content.DCValue;
import org.dspace.content.Item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public class RemoveMetadata implements MetadataManipulation {
    private final String field;

    public RemoveMetadata(String field) {
        this.field = field;
    }

    @Override
    public Map<ManipulationAction, List<DCValue>> getActions(Item item, List<DCValue> originalValues) {
        Map<ManipulationAction, List<DCValue>> map = new HashMap<ManipulationAction, List<DCValue>>();
        List<DCValue> toRemove = new LinkedList<DCValue>();
        MetadatumExtended fieldToRemove = MetadataFieldString.encapsulate(field);
        for (DCValue originalValue : originalValues) {
            if (fieldToRemove.hasSameFieldAs(originalValue)) {
                toRemove.add(originalValue);
            }
        }
        map.put(ManipulationAction.REMOVE, toRemove);
        return map;
    }
}
