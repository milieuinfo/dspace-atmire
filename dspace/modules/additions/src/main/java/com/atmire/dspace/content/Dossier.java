package com.atmire.dspace.content;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 05 Nov 2014
 */
public class Dossier extends RelationShipObject {


    private List<Document> documents;

    protected Dossier(Relationship coreRelationship, List<Relationship> incoming, List<Relationship> outgoing, Item item, List<Document> documents) {
        super(coreRelationship, incoming, outgoing, item);
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }


    @Override
    public int getType() {
        return Constants.DOSSIER;
    }


    @Override
    public String getName() {
        return getItem().getName();
    }

    @Override
    protected Context getContext() {
        return null;
    }


    @Override
    public void update() throws SQLException, AuthorizeException {

    }

    @Override
    public void updateLastModified() {

    }

}
