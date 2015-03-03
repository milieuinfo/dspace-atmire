package com.atmire.discovery;

import com.atmire.dspace.content.*;
import org.apache.solr.common.SolrInputDocument;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.discovery.SolrServiceIndexPlugin;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Philip Vissenaekens (philip at atmire dot com)
 * Date: 03/03/15
 * Time: 14:11
 */
public class SolrServiceRelationIndexPlugin  implements SolrServiceIndexPlugin {
    @Override
    public void additionalIndex(Context context, DSpaceObject dso, SolrInputDocument document) {
        if(dso instanceof Item) {
            RelationshipObjectService<Record> recordService = RelationshipObjectServiceFactory.getInstance().getRelationshipObjectService(Record.class);
            String leftLabel = ((RecordService) recordService).getRelationLeftLabel(context);
            String rightlabel = ((RecordService) recordService).getRelationRightLabel(context);

            //find parent
            Collection<Relationship> relations = recordService.findRelationsByItem(context, null, (Item) dso);
            Iterator<Relationship> iterator = relations.iterator();

            while(iterator.hasNext()) {

                Relationship relationship = iterator.next();

                if (relationship != null) {

                        document.addField("relation." + leftLabel, relationship.getLeft().getID());
                }
            }

            //find children
            relations = recordService.findRelationsByItem(context, (Item) dso, null);
            iterator = relations.iterator();

            while(iterator.hasNext()) {

                Relationship relationship = iterator.next();

                if (relationship != null) {
                    document.addField("relation." + rightlabel, relationship.getRight().getID());

                }
            }
        }
    }
}
