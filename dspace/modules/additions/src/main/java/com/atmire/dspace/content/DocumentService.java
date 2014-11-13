package com.atmire.dspace.content;

import edu.emory.mathcs.backport.java.util.Collections;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 07 Nov 2014
 */
@Component
public class DocumentService extends AbstractRelationshipObjectServiceImpl<Document> {

	private RelationshipObjectService<Dossier> dossierRelationshipObjectService;

	public RelationshipObjectService<Dossier> getDocumentRelationshipObjectService() {
		return dossierRelationshipObjectService;
	}
	@Autowired
	public void setDocumentRelationshipObjectService(RelationshipObjectService<Dossier> documentRelationshipObjectService) {
		this.dossierRelationshipObjectService = documentRelationshipObjectService;
	}

	@Override
	protected Document findByRelationshipUnique(Context context, Relationship relationship) {
		return null;
	}

	@Override
	protected RelationshipType getRelationshipType(Context context) {
		return getRelationshipTypeService().findByExampleUnique(context, new RelationshipType(Dossier.class.getCanonicalName(), Document.class.getCanonicalName()));
	}

	@Override
	protected RelationshipType getRelationshipLoopType(Context context) {
		return getRelationshipTypeService().findByExampleUnique(context, new RelationshipType(Document.class.getCanonicalName(), Document.class.getCanonicalName()));
	}


	@Override
	public Document findById(Context context, Integer id) {
		Relationship byId = getRelationshipService().findById(context, id);
		if (byId.getType().getId() != getRelationshipLoopType(context).getId())
			throw new IllegalArgumentException("Not a Document ID");
		return createDocument(context, byId);
	}


	private Document createDocument(Context context, Relationship rs) {
		Relationship prototype = new Relationship(null, null, rs.getLeft(), getRelationshipType(context));
		Relationship byExampleUnique = getRelationshipService().findByExampleUnique(context, prototype);
		if (byExampleUnique == null) throw new IllegalStateException("Orphaned document");
		Dossier dossier = getDocumentRelationshipObjectService().findById(context, byExampleUnique.getId());
		return new Document(rs, Collections.emptyList(), Collections.singletonList(byExampleUnique),rs.getLeft(), dossier);

	}

	@Override
	public void update(Context context, Document document) throws RelationshipException {
		throw new UnsupportedOperationException("It doesn't make any sense to update a document since the item cannot change.");


	}

	@Override
	public boolean delete(Context context, Document document) throws RelationshipException {
		RelationshipService relationshipService = getRelationshipService();

		boolean ret=relationshipService.delete(context, document.getCoreRelationship());
		if(ret){
			try {
				document.getItem().withdraw();
			} catch (Exception e) {
				throw new RelationshipException(e);
			}
			for (Relationship relationship : document.getIncoming()) {
				relationshipService.delete(context,relationship);
			}
			for (Relationship relationship : document.getOutgoing()) {
				relationshipService.delete(context,relationship);
			}
		}

		return ret;
	}

	@Override
	public Document create(Context context, Document document) throws RelationshipException {
		return null;
	}
}
