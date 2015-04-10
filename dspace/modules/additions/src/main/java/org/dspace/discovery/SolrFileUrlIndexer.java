package org.dspace.discovery;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by jonas on 09/04/15.
 */
public class SolrFileUrlIndexer implements SolrServiceIndexPlugin {

    public static final String[] bundleType = {"ORIGINAL", "IMPORT", "XML-Communicatie"};

    private static final String FIELD_NAME = "file.url";

    private static final String BITSREAM = "bitstream";

    private static final String HANDLE = "handle";

    private static final String DSPACE_URL = "dspace.url";
    protected static Logger log = Logger.getLogger(SolrFileUrlIndexer.class);

    @Override
    public void additionalIndex(Context context, DSpaceObject dso, SolrInputDocument document) {

        Bundle[] bundlesFromItem = null;
        if (!(dso instanceof Item)) {
            return;
        }
        Item item = (Item) dso;
        String dspaceDir = ConfigurationManager.getProperty(DSPACE_URL);
        String preUrlPath = dspaceDir + "/" + BITSREAM + "/" + HANDLE + "/" + item.getHandle() + "/";
        try {
            bundlesFromItem = item.getBundles();
        } catch (SQLException e) {
            log.error("Error while retrieving bundles from item : ",e);
        }
        for (Bundle bundle : bundlesFromItem) {
            if (Arrays.asList(bundleType).contains(bundle.getName())) {
                for (Bitstream bitstream : bundle.getBitstreams()) {
                    document.addField(FIELD_NAME, preUrlPath + bitstream.getName()+"?sequence="+bitstream.getSequenceID());
                }
            }
        }

    }

}
