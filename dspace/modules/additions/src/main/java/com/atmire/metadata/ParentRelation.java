package com.atmire.metadata;

import com.atmire.dspace.content.Record;
import com.atmire.dspace.content.Relationship;
import com.atmire.dspace.content.RelationshipObjectService;
import com.atmire.dspace.content.RelationshipObjectServiceFactory;
import com.atmire.util.subclasses.MetadatumExtended;
import org.dspace.content.DCValue;
import org.dspace.content.Item;
import org.dspace.core.Context;

import java.util.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 08 Jul 2016
 */
public class ParentRelation implements MetadataManipulation {

    private final String parentField;
    private final String relationField;

    public ParentRelation(String parentField, String relationField) {
        this.parentField = parentField;
        this.relationField = relationField;
    }

    @Override
    public Map<ManipulationAction, List<DCValue>> getActions(Context c, Item item, List<DCValue> originalValues) {
        Map<ManipulationAction, List<DCValue>> map = new HashMap<ManipulationAction, List<DCValue>>();
        List<DCValue> toAdd = new LinkedList<DCValue>();
        map.put(ManipulationAction.ADD, toAdd);

        RelationshipObjectService<Record> recordService = RelationshipObjectServiceFactory.getInstance().getRelationshipObjectService(Record.class);
        java.util.List<Relationship> parents = findRelationsByItem(null, item, recordService, c);

        if (parents.size() > 0) {
            for (Relationship rls : parents) {
                Item parent = rls.getLeft();
                DCValue[] metadata = parent.getMetadata(parentField);
                for (DCValue dcValue : metadata) {
                    MetadatumExtended newValue = new MetadatumExtended(relationField, dcValue.value);
                    newValue = newValue.filledWithExceptField(dcValue);
                    toAdd.add(newValue);
                }
            }
        }

        return map;
    }

    private java.util.List<Relationship> findRelationsByItem(org.dspace.content.Item left, org.dspace.content.Item right, RelationshipObjectService<Record> recordService, Context context) {
        java.util.List<Relationship> relationList = new ArrayList<Relationship>();
        java.util.Collection<Relationship> relations = recordService.findRelationsByItem(context, left, right);

        for (Relationship relationship : relations) {
            if (relationship != null) {
                relationList.add(relationship);
            }
        }
        return relationList;
    }
}
