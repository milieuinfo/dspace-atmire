package com.atmire.dspace.content;

import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 13 Nov 2014
 */
public class RelationshipObjectServiceFactory {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(RelationshipObjectServiceFactory.class);

    /**
     * The mapping of relationship object classes to their respective services
     */
    private Map<Class, RelationshipObjectService<? extends RelationShipObject>> mapping;

    public static RelationshipObjectServiceFactory getInstance() {
        return new DSpace().getServiceManager().getServiceByName("relationshipObjectServiceFactory", RelationshipObjectServiceFactory.class);
    }

    @SuppressWarnings("unchecked") // not a problem if configured correctly
    public <T extends RelationShipObject> RelationshipObjectService<T> getRelationshipObjectService(Class<T> type) {
        return (RelationshipObjectService<T>) mapping.get(type);
    }

    public Map<Class, RelationshipObjectService<? extends RelationShipObject>> getMapping() {
        return mapping;
    }

    public void setMapping(Map<Class, RelationshipObjectService<? extends RelationShipObject>> mapping) {
        this.mapping = mapping;
    }

    /**
     * Returns a set of classes that can be used on getRelationshipObjectService(Class<T> type)
     * where the resulting RelationshipObjectService's findByItem(context,item) will provide a non null RelationShipObject
     */
    public Set<Class<? extends RelationShipObject>> getRelationshipObjectClassesForItem(Context context, Item item) {
        Set<Class<? extends RelationShipObject>> classes = new HashSet<Class<? extends RelationShipObject>>();

        if(item != null) {

            // Get all loop types
            RelationshipTypeService relationshipTypeService = new DSpace().getServiceManager().getServiceByName("relationshipTypeService", RelationshipTypeService.class);
            Collection<RelationshipType> allRelationShipTypes = relationshipTypeService.findByExample(context, new RelationshipType());
            Set<RelationshipType> loopTypes = new HashSet<RelationshipType>();
            for (RelationshipType relationShipType : allRelationShipTypes) {
                if (relationShipType.getLeftType().equals(relationShipType.getRightType())) {
                    loopTypes.add(relationShipType);
                }
            }

            // See for which loop types a relationship exists with this item
            RelationshipService relationshipService = new DSpace().getServiceManager().getServiceByName("relationshipService", RelationshipService.class);
            for (RelationshipType loopType : loopTypes) {

                Relationship example = new Relationship(null, item, item, loopType);
                Relationship relationship = relationshipService.findByExampleUnique(context, example);
                if (relationship != null) {
                    try {
                        String leftType = loopType.getLeftType();
                        Class<? extends RelationShipObject> theClass = (Class<? extends RelationShipObject>) Class.forName(leftType);
                        classes.add(theClass);
                    } catch (ClassNotFoundException e) {
                        log.error("Error", e);
                    }
                }
            }

        }

        return classes;
    }
}
