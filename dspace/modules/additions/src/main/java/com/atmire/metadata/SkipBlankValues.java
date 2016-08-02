package com.atmire.metadata;

import org.apache.commons.lang3.StringUtils;
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
public class SkipBlankValues implements MetadataManipulation {

    @Override
    public Map<ManipulationAction, List<DCValue>> getActions(Context c, Item item, List<DCValue> originalValues) {
        Map<ManipulationAction, List<DCValue>> map = new HashMap<ManipulationAction, List<DCValue>>();
        List<DCValue> toRemove = new LinkedList<DCValue>();
        for (DCValue originalValue : originalValues) {
            if (StringUtils.isBlank(originalValue.value)) {
                toRemove.add(originalValue);
            }
        }
        map.put(ManipulationAction.REMOVE, toRemove);
        return map;
    }
}
