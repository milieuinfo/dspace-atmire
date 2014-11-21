package com.atmire.util;

import org.dspace.content.Collection;
import org.dspace.content.Community;

import java.sql.SQLException;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 21 Nov 2014
 */
public class LneUtils {


    /**
     * temporary implementation until more specific instructions about this arise
     */
    public static Collection[] getDocumentCollections(Community community) throws SQLException {
        Collection[] collections = new Collection[0];
        if (community != null) {
            Collection[] collections1 = community.getCollections();
            for (Collection collection : collections1) {
                if (collection.getName().equals("Documents")) {
                    collections = new Collection[]{collection};
                }
            }
        }
        return collections;
    }

    public static Collection[] getDossierCollections(Community community) throws SQLException {
        Collection[] collections = new Collection[0];
        if (community != null) {
            Collection[] collections1 = community.getCollections();
            for (Collection collection : collections1) {
                if (collection.getName().equals("Dossiers")) {
                    collections = new Collection[]{collection};
                }
            }
        }
        return collections;
    }
}
