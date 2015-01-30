/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.administer;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.cli.*;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;

import java.util.Map;

public class CreateResourcePolicy {

    private final Context context;

    private final Map<Integer, String> actionMap;
    private final Map<Integer, String> resourceTypeMap;

    /**
     * For invoking via the command line.  If called with no command line arguments,
     * it will negotiate with the user for the administrator details
     *
     * @param argv command-line arguments
     */
    public static void main(String[] argv) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        CreateResourcePolicy createResourcePolicy = new CreateResourcePolicy();

        options.addOption("h", "help", false, "Help");
        options.addOption(OptionBuilder.withLongOpt("group")
                .withDescription("Group id")
                .withType(Number.class)
                .hasArg()
                .create("g"));
        options.addOption(OptionBuilder.withLongOpt("type")
                .withDescription("Resource type")
                .withType(Number.class)
                .hasArg()
                .create("t"));
        options.addOption(OptionBuilder.withLongOpt("id")
                .withDescription("Resource id")
                .withType(Number.class)
                .hasArg()
                .create("i"));
        options.addOption(OptionBuilder.withLongOpt("action")
                .withDescription("Action")
                .withType(Number.class)
                .hasArg()
                .create("a"));

        CommandLine line = parser.parse(options, argv);

        if (line.hasOption('h')) {
            createResourcePolicy.printHelp(options);
        } else if (line.hasOption("t") && line.hasOption("i") && line.hasOption("a")) {
            final int group = ((Number) line.getParsedOptionValue("g")).intValue();
            final int action = ((Number) line.getParsedOptionValue("a")).intValue();
            final int type = ((Number) line.getParsedOptionValue("t")).intValue();
            final int id = ((Number) line.getParsedOptionValue("i")).intValue();
            if (createResourcePolicy.getResourceTypeMap().containsKey(type)) {
                if (createResourcePolicy.getActionMap().containsKey(action)) {
                    final int policyId = createResourcePolicy.createPolicy(group, id, type, action);
                    if (policyId != -1 && policyId != 0) {
                        System.out.println("Policy created with ID: [" + policyId + "]");
                    } else if(policyId == -1) {
                        System.out.println("Policy could not be created because the resource could not be found!");
                    } else {
                        System.out.println("Policy could not be created because the group could not be found!");
                    }
                } else {
                    System.out.println("Policy could not be created because the action is unknown!");
                }
            } else {
                System.out.println("Policy could not be created because the resource type is unknown!");
            }

        }
        System.exit(0);
    }

    /**
     * constructor, which just creates and object with a ready context
     *
     * @throws Exception
     */
    private CreateResourcePolicy() throws Exception {
        context = new Context();
        actionMap = ImmutableMap.<Integer, String>builder()
                .put(Constants.READ, "Read")
                .put(Constants.WRITE, "Write")
                .put(Constants.DELETE, "Delete")
                .put(Constants.ADD, "Add")
                .put(Constants.REMOVE, "Remove")
                .put(Constants.DEFAULT_BITSTREAM_READ, "Default Bitstream Read")
                .put(Constants.DEFAULT_ITEM_READ, "Default Item Read")
                .put(Constants.ADMIN, "Admin")
                .build();
        resourceTypeMap = ImmutableMap.<Integer, String>builder()
                .put(Constants.BITSTREAM, "Bitstream")
                .put(Constants.BUNDLE, "Bundle")
                .put(Constants.ITEM, "Item")
                .put(Constants.COLLECTION, "Collection")
                .put(Constants.COMMUNITY, "Community")
                .put(Constants.SITE, "Site")
                .put(Constants.GROUP, "Group")
                .put(Constants.EPERSON, "EPerson")
                .build();
    }

    public int createPolicy(int groupId, int resourceId, int resourceType, int action) throws Exception {
        context.setIgnoreAuthorization(true);
        final Group group = Group.find(context, groupId);
        if (group != null) {
            final DSpaceObject resource = DSpaceObject.find(context, resourceType, resourceId);
            if (resource != null) {
                final ResourcePolicy myPolicy = ResourcePolicy.create(context);
                myPolicy.setResource(resource);
                myPolicy.setAction(action);
                myPolicy.setGroup(group);
                myPolicy.update();
                context.complete();
                return myPolicy.getID();
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    /**
     * Print the help options for the user
     *
     * @param options that are available for the user
     */
    private void printHelp(Options options) {
        HelpFormatter myhelp = new HelpFormatter();

        myhelp.printHelp("Policy Creator for Resources\n", options);

        System.out.println("\nPossible Resource types:");
        for (Integer key : resourceTypeMap.keySet()) {
            System.out.print("\n\t" + resourceTypeMap.get(key) + ": " + key);
        }

        System.out.println("\n");

        System.out.println("\nPossible Actions:");
        for (Integer key : actionMap.keySet()) {
            System.out.print("\n\t" + actionMap.get(key) + ": " + key);
        }
        System.out.println("\n");
    }

    public Map<Integer, String> getActionMap() {
        return actionMap;
    }

    public Map<Integer, String> getResourceTypeMap() {
        return resourceTypeMap;
    }
}
