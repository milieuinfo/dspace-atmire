package com.atmire.scripts;

import org.apache.commons.cli.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 21 May 2014
 */
public class Script {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(Script.class);

    protected PrintWriter print = null;

    protected boolean printToLog = false;

    public Script() {
        print = new PrintWriter(System.out, true);
    }

    public static void main(String[] args) {

        Script Script = new Script();
        Script.mainImpl(args);
    }

    protected void mainImpl(String[] args) {
        try {
            if (processArgs(args) == 0) {
                System.exit(0);
            }
            run();

        } catch (ParseException e) {
            print(e.getMessage());
        } catch (Exception e) {
            printAndLogError(e);
        }
    }

    protected int processArgs(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();
        Options options = createCommandLineOptions();
        CommandLine line = parser.parse(options, args);

        // help

        HelpFormatter helpFormatter = new HelpFormatter();
        if (line.hasOption("h")) {
            helpFormatter.printHelp("dsrun " + getClass().getCanonicalName(), options);
            return 0;
        }

        // other arguments
        boolean exit = processArgs(line);
        if (exit) {
            return 0;
        }

        // print to std out
        setPrinter(new PrintWriter(System.out, true));
        return 1;
    }

    /**
     * Hook for subclasses
     * @return true if the script should end after processing the line
     *         false if the script may run
     */
    protected boolean processArgs(CommandLine line) {
        return false;
    }

    protected Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Prints a helpful message about this script's usage");

        /** Example - long
         OptionBuilder.withDescription("File to import");
         OptionBuilder.withLongOpt("file");
         OptionBuilder.hasArg(true);
         OptionBuilder.isRequired(true);
         options.addOption(OptionBuilder.create("f"));
         */
        return options;
    }

    /**
     * The actual functionality of the script.
     */
    public void run() {
        print("Script initializing...");
        try {
            print.println("Running wild !");
        } catch (Exception e) {
            printAndLogError(e);
        }
        print("Script done.");
    }




    public void print(String line) {
        if (printToLog) {
            log.info(line);
        } else {
            print.println(line);
        }
    }

    public void printAndLogError(Exception error) {
        if(print!=null) {
            print.println("Error in Script " + error.getClass());
            error.printStackTrace(print);
        }
        log.error("Error in Script", error);
    }

    public void setPrinter(PrintWriter print) {
        this.print = print;
    }

    public void setPrintToLog(boolean printToLog) {
        this.printToLog = printToLog;
    }
}
