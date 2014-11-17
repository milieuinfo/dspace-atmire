package com.atmire.dspace.content;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 05 Nov 2014
 */
@Component
public class DossierService extends AbstractRelationshipObjectServiceImpl<Dossier> {

    @Autowired
    private RelationshipObjectService<Document> documentRelationshipObjectService;

    @Override
    protected Dossier findByRelationshipUnique(Context context, Relationship relationship) {
        Relationship foundRelationship = getRelationshipService().findByExampleUnique(context, relationship);
        if (foundRelationship == null) {
            return null;
        }
        // the foundRelationship could be dos-doc or dos-dos, in both cases the L item is the one
        // but this doesn't consider future kinds of relationships with dossiers
        Relationship exampleLoopType = new Relationship(null, foundRelationship.getLeft(), foundRelationship.getLeft(), getRelationshipLoopType(context));
        foundRelationship = getRelationshipService().findByExampleUnique(context, exampleLoopType);
        return newDossierInstance(context, foundRelationship);
    }

    private Dossier newDossierInstance(Context context, Relationship foundRelationship) {
        Dossier dossier = null;
        if (foundRelationship != null) {
            Relationship example = new Relationship(null, foundRelationship.getLeft(), null, getRelationshipType(context));
            Collection<Relationship> relationships = getRelationshipService().findByExample(context, example);
            List<Document> documents = new LinkedList<Document>();
            for (Relationship relationship1 : relationships) {
                Document document = documentRelationshipObjectService.findByItems(context, relationship1.getLeft(), relationship1.getRight());
                if (document != null) {
                    documents.add(document);
                }
            }

            List<Relationship> incoming = Collections.emptyList();
            LinkedList<Relationship> outgoing = new LinkedList<Relationship>(relationships);
            Item item = foundRelationship.getLeft();
            dossier = new Dossier(foundRelationship, incoming, outgoing, item, documents);
            for (Document document : dossier.getDocuments()) {
                document.setDossier(dossier);
            }
        }
        return dossier;
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
        if (byId == null) {
            return null;
        }
        if (byId.getType().getId() != getRelationshipLoopType(context).getId()) {
            throw new IllegalArgumentException("Not a dossier ID");
        }
        return newDossierInstance(context, byId);
    }

    @Override
    public void update(Context context, Dossier dossier) throws RelationshipException {
        throw new UnsupportedOperationException("To alter the documents in a dossier simply create a document with dossier parent.");
    }

    @Override
    public boolean delete(Context context, Dossier dossier) throws RelationshipException {
        Dossier actual = findByExample(context, dossier);
        if (actual == null || actual.getDocuments().size() > 0) {
            return false;
        }

        Relationship example = new Relationship(null, actual.getItem(), actual.getItem(), getRelationshipLoopType(context));
        Relationship relationship = getRelationshipService().findByExampleUnique(context, example);
        boolean relationshipDeleted = getRelationshipService().delete(context, relationship);
        if (relationshipDeleted) {
            try {
                actual.getItem().withdraw();
            } catch (Exception e) {
                throw new RelationshipException(e);
            }
        }
        return relationshipDeleted;
    }

    @Override
    public Dossier create(Context context, Dossier dossier) throws RelationshipException {
        try {
            Relationship relationship = getRelationshipService().create(context, dossier.getItem(), dossier.getItem(), getRelationshipLoopType(context));
            for (Document document : dossier.getDocuments()) {
                document.setDossier(dossier); // make sure it's set or throw an exception if it isn't ?
                documentRelationshipObjectService.create(context, document);
            }

            return findByRelationshipUnique(context, relationship);
        } catch (SQLException e) {
            throw new RelationshipException(e);
        }
    }
}
