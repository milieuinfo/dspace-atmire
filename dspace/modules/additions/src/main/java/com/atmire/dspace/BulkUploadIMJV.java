package com.atmire.dspace;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.HashMap;

/**
 * Created by philip on 07/11/14.
 */
public class BulkUploadIMJV {
    public static void main(String[] args) {
        try {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();
            Option inputDirectoryOption = OptionBuilder.withArgName("directory").hasArg().withDescription("directory containing the xml and bitstream files").isRequired().create('d');
            Option schemaOption = OptionBuilder.withArgName("schema").hasArg().withDescription("schema name to validate the xml against").create('s');
            Option validationOption = OptionBuilder.withArgName("validation").withDescription("use this argument to disable validation based on a schema, validation is enabled by default").create('v');
            Option outputDirectoryOption = OptionBuilder.withArgName("outputdirectory").hasArg().withDescription("specify a directory to save the output, the default is /archive in the directory containing the xml and bitstream files").create('o');

            options.addOption(inputDirectoryOption);
            options.addOption(schemaOption);
            options.addOption(validationOption);
            options.addOption(outputDirectoryOption);
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

            String outputFolderPath = "";
            if(line.hasOption('o')){
                outputFolderPath = line.getOptionValue("o");
            }
            else {
                outputFolderPath = inputFolderPath + File.separator + "archive";
            }

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

            int itemCounter = 0;

            for (File xmlFile : xmlFiles) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);

                if(!line.hasOption("v")) {
                    String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
                    SchemaFactory factory = SchemaFactory.newInstance(language);
                    Schema schema = factory.newSchema(new File(org.dspace.core.ConfigurationManager.getProperty("dspace.dir") + File.separator + "config" + File.separator + "schemas-imjv" + File.separator + "xsd" + File.separator + schemaString));

                    Validator validator = schema.newValidator();
                    validator.validate(new DOMSource(doc));
                }

                NodeList items = doc.getFirstChild().getChildNodes();

                StringBuilder stringBuilder = new StringBuilder();

                String commonIdentifier = "";

                for(int i = 0; i<items.getLength();i++){
                    if(items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element item = (Element) items.item(i);

                        if (item.getNodeName().equals("IdentificatieMetaData")) {
                            stringBuilder.append("<dublin_core>\n");

                            HashMap<String, String[]> hashmap = createIdentificatieMetaDataHashmap();

                            createDublinCoreFileString(stringBuilder, hashmap, item);

                            NodeList identifierList = item.getElementsByTagName("CBBExploitatieNummer");

                            if(identifierList!= null && identifierList.getLength()>0){
                                commonIdentifier = identifierList.item(0).getTextContent();
                            }

                            addCommonIdentifier(stringBuilder, commonIdentifier);

                        }
                        else if(item.getNodeName().equals("MilieuVerslagMetaData")){
                            HashMap<String, String[]> hashmap = createMilieuVerslagMetaDataHashmap();

                            createDublinCoreFileString(stringBuilder, hashmap, item);

                            stringBuilder.append("</dublin_core>\n");

                            String path = output.getAbsolutePath() + File.separator + "ITEM_" + String.format("%04d", itemCounter++) + File.separator + "dublin_core.xml";
                            writeToFile(stringBuilder, path);
                        }
                        else {
                            HashMap<String, String[]> hashmap = createAangifteHashmap();

                            NodeList aangiftes = item.getElementsByTagName("Aangifte");


                            for(int j = 0; j< aangiftes.getLength();j++) {

                                if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                                    Element aangifte = (Element) aangiftes.item(j);
                                    stringBuilder.setLength(0);

                                    stringBuilder.append("<dublin_core>\n");

                                    createDublinCoreFileString(stringBuilder, hashmap, aangifte);
                                    addCommonIdentifier(stringBuilder, commonIdentifier);

                                    stringBuilder.append("</dublin_core>\n");

                                    String outputItemPath = output.getAbsolutePath() + File.separator + "ITEM_" + String.format("%04d", itemCounter++);
                                    writeToFile(stringBuilder, outputItemPath + File.separator + "dublin_core.xml");

                                    NodeList aangiftePDFs = aangifte.getElementsByTagName("AangiftePdf");

                                    for (int h = 0; h<aangiftePDFs.getLength();h++){
                                        addFileToArchive(inputFolderPath, outputItemPath,aangiftePDFs.item(h).getTextContent());
                                    }

                                    NodeList bestanden = aangifte.getElementsByTagName("Bestand");

                                    for (int h = 0; h<bestanden.getLength();h++){
                                        addFileToArchive(inputFolderPath, outputItemPath,bestanden.item(h).getTextContent());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addCommonIdentifier(StringBuilder stringBuilder, String commonIdentifier) {
        stringBuilder.append("<dcvalue element=\"identifier\" qualifier=\"other\">" + commonIdentifier + "</dcvalue>\n");
    }

    private static void addFileToArchive(String inputFolderPath, String outputItemPath, String filename) throws IOException {
        File source = new File(inputFolderPath + File.separator + filename);
        File destination = new File(outputItemPath + File.separator + filename);
        File contents = new File(outputItemPath + File.separator + "contents");

        FileUtils.copyFile(source,destination);

        BufferedWriter output;
        output = new BufferedWriter(new FileWriter(contents,true));
        output.append(destination.getName());
        output.newLine();
        output.close();
    }

    private static void writeToFile( StringBuilder stringBuilder, String path) throws IOException {
        File file = new File(path);

        FileUtils.writeStringToFile(file, stringBuilder.toString());
    }

    private static void createDublinCoreFileString(StringBuilder stringBuilder, HashMap<String, String[]> hashmap, Element item) {
        for (String s : hashmap.keySet()) {
            String[] splitPath = s.split("/");

            NodeList nodeList = item.getElementsByTagName(splitPath[0]);

            // go down the nodetree to the item of which we want the value
            for (int i = 1; i<splitPath.length;i++) {
                if (nodeList.getLength()>0 && nodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                    nodeList = ((Element) nodeList.item(0)).getElementsByTagName(splitPath[i]);
                }
            }

            for (int j = 0; j < nodeList.getLength(); j++) {
                stringBuilder.append("<dcvalue element=\"" +  hashmap.get(s)[0]);

                if(!hashmap.get(s)[1].equals("")){
                    stringBuilder.append("\" qualifier=\"" + hashmap.get(s)[1]);
                }

                stringBuilder.append("\">");

                stringBuilder.append(nodeList.item(j).getTextContent());

                stringBuilder.append("</dcvalue>\n");

            }
        }

    }

    private static HashMap<String, String[]> createIdentificatieMetaDataHashmap() {
        HashMap<String, String[]> hashMap = new HashMap<String, String[]>();
        hashMap.put("Exploitatie/Naam",new String[]{"contributor","author"});

        return hashMap;
    }


    private static HashMap<String, String[]> createMilieuVerslagMetaDataHashmap() {
        HashMap<String, String[]> hashMap = new HashMap<String, String[]>();

        return hashMap;
    }

    private static HashMap<String, String[]> createAangifteHashmap() {
        HashMap<String, String[]> hashMap = new HashMap<String, String[]>();
        hashMap.put("AangifteType",new String[]{"type",""});

        return hashMap;
    }
}
