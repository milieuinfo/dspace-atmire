package org.dspace.sword2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
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

import com.atmire.dspace.BulkUploadRecords;
import com.atmire.dspace.content.Record;

/**
 * Created by philip on 05/12/14.
 */
public class SwordLNEContentIngester extends AbstractSwordContentIngester {
	/** Log4j logger */
	public static final Logger log = Logger.getLogger(SwordLNEContentIngester.class);

	public static final String TEMP_DIR = ConfigurationManager.getProperty("org.dspace.app.itemexport.work.dir");

	@Override
	public DepositResult ingestToCollection(Context context, Deposit deposit, Collection collection,
			VerboseDescription verboseDescription, DepositResult result)
					throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
		HashMap<String, Record> recordMap = new HashMap<>();

		try {
			File mainDirectory = new File(TEMP_DIR + File.separator + "MilieuVerslagen");
			File directory = new File(mainDirectory.getPath() + File.separator + "Milieuverslag" + System.nanoTime());

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
			bulkUploadRecords.setCommunity((Community) collection.getParentObject());

			bulkUploadRecords.importArchives(directory, new File("non_existing_files" + System.currentTimeMillis()),
					recordMap);

			FileUtils.deleteDirectory(directory);

			result = new DepositResult();
			if (bulkUploadRecords.lastItemId > 0) {
				result.setItem(Item.find(context, bulkUploadRecords.lastItemId));
			} else {
				result.setItem(collection.getAllItems().next());
			}

			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				// As the code here is not very transactional we need to do the
				// cleanup ourselves.
				java.util.Collection<Record> records = recordMap.values();
				for (Record record : records) {
					Item item = record.getItem();
					Collection[] collections = item.getCollections();
					// normally only 1 collection
					collections[0].removeItem(item);
				}
				// commit remove because afterwards there will be a row back and
				// the added rows will not be removed...
				// It would have been nice if we could rollback the tansaction
				// in the first place so we don't need to
				// do the cleanup ourselves (2 times work for the database).
				context.commit();

			} catch (Exception ex) {
				log.error("Error while cleanup...", e);
			}

			throw new DSpaceSwordException(e);
		} finally {

			context.restoreAuthSystemState();

			// Do not close connection here as it will be used afterwards...
		}
	}

	@Override
	public DepositResult ingestToItem(Context context, Deposit deposit, Item item,
			VerboseDescription verboseDescription, DepositResult result)
					throws DSpaceSwordException, SwordError, SwordAuthException, SwordServerException {
		log.info("SwordLNEMilieuverslagContentIngester ingestToItem parameters: deposit: " + deposit + ", item: " + item
				+ ", verboseDescription: " + verboseDescription + ", result: " + result);
		return null;
	}
}
