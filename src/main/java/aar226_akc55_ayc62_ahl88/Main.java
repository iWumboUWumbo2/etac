package aar226_akc55_ayc62_ahl88;

import aar226_akc55_ayc62_ahl88.Errors.EtaError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.newast.AstNode;
import aar226_akc55_ayc62_ahl88.newast.Program;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRConst;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.IRNode;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;
import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Main {
    private static String outputDirectory;
    private static String inputDirectory;
    public static String libpathDirectory;

    private static boolean isOutputDirSpecified;
    private static boolean isInputDirSpecified;
    public static boolean isLibpathDirSpecified;

    public static Optimizations opts;

    // Write the lexed string into the corresponding file name
    private static void writeOutput(String filename, String output, String extension) {
        Path path = (isOutputDirSpecified)
                        ? Paths.get(outputDirectory, filename)
                        : Paths.get(filename);

        String pathname = path.toString();
        pathname = pathname.substring(0, pathname.length() - 3) + extension;
//        System.out.println(pathname);
        Path parentPath = path.getParent();
        String dirname = (parentPath == null) ? "" : parentPath.toString();
//        System.out.println(dirname);
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

    private static String getZhenFilename(String filename) {
        return (isInputDirSpecified)
                ? Paths.get(inputDirectory, filename).toString()
                : filename;
    }

    private static void lexFile(String filename, StringBuilder lexedOutput, boolean shouldWrite) throws IOException {
        try {
            if (filename.endsWith(".eta") || filename.endsWith(".eti")) {
                String zhenFilename = getZhenFilename(filename);

                Lexer etaLexer;
                try {
                    etaLexer = new Lexer(new FileReader(zhenFilename));
                } catch (Exception e) {
                    System.out.println("File without name " + filename + " found");
                    return;
                }

                try {
                    while (true) {
                        Symbol t = etaLexer.next_token();
                        if (t.sym == sym.EOF) break;
                        String lexed = prettyOut(t);
                        lexedOutput.append(lexed);
                    }
                }
                catch (EtaError e) {
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

    private static void parseFile(String filename, boolean shouldWrite) throws IOException {
        try {
            String zhenFilename = getZhenFilename(filename);

            Lexer lex;
            try {
                lex = new Lexer(new FileReader(zhenFilename));
            } catch (Exception e) {
                System.out.println("File without name " + filename + " found");
                return;
            }

            lr_parser p = null;
            if (filename.endsWith(".eta")) p = new EtaParser(lex);
            else if (filename.endsWith(".eti")) p = new EtiParser(lex);
            else throw new FileNotFoundException(
                        "Invalid filename "
                                + filename
                                + " provided: All files passed to etac must have a .eta extension");

            try {
                StringWriter out = new StringWriter();
                PrintWriter cw = new PrintWriter(out);
                CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(cw);

                if (filename.endsWith(".eta")) {
                    Program result = (Program) p.parse().value;
                    result.prettyPrint(printer);
                }
                else if (filename.endsWith(".eti")) {
                    EtiInterface result = (EtiInterface) p.parse().value;
                    result.prettyPrint(printer);
                }

                printer.close();
                if (shouldWrite) {
                    writeOutput(filename, out.toString(), "parsed");
                }
            } catch (EtaError e) {
                if (shouldWrite) {
                    writeOutput(filename, e.getMessage(), "parsed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("File without name " + filename + " found");
            }
        }
        catch (FileNotFoundException invalidFilename) {
            System.out.println(invalidFilename.getMessage());
        }
    }

    private static void typeCheckFile(String filename, boolean shouldWrite) throws IOException {
        try {
            String zhenFilename = getZhenFilename(filename);

            Lexer lex = null;
            try {
                lex = new Lexer(new FileReader(zhenFilename));
            } catch (Exception e) {
                System.out.println("No file found with filename " + filename);
                return;
            }

            lr_parser p;
            if (filename.endsWith(".eta")) p = new EtaParser(lex);
            else if (filename.endsWith(".eti")) p = new EtiParser(lex);
            else throw new FileNotFoundException(
                        "Invalid filename "
                                + filename
                                + " provided: All files passed to etac must have a .eta extension");

            try {
                if (filename.endsWith(".eta")) {
                    Program result = (Program) p.parse().value;
                    result.typeCheck(new SymbolTable<>(), zhenFilename);
                } else if (filename.endsWith(".eti")) {
                    EtiInterface result = (EtiInterface) p.parse().value;
                    result.firstPass(); // Just to throw EtaErrors
                }

                if (shouldWrite) {
                    writeOutput(filename, "Valid Eta Program", "typed");
                }
            } catch (EtaError e) {
                e.printError(zhenFilename);
                if (shouldWrite) {
                    writeOutput(filename, e.getMessage(), "typed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("File without name " + filename + " found");
            }
        }
        catch (FileNotFoundException invalidFilename) {
            System.out.println(invalidFilename.getMessage());
        }
    }



    private static IRNode irbuild(String filename) throws FileNotFoundException {
        try {
            String zhenFilename = getZhenFilename(filename);

            Lexer lex = null;
            try {
                lex = new Lexer(new FileReader(zhenFilename));
            } catch (Exception e) {
                System.out.println("No file found with filename " + filename);
                return null;
            }

            lr_parser p;
            if (filename.endsWith(".eta")) p = new EtaParser(lex);
            else if (filename.endsWith(".eti")) p = new EtiParser(lex);
            else throw new FileNotFoundException(
                        "Invalid filename "
                                + filename
                                + " provided: All files passed to etac must have a .eta extension");

            try {
                if (filename.endsWith(".eta")) {
                    Program result = (Program) p.parse().value;
                    result.typeCheck(new SymbolTable<>(), zhenFilename);
                    IRNode ir = result.accept(new IRVisitor(filename.substring(0, filename.length() - 2)));
                    return ir;
                } else if (filename.endsWith(".eti")) {
                    EtiInterface result = (EtiInterface) p.parse().value;
                    result.firstPass(); // Just to throw EtaErrors
                    System.out.println("Fuck");
                }
                else {System.out.println("Gigafuck ");}

//                if (opts.isSet(OptimizationTypes.CONSTANT_FOLDING)) {
//
//                }

            } catch (EtaError e) {
                e.printError(zhenFilename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException invalidFilename) {
            System.out.println(invalidFilename.getMessage());
        }

        return null;
    }

    private static void irgenFile(String filename, boolean shouldWrite) {
        String zhenFilename = getZhenFilename(filename);

        try {
            IRNode ir = irbuild(zhenFilename);

            StringWriter out = new StringWriter();
            PrintWriter pw = new PrintWriter(out);

            CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(pw);
            System.out.println(ir);
            System.out.println(ir instanceof IRCompUnit);
            ir.printSExp(printer);

            printer.close();

            if (shouldWrite) {
                writeOutput(filename, out.toString(), "ir");
            }
        }
        catch (EtaError e) {
            e.printError(zhenFilename);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void irrunFile(String filename, boolean shouldWrite) {
        String zhenFilename = getZhenFilename(filename);

        try {
            IRNode ir = irbuild(zhenFilename);

            StringWriter out = new StringWriter();
            PrintWriter pw = new PrintWriter(out);

            CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(pw);

            IRSimulator sim = new IRSimulator((IRCompUnit) ir);
            sim.call("_Imain_paai", 0);

            printer.close();

            if (shouldWrite) {
                writeOutput(filename, out.toString(), "ir");
            }
        }
        catch (EtaError e) {
            e.printError(zhenFilename);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws java.io.IOException {
        ArrayList<String> filenames = new ArrayList<>();
        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i].endsWith(".eta") || args[i].endsWith(".eti")) {
                filenames.add(args[i]);
            }
        }

        // Create the command line parser
        CommandLineParser parser = new DefaultParser();

        // Create the Options
        Options options = new Options();

        Option helpOpt = new Option("h", "help", false,
                "Print a synopsis of options.");

        Option lexOpt = new Option(null, "lex", false,
                "Generate output from lexical analysis.");
        Option parseOpt = new Option(null, "parse", false,
                "Generate output from syntactic analysis.");
        Option typeOpt = new Option(null, "typecheck", false,
                "Generate output from semantic analysis.");
        Option irgenOpt = new Option (null, "irgen", false,
                "Generate intermediate code.");


        Option sourcepathOpt   = new Option ("sourcepath", true,
                "Specify where to find input source files.");
        Option libpathOpt = new Option ("libpath", true,
                "Specify where to find library interface files.");

        Option dirOpt   = new Option ("D", true,
                "Specify where to place generated diagnostic files.");
        Option optOpt   = new Option ("O", false,
                " Disable optimizations.");
        Option irrunOpt = new Option (null, "irrun", false,
                "Generate and interpret intermediate code.");

        options.addOption(helpOpt);
        options.addOption(lexOpt);
        options.addOption(parseOpt);
        options.addOption(typeOpt);
        options.addOption(irgenOpt);

        options.addOption(sourcepathOpt);
        options.addOption(libpathOpt);
        options.addOption(optOpt);
        options.addOption(dirOpt);
        options.addOption(irrunOpt);

        HelpFormatter formatter = new HelpFormatter();

        opts = new Optimizations();
        opts.setOptimizations(OptimizationTypes.CONSTANT_FOLDING, OptimizationTypes.IR_LOWERING);

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

            if (cmd.hasOption("O")) {
                System.out.println(opts);
                opts.clearOptimizations(OptimizationTypes.CONSTANT_FOLDING, OptimizationTypes.IR_LOWERING);
                System.out.println(opts);
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
                for (String filename : filenames) {
                    typeCheckFile(filename, false);
                    lexFile(filename, new StringBuilder(), true);
                }
            }

            if (cmd.hasOption("parse")) {
                for (String filename : filenames) {
                    typeCheckFile(filename, false);
                    parseFile(filename, true);
                }
            }

            if (cmd.hasOption("typecheck")) {
                for (String filename : filenames) {
                    typeCheckFile(filename, true);
                }
            }

            if (cmd.hasOption("irgen")) {
                for (String filename : filenames) {
                    irgenFile(filename, true);
                }
            }

            if (cmd.hasOption("irrun")) {
                for (String filename : filenames) {
                    irrunFile(filename, true);
                }
            }

        }
        catch (ParseException parseException) {
            formatter.printHelp("etac [options] <source files>", options);
            System.out.println("Unexpected exception: " + parseException.getMessage());
        }
    }
}