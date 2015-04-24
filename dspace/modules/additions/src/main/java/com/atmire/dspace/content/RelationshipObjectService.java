package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;

import java.util.Collection;
import java.util.List;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 04 Nov 2014
 */
public interface RelationshipObjectService<T extends RelationShipObject> {

    public T findByItem(Context context, Item item);

    public Collection<Relationship> findRelationsByItem(Context context,Item leftItem, Item rightItem);

    public T findByItems(Context context, Item left, Item right);

    public T findById(Context context, Integer id);

    public T findByExample(Context context, T t);

    public void update(Context context, T t) throws RelationshipException;

    public boolean delete(Context context, T t) throws RelationshipException;

    public T create(Context context, T t) throws RelationshipException;

    public List<RelationShipObject> getIncomingRelationshipObjects(Context context, RelationShipObject relationShipObject);

    public List<RelationShipObject> getOutgoingRelationshipObjects(Context context, RelationShipObject relationShipObject);

}
