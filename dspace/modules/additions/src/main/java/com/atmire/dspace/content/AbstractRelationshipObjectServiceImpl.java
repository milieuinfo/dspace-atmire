package com.atmire.dspace.content;

import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 05 Nov 2014
 */
public abstract class AbstractRelationshipObjectServiceImpl<T extends RelationShipObject> implements RelationshipObjectService<T> {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(AbstractRelationshipObjectServiceImpl.class);
    private RelationshipService relationshipService;
    private RelationshipTypeService relationshipTypeService;

    protected RelationshipService getRelationshipService() {
        return relationshipService;
    }

    @Autowired
    public void setRelationshipService(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    protected RelationshipTypeService getRelationshipTypeService() {
        return relationshipTypeService;
    }

    @Autowired
    public void setRelationshipTypeService(RelationshipTypeService relationshipTypeService) {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Override
    public T findByExample(Context context, T t){
        return findByItem(context, t.getItem());
    }

    protected abstract T findByRelationshipUnique(Context context, Relationship relationship);

    protected abstract RelationshipType getRelationshipType(Context context);

    protected abstract RelationshipType getRelationshipLoopType(Context context);

    @Override
    public T findByItems(Context context, Item left, Item right) {
        Relationship example = new Relationship(null, left, right, getRelationshipType(context));
        Relationship relationship = relationshipService.findByExampleUnique(context, example);
        return findByRelationshipUnique(context, relationship);
    }

    @Override
    public T findByItem(Context context, Item item) {
        Relationship example = new Relationship(null, item, item, getRelationshipLoopType(context));
        Relationship relationship = relationshipService.findByExampleUnique(context, example);
        T uniqueRelations = null;
        if(relationship!=null){
            uniqueRelations = findByRelationshipUnique(context, relationship);
        }
        return uniqueRelations;
    }

    @Override
    public Collection<Relationship> findRelationsByItem(Context context, Item leftItem, Item rightItem) {
        Relationship example = new Relationship(null, leftItem, rightItem, getRelationshipLoopType(context));
        Collection<Relationship> relationships = relationshipService.findByExample(context, example);
        return relationships;
    }


    @Override
    public List<RelationShipObject> getIncomingRelationshipObjects(Context context, RelationShipObject relationShipObject) {
        List<RelationShipObject> incomingRelationshipObjects = new LinkedList<RelationShipObject>();
        Item right = relationShipObject.getItem();
        List<Relationship> relationships = relationShipObject.getIncoming();
        for (Relationship relationship : relationships) {
            Item left = relationship.getLeft();
            Relationship found = relationshipService.findByItems(context, left, right, null);
            String type = found.getType().getLeftType();
            RelationShipObject incomingRelationshipObject = findRelationshipObjectForType(context, left, type);
            if (incomingRelationshipObject != null) {
                incomingRelationshipObjects.add(incomingRelationshipObject);
            }
        }
        return incomingRelationshipObjects;
    }

    @Override
    public List<RelationShipObject> getOutgoingRelationshipObjects(Context context, RelationShipObject relationShipObject){
        List<RelationShipObject> outgoingRelationshipObjects = new LinkedList<RelationShipObject>();
        Item left = relationShipObject.getItem();
        List<Relationship> relationships = relationShipObject.getOutgoing();
        for (Relationship relationship : relationships) {
            Item right = relationship.getRight();
            Relationship found = relationshipService.findByItems(context, left, right, null);
            String type = found.getType().getRightType();
            RelationShipObject outgoingRelationshipObject = findRelationshipObjectForType(context, right, type);
            if (outgoingRelationshipObject != null) {
                outgoingRelationshipObjects.add(outgoingRelationshipObject);
            }
        }
        return outgoingRelationshipObjects;
    }

    private RelationShipObject findRelationshipObjectForType(Context context, Item item, String type) {
        RelationShipObject incomingRelationshipObject = null;
        try {
            Class<? extends RelationShipObject> aClass = (Class<? extends RelationShipObject>) Class.forName(type);
            RelationshipObjectService<? extends RelationShipObject> relationshipObjectService = RelationshipObjectServiceFactory.getInstance().getRelationshipObjectService(aClass);
            incomingRelationshipObject = relationshipObjectService.findByItem(context, item);
        } catch (ClassNotFoundException e) {
            log.error("RelationshipObject's type cannot be converted to a class", e);
        } catch (ClassCastException e) {
            log.error("RelationshipObject's type cannot be converted to a class extending RelationshipObject", e);
        }
        return incomingRelationshipObject;
    }
}
