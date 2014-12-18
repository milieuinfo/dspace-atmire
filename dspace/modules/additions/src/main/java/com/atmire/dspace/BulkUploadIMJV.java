package com.atmire.dspace;

import com.atmire.dspace.content.Dossier;
import com.atmire.dspace.content.RelationshipObjectService;
import com.atmire.dspace.content.RelationshipObjectServiceFactory;
import com.atmire.scripts.ContextScript;
import com.atmire.util.LneUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.dspace.app.itemimport.ItemImport;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.jdom.transform.XSLTransformException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by philip on 07/11/14.
 */
public class BulkUploadIMJV extends ContextScript {

    protected static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    protected static TransformerFactory transFactory = TransformerFactory.newInstance();
    protected static SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private Transformer transformer;
    private RelationshipObjectService<Dossier> dossierService = RelationshipObjectServiceFactory.getInstance().getRelationshipObjectService(Dossier.class);
    private String directory;
    private String XSLPath;
    private String schemaString;
    private boolean validationEnabled;
        private Community community;;

    public BulkUploadIMJV(Context context) {
        super(context);
    }

    public BulkUploadIMJV() {
    }

    public static void main(String[] args) {
        BulkUploadIMJV script = new BulkUploadIMJV();
        script.mainImpl(args);
    }

    @Override
    protected Options createCommandLineOptions() {
        Options options = super.createCommandLineOptions();

        OptionBuilder.withArgName("directory");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("directory containing the xml and bitstream files");
        OptionBuilder.isRequired();
        Option inputDirectoryOption = OptionBuilder.create('d');
        options.addOption(inputDirectoryOption);

        OptionBuilder.withArgName("xsl");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("xsl stylesheet used to covert the xml");
        Option xslPathOption = OptionBuilder.create('x');
        options.addOption(xslPathOption);

        OptionBuilder.withArgName("schema");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("schema name to validate the xml against");
        Option schemaOption = OptionBuilder.create('s');
        options.addOption(schemaOption);

        OptionBuilder.withArgName("validation");
        OptionBuilder.withDescription("use this argument to disable validation based on a schema, validation is enabled by default");
        Option validationOption = OptionBuilder.create('v');
        options.addOption(validationOption);

        OptionBuilder.withArgName("community");
        OptionBuilder.withDescription("Handle of the target community");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        Option communityOption = OptionBuilder.create('c');
        options.addOption(communityOption);
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

        if (line.hasOption('c')) {
            String communityHandle = line.getOptionValue('c');
            DSpaceObject community;
            try {
                community = HandleManager.resolveToObject(context, communityHandle);

                if (community instanceof Community) {
                    setCommunity((Community) community);
                } else {
                    print("The provided handle was not for a community");
                    exit = true;
                }
            } catch (SQLException e) {
                printAndLogError(e);
                exit = true;
            }
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
            File dir = new File(directory);
            File[] subdirs = dir.listFiles();
            for (File subdir : subdirs) {
                if(subdir.isDirectory()) {
                    String workingDirPath = subdir.getAbsolutePath() + File.separator + "IngediendeDocumentenOrigineel";
                    String outputFolderPath = workingDirPath + File.separator + "archive";
                    File workingDir = new File(workingDirPath);
                    File output = new File(outputFolderPath);
                    output.mkdir();

                    makeArchives(outputFolderPath, workingDir);
                    importArchives(output);
                }
            }
            context.commit();

        } catch (Exception e) {
            printAndLogError(e);
        }
    }

    protected void importArchives(File outputFolder) throws Exception {

        File[] documentArchives = outputFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("aangifte");
            }
        });

        File[] dossierArchives = outputFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("IdentificatieMetaData");
            }
        });

        if (dossierArchives.length != 1) {
            throw new IllegalStateException(dossierArchives.length + " dossier archives found");
        }
        File dossierArchive = dossierArchives[0];

        List<Item> createdItems = new LinkedList<Item>();
        List<com.atmire.dspace.content.Document> documents = new LinkedList<com.atmire.dspace.content.Document>();
        for (File documentArchive : documentArchives) {
            Item documentItem = importItem(outputFolder, documentArchive, LneUtils.getDocumentCollections(community));

            createImportBundle(documentItem, documentArchive);

            com.atmire.dspace.content.Document document = new com.atmire.dspace.content.Document(documentItem);
            documents.add(document);
            createdItems.add(documentItem);
        }

        Item dossierItem = importItem(outputFolder, dossierArchive, LneUtils.getDossierCollections(community));

        createImportBundle(dossierItem, dossierArchive);

        Dossier dossier = new Dossier(dossierItem, documents);
        dossierService.create(context, dossier);

        createdItems.add(dossierItem);
        for (Item createdItem : createdItems) {
            createdItem.decache();
        }
    }

    private void createImportBundle(Item item, File folder) throws SQLException, IOException, AuthorizeException {
        Bundle bundle = item.createBundle("IMPORT");
        InputStream inputstream = new FileInputStream(folder.getPath() + File.separator + "source.xml");
        Bitstream bs = bundle.createBitstream(inputstream);
        bs.setName("source.xml");
        BitstreamFormat bf = FormatIdentifier.guessFormat(context, bs);
        bs.setFormat(bf);
        bs.update();
    }

    protected Item importItem(File outputFolder, File itemArchive, Collection[] collections) throws Exception {
        ItemImport myloader = new ItemImport();
        String path = outputFolder.getAbsolutePath();
        String itemFolder = itemArchive.getName();
        String mapFile = outputFolder.getAbsolutePath() + File.separator + "mapfile-" + itemFolder; //do not include it inside the itemArchive
        PrintWriter mapOut = new PrintWriter(new FileWriter(mapFile, false));
        boolean useTemplate = false;
        return myloader.addItem(context, collections, path, itemFolder, mapOut, useTemplate);
    }

    protected void makeArchives(String outputFolderPath, File dir) throws ParserConfigurationException, SAXException, IOException, XSLTransformException {
        File[] xmlFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });

        for (File xmlFile : xmlFiles) {
            DocumentBuilder dBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            if (validationEnabled) {
                ValidateAgainstSchema(doc, schemaString);
            }

            convertToArchive(xmlFile, outputFolderPath + File.separator + "result.xml", XSLPath);
        }
    }

    protected void ValidateAgainstSchema(Document doc, String schemaString) throws IOException, SAXException {
        Schema schema = schemaFactory.newSchema(new File(org.dspace.core.ConfigurationManager.getProperty("dspace.dir") + File.separator + "config" + File.separator + "schemas-imjv" + File.separator + "xsd" + File.separator + schemaString));

        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(doc));
    }

    protected boolean convertToArchive(File input, String outputPath, String xslPath) throws XSLTransformException {
        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(input);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(new File(xslPath));
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(new File(outputPath));

        try {
            Transformer transformer = getTransformer(xsltSource, xslPath);
            transformer.transform(xmlSource, result);
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

    private Transformer getTransformer(Source xsltSource, String xslPath) throws TransformerConfigurationException {
        if (transformer == null) {
            // create an instance of TransformerFactory
            try {
                transformer = transFactory.newTransformer(xsltSource);
            } catch (TransformerConfigurationException e) {
                print("Error: the stylesheet at '" + xslPath + "' couldn't be used");
                throw e;
            }
        }
        return transformer;

    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getXSLPath() {
        return XSLPath;
    }

    public void setXSLPath(String XSLPath) {
        this.XSLPath = XSLPath;
    }

    public String getSchemaString() {
        return schemaString;
    }

    public void setSchemaString(String schemaString) {
        this.schemaString = schemaString;
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setValidationEnabled(boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }
}
