package aar226_akc55_ayc62_ahl88;

import aar226_akc55_ayc62_ahl88.ast.*;
import java_cup.runtime.Symbol;
import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
class Main {
    private static String outputDirectory;

    private static boolean isDirSpecified;

    // Write the lexed string into the corresponding file name
    private static void writeOutput(String filename, String output, String extension) {
        if (!isDirSpecified) {
            filename = Paths.get(filename).getFileName().toString();
        }

        Path path = Paths.get(outputDirectory, filename);

        String pathname = path.toString();
        pathname = pathname.substring(0, pathname.length() - 3) + extension;

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

            myWriter.write(output);
            myWriter.close();

            System.out.println("Successfully wrote to the file.");
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void parseFile(String filename, StringBuilder parsedOutput) throws IOException {
        try {
            if (filename.endsWith(".eta")) {
                try {
                    parser p = new parser(new Lexer(new FileReader(filename)));
                    Program result = (Program) p.parse().value;
                    StringWriter out = new StringWriter();
//                    PrintWriter cw = new PrintWriter(System.out);
                    PrintWriter cw = new PrintWriter(out);
                    CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(cw);
                    result.prettyPrint(printer);
                    printer.close();
                    writeOutput(filename, out.toString(), "parsed");
//                    System.out.println("Result = " + result );
                }
                catch (Exception e){
                    System.out.println("Parsing Error or file issue");
                    e.getMessage();
                    e.printStackTrace();
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
    }
    private static String prettyOut(Symbol s){
        String out = s.value().toString();
        switch (s.sym){
            case (sym.INTEGER_LITERAL):
                if (out.length() > 1){
                    out = out.substring(1);
                }
                out = "integer " + out;
                break;
            case (sym.BOOL_LITERAL):
                out = "boolean " + out;
                break;
            case (sym.IDENTIFIER):
                out = "id " +  out;
                break;
            case (sym.CHARACTER_LITERAL):
                out = "character " +  out;
                break;
            case (sym.STRING_LITERAL):
                out = "string " + out;
                break;
            default:
                break;
        }
        return String.format("%d:%d %s\n", s.left, s.right, out);
    }
    private static void lexFile(String filename, StringBuilder lexedOutput) throws IOException {
        try {
            if (filename.endsWith(".eta")) {
                FileReader f;
                try {
                    f = new FileReader(filename);
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                    return;
                }
                Lexer etaLexer = new Lexer(f);
                try {
                    while (true) {
                        Symbol t = etaLexer.next_token();
                        if (t.sym == sym.EOF) break;
                        String lexed =  prettyOut(t);
                        lexedOutput.append(lexed);
                    }
                }
                catch (Error e) {
                    System.out.println(e.getMessage());
                    String lexed = e.getMessage();
                    lexedOutput.append(lexed);
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

        writeOutput(filename, lexedOutput.toString(), "lexed");
    }

//    private static void lexFile(String filename, StringBuilder lexedOutput) throws IOException {
//        try {
//            if (filename.endsWith(".eta")) {
//                try {
//                    EtaLexer etaLexer = new EtaLexer(new FileReader(filename));
//                    try {
//                        while (true) {
//                            EtaLexer.Token t = etaLexer.nextToken();
//                            if (t == null) break;
//
//                            if (t.type == EtaLexer.TokenType.ERROR) {
//                                String lexed = String.format("%d:%d error: %s\n", t.line,
//                                        t.col, t.text);
//                                lexedOutput.append(lexed);
//                                break;
//                            }
//
//                            String lexed = String.format("%d:%d %s\n", t.line, t.col, t.text);
//                            lexedOutput.append(lexed);
//                        }
//                    }
//                    catch (Error e) {
//                        String lexed = String.format("%d:%d lexical error \n", etaLexer.lineNumber(),
//                                etaLexer.column());
//                        lexedOutput.append(lexed);
//                    }
//                }
//                catch (FileNotFoundException fileNotFoundException) {
//                    System.out.println(fileNotFoundException.getMessage());
//                    return;
//                }
//            }
//            else {
//                throw new FileNotFoundException(
//                        "Invalid filename "
//                                + filename
//                                + " provided: All files passed to etac must have a .eta extension");
//            }
//        }
//        catch (FileNotFoundException invalidFilename) {
//            System.out.println(invalidFilename.getMessage());
//            return;
//        }
//
//        writeLexedOutput(filename, lexedOutput.toString());
//    }

    public static void main(String[] args) throws java.io.IOException {
        // Create the command line parser
        CommandLineParser parser = new DefaultParser();

        // Create the Options
        Options options = new Options();

        Option helpOpt = new Option("h", "help", false,
                "Print a synopsis of options.");
        Option lexOpt = new Option(null, "lex", true,
                "Generate output from lexical analysis.");
        Option parseOpt = new Option(null, "parse", true,
                "Generate output from lexical analysis.");
        Option dirOpt   = new Option ("D", true,
                "Specify where to place generated diagnostic files.");

        lexOpt.setArgs(Option.UNLIMITED_VALUES);

        options.addOption(helpOpt);
        options.addOption(dirOpt);
        options.addOption(parseOpt);
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

            if (cmd.hasOption("parse")) {
                String[] filenames = cmd.getOptionValues("parse");
                for (String filename : filenames) {
                    parseFile(filename, new StringBuilder());
                }
            }
        }
        catch (ParseException parseException) {
            formatter.printHelp("etac [options] <source files>", options);
            System.out.println("Unexpected exception: " + parseException.getMessage());
        }
    }
}