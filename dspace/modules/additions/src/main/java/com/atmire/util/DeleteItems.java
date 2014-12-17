package com.atmire.util;

import org.apache.commons.cli.*;
import org.dspace.content.*;
import org.dspace.content.Collection;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;

public class DeleteItems {

    public static void main(String[] args) throws Exception {
        // create an options object and populate it
        CommandLineParser cmdLineParser = new PosixParser();

        Options options = new Options();
        options.addOption("i", "identifier", true, "The parent from which to delete all items");

        CommandLine line = null;
        try {
            line = cmdLineParser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("ERROR: error while parsing arguments " + e);
            System.exit(1);
        }
        Context c = null;

        c = new Context();

        // have to be super-user to do the filtering
        c.turnOffAuthorisationSystem();

        if (line.hasOption("i")) {
            String identifier = line.getOptionValue('i');
            DSpaceObject dso = HandleManager.resolveToObject(c, identifier);
            if (dso == null) {
                throw new IllegalArgumentException("Cannot resolve "
                        + identifier + " to a DSpace object");
            }

            switch (dso.getType()) {
                case Constants.COMMUNITY:
                    deleteItemsCommunity(c, (Community) dso);
                    break;
                case Constants.COLLECTION:
                    deleteItemsCollection(c, (Collection) dso);
                    break;
                case Constants.ITEM:
                    deleteItemsItem(c, (Item) dso);
                    break;
            }
        }
        c.restoreAuthSystemState();
        c.complete();

    }

    public static void deleteItemsCommunity(Context c, Community community)
            throws Exception {
        Community[] subcommunities = community.getSubcommunities();
        for (Community subcommunity : subcommunities) {
            deleteItemsCommunity(c, subcommunity);
        }

        Collection[] collections = community.getCollections();
        for (Collection collection : collections) {
            deleteItemsCollection(c, collection);
        }
    }

    public static void deleteItemsCollection(Context c, Collection collection)
            throws Exception {
        ItemIterator i = collection.getItems();
        try {
            while (i.hasNext()) {
                deleteItemsItem(c, i.next());
            }
        } finally {
            if (i != null) {
                i.close();
            }
        }
    }

    public static void deleteItemsItem(Context c, Item item) throws Exception {
        Collection[] colls = item.getCollections();
        for (Collection coll : colls) {
            coll.removeItem(item);
        }
        c.commit();
    }
}
