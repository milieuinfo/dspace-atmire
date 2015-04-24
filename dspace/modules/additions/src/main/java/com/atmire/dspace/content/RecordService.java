package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Philip Vissenaekens (philip at atmire dot com)
 * Date: 27/02/15
 * Time: 09:59
 */

@Component
public class RecordService extends AbstractRelationshipObjectServiceImpl<Record> {

    @Override
    protected Record findByRelationshipUnique(Context context, Relationship relationship) {
        Relationship foundRelationship = getRelationshipService().findByExampleUnique(context, relationship);
        if (foundRelationship == null) {
            return null;
        }

        Relationship exampleLoopType = new Relationship(null, foundRelationship.getRight(), foundRelationship.getRight(), getRelationshipLoopType(context));
        foundRelationship = getRelationshipService().findByExampleUnique(context, exampleLoopType);

        return newRecordInstance(context, foundRelationship);
    }

    private Record newRecordInstance(Context context, Relationship foundRelationship) {
        Record record = null;
        if (foundRelationship != null) {
            Relationship example = new Relationship(null, foundRelationship.getLeft(), null, getRelationshipType(context));
            Collection<Relationship> relationships = getRelationshipService().findByExample(context, example);
            List<Record> records = new LinkedList<Record>();
            for (Relationship relationship1 : relationships) {
                Record record1 = findByItems(context, relationship1.getLeft(), relationship1.getRight());
                if (record1 != null) {
                    records.add(record1);
                }
            }

            List<Relationship> incoming = Collections.emptyList();
            LinkedList<Relationship> outgoing = new LinkedList<Relationship>(relationships);
            Item item = foundRelationship.getLeft();
            record = new Record(foundRelationship, incoming, outgoing, item, records);
            for (Record record1 : record.getRecords()) {
                record1.setRecord(record);
            }
        }
        return record;
    }


    @Override
    protected RelationshipType getRelationshipType(Context context) {
        return getRelationshipTypeService().findByExampleUnique(context, new RelationshipType(Record.class.getCanonicalName(), Record.class.getCanonicalName()));
    }

    @Override
    protected RelationshipType getRelationshipLoopType(Context context) {
        return getRelationshipTypeService().findByExampleUnique(context, new RelationshipType(Record.class.getCanonicalName(), Record.class.getCanonicalName()));
    }

    public String getRelationLeftLabel(Context context) {
        return getRelationshipType(context).getLeftLabel();
    }

    public String getRelationRightLabel(Context context) {
        return getRelationshipType(context).getRightLabel();
    }

    @Override
    public Record findById(Context context, Integer id) {
        Relationship byId = getRelationshipService().findById(context, id);
        if (byId == null) {
            return null;
        }
        if (byId.getType().getId() != getRelationshipLoopType(context).getId()) {
            throw new IllegalArgumentException("Not a record ID");
        }
        return newRecordInstance(context, byId);
    }

    @Override
    public void update(Context context, Record record) throws RelationshipException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean delete(Context context, Record record) throws RelationshipException {
        Record actual = findByExample(context, record);
        if (actual == null || actual.getRecords().size() > 0) {
            return false;
        }

        Relationship example = new Relationship(null, actual.getItem(), actual.getItem(), getRelationshipLoopType(context));
        Relationship relationship = getRelationshipService().findByExampleUnique(context, example);
        boolean relationshipDeleted = getRelationshipService().delete(context, relationship);
        if (relationshipDeleted) {
            try {
                actual.getItem().withdraw();
            } catch (Exception e) {
                throw new RelationshipException(e);
            }
        }
        return relationshipDeleted;
    }

    @Override
    public Record create(Context context, Record record) throws RelationshipException {
        try {
            if(record.getRecord()!=null){
                getRelationshipService().create(context, record.getRecord().getItem(), record.getItem(), getRelationshipType(context));
            }

            return record;
        } catch (SQLException e) {
            throw new RelationshipException(e);
        }
    }
}
