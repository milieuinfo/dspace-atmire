package com.atmire.lne.xml;

import com.atmire.dspace.content.RelationshipType;
import com.atmire.dspace.content.RelationshipTypeService;
import com.atmire.scripts.ContextScript;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.dspace.core.ConfigurationManager;
import org.dspace.utils.DSpace;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 17 Nov 2014
 */
public class UpdateRelationshipTypes extends ContextScript {

    protected String xmlPath;

    public static void main(String[] args) {
        UpdateRelationshipTypes script = new UpdateRelationshipTypes();
        script.mainImpl(args);
    }

    @Override
    protected Options createCommandLineOptions() {
        Options options = super.createCommandLineOptions();
        options.addOption("f", "file", true, "Path to the xml file. If omitted the default configuration is used.");
        return options;
    }

    @Override
    protected boolean processArgs(CommandLine line) {
        if (line.hasOption('f')) {
            String file = line.getOptionValue('f');
            setXmlPath(file);
        } else {
            String file = ConfigurationManager.getProperty("dspace.dir") + File.separator + "config" + File.separator + "relationship-types.xml";
            setXmlPath(file);
        }
        return false;
    }

    @Override
    public void run() {

        try {
            RelationshipTypeService relationshipTypeService = new DSpace().getServiceManager().getServicesByType(RelationshipTypeService.class).get(0);

            File file = new File(xmlPath);
            FileInputStream fileInputStream = new FileInputStream(file);
            RelationshipTypeXMLConverter converter = new RelationshipTypeXMLConverter();
            List<RelationshipType> types = converter.getTypes(fileInputStream);
            Set<RelationshipType> uniqueTypes = new HashSet<RelationshipType>(types);
            for (RelationshipType type : uniqueTypes) {
                try {
                    RelationshipType found = relationshipTypeService.findByExampleUnique(context, type);
                    if (found == null) {
                        relationshipTypeService.create(context, type);
                        print("New type added: " + type);
                    } else {
                        print("Already present: " + type);
                    }
                } catch (IllegalArgumentException notUnique) {
                    print("Offending type: " + type);
                    printAndLogError(notUnique);
                }
            }
            context.commit();
        } catch (Exception e) {
            printAndLogError(e);
        }

    }

    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }
}
