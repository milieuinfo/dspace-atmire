/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.artifactbrowser;

import com.atmire.dspace.content.*;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DCValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Display a single item.
 *
 * @author Bert
 */
public class RelationViewer extends AbstractDSpaceTransformer
{
    public void addBody(Body body) throws SAXException, WingException,
            UIException, SQLException, IOException, AuthorizeException
    {

        DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
        if (!(dso instanceof Item))
        {
            return;
        }
        Item item = (Item) dso;

        RelationshipObjectService<Record> recordService = RelationshipObjectServiceFactory.getInstance().getRelationshipObjectService(Record.class);
        String leftLabel = ((RecordService) recordService).getRelationLeftLabel(context);
        String rightlabel = ((RecordService) recordService).getRelationRightLabel(context);

        java.util.List<Relationship> children = findRelationsByItem(item,null,recordService);
        java.util.List<Relationship> parents = findRelationsByItem(null,item,recordService);

        Division divRelations = body.addDivision("relations");

        if(parents.size()>0){
            Division divisionOut = divRelations.addDivision("relation_" + leftLabel);

            List relationListOut = divisionOut.addList("relationList_"+ leftLabel,List.TYPE_GLOSS);

            relationListOut.setHead(leftLabel+":");


            for (Relationship rls : parents) {
                String label = rls.getLeft().getName();
                if (ConfigurationManager.getProperty("relation-metadata", "relationviewer.fieldname") != null) {
                    DCValue[] metadata = rls.getLeft().getMetadata(ConfigurationManager.getProperty("relation-metadata", "relationviewer.fieldname"));
                    if (metadata.length > 0)
                        label = metadata[0].value;
                }
                relationListOut.addItemXref(contextPath + "/handle/" + rls.getLeft().getHandle(), label);
            }
        }

        if(children.size()>0){
            Division divisionOut = divRelations.addDivision("relation_" + rightlabel);

            List relationListOut = divisionOut.addList("relationList_"+ rightlabel,List.TYPE_GLOSS);

            relationListOut.setHead(rightlabel+":");

            //alternative by name using http://www.davekoelle.com/files/AlphanumComparator.java
            Comparator<Relationship> sortByHandle = new Comparator<Relationship>() {
                @Override
                public int compare(Relationship relationship, Relationship t1) {
                    try {
                        return relationship.getRight().getHandle().compareTo(t1.getRight().getHandle());
                    } catch (Exception e) {
                        return 0;
                    }
                }
            };

            Collections.sort(children, sortByHandle);
            for (Relationship rls : children) {
                String label = rls.getRight().getName();
                if (ConfigurationManager.getProperty("relation-metadata", "relationviewer.fieldname") != null) {
                    DCValue[] metadata = rls.getRight().getMetadata(ConfigurationManager.getProperty("relation-metadata", "relationviewer.fieldname"));
                    if (metadata.length > 0)
                        label = metadata[0].value;
                }
                relationListOut.addItemXref(contextPath + "/handle/" + rls.getRight().getHandle(), label);
            }
        }
    }

    private java.util.List<Relationship> findRelationsByItem(Item left, Item right, RelationshipObjectService<Record> recordService){
        java.util.List<Relationship> relationList = new ArrayList<Relationship>();
        Collection<Relationship>  relations = recordService.findRelationsByItem(context, left, right);
        Iterator<Relationship> iterator = relations.iterator();

        while(iterator.hasNext()) {

            Relationship relationship = iterator.next();

            if (relationship != null) {
                relationList.add(relationship);
            }
        }
        return relationList;
    }
}
