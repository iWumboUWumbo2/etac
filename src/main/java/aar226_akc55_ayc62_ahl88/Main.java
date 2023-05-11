package aar226_akc55_ayc62_ahl88;

import aar226_akc55_ayc62_ahl88.Errors.EtaError;
import aar226_akc55_ayc62_ahl88.SymbolTable.SymbolTable;
import aar226_akc55_ayc62_ahl88.asm.ASMCompUnit;
import aar226_akc55_ayc62_ahl88.asm.ASMData;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMInstruction;
import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import aar226_akc55_ayc62_ahl88.asm.Opts.CFGGraphBasicBlockASM;
import aar226_akc55_ayc62_ahl88.asm.Opts.GraphColorAllocator;
import aar226_akc55_ayc62_ahl88.asm.visit.RegisterAllocationTrivialVisitor;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.BasicBlocks.*;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.OptimizationType;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.Optimizations;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.CopyPropNoSSA;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.DeadCodeElimNoSSA;
import aar226_akc55_ayc62_ahl88.newast.Program;
import aar226_akc55_ayc62_ahl88.newast.Type;
import aar226_akc55_ayc62_ahl88.newast.interfaceNodes.EtiInterface;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.*;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.AbstractASMVisitor;
import aar226_akc55_ayc62_ahl88.src.edu.cornell.cs.cs4120.xic.ir.visit.IRLoweringVisitor;
import aar226_akc55_ayc62_ahl88.cfg.optimizations.ir.FunctionInliningVisitor;
import aar226_akc55_ayc62_ahl88.src.polyglot.util.Pair;
import aar226_akc55_ayc62_ahl88.visitors.IRVisitor;
import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;
import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    private static HashMap<String, IRNode> IRs;


    private static boolean isOutputAsmDirSpecified;
    private static boolean isOutputDiagnosticDirSpecified;
    private static boolean isInputDirSpecified;
    public static boolean isLibpathDirSpecified;

    public static Optimizations opts;

    private static Target target;

    // Write the lexed string into the corresponding file name
    private static void writeOutputGeneric(String filename, String suffix, String output, String extension, boolean isAsm) {
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
        pathname = pathname.substring(0, pathname.length() - 4) + suffix + "." + extension;
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
        writeOutputGeneric(filename, "", output, extension, false);
    }

    private static void writeOutputAsm(String filename, String output, String extension) {
        writeOutputGeneric(filename, "", output, extension, true);
    }

    private static void writeOutputOptIR(String filename, String phase, String output) {
        System.out.println(filename + " " + phase);
        System.out.println(output);
        writeOutputGeneric(filename, "_" + phase, output, "ir", false);
    }

    public static void writeOutputDot(String filename, String function, String phase, String output) {
        writeOutputGeneric(filename, "_" + function + "_" + phase, output, "dot", false);
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
                out.close();
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
                    IRNode ir = result.accept(new IRVisitor("CompUnit"));


                    ir = new IRLoweringVisitor(new IRNodeFactory_c()).visit(ir);
                    IRs.put("initial", ir);
//                    if (opts.allClear()){ // Added if we only doing reg allocate
//                        return ir;
//                    }

                    if (opts.isSet(OptimizationType.INLINING)) {
                        FunctionInliningVisitor fv = new FunctionInliningVisitor();
                        ir = ir.accept(fv);
                        IRs.put("inline",ir);
                    }

                    if (opts.isSet(OptimizationType.COPYPROP)) {
                        ir = new CopyPropNoSSA().eliminateCode((IRCompUnit) ir);
//                        IRs.put("postCopy", ir);
                    }
                    if (opts.isSet(OptimizationType.DEAD_CODE_ELIMINATION)) {
                        ir = new DeadCodeElimNoSSA().eliminateCode((IRCompUnit) ir);
//                        IRs.put("postDead", ir);
                    }
                    HashMap<Pair<String,Type>,DominatorBlockDataflow> domBlocks = new HashMap<>();
                    HashMap<Pair<String,Type>,CFGGraphBasicBlock > funcToSSA = new HashMap<>();
                    for (Map.Entry<String, IRFuncDecl> map : ((IRCompUnit) ir).functions().entrySet()) {
                        CFGGraphBasicBlock stmtGraphBlocks = new CFGGraphBasicBlock((ArrayList<IRStmt>) ((IRSeq) map.getValue().body()).stmts());
                        stmtGraphBlocks.removeUnreachableNodes();
                        stmtGraphBlocks.removeDeletedNodes();
                        CFGGraphBasicBlock cleanedStmtGraphBlocks  = new CFGGraphBasicBlock(stmtGraphBlocks.optimizeJumpsAndLabels());
                        DominatorBlockDataflow domBlock = new DominatorBlockDataflow(cleanedStmtGraphBlocks);
                        domBlock.convertToSSA();
                        funcToSSA.put(new Pair<>(map.getKey(),map.getValue().functionSig),cleanedStmtGraphBlocks);
                        domBlocks.put(new Pair<>(map.getKey(),map.getValue().functionSig),domBlock);
                    }
                    if (opts.isSet(OptimizationType.DEAD_CODE_ELIMINATION)) {
                        for (Pair<String, Type> func : funcToSSA.keySet()) {
                            CFGGraphBasicBlock funcStatements = funcToSSA.get(func);
                            new DeadCodeEliminationSSA(funcStatements).workList();
                        }
                    }
                    if (opts.isSet(OptimizationType.CONSTPROP)) {
                        for (Pair<String, Type> func : funcToSSA.keySet()) {
                            CFGGraphBasicBlock funcStatements = funcToSSA.get(func);
//                            writeOutputDot(filename,func.part1(),"preConst",funcStatements.CFGtoDOT());
                            new ConstantPropSSA(funcStatements).workList();
                            funcStatements.removeUnreachableNodes();
                            new ConstantPropSSA(funcStatements).workList();
                            funcStatements.removeUnreachableNodes();
                            new ConstantPropSSA(funcStatements).workList();
                            funcStatements.removeUnreachableNodes();
//                            writeOutputDot(filename,func.part1(),"postConst",funcStatements.CFGtoDOT());
                        }
                    }

                    if (opts.isSet(OptimizationType.DEAD_CODE_ELIMINATION)) {
                        for (Pair<String, Type> func : funcToSSA.keySet()) {
                            CFGGraphBasicBlock funcStatements = funcToSSA.get(func);
                            new DeadCodeEliminationSSA(funcStatements).workList();
                            funcStatements.removeUnreachableNodes();
                        }
                    }

                    HashMap<String,IRFuncDecl> cfgIR = new HashMap<>();
                    for (Map.Entry<Pair<String,Type>,CFGGraphBasicBlock > kv : funcToSSA.entrySet()){
//                        writeOutputDot(filename,kv.getKey().part1(),"pressa",kv.getValue().CFGtoDOT());
                        DominatorBlockDataflow.unSSA(kv.getValue(),domBlocks.get(kv.getKey()).retArgsReverseMapping);
//                        writeOutputDot(filename,kv.getKey().part1(),"postssa",kv.getValue().CFGtoDOT());
                        ArrayList<IRStmt> stmtss =  kv.getValue().getBackIR();
                        IRFuncDecl optFunc = new IRFuncDecl(kv.getKey().part1(), new IRSeq(stmtss));
                        optFunc.functionSig = kv.getKey().part2();
                        cfgIR.put(kv.getKey().part1(),optFunc);
                    }

                    ir = new IRCompUnit(((IRCompUnit) ir).name(),cfgIR,new ArrayList<>(),((IRCompUnit) ir).dataMap());
                    if (opts.isSet(OptimizationType.LICM)){
                        ir = new LoopOptsVisitorNoSSA().optimizeLoops((IRCompUnit) ir);
                    }
//                    for (String funcName : ((IRCompUnit) ir).functions().keySet()){
//                        IRFuncDecl func = ((IRCompUnit) ir).functions().get(funcName);
//                        CFGGraphBasicBlock graph = new CFGGraphBasicBlock((ArrayList<IRStmt>) ((IRSeq) func.body()).stmts());
//                        writeOutputDot(filename,funcName,"preLoop",graph.CFGtoDOT());
//                        LoopOpts loopOpts = new LoopOpts(graph);
//                        writeOutputDot(filename,funcName,"postLoop",graph.CFGtoDOT());
//                    }
                    if (opts.isSet(OptimizationType.COPYPROP)) {
                        ir = new CopyPropNoSSA().eliminateCode((IRCompUnit) ir);
//                        IRs.put("postCopy", ir);
                    }
                    if (opts.isSet(OptimizationType.DEAD_CODE_ELIMINATION)) {
                        ir = new DeadCodeElimNoSSA().eliminateCode((IRCompUnit) ir);
//                        IRs.put("postDead", ir);
                    }
                    if (opts.isSet(OptimizationType.DEAD_CODE_ELIMINATION)) {
                        ir = new DeadCodeElimNoSSA().eliminateCode((IRCompUnit) ir);
//                        IRs.put("postDead", ir);
                    }

//                    IRs.put("final", ir);
                    return ir;
                } else if (filename.endsWith(".eti")) {
                    EtiInterface result = (EtiInterface) p.parse().value;
                    result.firstPass(); // Just to throw EtaErrors
                }
                else {
                    System.out.println("Why are we here");
                }

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

            if (shouldWrite) {
                assert ir != null;
                writeOutput(filename, ir.toString(), "ir");
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

//            StringWriter out = new StringWriter();
//            PrintWriter pw = new PrintWriter(out);

//            CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(pw);

            IRSimulator sim = new IRSimulator((IRCompUnit) ir);
            sim.call("_Imain_paai", 0);
//            ir.printSExp(printer);

//            printer.close();

            if (shouldWrite) {
                assert ir != null;
                writeOutput(filename, ir.toString(), "ir");
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
            ArrayList<ASMInstruction> postAlloc = new ArrayList<>();
            boolean mainCalled = FunctionInliningVisitor.isMainCalled((IRCompUnit) ir);
//            for (Map.Entry<String, ArrayList<ASMInstruction>> kv: comp.getFunctionToInstructionList().entrySet()){
//                StringWriter out = new StringWriter();
//                ArrayList<ASMInstruction> abstractInstrs = kv.getValue();
//                for (ASMInstruction instr : abstractInstrs){
//                    if (!(instr instanceof ASMLabel)){
//                        out.write(INDENT_SFILE + instr + '\n');
//                    }else{
//                        out.write(instr+"\n");
//                    }
//                }
//                out.close();
//            }

            if (opts.isSet(OptimizationType.REGALLOC)) {
                boolean failed = false;
                for (Map.Entry<String, ArrayList<ASMInstruction>> kv : comp.getFunctionToInstructionList().entrySet()) {
                    CFGGraphBasicBlockASM asmBasicblocks = new CFGGraphBasicBlockASM(kv.getValue(),kv.getKey());
                    asmBasicblocks.removeUnreachableNodes();
                    GraphColorAllocator getColors = new GraphColorAllocator(asmBasicblocks, comp, kv.getKey(),false,filename,mainCalled);
                    getColors.MainFunc();
                    if (getColors.failed){
                        failed = true;
                        break;
                    }else {
                        postAlloc.addAll(getColors.replaceTempReserve());
                    }
                }
                if (failed){
                    System.out.println("failed with extra");
                    postAlloc = new ArrayList<>();
                    for (Map.Entry<String, ArrayList<ASMInstruction>> kv : comp.getFunctionToInstructionList().entrySet()) {
                        CFGGraphBasicBlockASM asmBasicblocks = new CFGGraphBasicBlockASM(kv.getValue(),kv.getKey());
                        asmBasicblocks.removeUnreachableNodes();
                        GraphColorAllocator getColors = new GraphColorAllocator(asmBasicblocks, comp, kv.getKey(),true,filename,mainCalled);
                        getColors.MainFunc();
                        postAlloc.addAll(getColors.replaceTempReserve());
                    }
                }else{
                    System.out.println("made it with extra");
                }
            }else{
                postAlloc = new RegisterAllocationTrivialVisitor().visit(comp);
            }

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
            out.close();
        }
        catch (EtaError e) {
            e.printError(zhenFilename);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws java.io.IOException {
        // Create the command line parser
        CommandLineParser parser = new DefaultParser();

        // Create the Options
        Options options = new Options();

        Option helpOpt = new Option("h", "help", false,
                "Print a synopsis of options.");
        Option reportOpt = new Option(null, "report-opts", false,
                "Output a list of optimizations supported by the compiler");

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

        Option optirOpt = new Option(null, "optir", true,
                "Report the intermediate code at the specified phase of optimization.");
        Option optcfgOpt = new Option(null, "optcfg", true,
                "Report the control-flow graph at the specified phase of optimization.");

        Option sourcepathOpt   = new Option ("sourcepath", true,
                "Specify where to find input source files.");
        Option libpathOpt = new Option ("libpath", true,
                "Specify where to find library interface files.");

        Option diagDirOpt   = new Option ("D", true,
                "Specify where to place generated diagnostic files.");
        Option asmDirOpt   = new Option ("d", true,
                "Specify where to place generated assembly output files.");
        Option optOpt   = new Option ("O", false,
                "Disable optimizations.");

        Option cfOptOpt = new Option ("Ocf", false,
                "Enable constant folding optimization.");
        Option inlOptOpt = new Option ("Oinl", false,
                "Enable function inlining optimization.");
        Option constPropOpt = new Option ("Ocp", false,
                "Constant propagation optimization.");
        Option copyPropOpt = new Option ("Ocopy", false,
                "Copy propagation optimization.");
        Option licmOpt = new Option ("Olicm", false,
                "Enable Loop Invariant Code Motion optimization.");
        Option dceOpt = new Option ("Odce", false,
                "Dead code elimination optimization.");
        Option regOpt = new Option ("Oreg", false,
                "Register allocation optimization.");

        optOpt.setOptionalArg(true);

        Option targetOpt = new Option("target", true,
                "Specify the operating system for which to generate code.");

        options.addOption(helpOpt);
        options.addOption(reportOpt);
        options.addOption(lexOpt);
        options.addOption(parseOpt);
        options.addOption(typeOpt);
        options.addOption(irgenOpt);
        options.addOption(irrunOpt);

        options.addOption(optirOpt);
        options.addOption(optcfgOpt);

        options.addOption(cfOptOpt);
        options.addOption(inlOptOpt);
        options.addOption(constPropOpt);
        options.addOption(copyPropOpt);
        options.addOption(licmOpt);
        options.addOption(dceOpt);
        options.addOption(regOpt);

        options.addOption(sourcepathOpt);
        options.addOption(libpathOpt);

        options.addOption(diagDirOpt);
        options.addOption(asmDirOpt);

        options.addOption(optOpt);
        options.addOption(targetOpt);

        HelpFormatter formatter = new HelpFormatter();

        opts = new Optimizations();
        opts.setAll();

        isOutputAsmDirSpecified = isOutputDiagnosticDirSpecified = isInputDirSpecified = isLibpathDirSpecified = false;
        outputAsmDirectory = outputDiagnosticDirectory = inputDirectory = libpathDirectory =
                Paths.get("").toAbsolutePath().toString();
        target = Target.LINUX;

        IRs = new HashMap<>();

        boolean shouldAsmGen = true;

        ArrayList<String> filenames = new ArrayList<>();
        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i].endsWith(".eta") || args[i].endsWith(".eti")) {
                filenames.add(args[i]);
            }
        }

        opts.setAll();
        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i].startsWith("-O")) {
                opts.clearAll();
                break;
            }
        }

        try {
            CommandLine cmd = parser.parse(options, args);
            if (args.length == 0 || cmd.hasOption("help")) {
                formatter.printHelp("etac [options] <source files>", options);
                return;
            }

            if (cmd.hasOption("report-opts")) {
                opts.reportOpts();
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
                opts.clearAll();
            }

            if (cmd.hasOption("Ocf")) {
                opts.setOptimizations(OptimizationType.CONSTANT_FOLDING);
            }

            if (cmd.hasOption("Oinl")) {
                opts.setOptimizations(OptimizationType.INLINING);
            }

            if (cmd.hasOption("Ocp")) {
                opts.setOptimizations(OptimizationType.CONSTPROP);
            }

            if (cmd.hasOption("Ocopy")) {
                opts.setOptimizations(OptimizationType.COPYPROP);
            }
            if (cmd.hasOption("Olicm")){
                opts.setOptimizations(OptimizationType.LICM);
            }
            if (cmd.hasOption("Odce")) {
                opts.setOptimizations(OptimizationType.DEAD_CODE_ELIMINATION);
            }

            if (cmd.hasOption("Oreg")) {
                opts.setOptimizations(OptimizationType.REGALLOC);
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

            // TODO: fix optir and optcfg
            if (cmd.hasOption("optir")) {
                String[] phases = cmd.getOptionValues("optir");
                System.out.println(Arrays.toString(phases));

                for (String filename : filenames) {
                    String zhenFilename = getZhenFilename(filename);
                    irbuild(zhenFilename);

                    for (String phase : phases) {
                        IRNode ir = IRs.get(phase);
                        writeOutputOptIR(filename, phase, ir.toString());
                    }
                }
            }

            if (cmd.hasOption("optcfg")) {
                String[] phases = cmd.getOptionValues("optcfg");

                for (String filename : filenames) {
                    String zhenFilename = getZhenFilename(filename);
                    irbuild(zhenFilename);

                    for (String phase : phases) {
                        IRNode ir = IRs.get(phase);

                        for (Map.Entry<String, IRFuncDecl> map : ((IRCompUnit) ir).functions().entrySet()) {
                            String funcName = map.getValue().name();
                            CFGGraphBasicBlock stmtGraph = new CFGGraphBasicBlock((ArrayList<IRStmt>) ((IRSeq) map.getValue().body()).stmts());

//                            System.out.println(stmtGraph.CFGtoDOT());
                            writeOutputDot(filename, funcName, phase, stmtGraph.CFGtoDOT());

//                            break;
                        }
                    }
                }
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

//            System.out.println(opts);
        }
        catch (ParseException parseException) {
            formatter.printHelp("etac [options] <source files>", options);
            System.out.println("Unexpected exception: " + parseException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}