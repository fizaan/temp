package lc3.compiler.main;

import java.io.File;

import lc3.compiler.assemble.AssemblyText;
import lc3.compiler.parser.Parser;
import lc3.compiler.utilities.Keyword;

public class CompilerDriver {

	public static void main(String[] args) throws Exception {
		String keywordsfile = System.getenv("keywords");
		Keyword kw = new Keyword();
		kw.loadKeywords(new File(keywordsfile));
		File srcode = new File(args[0]);
		Parser p = new Parser();
		p.loadSrcCode(srcode);
		//p.printFunctions();
		AssemblyText asm = new AssemblyText(p.getFunctionList());
		//System.out.println(asm);
		String outputfile = System.getenv("assemblyfilehome") +
				"\\" + srcode.getName() + "_asm.txt";
		asm.generateASMFile(new File(outputfile));
		
	}

}
