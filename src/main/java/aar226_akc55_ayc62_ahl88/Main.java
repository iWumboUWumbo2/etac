package aar226_akc55_ayc62_ahl88;

import aar226_akc55_ayc62_ahl88.Errors.EtaError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.Program;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import java_cup.runtime.Symbol;
import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Main {
    private static String outputDirectory;
    private static String inputDirectory;
    public static String libpathDirectory;

    private static boolean isOutputDirSpecified;
    private static boolean isInputDirSpecified;
    public static boolean isLibpathDirSpecified;

    // Write the lexed string into the corresponding file name
    private static void writeOutput(String filename, String output, String extension) {
        Path path = (isOutputDirSpecified)
                        ? Paths.get(outputDirectory, filename)
                        : Paths.get(filename);

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
            System.out.println("An error occurred when creating the file " + filename);
            return;
        }

        // Write to file
        try {
            FileWriter myWriter = new FileWriter(pathname);

            myWriter.write(output);
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred when writing to the file " + filename);
        }
    }

    private static void parseFile(String filename, StringBuilder parsedOutput, boolean shouldWrite) throws IOException {
        try {
            String zhenFilename =
                    (isInputDirSpecified)
                            ? Paths.get(inputDirectory, filename).toString()
                            : filename;

            if (filename.endsWith(".eta")) {
                try {
                    EtaParser p = new EtaParser(new Lexer(new FileReader(zhenFilename)));
                    try {
                        Program result = (Program) p.parse().value;
                        StringWriter out = new StringWriter();

                        PrintWriter cw = new PrintWriter(out);
                        CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(cw);
                        result.prettyPrint(printer);
                        printer.close();

                        if (shouldWrite) {
                            writeOutput(filename, out.toString(), "parsed");
                        }
                    }
                    catch (EtaError e) {
//                        System.out.printf("%s error beginning at %s:%d:%d: %s\n",
//                                e.getErrorType(), zhenFilename, e.getLine(), e.getCol(), e.getErrorString());
                        if (shouldWrite) {
                            writeOutput(filename, e.getMessage(), "parsed");
                        }
                    }
                }
                catch (Exception e){
                    System.out.println("File without name " + filename + " found");
                }
            }
            else if (filename.endsWith(".eti")) {
                try {
                    EtiParser p = new EtiParser(new Lexer(new FileReader(zhenFilename)));
                    try {
                        EtiInterface result = (EtiInterface) p.parse().value;
                        StringWriter out = new StringWriter();

                        PrintWriter cw = new PrintWriter(out);
                        CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(cw);
                        result.prettyPrint(printer);
                        printer.close();
                        if (shouldWrite) {
                            writeOutput(filename, out.toString(), "parsed");
                        }
                    }
                    catch (EtaError e) {
//                        System.out.printf("%s error beginning at %s:%d:%d: %s\n",
//                                e.getErrorType(), zhenFilename, e.getLine(), e.getCol(), e.getErrorString());
                        if (shouldWrite) {
                            writeOutput(filename, e.getMessage(), "parsed");
                        }
                    }
                }
                catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("No file with ");
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
    private static void typeCheckFile(String filename, boolean shouldWrite) throws IOException {
        try {
            String zhenFilename =
                    (isInputDirSpecified) ? Paths.get(inputDirectory, filename).toString() : filename;
            if (filename.endsWith(".eta")) {
                try {
                    EtaParser p = new EtaParser(new Lexer(new FileReader(zhenFilename)));
                    try {
                        Program result = (Program) p.parse().value;
                        SymbolTable<Type> context = new SymbolTable<>();
                        result.typeCheck(context,zhenFilename);
                        if (shouldWrite) {
                            writeOutput(filename, "Valid Eta Program", "typed");
                        }
                    }
                    catch (EtaError e) {
                        System.out.printf("%s error beginning at %s:%d:%d: %s\n",
                                e.getErrorType(), zhenFilename, e.getLine(), e.getCol(), e.getErrorString());
                        if (shouldWrite) {
                            writeOutput(filename, e.getMessage(), "typed");
                        }
                    }
                catch (Error e) {
//                        System.out.println("FIX THIS ERROR INTO ONE OF THE THREE");
                        if (shouldWrite) {
                            writeOutput(filename, e.getMessage(), "typed");
                        }
                    }
                }catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("No file found with filename " + filename);
                }
            }
            else if (filename.endsWith(".eti")) {
                try {
                    EtiParser p = new EtiParser(new Lexer(new FileReader(zhenFilename)));
                    try {
                        EtiInterface result = (EtiInterface) p.parse().value;
                        SymbolTable<Type> context = new SymbolTable<Type>();
                        result.firstPass();
                        if (shouldWrite) {
                            writeOutput(filename, "Valid Eta Program", "typed");
                        }
                    }
                    catch (EtaError e) {
                        System.out.printf("%s error beginning at %s:%d:%d: %s\n",
                                e.getErrorType(), zhenFilename, e.getLine(), e.getCol(), e.getErrorString());
                        if (shouldWrite) {
                            writeOutput(filename, e.getMessage(), "typed");
                        }
                    }
//                    catch (Error e) {
//                        if (shouldWrite) {
//                            writeOutput(filename, e.getMessage(), "typed");
//                        }
//                    }
                }
                catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("No file found with filename " + filename);
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
    private static void lexFile(String filename, StringBuilder lexedOutput, boolean shouldWrite) throws IOException {
        try {
            if (filename.endsWith(".eta") || filename.endsWith(".eti")) {

                String zhenFilename =
                        (isInputDirSpecified) ? Paths.get(inputDirectory, filename).toString() : filename;

                FileReader f;
                try {
                    f = new FileReader(zhenFilename);
                }
                catch(Exception e) {
//                    System.out.println(e.getMessage());
                    return;
                }
                Lexer etaLexer = new Lexer(f);
                try {
                    while (true) {
                        Symbol t = etaLexer.next_token();
                        if (t.sym == sym.EOF) break;
                        String lexed = prettyOut(t);
                        lexedOutput.append(lexed);
                    }
                }
                catch (EtaError e) {
//                    System.out.printf("%s error beginning at %s:%d:%d: %s\n",
//                            e.getErrorType(), zhenFilename, e.getLine(), e.getCol(), e.getErrorString());
                    lexedOutput.append(e.getMessage());
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

        if (shouldWrite) {
            writeOutput(filename, lexedOutput.toString(), "lexed");
        }
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
        Option parseOpt = new Option(null, "parse", true,
                "Generate output from syntactic analysis.");
        Option typeOpt = new Option(null, "typecheck", true,
                "Generate output from semantic analysis.");
        Option sourcepathOpt   = new Option ("sourcepath", true,
                "Specify where to find input source files.");
        Option dirOpt   = new Option ("D", true,
                "Specify where to place generated diagnostic files.");
        Option libpathOpt = new Option ("libpath", true,
                "Specify where to find library interface files.");

        lexOpt.setArgs(Option.UNLIMITED_VALUES);

        options.addOption(helpOpt);
        options.addOption(dirOpt);
        options.addOption(parseOpt);
        options.addOption(sourcepathOpt);
        options.addOption(lexOpt);
        options.addOption(typeOpt);
        options.addOption(libpathOpt);

        HelpFormatter formatter = new HelpFormatter();

        isOutputDirSpecified = isInputDirSpecified = isLibpathDirSpecified = false;
        outputDirectory = inputDirectory = libpathDirectory = Paths.get("").toAbsolutePath().toString();

//        System.out.println(outputDirectory);

        try {
            CommandLine cmd = parser.parse(options, args);
            if (args.length == 0 || cmd.hasOption("help")) {
                formatter.printHelp("etac [options] <source files>", options);
                return;
            }

            if (cmd.hasOption("D")) {
                outputDirectory = cmd.getOptionValue("D");
                isOutputDirSpecified = true;
            }

            if (cmd.hasOption("sourcepath")) {
                inputDirectory = cmd.getOptionValue("sourcepath");
                isInputDirSpecified = true;
            }

            if (cmd.hasOption("libpath")) {
                libpathDirectory = cmd.getOptionValue("libpath");
                isLibpathDirSpecified = true;
            }

            if (cmd.hasOption("lex")) {
                String[] filenames = cmd.getOptionValues("lex");
                for (String filename : filenames) {
                    typeCheckFile(filename, false);
                    lexFile(filename, new StringBuilder(), true);
                }
            }

            if (cmd.hasOption("parse")) {
                String[] filenames = cmd.getOptionValues("parse");
                for (String filename : filenames) {
                    typeCheckFile(filename, false);
                    parseFile(filename, new StringBuilder(), true);
                }
            }
            if (cmd.hasOption("typecheck")) {
                String[] filenames = cmd.getOptionValues("typecheck");
                for (String filename : filenames) {
                    typeCheckFile(filename, true);
                }
            }
        }
        catch (ParseException parseException) {
            formatter.printHelp("etac [options] <source files>", options);
            System.out.println("Unexpected exception: " + parseException.getMessage());
        }
    }
}