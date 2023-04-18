package aar226_akc55_ayc62_ahl88;

import aar226_akc55_ayc62_ahl88.Errors.EtaError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.asm.ASMCompUnit;
import aar226_akc55_ayc62_ahl88.asm.ASMData;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMComment;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;
import aar226_akc55_ayc62_ahl88.cfg.CFGGraph;
//import aar226_akc55_ayc62_ahl88.cfg.Worklist;
//import aar226_akc55_ayc62_ahl88.cfg.optimizations.DeadCodeElimination;
import aar226_akc55_ayc62_ahl88.newast.Program;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.CheckConstFoldedIRVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRLoweringVisitor;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;
import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Main {
    enum Target {
        LINUX,
        WINDOWS,
        MACOS
    }

    private static String outputAsmDirectory;
    private static String outputDiagnosticDirectory;
    private static String inputDirectory;
    public static String libpathDirectory;

    private static boolean isOutputAsmDirSpecified;
    private static boolean isOutputDiagnosticDirSpecified;
    private static boolean isInputDirSpecified;
    public static boolean isLibpathDirSpecified;

    public static Optimizations opts;

    private static Target target;

    // Write the lexed string into the corresponding file name
    private static void writeOutputGeneric(String filename, String output, String extension, boolean isAsm) {
        Path path;
        Path path1 = Paths.get(filename);

        if (!isAsm) {
            path = (isOutputDiagnosticDirSpecified)
                    ? Paths.get(outputDiagnosticDirectory, filename)
                    : path1;
        }
        else {
            path = (isOutputAsmDirSpecified)
                    ? Paths.get(outputAsmDirectory, filename)
                    : path1;
        }

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

    private static void writeOutput(String filename, String output, String extension) {
        writeOutputGeneric(filename, output, extension, false);
    }

    private static void writeOutputAsm(String filename, String output, String extension) {
        writeOutputGeneric(filename, output, extension, true);
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



    private static IRNode irbuild(String filename) throws Exception {
        try {
            String zhenFilename = getZhenFilename(filename);

            Lexer lex;
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
//                    filename.substring(0, filename.length() - 2)
                    IRNode ir = result.accept(new IRVisitor("CompUnit"));
//                    {
//                        StringWriter out = new StringWriter();
//                        PrintWriter pw = new PrintWriter(out);
//
//                        CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(pw);
//                        ir.printSExp(printer);
//                        printer.close();
//                        writeOutput(filename, out.toString(), "irnoLower");
//                    }
                    // IR constant-folding checker demo
                    {
                        CheckConstFoldedIRVisitor cv = new CheckConstFoldedIRVisitor();
//                        System.out.print("Constant-folded?: ");
//                        System.out.println(cv.visit(ir));
                    }

                    ir = new IRLoweringVisitor(new IRNodeFactory_c()).visit(ir);
//                    for (Map.Entry<String, IRFuncDecl> map : ((IRCompUnit) ir).functions().entrySet()) {
//                        CFGGraph<IRStmt> stmtGraph = new CFGGraph<>((ArrayList<IRStmt>) ((IRSeq) map.getValue().body()).stmts());
//                        System.out.println(stmtGraph);
//                    }
                    {
                        CheckCanonicalIRVisitor cv = new CheckCanonicalIRVisitor();

//                        System.out.print("Canonical?: ");
//                        System.out.println(cv.visit(ir));
                    }
                    return ir;
                } else if (filename.endsWith(".eti")) {
                    EtiInterface result = (EtiInterface) p.parse().value;
                    result.firstPass(); // Just to throw EtaErrors
                }
                else {
                    System.out.println("Why are we here");
                }

//                if (opts.isSet(OptimizationTypes.CONSTANT_FOLDING)) {
//
//                }

            } catch (EtaError e) {
                e.printError(zhenFilename);
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
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
//            System.out.println(ir);
//            System.out.println(ir instanceof IRCompUnit);
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

    public static final String INDENT_SFILE = "    ";
    private static void asmGenFile(String filename, boolean shouldWrite) {
        String zhenFilename = getZhenFilename(filename);

        try {
            // DO SHIT
            IRNode ir = irbuild(zhenFilename);
            ASMCompUnit comp = new AbstractASMVisitor().visit((IRCompUnit) ir);
//            System.out.println(comp.printInstructions());
            ArrayList<ASMInstruction> postAlloc = new RegisterAllocationTrivialVisitor().visit(comp);
            StringWriter out = new StringWriter();
            out.write(INDENT_SFILE+ ".file  \""+zhenFilename+"\"\n");
            out.write(INDENT_SFILE+".intel_syntax noprefix\n");
            out.write(INDENT_SFILE+".text\n");
            out.write(INDENT_SFILE+".globl  _Imain_paai\n");
            out.write(INDENT_SFILE+".type	_Imain_paai, @function\n");
            for (ASMInstruction instr: postAlloc){
                if (!(instr instanceof ASMLabel)){
                    out.write(INDENT_SFILE + instr + '\n');
                }else{
                    out.write(instr+"\n");
                }
            }
            out.write("\n");
            out.write(INDENT_SFILE+".data\n");
            for (ASMData data: comp.getGlobals()){
                out.write(data.toString());
            }
            if (shouldWrite) {
                writeOutputAsm(filename, out.toString(), "s");
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
        Option irrunOpt = new Option (null, "irrun", false,
                "Generate and interpret intermediate code.");


        Option sourcepathOpt   = new Option ("sourcepath", true,
                "Specify where to find input source files.");
        Option libpathOpt = new Option ("libpath", true,
                "Specify where to find library interface files.");

        Option diagDirOpt   = new Option ("D", true,
                "Specify where to place generated diagnostic files.");
        Option asmDirOpt   = new Option ("d", true,
                "Specify where to place generated assembly output files.");
        Option optOpt   = new Option ("O", false,
                " Disable optimizations.");

        Option targetOpt = new Option("target", true,
                "Specify the operating system for which to generate code.");

//        optOpt.setOptionalArg(true);

        options.addOption(helpOpt);
        options.addOption(lexOpt);
        options.addOption(parseOpt);
        options.addOption(typeOpt);
        options.addOption(irgenOpt);
        options.addOption(irrunOpt);

        options.addOption(sourcepathOpt);
        options.addOption(libpathOpt);

        options.addOption(diagDirOpt);
        options.addOption(asmDirOpt);

        options.addOption(optOpt);
        options.addOption(targetOpt);

        HelpFormatter formatter = new HelpFormatter();

        opts = new Optimizations();
//        opts.setOptimizations(OptimizationTypes.CONSTANT_FOLDING, OptimizationTypes.IR_LOWERING);
        opts.setOptimizations(OptimizationTypes.CONSTANT_FOLDING);

        isOutputAsmDirSpecified = isOutputDiagnosticDirSpecified = isInputDirSpecified = isLibpathDirSpecified = false;
        outputAsmDirectory = outputDiagnosticDirectory = inputDirectory = libpathDirectory =
                Paths.get("").toAbsolutePath().toString();
        target = Target.LINUX;

        boolean shouldAsmGen = true;

//        System.out.println(outputDirectory);

        try {
            CommandLine cmd = parser.parse(options, args);
            if (args.length == 0 || cmd.hasOption("help")) {
                formatter.printHelp("etac [options] <source files>", options);
                return;
            }

            if (cmd.hasOption("D")) {
                outputDiagnosticDirectory = cmd.getOptionValue("D");
                isOutputDiagnosticDirSpecified = true;
            }

            if (cmd.hasOption("d")) {
                outputAsmDirectory = cmd.getOptionValue("d");
                isOutputAsmDirSpecified = true;
            }

            if (cmd.hasOption("O")) {
                opts.clearOptimizations(OptimizationTypes.CONSTANT_FOLDING);
            }

            if (cmd.hasOption("sourcepath")) {
                inputDirectory = cmd.getOptionValue("sourcepath");
                isInputDirSpecified = true;
            }

            if (cmd.hasOption("libpath")) {
                libpathDirectory = cmd.getOptionValue("libpath");
                isLibpathDirSpecified = true;
            }

            if (cmd.hasOption("target")) {
                target = switch (cmd.getOptionValue("target").toLowerCase()) {
                    case "linux" -> Target.LINUX;
                    case "windows" -> Target.WINDOWS;
                    case "macos" -> Target.MACOS;
                    default -> throw new IllegalStateException("Unexpected value: " + cmd.getOptionValue("target"));
                };
            }

            if (cmd.hasOption("lex")) {
                shouldAsmGen = false;
                for (String filename : filenames) {
                    typeCheckFile(filename, false);
                    lexFile(filename, new StringBuilder(), true);
                }
            }

            if (cmd.hasOption("parse")) {
                shouldAsmGen = false;
                for (String filename : filenames) {
                    typeCheckFile(filename, false);
                    parseFile(filename, true);
                }
            }

            if (cmd.hasOption("typecheck")) {
                shouldAsmGen = false;
                for (String filename : filenames) {
                    typeCheckFile(filename, true);
                }
            }

            if (cmd.hasOption("irgen")) {
                shouldAsmGen = false;
                for (String filename : filenames) {
                    irgenFile(filename, true);
                }
            }

            if (cmd.hasOption("irrun")) {
                shouldAsmGen = false;
                for (String filename : filenames) {
                    irrunFile(filename, true);
                }
            }

            if (shouldAsmGen) {
                for (String filename : filenames) {
                    asmGenFile(filename, true);
                }
            }
        }
        catch (ParseException parseException) {
            formatter.printHelp("etac [options] <source files>", options);
            System.out.println("Unexpected exception: " + parseException.getMessage());
        }
    }
}