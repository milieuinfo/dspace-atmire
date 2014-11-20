package com.atmire.dspace;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.transform.XSLTransformException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philip on 07/11/14.
 */
public class BulkUploadIMJV {
    private static final Logger log = Logger.getLogger(BulkUploadIMJV.class);

    public static void main(String[] args) {
        try {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();
            Option inputDirectoryOption = OptionBuilder.withArgName("directory").hasArg().withDescription("directory containing the xml and bitstream files").isRequired().create('d');
            Option xslPathOption = OptionBuilder.withArgName("xsl").hasArg().withDescription("xsl stylesheet used to covert the xml").isRequired().create('x');
            Option schemaOption = OptionBuilder.withArgName("schema").hasArg().withDescription("schema name to validate the xml against").create('s');
            Option validationOption = OptionBuilder.withArgName("validation").withDescription("use this argument to disable validation based on a schema, validation is enabled by default").create('v');

            options.addOption(inputDirectoryOption);
            options.addOption(xslPathOption);
            options.addOption(schemaOption);
            options.addOption(validationOption);
            CommandLine line = parser.parse(options, args);

            String inputFolderPath = null;
            if (line.hasOption("d"))
            {
                inputFolderPath = line.getOptionValue("d");
            }
            else
            {
                System.exit(0);
            }

            String xslPath = null;
            if (line.hasOption("x"))
            {
                xslPath = line.getOptionValue("x");
            }
            else
            {
                System.exit(0);
            }

            String schemaString = "";

            //when validation is enabled there must be a schema available to validate the xml against
            if(!line.hasOption("v")){
                if(line.hasOption("s")){
                    schemaString = line.getOptionValue("s");
                }
                else {
                    System.exit(0);
                }
            }

            String outputFolderPath = inputFolderPath + File.separator + "archive";
            File dir = new File(inputFolderPath);
            File output = new File(outputFolderPath);
            output.mkdir();

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

                if(!line.hasOption("v")) {
                    ValidateAgainstSchema(doc,schemaString);
                }

                convertToArchive(xmlFile,outputFolderPath + File.separator + "result.xml",xslPath);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void ValidateAgainstSchema(Document doc, String schemaString) throws IOException, SAXException {
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        SchemaFactory factory = SchemaFactory.newInstance(language);
        Schema schema = factory.newSchema(new File(org.dspace.core.ConfigurationManager.getProperty("dspace.dir") + File.separator + "config" + File.separator + "schemas-imjv" + File.separator + "xsd" + File.separator + schemaString));

        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(doc));
    }

    private static boolean convertToArchive(File input, String outputPath, String xslPath) throws XSLTransformException {
        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(input);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(new File(xslPath));
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(new File(outputPath));

        // create an instance of TransformerFactory
        javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();

        javax.xml.transform.Transformer trans;
        try {
            trans = transFact.newTransformer(xsltSource);
        } catch (TransformerConfigurationException e) {
            log.error("Error: the stylesheet at '" + xslPath + "' couldn't be used");
            return false;
        }

        try {
            trans.transform(xmlSource, result);
        } catch (Throwable t) {
            log.error("Error: couldn't convert the metadata file at '" + input.getAbsolutePath());
            return false;
        }

        File file = new File(outputPath);

        if(file.exists()){
            file.delete();
        }

        return true;
    }

}
