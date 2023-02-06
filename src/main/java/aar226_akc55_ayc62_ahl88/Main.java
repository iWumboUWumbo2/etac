package aar226_akc55_ayc62_ahl88;

import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

class Main {
    private static String outputDirectory;

    private static boolean isDirSpecified;

    // Write the lexed string into the corresponding file name
    private static void writeLexedOutput(String filename, String lexed) {
        if (!isDirSpecified) {
            filename = Paths.get(filename).getFileName().toString();
        }

        Path path = Paths.get(outputDirectory, filename);

        String pathname = path.toString();
        pathname = pathname.substring(0, pathname.length() - 3) + "lexed";

        Path parentPath = path.getParent();
        String dirname = (parentPath == null) ? "" : parentPath.toString();

        // Create directory
        File dir = new File(dirname);
        dir.mkdirs();

        // Create file
        try {
            File file = new File(pathname);
            file.createNewFile();
        }
        catch (IOException e) {
            System.out.println("An error occurred when writing to file.");
            e.printStackTrace();
            return;
        }

        // Write to file
        try {
            FileWriter myWriter = new FileWriter(pathname);

            myWriter.write(lexed);
            myWriter.close();

            System.out.println("Successfully wrote to the file.");
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void lexFile(String filename, StringBuilder lexedOutput) throws IOException {
        try {
            if (filename.endsWith(".eta")) {
                try {
                    EtaLexer etaLexer = new EtaLexer(new FileReader(filename));
                    try {
                        while (true) {
                            EtaLexer.Token t = etaLexer.nextToken();
                            if (t == null) break;

                            if (t.type == EtaLexer.TokenType.ERROR) {
                                String lexed = String.format("%d:%d error: %s\n", t.line,
                                        t.col, t.text);
                                lexedOutput.append(lexed);
                                break;
                            }

                            String lexed = String.format("%d:%d %s\n", t.line, t.col, t.text);
                            lexedOutput.append(lexed);
                        }
                    }
                    catch (Error e) {
                        String lexed = String.format("%d:%d lexical error \n", etaLexer.lineNumber(),
                                etaLexer.column());
                        lexedOutput.append(lexed);
                    }
                }
                catch (FileNotFoundException fileNotFoundException) {
                    System.out.println(fileNotFoundException.getMessage());
                    return;
                }
            }
            else {
                throw new FileNotFoundException(
                        "Invalid filename "
                                + filename
                                + " provided: All files passed to etac must have a .eta extension");
            }
        }
        catch (FileNotFoundException invalidFilename) {
            System.out.println(invalidFilename.getMessage());
            return;
        }

        writeLexedOutput(filename, lexedOutput.toString());
    }

    public static void main(String[] args) throws java.io.IOException {
        // Create the command line parser
        CommandLineParser parser = new DefaultParser();

        // Create the Options
        Options options = new Options();

        Option helpOpt = new Option("h", "help", false,
                "Print a synopsis of options.");
        Option lexOpt = new Option(null, "lex", true,
                "Generate output from lexical analysis.");
        Option dirOpt   = new Option ("D", true,
                "Specify where to place generated diagnostic files.");

        lexOpt.setArgs(Option.UNLIMITED_VALUES);

        options.addOption(helpOpt);
        options.addOption(dirOpt);
        options.addOption(lexOpt);

        HelpFormatter formatter = new HelpFormatter();

        isDirSpecified = false;
        outputDirectory = Paths.get("").toAbsolutePath().toString();

//        System.out.println(outputDirectory);

        try {
            CommandLine cmd = parser.parse(options, args);
            if (args.length == 0 || cmd.hasOption("help")) {
                formatter.printHelp("etac [options] <source files>", options);
                return;
            }

            if (cmd.hasOption("D")) {
                outputDirectory = cmd.getOptionValue("D");
                isDirSpecified = true;
            }

            if (cmd.hasOption("lex")) {
                String[] filenames = cmd.getOptionValues("lex");
                for (String filename : filenames) {
                    lexFile(filename, new StringBuilder());
                }
            }
        }
        catch (ParseException parseException) {
            formatter.printHelp("etac [options] <source files>", options);
            System.out.println("Unexpected exception: " + parseException.getMessage());
        }
    }
}