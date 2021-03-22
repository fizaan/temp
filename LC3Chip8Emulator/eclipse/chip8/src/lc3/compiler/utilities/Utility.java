package lc3.compiler.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import lc3.compiler.exception.LC3CompileException;

public class Utility {	
	
	public ArrayList<String> getLines(File file) throws Exception
	{	ArrayList<char[]> ar=new ArrayList<char[]>();
		FileReader reader=new FileReader(file);
		while(true) 
		{	char[] buf = new char[20];
			int n=reader.read(buf);
			if(n<0)
				break;
			
			ar.add(buf);
		}
		reader.close();
		
		return getLines(getBuffer(ar));
		
	}
	
	public ArrayList<String> getLinesWhiteSpaces(File file) throws Exception
	{	ArrayList<char[]> ar=new ArrayList<char[]>();
		FileReader reader=new FileReader(file);
		while(true) 
		{	char[] buf = new char[20];
			int n=reader.read(buf);
			if(n<0)
				break;
			
			ar.add(buf);
		}
		reader.close();
		
		return getLinesWithSpaces(getBuffer(ar));
		
	}
	
	/*
	 * Used by Compiler.
	 */
	public static ArrayList<String> readSourceCode(File file) throws Exception
	{	BufferedReader reader=new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line;
		ArrayList<String> arr= new ArrayList<String>();
		short lineNumber = 1;
		while((line=reader.readLine())!=null) {
			//handles multi-line comments within /*....*/
			//continue reading next line until */ is found
			if(line.indexOf("/*") > -1) {
				while(line.indexOf("*/")==-1)
					line=reader.readLine();
				line=reader.readLine();
			}
			
			//trim the line
			line=line.trim();
			//if line is empty continue
			if(line.length()==0) continue;
			
			//Also comment	
			if(line.startsWith("//")) continue;
			
			
			int strBegin = line.indexOf("\"");
			int strEnd = -1;
			String str = null;
			
			if(strBegin>-1) { //String case
				strEnd = line.indexOf("\"",strBegin+1);
				if(strEnd == -1)
					throw new LC3CompileException("String not closed at line: "+
							lineNumber);
				else 
					str = line.substring(strBegin,strEnd+1);
				
				line = line.substring(0,strBegin);
				String[] tokens = line.split("\\s+");
				for(String s: tokens)
					arr.add(s.trim());
				arr.add(str);
			}
			else {	//normal case
				String[] tokens = line.split("\\s+");
				for(String s: tokens)
					arr.add(s.trim());
			}
			
			lineNumber++;
		}
		
		//close
		reader.close();
		//return arr
		return arr;
		
	}
	
	
	
	private StringBuffer getBuffer(ArrayList<char[]> ar)
	{	StringBuffer sb=new StringBuffer();
		for(char[] k:ar)
			for(int i=0;i<k.length;i++)
				sb.append(k[i]);
		return sb;
	}
	
	private ArrayList<String> getLines(StringBuffer sb)
	{	int sblen=sb.length();
		StringBuffer retbuf=new StringBuffer();
		ArrayList<String> ar=new ArrayList<String>();
		int newline=10;
		int carriageret=13;
		for(int i=0;i<sblen;i++)
			if(sb.charAt(i)==newline||sb.charAt(i)==carriageret)
			{	if(retbuf.toString().length()!=0)
				{	ar.add(retbuf.toString());
					retbuf=new StringBuffer();
				}	
			}
			else
				retbuf.append(sb.charAt(i));
		
		ar.add(retbuf.toString());
		
		return ar;
		
	}
	
	private ArrayList<String> getLinesWithSpaces(StringBuffer sb)
	{	int sblen=sb.length();
		StringBuffer retbuf=new StringBuffer();
		ArrayList<String> ar=new ArrayList<String>();
		int newline=10;
		int carriageret=13;
		int tab=9;
		int singlespace=32;
		for(int i=0;i<sblen;i++)
		{	if(sb.charAt(i)==newline)
				retbuf.append("[NL]");
			else if(sb.charAt(i)==carriageret)
				retbuf.append("[CR]");
			else if(sb.charAt(i)==tab)
				retbuf.append("[TAB]");
			else if(sb.charAt(i)==singlespace)
				retbuf.append("[SPACE]");
				
			retbuf.append(sb.charAt(i));
		}	
		
		ar.add(retbuf.toString());
		
		return ar;
		
	}
}