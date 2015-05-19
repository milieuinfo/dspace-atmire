package com.atmire.util;

import org.apache.commons.io.FilenameUtils;
import org.dspace.content.Collection;
import org.dspace.content.Community;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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


    public static List<File> getAllAanvullingFiles(String path, String jaar, String nummer, String type) throws SQLException {

        List<File> aanvullingFiles = new ArrayList<File>();
        // LuchtEmissie (type) <> Luchtemissie (fileName)
        String syntaxStart = jaar + "_" + nummer + "_" + type + "_Aanvulling_";
        for (File file : new File(path).listFiles()) {
            if (FilenameUtils.getName(file.getName()).startsWith(syntaxStart)) {
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
