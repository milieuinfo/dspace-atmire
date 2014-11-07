package com.atmire.dspace;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by philip on 07/11/14.
 */
public class BulkUploadIMJV {
    public static void main(String[] args) {
        try {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();
            options.addOption("d", "directory", true, "directory containing the xml and bitstream files");
            CommandLine line = parser.parse(options, args);

            String folder = null;
            if (line.hasOption("d"))
            {
                folder = line.getOptionValue('d');
            }
            else
            {
                System.exit(0);
            }

            File dir = new File(folder);

            File[] xmlFiles = dir.listFiles(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return  name.endsWith(".xml");
                }
            });

            for (File xmlFile : xmlFiles) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);

                System.out.println(doc.getFirstChild().getNodeName());

                String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
                SchemaFactory factory = SchemaFactory.newInstance(language);
                Schema schema = factory.newSchema(new File(org.dspace.core.ConfigurationManager.getProperty("dspace.dir") + File.separator + "config" + File.separator + "schemas-imjv" + File.separator + "xsd" + File.separator + "imjv.xsd"));

                Validator validator = schema.newValidator();
                validator.validate(new DOMSource(doc));

                System.out.println("test");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
