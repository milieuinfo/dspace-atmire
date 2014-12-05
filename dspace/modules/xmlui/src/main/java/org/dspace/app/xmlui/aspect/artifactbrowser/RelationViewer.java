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
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.utils.DSpace;
import org.xml.sax.SAXException;
import org.dspace.app.xmlui.wing.element.List;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        RelationshipTypeService relationshipTypeService = new DSpace().getServiceManager().getServiceByName("relationshipTypeService", RelationshipTypeService.class);
        java.util.Collection<RelationshipType> relationshipTypes = relationshipTypeService.findByExample(context, new RelationshipType());
        java.util.List<String> theTypes = new ArrayList<String>();
        for(RelationshipType relationshipType : relationshipTypes){
            if(!theTypes.contains(relationshipType.getLeftType())) theTypes.add(relationshipType.getLeftType());
        }

        Map<String, java.util.List<RelationShipObject>> outObjects = new HashMap<String, java.util.List<RelationShipObject>>();
        for(String theType : theTypes){
            RelationshipObjectServiceFactory rlsf = RelationshipObjectServiceFactory.getInstance();
            try {
                if(theType!=null){
                    Class<? extends RelationShipObject> theClass = (Class<? extends RelationShipObject>) Class.forName(theType);
                    if(theClass!=null){
                        RelationshipObjectService<? extends RelationShipObject> relationshipObjectService = rlsf.getRelationshipObjectService(theClass);
                        if(relationshipObjectService!=null){
                            RelationShipObject relationShipObject = relationshipObjectService.findByItem(context,item);
                            if(relationShipObject!=null){
                                outObjects.put(relationShipObject.getTypeText(),relationshipObjectService.getOutgoingRelationshipObjects(context,relationShipObject));
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Division divRelations = body.addDivision("relations");

        for(Map.Entry<String, java.util.List<RelationShipObject>> entry : outObjects.entrySet()) {
            String key = entry.getKey();

            Division divisionOut = divRelations.addDivision("relation_" + key);

            List relationListOut = divisionOut.addList("relationList_"+ key,List.TYPE_GLOSS);

            relationListOut.setHead(key+":");//.addItem(key+":","ds-list-head").addContent(key+":");


            for (RelationShipObject rls : entry.getValue()) {
                relationListOut.addItemXref(contextPath + "/handle/" + rls.getHandle(), rls.getName());
            }
        }
    }


}
