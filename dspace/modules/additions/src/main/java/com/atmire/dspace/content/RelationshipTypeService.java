package com.atmire.dspace.content;

import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 03 Nov 2014
 */
public interface RelationshipTypeService {

    public RelationshipType findById(Context context, int id);

    public RelationshipType findByExampleUnique(Context context, RelationshipType relationshipType);

    public Collection<RelationshipType> findByExample(Context context, RelationshipType relationshipType);

    public void update(Context context, RelationshipType relationshipType) throws SQLException;

    public boolean delete(Context context, RelationshipType relationshipType);

    public RelationshipType create(Context context, RelationshipType relationshipType) throws SQLException;

}
