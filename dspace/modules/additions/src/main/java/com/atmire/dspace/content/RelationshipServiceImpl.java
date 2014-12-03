package com.atmire.dspace.content;

import com.atmire.util.WhereConstruct;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 04 Nov 2014
 */
public class RelationshipServiceImpl implements RelationshipService {


    private final String RELATIONSHIP_TABLE = "Relationship";
    private final List<String> RELATIONSHIP_TABLE_COLUMNS = Arrays.asList("relationship_id", "left_id", "type_id", "right_id");

    private RelationshipTypeService relationshipTypeService;

    @Autowired
    public void setRelationshipTypeService(RelationshipTypeService relationshipTypeService) {
        this.relationshipTypeService = relationshipTypeService;
    }

    protected RelationshipTypeService getRelationshipTypeService() {
        return this.relationshipTypeService;
    }

    @Override
    public Relationship findById(Context context, int id) {
        Relationship relationship = null;
        try {
            TableRow row = DatabaseManager.find(context, RELATIONSHIP_TABLE, id);
            relationship = makeRelationship(context, row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return relationship;
    }

    @Override
    public Relationship findByExampleUnique(Context context, Relationship example) {
        Collection<Relationship> byExample = findByExample(context, example);
        Iterator<Relationship> iterator = byExample.iterator();
        Relationship type = null;
        if (iterator.hasNext()) {
            type = iterator.next();
        }
        if (iterator.hasNext()) {
            throw new IllegalArgumentException("The results for provided argument is not unique");
        }
        return type;
    }

    @Override
    public Collection<Relationship> findByExample(Context context, Relationship example) {
        Collection<Relationship> results = new LinkedList<Relationship>();

        String myQuery = "SELECT * FROM " + RELATIONSHIP_TABLE;
        WhereConstruct where = new WhereConstruct();

        if (example.getId() != null) {
            where.addEqualField("relationship_id", example.getId());
        }
        if (example.getLeft() != null) {
            where.addEqualField("left_id", example.getLeft().getID());
        }
        if (example.getType() != null) {
            where.addEqualField("type_id", example.getType().getId());
        }
        if (example.getRight() != null) {
            where.addEqualField("right_id", example.getRight().getID());
        }

        myQuery += " " + where;

        try {
            TableRowIterator rows = DatabaseManager.queryTable(context, RELATIONSHIP_TABLE, myQuery);
            while (rows.hasNext()) {
                TableRow tableRow = rows.next();
                Relationship type = makeRelationship(context, tableRow);
                results.add(type);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    @Override
    public void update(Context context, Relationship relationship) throws SQLException {
        TableRow row = makeTableRow(relationship);
        DatabaseManager.update(context, row);
    }

    @Override
    public boolean delete(Context context, Relationship relationship) {
        boolean deleted = false;
        if (relationship != null) {
            TableRow row = makeTableRow(relationship);
            try {
                int affectRows = DatabaseManager.delete(context, row);
                deleted = affectRows > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return deleted;
    }

    @Override
    public Relationship create(Context context, Relationship relationship) throws SQLException {
        TableRow row = DatabaseManager.create(context, RELATIONSHIP_TABLE);
        int id = row.getIntColumn("relationship_id");
        row = makeTableRow(relationship, row);
        row.setColumn("relationship_id", id);
        DatabaseManager.update(context, row);
        return makeRelationship(context, row);
    }

    @Override
    public Relationship create(Context context, Item left, Item right, RelationshipType type) throws SQLException {
        return create(context, new Relationship(null, left, right, type));
    }

    @Override
    public Relationship findByItems(Context context, Item left, Item right, RelationshipType type) {
        return findByExampleUnique(context, new Relationship(null, left, right, type));
    }

    private Relationship makeRelationship(Context context, TableRow row) throws SQLException {
        Relationship relationship = null;
        if (row != null) {
            relationship = new Relationship();
            int relationship_id = row.getIntColumn("relationship_id");
            int left_id = row.getIntColumn("left_id");
            int type_id = row.getIntColumn("type_id");
            int right_id = row.getIntColumn("right_id");

            relationship.setId(relationship_id);

                RelationshipType type = getRelationshipTypeService().findById(context, type_id);
                relationship.setType(type);

            Item leftItem = Item.find(context, left_id);
            Item rightItem = Item.find(context, right_id);

            relationship.setLeft(leftItem);
            relationship.setRight(rightItem);
        }
        return relationship;
    }

    private TableRow makeTableRow(Relationship relationship) {
        TableRow row = new TableRow(RELATIONSHIP_TABLE, RELATIONSHIP_TABLE_COLUMNS);
        return makeTableRow(relationship, row);
    }

    private TableRow makeTableRow(Relationship relationship, TableRow row) {
        if(relationship.getId()!=null) {
            row.setColumn("relationship_id", relationship.getId());
        }
        row.setColumn("left_id", relationship.getLeft().getID());
        row.setColumn("type_id", relationship.getType().getId());
        row.setColumn("right_id", relationship.getRight().getID());
        return row;
    }
}
