/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.administer;

import org.apache.commons.cli.*;
import org.dspace.core.Context;
import org.dspace.eperson.Group;

public final class CreateGroup {

    private final Context context;

    /**
     * For invoking via the command line.  If called with no command line arguments,
     * it will negotiate with the user for the administrator details
     *
     * @param argv command-line arguments
     */
    public static void main(String[] argv) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        CreateGroup createGroups = new CreateGroup();

        options.addOption("h", "help", false, "Help");
        options.addOption("n", "name", true, "Group name");

        CommandLine line = parser.parse(options, argv);

        if (line.hasOption('h')) {
            printHelp(options);
        } else if (line.hasOption("n")) {
            final String name = line.getOptionValue("n");
            final int groupId = createGroups.createGroup(name);
            System.out.println("Group '" + name + "' created with ID:" + groupId);
        }

        System.exit(0);
    }

    /**
     * constructor, which just creates and object with a ready context
     *
     * @throws Exception
     */
    private CreateGroup() throws Exception {
        context = new Context();
    }

    public int createGroup(String name) throws Exception {
        context.setIgnoreAuthorization(true);
        final Group group = Group.create(context);
        group.setName(name);
        group.update();
        context.complete();
        return group.getID();
    }

    /**
     * Print the help options for the user
     *
     * @param options that are available for the user
     */
    private static void printHelp(Options options) {
        HelpFormatter myhelp = new HelpFormatter();

        myhelp.printHelp("Group Creator\n", options);
        System.out.println("\nSimply creates a Group with the provided name");
    }

}
