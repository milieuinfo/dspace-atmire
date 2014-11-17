package com.atmire.dspace.content;

import com.atmire.util.WhereConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 03 Nov 2014
 */
public class RelationshipTypeServiceImpl implements RelationshipTypeService {

    private final String TYPE_TABLE = "Type";
    private final List<String> TYPE_TABLE_COLUMNS = Arrays.asList("type_id", "left_type", "right_type", "left_label", "right_label", "left_min_cardinality", "left_max_cardinality", "right_min_cardinality", "right_max_cardinality", "semantic_ruleset");

    @Override
    public RelationshipType findById(Context context, int id) {
        RelationshipType relationshipType = null;
        try {
            TableRow row = DatabaseManager.find(context, TYPE_TABLE, id);
            relationshipType = makeRelationshipType(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return relationshipType;
    }

    @Override
    public RelationshipType findByExampleUnique(Context context, RelationshipType relationshipType) {
        Collection<RelationshipType> byExample = findByExample(context, relationshipType);
        Iterator<RelationshipType> iterator = byExample.iterator();
        RelationshipType type = null;
        if (iterator.hasNext()) {
            type = iterator.next();
        }
        if (iterator.hasNext()) {
            throw new IllegalArgumentException("The results for provided argument is not unique");
        }
        return type;
    }

    @Override
    public Collection<RelationshipType> findByExample(Context context, RelationshipType relationshipType) {
        Collection<RelationshipType> results = new LinkedList<RelationshipType>();

        String myQuery = "SELECT * FROM " + TYPE_TABLE;
        WhereConstruct where = new WhereConstruct();
        if (relationshipType.getId() != 0) {
            where.addEqualField("type_id", relationshipType.getId());
        }
        if (StringUtils.isNotBlank(relationshipType.getLeftLabel())) {
            where.addEqualField("left_label", relationshipType.getLeftLabel());
        }
        if (StringUtils.isNotBlank(relationshipType.getRightLabel())) {
            where.addEqualField("right_label", relationshipType.getRightLabel());
        }
        if (StringUtils.isNotBlank(relationshipType.getLeftType())) {
            where.addEqualField("left_type", relationshipType.getLeftType());
        }
        if (StringUtils.isNotBlank(relationshipType.getRightType())) {
            where.addEqualField("right_type", relationshipType.getRightType());
        }
        Pair<Integer, Integer> leftCardinality = relationshipType.getLeftCardinality();
        if (leftCardinality != null) {
            if(leftCardinality.getLeft()!=null) {
                where.addEqualField("left_min_cardinality", leftCardinality.getLeft());
            }
            if(leftCardinality.getRight()!=null) {
                where.addEqualField("left_max_cardinality", leftCardinality.getRight());
            }
        }
        Pair<Integer, Integer> rightCardinality = relationshipType.getRightCardinality();
        if (rightCardinality != null) {
            if(rightCardinality.getLeft()!=null) {
                where.addEqualField("right_min_cardinality", rightCardinality.getLeft());
            }
            if(rightCardinality.getRight()!=null) {
                where.addEqualField("right_max_cardinality", rightCardinality.getRight());
            }
        }
        if (StringUtils.isNotBlank(relationshipType.getSemanticRuleset())) {
            where.addEqualField("semantic_ruleset", relationshipType.getSemanticRuleset());
        }

        myQuery += " " + where;

        try {
            TableRowIterator rows = DatabaseManager.queryTable(context, TYPE_TABLE, myQuery);
            while (rows.hasNext()) {
                TableRow tableRow = rows.next();
                RelationshipType type = makeRelationshipType(tableRow);
                results.add(type);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    @Override
    public void update(Context context, RelationshipType relationshipType) throws SQLException {
        TableRow row = makeTableRow(relationshipType);
        DatabaseManager.update(context, row);
    }

    @Override
    public boolean delete(Context context, RelationshipType relationshipType) {
        boolean deleted = false;
        TableRow row = makeTableRow(relationshipType);
        try {
            int affectRows = DatabaseManager.delete(context, row);
            deleted = affectRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deleted;
    }

    @Override
    public RelationshipType create(Context context, RelationshipType relationshipType) throws SQLException {
        TableRow row = DatabaseManager.create(context, TYPE_TABLE);
        int id = row.getIntColumn("type_id");
        row = makeTableRow(relationshipType, row);
        row.setColumn("type_id", id);
        DatabaseManager.update(context, row);
        return makeRelationshipType(row);
    }

    private RelationshipType makeRelationshipType(TableRow row) {
        RelationshipType relationshipType = null;
        if (row != null) {
            relationshipType = new RelationshipType();
            relationshipType.setId(row.getIntColumn("type_id"));
            relationshipType.setLeftType(row.getStringColumn("left_type"));
            relationshipType.setRightType(row.getStringColumn("right_type"));
            relationshipType.setLeftLabel(row.getStringColumn("left_label"));
            relationshipType.setRightLabel(row.getStringColumn("right_label"));
            Pair<Integer, Integer> leftCardinality = new ImmutablePair<Integer, Integer>(row.getIntColumn("left_min_cardinality"), row.getIntColumn("left_max_cardinality"));
            relationshipType.setLeftCardinality(leftCardinality);
            Pair<Integer, Integer> rightCardinality = new ImmutablePair<Integer, Integer>(row.getIntColumn("right_min_cardinality"), row.getIntColumn("right_max_cardinality"));
            relationshipType.setRightCardinality(rightCardinality);
            relationshipType.setSemanticRuleset(row.getStringColumn("semantic_ruleset"));
        }
        return relationshipType;
    }

    private TableRow makeTableRow(RelationshipType relationshipType) {
        TableRow row = new TableRow(TYPE_TABLE, TYPE_TABLE_COLUMNS);
        return makeTableRow(relationshipType, row);
    }

    private TableRow makeTableRow(RelationshipType relationshipType, TableRow row) {
        row.setColumn("type_id", relationshipType.getId());
        row.setColumn("left_type", relationshipType.getLeftType());
        row.setColumn("right_type", relationshipType.getRightType());
        row.setColumn("left_label", relationshipType.getLeftLabel());
        row.setColumn("right_label", relationshipType.getRightLabel());
        Pair<Integer, Integer> leftCardinality = relationshipType.getLeftCardinality();
        if(leftCardinality !=null) {
            if(leftCardinality.getLeft()!=null) {
                row.setColumn("left_min_cardinality", leftCardinality.getLeft());
            }
            if(leftCardinality.getRight()!=null) {
                row.setColumn("left_max_cardinality", leftCardinality.getRight());
            }
        }
        Pair<Integer, Integer> rightCardinality = relationshipType.getRightCardinality();
        if(rightCardinality !=null) {
            if(rightCardinality.getLeft()!=null) {
                row.setColumn("right_min_cardinality", rightCardinality.getLeft());
            }
            if(rightCardinality.getRight()!=null) {
                row.setColumn("right_max_cardinality", rightCardinality.getRight());
            }
        }
        row.setColumn("semantic_ruleset", relationshipType.getSemanticRuleset());
        return row;
    }
}
