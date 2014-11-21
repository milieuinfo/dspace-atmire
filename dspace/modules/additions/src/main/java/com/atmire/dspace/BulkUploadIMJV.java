package com.atmire.dspace;

import com.atmire.scripts.Script;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.jdom.transform.XSLTransformException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by philip on 07/11/14.
 *
 */
public class BulkUploadIMJV extends Script {

    private String directory;
    private String XSLPath;
    private String schemaString;
    private boolean validationEnabled;

    public static void main(String[] args) {
        BulkUploadIMJV script = new BulkUploadIMJV();
        script.mainImpl(args);
    }

    @Override
    protected Options createCommandLineOptions() {
        Options options = super.createCommandLineOptions();
        Option inputDirectoryOption = OptionBuilder.withArgName("directory").hasArg().withDescription("directory containing the xml and bitstream files").isRequired().create('d');
        Option xslPathOption = OptionBuilder.withArgName("xsl").hasArg().withDescription("xsl stylesheet used to covert the xml").create('x');
        Option schemaOption = OptionBuilder.withArgName("schema").hasArg().withDescription("schema name to validate the xml against").create('s');
        Option validationOption = OptionBuilder.withArgName("validation").withDescription("use this argument to disable validation based on a schema, validation is enabled by default").create('v');

        options.addOption(inputDirectoryOption);
        options.addOption(xslPathOption);
        options.addOption(schemaOption);
        options.addOption(validationOption);
        return options;
    }

    @Override
    protected boolean processArgs(CommandLine line) {
        boolean exit = false;
        if (line.hasOption('d')) {
            setDirectory(line.getOptionValue("d"));
        } else {
            exit = true;
        }

        if (line.hasOption('x')) {
            setXSLPath(line.getOptionValue("x"));
        } else {
            setXSLPath(ConfigurationManager.getProperty("imjv-import", "transformation.stylesheet"));
        }

        boolean validationEnabled = !line.hasOption("v");
        setValidationEnabled(validationEnabled);
        //when validation is enabled there must be a schema available to validate the xml against
        if (validationEnabled) {
            if (line.hasOption("s")) {
                setSchemaString(line.getOptionValue("s"));
            } else {
                exit = true;
            }
        }

        return exit;
    }

    @Override
    public void run() {
        try {
            String outputFolderPath = directory + File.separator + "archive";
            File dir = new File(directory);
            File output = new File(outputFolderPath);
            output.mkdir();

            File[] xmlFiles = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });

            for (File xmlFile : xmlFiles) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);

                if (validationEnabled) {
                    ValidateAgainstSchema(doc, schemaString);
                }

                convertToArchive(xmlFile, outputFolderPath + File.separator + "result.xml", XSLPath);
            }
        } catch (Exception e) {
            printAndLogError(e);
        }
    }

    private void ValidateAgainstSchema(Document doc, String schemaString) throws IOException, SAXException {
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        SchemaFactory factory = SchemaFactory.newInstance(language);
        Schema schema = factory.newSchema(new File(org.dspace.core.ConfigurationManager.getProperty("dspace.dir") + File.separator + "config" + File.separator + "schemas-imjv" + File.separator + "xsd" + File.separator + schemaString));

        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(doc));
    }

    private boolean convertToArchive(File input, String outputPath, String xslPath) throws XSLTransformException {
        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(input);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(new File(xslPath));
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(new File(outputPath));

        // create an instance of TransformerFactory
        javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();

        javax.xml.transform.Transformer trans;
        try {
            trans = transFact.newTransformer(xsltSource);
        } catch (TransformerConfigurationException e) {
            print("Error: the stylesheet at '" + xslPath + "' couldn't be used");
            return false;
        }

        try {
            trans.transform(xmlSource, result);
        } catch (Throwable t) {
            print("Error: couldn't convert the metadata file at '" + input.getAbsolutePath());
            return false;
        }

        File file = new File(outputPath);

        if (file.exists()) {
            file.delete();
        }

        return true;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public void setXSLPath(String XSLPath) {
        this.XSLPath = XSLPath;
    }

    public String getXSLPath() {
        return XSLPath;
    }

    public void setSchemaString(String schemaString) {
        this.schemaString = schemaString;
    }

    public String getSchemaString() {
        return schemaString;
    }

    public void setValidationEnabled(boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }
}
