package com.atmire.dspace.content;

import org.dspace.utils.DSpace;

import java.util.Map;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 13 Nov 2014
 */
public class RelationshipObjectServiceFactory {

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
}
