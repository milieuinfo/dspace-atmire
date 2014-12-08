package org.dspace.sword2;

import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.swordapp.server.Deposit;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by philip on 05/12/14.
 */
public class SwordLNEMilieuverslagContentIngester extends AbstractSwordContentIngester {
    /** Log4j logger */
    public static final Logger log = Logger.getLogger(SwordLNEMilieuverslagContentIngester.class);

    @Override
    public DepositResult ingestToCollection(Context context, Deposit deposit, Collection collection, VerboseDescription verboseDescription, DepositResult result) throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
        try {
            File depositFile = deposit.getFile();
            ZipFile zip = new ZipFile(depositFile);

            Enumeration zenum = zip.entries();
            while (zenum.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry) zenum.nextElement();
                log.info(entry.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DepositResult ingestToItem(Context context, Deposit deposit, Item item, VerboseDescription verboseDescription, DepositResult result) throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
        log.info("SwordLNEMilieuverslagContentIngester ingestToItem parameters: deposit: " + deposit + ", item: " + item + ", verboseDescription: " + verboseDescription + ", result: " + result );
        return null;
    }
}
