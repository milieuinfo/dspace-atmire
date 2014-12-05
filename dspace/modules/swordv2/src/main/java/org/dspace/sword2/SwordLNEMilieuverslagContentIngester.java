package org.dspace.sword2;

import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.swordapp.server.Deposit;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

/**
 * Created by philip on 05/12/14.
 */
public class SwordLNEMilieuverslagContentIngester extends AbstractSwordContentIngester {
    /** Log4j logger */
    public static final Logger log = Logger.getLogger(SwordLNEMilieuverslagContentIngester.class);

    @Override
    public DepositResult ingestToCollection(Context context, Deposit deposit, Collection collection, VerboseDescription verboseDescription, DepositResult result) throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
        log.info("deposit: " + deposit + ", collection: " + collection + ", verboseDescription: " + verboseDescription + ", result: " + result );
        return null;
    }

    @Override
    public DepositResult ingestToItem(Context context, Deposit deposit, Item item, VerboseDescription verboseDescription, DepositResult result) throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
        log.info("deposit: " + deposit + ", item: " + item + ", verboseDescription: " + verboseDescription + ", result: " + result );
        return null;
    }
}
