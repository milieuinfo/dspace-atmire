package com.atmire.metadata;

import org.dspace.content.DCValue;
import org.dspace.content.Item;
import org.dspace.utils.DSpace;

import java.util.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public class MetadataManipulator {

    private final List<MetadataManipulation> manipulations;

    public MetadataManipulator(List<MetadataManipulation> manipulations) {
        this.manipulations = manipulations;
    }

    public MetadataManipulator(String beanName) {
        MetadataManipulator manipulator = new DSpace().getServiceManager()
                .getServiceByName(beanName, MetadataManipulator.class);
        if (manipulator != null) {
            this.manipulations = manipulator.manipulations;
        } else {
            this.manipulations = Collections.emptyList();
        }
    }

    public List<DCValue> manipulateMetadata(Item item, List<DCValue> originalValues) {
        Map<ManipulationAction, List<DCValue>> actions = new HashMap<ManipulationAction, List<DCValue>>();
        for (MetadataManipulation manipulation : manipulations) {
            Map<ManipulationAction, List<DCValue>> manipulationActions = manipulation.getActions(item, originalValues);
            actions.putAll(manipulationActions);
        }

        List<DCValue> newValues = new ArrayList<DCValue>(originalValues);
        for (ManipulationAction action : ManipulationAction.values()) {
            List<DCValue> dcValues = actions.get(action);
            action.act(newValues, dcValues);
        }
        return newValues;
    }
}
