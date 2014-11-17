package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 07 Nov 2014
 */
@Component
public class DocumentService extends AbstractRelationshipObjectServiceImpl<Document> {

    @Autowired
    private RelationshipObjectService<Dossier> dossierRelationshipObjectService;

    @Override
    protected Document findByRelationshipUnique(Context context, Relationship relationship) {
        Relationship foundRelationship = getRelationshipService().findByExampleUnique(context, relationship);
        if (foundRelationship == null) {
            return null;
        }
        // the foundRelationship could be dos-doc or doc-doc, in both cases the R item is the one
        // but this doesn't consider future kinds of relationships with documents
        Relationship exampleLoopType = new Relationship(null, foundRelationship.getRight(), foundRelationship.getRight(), getRelationshipLoopType(context));
        foundRelationship = getRelationshipService().findByExampleUnique(context, exampleLoopType);

        return newDocumentInstance(context, foundRelationship);
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
        if (byId == null) {
            return null;
        }
        if (byId.getType().getId() != getRelationshipLoopType(context).getId()) {
            throw new IllegalArgumentException("Not a Document ID");
        }
        return newDocumentInstance(context, byId);
    }

    private Document newDocumentInstance(Context context, Relationship relationship) {
        Document document = null;
        if (relationship != null) {

            // To find the dossier reference:
            // we're looking for the relationship with a R-item that is the document's item. (that's the relationship's L or R item, they're the same).
            // and the type should be dossier-document, which is provided by getRelationshipType(context)
            Relationship prototype = new Relationship(null, null, relationship.getLeft(), getRelationshipType(context));
            Relationship foundRelationship = getRelationshipService().findByExampleUnique(context, prototype);
            if (foundRelationship == null) {
                throw new IllegalStateException("Orphaned document");
            }
//            Dossier dossier = dossierRelationshipObjectService.findByItem(context, foundRelationship.getLeft());
            // removed the reference to the actual dossier, because it leads to an endless recursion
            // the caller can use setDossier() afterwards if necessary
            List<Relationship> incoming = Collections.singletonList(foundRelationship);
            List<Relationship> outgoing = Collections.emptyList();
            Item item = relationship.getLeft();
            document = new Document(relationship, incoming, outgoing, item);
        }
        return document;
    }

    @Override
    public void update(Context context, Document document) throws RelationshipException {
        throw new UnsupportedOperationException("It doesn't make any sense to update a document since the item cannot change.");
    }

    @Override
    public boolean delete(Context context, Document document) throws RelationshipException {
        RelationshipService relationshipService = getRelationshipService();

        boolean ret = relationshipService.delete(context, document.getCoreRelationship());
        if (ret) {
            try {
                document.getItem().withdraw();
            } catch (Exception e) {
                throw new RelationshipException(e);
            }
            for (Relationship relationship : document.getIncoming()) {
                relationshipService.delete(context, relationship);
            }
            for (Relationship relationship : document.getOutgoing()) {
                relationshipService.delete(context, relationship);
            }
        }

        return ret;
    }

    @Override
    public Document create(Context context, Document document) throws RelationshipException {
        try {
            Dossier dossier = document.getDossier();
            Dossier dossierChecked = dossierRelationshipObjectService.findByExample(context, dossier);
            if (dossierChecked != null) {
                Relationship relationship = getRelationshipService().create(context, document.getItem(), document.getItem(), getRelationshipLoopType(context));
                getRelationshipService().create(context, dossierChecked.getItem(), document.getItem(), getRelationshipType(context));

                return findByRelationshipUnique(context, relationship);
            } else {
                throw new RelationshipException("Document does not have a valid dossier.");
            }
        } catch (SQLException e) {
            throw new RelationshipException(e);
        }
    }
}
