package com.atmire.util;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 21 Nov 2014
 */
public class LneUtils {


    /**
     * temporary implementation until more specific instructions about this arise
     */
    public static Collection[] getRecordCollections(Community community) throws SQLException {
        Collection[] collections = new Collection[0];
        if (community != null) {
            Collection[] collections1 = community.getCollections();
            for (Collection collection : collections1) {
                if (collection.getName().equals("Record")) {
                    collections = new Collection[]{collection};
                }
            }
        }
        return collections;
    }

    public static String getMetadataFilename(String path){
        File folder = new File(path);

        for (File file : folder.listFiles()) {
            if(file.getName().matches(".*METADATA\\.xml")){
                return file.getName();
            }
        }
        return "";
    }
    
    public static final NodeList getExtraMetaData(String directory, String type) throws Exception{
    	File extraFile = null;
    	File folder = new File(directory);
        for (File file : folder.listFiles()) {
            if(file.getName().matches(".*EXTRA\\.xml")){
                extraFile  =  file;
                break;
            }
        }
        if (null != extraFile){
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	Document doc = builder.parse(extraFile);
        	        	
        	XPathFactory xPathFactory = XPathFactory.newInstance();
        	XPath xPath = xPathFactory.newXPath();
        	XPathExpression expr = xPath.compile("/extra-meta/schema[@name='"+type+"']/dcvalue");
        	NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);    	
        	return nl;
          }else{
        	return null;
        }
    }

    public static final String getFileNameWithoutExtension(String fileName){
    	return StringUtils.substringBeforeLast(fileName, ".");
    }
    
    
    


    
    public static List<File> getAllAanvullingFiles(String path, String jaar, String nummer, String type) throws SQLException {

        List<File> aanvullingFiles = new ArrayList<File>();
        // LuchtEmissie (type) <> Luchtemissie (fileName)
        String syntaxStart = jaar + "_" + nummer + "_" + type + "_Aanvulling";
        for (File file : new File(path).listFiles()) {
            if (FilenameUtils.getName(file.getName().toLowerCase()).startsWith(syntaxStart.toLowerCase())) {
                aanvullingFiles.add(file);
            }
        }

        return aanvullingFiles;
    }

    public static int countAllAanvullingFiles(String path, String jaar, String nummer, String type) throws SQLException {
        return getAllAanvullingFiles(path,jaar,nummer,type).size();
    }

    public static String getFileNameBasedOnIndex(String path, String jaar, String nummer, String type,int index) throws SQLException {
        return getAllAanvullingFiles(path,jaar,nummer,type).get(index-1).getName();
    }
}
