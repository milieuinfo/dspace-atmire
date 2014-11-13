package com.atmire.dspace.content;

import edu.emory.mathcs.backport.java.util.Collections;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 05 Nov 2014
 */
@Component
public class DossierService extends AbstractRelationshipObjectServiceImpl<Dossier> {


	private RelationshipObjectService<Document> documentRelationshipObjectService;

	public RelationshipObjectService<Document> getDocumentRelationshipObjectService() {
		return documentRelationshipObjectService;
	}

	@Autowired
	public void setDocumentRelationshipObjectService(RelationshipObjectService<Document> documentRelationshipObjectService) {
		this.documentRelationshipObjectService = documentRelationshipObjectService;
	}

	@Override
	protected Dossier findByRelationshipUnique(Context context, Relationship relationship) {
		Relationship rs = getRelationshipService().findByExampleUnique(context, relationship);
		if (rs == null) return null;
		rs=getRelationshipService().findByExampleUnique(context,new Relationship(null,rs.getLeft(),rs.getLeft(),getRelationshipLoopType(context)));
		Relationship rs2 = new Relationship(null, rs.getLeft(), null, getRelationshipType(context));
		List<Document> documents = new LinkedList<Document>();
		for (Relationship relationship1 : getRelationshipService().findByExample(context, rs2)) {
			Document document = documentRelationshipObjectService.findByItems(context, relationship1.getLeft(), relationship1.getRight());
			if (document != null)
				documents.add(document);
		}

		return new Dossier(rs, Collections.<Relationship>emptyList(),new LinkedList<Relationship>(getRelationshipService().findByExample(context, rs2)),rs.getLeft(), documents);
	}


	@Override
	protected RelationshipType getRelationshipType(Context context) {
		return getRelationshipTypeService().findByExampleUnique(context, new RelationshipType(Dossier.class.getCanonicalName(), Document.class.getCanonicalName()));
	}

	@Override
	protected RelationshipType getRelationshipLoopType(Context context) {
		return getRelationshipTypeService().findByExampleUnique(context, new RelationshipType(Dossier.class.getCanonicalName(), Dossier.class.getCanonicalName()));
	}


	@Override
	public Dossier findById(Context context, Integer id) {
		Relationship byId = getRelationshipService().findById(context, id);
		if(byId.getType().getId()!=getRelationshipLoopType(context).getId()) throw new IllegalArgumentException("Not a dossier ID");
		Relationship rs2 = new Relationship(null, byId.getLeft(), null, getRelationshipType(context));
		List<Document> documents = new LinkedList<Document>();
		for (Relationship relationship1 : getRelationshipService().findByExample(context, rs2)) {
			Document document = documentRelationshipObjectService.findByItems(context, relationship1.getLeft(), relationship1.getRight());
			if (document != null)
				documents.add(document);
		}

		return new Dossier(byId, Collections.<Relationship>emptyList(),new LinkedList<Relationship>(getRelationshipService().findByExample(context, rs2)),byId.getLeft(), documents);
	}

	@Override
	public void update(Context context, Dossier dossier) throws RelationshipException {

		throw new UnsupportedOperationException("To alter the documents in a dossier simmply create a document with dossier parent.");
	}

	@Override
	public boolean delete(Context context, Dossier dossier) throws RelationshipException {
		Dossier actual = findByExample(context, dossier);
		if (actual == null || actual.getDocuments().size() > 0) return false;

		boolean bl = getRelationshipService().delete(context, getRelationshipService().findByExampleUnique(context, new Relationship(null, actual.getItem(), actual.getItem(), getRelationshipLoopType(context))));
		if (bl) {
			try {
				actual.getItem().withdraw();
			} catch (Exception e) {
				throw new RelationshipException(e);
			}
		}
		return bl;
	}

	@Override
	public Dossier create(Context context, Dossier dossier) throws RelationshipException {
		try {
			Relationship relationship = getRelationshipService().create(context, dossier.getItem(), dossier.getItem(), getRelationshipLoopType(context));
			for (Document document : dossier.getDocuments()) {
				documentRelationshipObjectService.create(context, document);
			}

			return findByRelationshipUnique(context, relationship);
		} catch (SQLException e) {
			throw new RelationshipException(e);
		}
	}
}
