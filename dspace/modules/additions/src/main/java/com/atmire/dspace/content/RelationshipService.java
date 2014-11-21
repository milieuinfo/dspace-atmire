package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 04 Nov 2014
 */
public interface RelationshipService {

    public Relationship findById(Context context, int id);

    public Relationship findByExampleUnique(Context context, Relationship example);

    public Collection<Relationship> findByExample(Context context, Relationship example);

    public void update(Context context, Relationship relationship) throws SQLException;

    public boolean delete(Context context, Relationship relationship);

    public Relationship create(Context context, Relationship relationship) throws SQLException;

	public Relationship create(Context context, Item left, Item right, RelationshipType type) throws SQLException;

	public Relationship findByItems(Context context, Item left, Item right, RelationshipType type);

}
