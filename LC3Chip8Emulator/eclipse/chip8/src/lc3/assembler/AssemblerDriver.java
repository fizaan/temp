package lc3.assembler;

import java.io.File;
import java.util.ArrayList;

public class AssemblerDriver {
	
	public static void main(String[] args) throws Exception {
		String[] assembleroptions=System.getenv("assembleroptions").split(",");
		String option1 = assembleroptions[0];
		Fixed.TROUBLE_SHOOT = option1.equals("debug") ? true : false;
		
		if(!args[0].endsWith(".txt"))
			throw new LC3AssemblerException("Assembly source must be a .txt file: " + args[0]);
		if(!args[1].endsWith(".obj"))
			throw new LC3AssemblerException("Binary file must be a .obj file: " + args[1]);
		
		File src = new File(args[0]); //input src file
		if(args.length==3)
			Fixed.PRINT_WARNING = false;
		Parser p = new Parser();
		ArrayList<String> ar = p.readTextAssemblyFie(src);
		
		/*
		 * The ordering of method calls below matters!
		 */
		
		//first pass of ArrayList
		p.setSize(ar);
		//second pass of ArrayList
		p.evaluate(ar);
		p.writeUserData();
		//output .obj file
		p.writeMachineCodeToFile(args[1]); 
		p.debugToFile();
		//Thread.currentThread();
		//Thread.sleep(100);
		if(args.length!=3)
			System.out.print("\n\n---\nStarting..\n---\n");

	}

}
