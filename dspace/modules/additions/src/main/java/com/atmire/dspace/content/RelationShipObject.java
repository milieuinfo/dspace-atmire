package com.atmire.dspace.content;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

import java.sql.SQLException;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 04 Nov 2014
 */
public class RelationShipObject extends DSpaceObject {

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public String getHandle() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    protected Context getContext() {
        return null;
    }

    @Override
    protected Logger getLogger() {
        return null;
    }

    @Override
    public void update() throws SQLException, AuthorizeException {

    }

    @Override
    public void updateLastModified() {

    }
}
