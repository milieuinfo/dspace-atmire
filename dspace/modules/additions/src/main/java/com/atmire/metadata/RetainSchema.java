package com.atmire.metadata;

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
public class RetainSchema implements MetadataManipulation {
    private final String schema;

    public RetainSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public Map<ManipulationAction, List<DCValue>> getActions(Context c, Item item, List<DCValue> originalValues) {
        Map<ManipulationAction, List<DCValue>> actionMap = new HashMap<ManipulationAction, List<DCValue>>();
        List<DCValue> toRemove = new LinkedList<DCValue>();
        for (DCValue originalValue : originalValues) {
            if (!originalValue.schema.equals(this.schema)) {
                toRemove.add(originalValue);
            }
        }
        actionMap.put(ManipulationAction.REMOVE, toRemove);
        return actionMap;
    }
}
