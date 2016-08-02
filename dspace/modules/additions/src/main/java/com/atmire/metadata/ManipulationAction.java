package com.atmire.metadata;

import org.dspace.content.DCValue;

import java.util.List;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public enum ManipulationAction {
    ADD, REMOVE;

    public void act(List<DCValue> newValues, List<DCValue> dcValues) {
        if (newValues != null && dcValues != null) {
            switch (this) {
                case ADD:
                    newValues.addAll(dcValues);
                    break;
                case REMOVE:
                    newValues.removeAll(dcValues);
                    break;
            }
        }
    }
}
