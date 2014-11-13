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


	private final Relationship coreRelationship;
	private final List<Relationship> incoming;
	private final List<Relationship> outgoing;

	protected List<Relationship> getIncoming() {
		return incoming;
	}

	protected List<Relationship> getOutgoing() {
		return outgoing;
	}

	protected Relationship getCoreRelationship(){
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

	protected RelationShipObject(Relationship coreRelationship,List<Relationship> incoming,List<Relationship> outgoing ,Item item) {
		this.coreRelationship=coreRelationship;
		this.item = item;
		this.incoming = incoming;
		this.outgoing = outgoing;
	}


	public final Item getItem(){
		return item;
	}

	/**
	 * log4j logger
	 */
	private static Logger log = Logger.getLogger(RelationShipObject.class);

	private Item item;




	@Override
	protected Logger getLogger() {
		return log;
	}
}
