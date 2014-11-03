package com.atmire.dspace.database;

import org.apache.commons.io.FileUtils;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;

import java.io.File;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 03 Nov 2014
 */
public class AddLNEEntities extends DatabaseLoader {

    @Override
    public void loadDatabaseChanges(Context context) throws Exception {

        String sqlFile = ConfigurationManager.getProperty("dspace.dir") + File.separator + "etc" + File.separator + "postgres" + File.separator + "lne" + File.separator + "entities.sql";
        String sql = FileUtils.readFileToString(new File(sqlFile), "UTF-8");
        DatabaseManager.loadSql(sql);

    }
}
