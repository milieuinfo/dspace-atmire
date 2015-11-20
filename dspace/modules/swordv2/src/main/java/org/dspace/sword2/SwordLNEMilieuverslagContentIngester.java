package org.dspace.sword2;

import com.atmire.dspace.BulkUploadRecords;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dspace.content.Bitstream;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.swordapp.server.Deposit;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by philip on 05/12/14.
 */
public class SwordLNEMilieuverslagContentIngester extends AbstractSwordContentIngester {
    /** Log4j logger */
    public static final Logger log = Logger.getLogger(SwordLNEMilieuverslagContentIngester.class);

    public static final String TEMP_DIR = ConfigurationManager.getProperty("org.dspace.app.itemexport.work.dir");

    @Override
    public DepositResult ingestToCollection(Context context, Deposit deposit, Collection collection, VerboseDescription verboseDescription, DepositResult result) throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
        try {
            File mainDirectory = new File(TEMP_DIR + File.separator + "MilieuVerslagen");
            File directory = new File(mainDirectory.getPath() + File.separator + "Milieuverslag");

            if (!directory.exists()) {
                directory.mkdir();
            }

            List<File> directories = new ArrayList<File>();
            List<Bitstream> derivedResources = new ArrayList<Bitstream>();

            File depositFile = deposit.getFile();
            ZipFile zip = new ZipFile(depositFile);

            Enumeration zenum = zip.entries();
            while (zenum.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) zenum.nextElement();

                File file = new File(directory, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                    directories.add(file);
                } else {
                    file.getParentFile().mkdirs();
                    InputStream in = zip.getInputStream(entry);
                    try {
                        OutputStream out = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int readCount = in.read(buffer);
                            if (readCount < 0) {
                                break;
                            }
                            out.write(buffer, 0, readCount);
                        }
                    } finally {
                        in.close();
                    }
                }
            }
            context.turnOffAuthorisationSystem();

            BulkUploadRecords bulkUploadRecords = new BulkUploadRecords(context);

            bulkUploadRecords.setDirectory(mainDirectory.getPath());
            bulkUploadRecords.setOutputDirectory(mainDirectory.getPath());
            bulkUploadRecords.setValidationEnabled(false);
            bulkUploadRecords.setCommunity((Community) collection.getParentObject());
            bulkUploadRecords.setXSLPath(ConfigurationManager.getProperty("imjv-import", "transformation.stylesheet"));

            bulkUploadRecords.run();

            context.restoreAuthSystemState();

            FileUtils.deleteDirectory(mainDirectory);

            result = new DepositResult();
            if (bulkUploadRecords.lastItemId > 0) {
                result.setItem(Item.find(context, bulkUploadRecords.lastItemId));
            } else {
                result.setItem(collection.getAllItems().next());
            }

            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new DSpaceSwordException(e);
        } catch (SQLException e1) {
            log.error(e1.getMessage(), e1);
            throw new DSpaceSwordException(e1);
        }
    }

    @Override
    public DepositResult ingestToItem(Context context, Deposit deposit, Item item, VerboseDescription verboseDescription, DepositResult result) throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
        log.info("SwordLNEMilieuverslagContentIngester ingestToItem parameters: deposit: " + deposit + ", item: " + item + ", verboseDescription: " + verboseDescription + ", result: " + result );
        return null;
    }
}
