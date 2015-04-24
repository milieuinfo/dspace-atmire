package com.atmire.dspace.content;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philip Vissenaekens (philip at atmire dot com)
 * Date: 27/02/15
 * Time: 09:52
 */
public class Record extends RelationShipObject {

    private Record record;

    private List<Record> records;

    public Record(Item item, List<Record> records) {
        this(null, null, null, item, records);
    }

    public Record(Item item) {
        this(null, null, null, item, null);
    }

    protected Record(Relationship coreRelationship, List<Relationship> incoming, List<Relationship> outgoing, Item item, List<Record> records) {
        super(coreRelationship, incoming, outgoing, item);
        this.records = records;
    }

    public List<Record> getRecords() {
        if(records==null){
            records = new ArrayList<Record>();
        }
        return records;
    }


    @Override
    public int getType() {
        return Constants.RECORD;
    }


    @Override
    public String getName() {
        return getItem().getName();
    }

    @Override
    protected Context getContext() {
        return null;
    }


    @Override
    public void update() throws SQLException, AuthorizeException {

    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public void addRecord(Record record){
        if(records==null){
            records = new ArrayList<Record>();
        }

        records.add(record);
    }

    @Override
    public void updateLastModified() {

    }
}
