package com.atmire.dspace.content;

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
    private final List<String> TYPE_TABLE_COLUMNS = Arrays.asList("type_id", "left_item_type", "right_item_type", "left_item_label", "right_item_label", "left_item_min_cardinality", "left_item_max_cardinality", "right_item_min_cardinality", "right_item_max_cardinality", "semantic_ruleset");

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
        List<String> wheres = new LinkedList<String>();
        if (relationshipType.getId() != 0) {
            wheres.add("type_id='" + relationshipType.getId() + "'");
        }
        if (StringUtils.isNotBlank(relationshipType.getLeftLabel())) {
            wheres.add("left_item_label='" + relationshipType.getLeftLabel() + "'");
        }
        if (StringUtils.isNotBlank(relationshipType.getRightLabel())) {
            wheres.add("right_item_label='" + relationshipType.getRightLabel() + "'");
        }
        if (StringUtils.isNotBlank(relationshipType.getLeftType())) {
            wheres.add("left_item_type='" + relationshipType.getLeftType() + "'");
        }
        if (StringUtils.isNotBlank(relationshipType.getRightType())) {
            wheres.add("right_item_type='" + relationshipType.getRightType() + "'");
        }
        if (relationshipType.getLeftCardinality() != null) {
            wheres.add("left_item_min_cardinality='" + relationshipType.getLeftCardinality().getLeft() + "'");
            wheres.add("left_item_max_cardinality='" + relationshipType.getLeftCardinality().getRight() + "'");
        }
        if (relationshipType.getRightCardinality() != null) {
            wheres.add("right_item_min_cardinality='" + relationshipType.getRightCardinality().getLeft() + "'");
            wheres.add("right_item_max_cardinality='" + relationshipType.getRightCardinality().getRight() + "'");
        }
        if (StringUtils.isNotBlank(relationshipType.getSemanticRuleset())) {
            wheres.add("semantic_ruleset='" + relationshipType.getSemanticRuleset() + "'");
        }

        String whereClause = "";
        if (!wheres.isEmpty()) {
            for (String where : wheres) {
                if (StringUtils.isNotBlank(whereClause)) {
                    whereClause += " AND ";
                }
                whereClause += where.trim();
            }
            myQuery += " WHERE " + whereClause;
        }

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
    public void delete(Context context, RelationshipType relationshipType) {
        TableRow row = makeTableRow(relationshipType);
        try {
            DatabaseManager.delete(context, row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RelationshipType create(Context context, RelationshipType relationshipType) throws SQLException {
        TableRow row = DatabaseManager.create(context, TYPE_TABLE);
        int id = row.getIntColumn("type_id");
        row = makeTableRow(relationshipType, row);
        row.setColumn("type_id", id);
        DatabaseManager.update(context, row);
        RelationshipType relationshipTypeResult = makeRelationshipType(row);
        relationshipTypeResult.setId(id);
        return relationshipTypeResult;
    }

    private RelationshipType makeRelationshipType(TableRow row) {
        RelationshipType relationshipType = null;
        if (row != null) {
            relationshipType = new RelationshipType();
            relationshipType.setId(row.getIntColumn("type_id"));
            relationshipType.setLeftType(row.getStringColumn("left_item_type"));
            relationshipType.setRightType(row.getStringColumn("right_item_type"));
            relationshipType.setLeftLabel(row.getStringColumn("left_item_label"));
            relationshipType.setRightLabel(row.getStringColumn("right_item_label"));
            Pair<Integer, Integer> leftCardinality = new ImmutablePair<Integer, Integer>(row.getIntColumn("left_item_min_cardinality"), row.getIntColumn("left_item_max_cardinality"));
            relationshipType.setLeftCardinality(leftCardinality);
            Pair<Integer, Integer> rightCardinality = new ImmutablePair<Integer, Integer>(row.getIntColumn("right_item_min_cardinality"), row.getIntColumn("right_item_max_cardinality"));
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
        row.setColumn("left_item_type", relationshipType.getLeftType());
        row.setColumn("right_item_type", relationshipType.getRightType());
        row.setColumn("left_item_label", relationshipType.getLeftLabel());
        row.setColumn("right_item_label", relationshipType.getRightLabel());
        row.setColumn("left_item_min_cardinality", relationshipType.getLeftCardinality().getLeft());
        row.setColumn("left_item_max_cardinality", relationshipType.getLeftCardinality().getRight());
        row.setColumn("right_item_min_cardinality", relationshipType.getRightCardinality().getLeft());
        row.setColumn("right_item_max_cardinality", relationshipType.getRightCardinality().getRight());
        row.setColumn("semantic_ruleset", relationshipType.getSemanticRuleset());
        return row;
    }
}
