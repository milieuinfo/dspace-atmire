package com.atmire.dspace.content;

import org.apache.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;

import java.util.List;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 04 Nov 2014
 */
public abstract class RelationShipObject extends DSpaceObject {


    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(RelationShipObject.class);

    /**
     * The loop relationship describing this relationship object
     * left item == right item
     */
    private final Relationship coreRelationship;

    /**
     * List of relationships where this object's item is the right item
     */
    private final List<Relationship> incoming;

    /**
     * List of relationships where this object's item is the left item
     */
    private final List<Relationship> outgoing;

    /**
     * The item belonging to this relationship object.
     */
    private Item item;

    protected RelationShipObject(Relationship coreRelationship, List<Relationship> incoming, List<Relationship> outgoing, Item item) {
        this.coreRelationship = coreRelationship;
        this.item = item;
        this.incoming = incoming;
        this.outgoing = outgoing;
    }

    protected List<Relationship> getIncoming() {
        return incoming;
    }

    protected List<Relationship> getOutgoing() {
        return outgoing;
    }

    protected Relationship getCoreRelationship() {
        return coreRelationship;
    }

    @Override
    public String getHandle() {
        return getItem().getHandle();
    }

    @Override
    public final int getID() {
        return coreRelationship.getId();
    }

    public final Item getItem() {
        return item;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelationShipObject)) {
            return false;
        }

        RelationShipObject that = (RelationShipObject) o;

        if (getType() != that.getType()) {
            return false;
        }

        if (item == null ? that.item != null : item.getID() != that.item.getID()) {
            return false;
        }

        if (coreRelationship == null ? that.coreRelationship != null : !coreRelationship.getId().equals(that.coreRelationship.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = coreRelationship != null ? coreRelationship.getId() : 0;
        result = 31 * result + getType();
        result = 31 * result + (item != null ? item.getID() : 0);
        return result;
    }
}
