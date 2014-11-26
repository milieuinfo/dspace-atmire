package com.atmire.dspace.database;

import org.apache.commons.io.FileUtils;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;

import java.io.File;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 03 Nov 2014
 */
public class AddLNEEntities extends DatabaseLoader {

    @Override
    public void loadDatabaseChanges(Context context) throws Exception {

        TableRow type = DatabaseManager.row("Type");
        if (type.toString().length() == 4) {
            // if the table does not exist, type.toString() will be "type"
            // otherwise it will have much more info
            String sqlFile = ConfigurationManager.getProperty("dspace.dir") + File.separator + "etc" + File.separator + "postgres" + File.separator + "lne" + File.separator + "entities.sql";
            String sql = FileUtils.readFileToString(new File(sqlFile), "UTF-8");
            DatabaseManager.loadSql(sql);
        }

    }
}
