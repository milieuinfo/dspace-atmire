package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 05 Nov 2014
 */
public abstract class AbstractRelationshipObjectServiceImpl<T extends RelationShipObject> implements RelationshipObjectService<T> {

	private RelationshipService relationshipService;

	@Override
	public T findByExample(Context context, T t) {
		return findByItem(context,t.getItem());
	}




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

	private RelationshipTypeService relationshipTypeService;

	protected  abstract T findByRelationshipUnique(Context context, Relationship relationship);



	protected abstract RelationshipType getRelationshipType(Context context);
	protected abstract RelationshipType getRelationshipLoopType(Context context);

	@Override
	public T findByItems(Context context, Item left, Item right) {
		return findByRelationshipUnique(context, relationshipService.findByExampleUnique(context, new Relationship(null, left, right, getRelationshipType(context))));
	}

	@Override
	public T findByItem(Context context, Item item) {

		Relationship byExampleUnique = relationshipService.findByExampleUnique(context, new Relationship(null, item, item, getRelationshipLoopType(context)));

		return findByRelationshipUnique(context,byExampleUnique);
	}


}
