package com.atmire.metadata;

import org.dspace.content.DCValue;
import org.dspace.content.Item;
import org.dspace.core.Context;

import java.util.List;
import java.util.Map;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public interface MetadataManipulation {
    Map<ManipulationAction,List<DCValue>> getActions(Context c, Item item, List<DCValue> originalValues);
}
