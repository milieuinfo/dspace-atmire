package com.atmire.dspace.content;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by: Roeland Dillen (roeland at atmire dot com)
 * Date: 05 Nov 2014
 */
public class Document extends RelationShipObject {

    private Dossier dossier;

    protected Document(Relationship coreRelationship, List<Relationship> incoming, List<Relationship> outgoing, Item item) {
        super(coreRelationship, incoming, outgoing, item);
    }

    public Document(Relationship coreRelationship, List<Relationship> incoming, List<Relationship> outgoing, Item item, Dossier dossier) {
        super(coreRelationship, incoming, outgoing, item);
        this.dossier = dossier;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    @Override
    public int getType() {
        return Constants.DOCUMENT;
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
