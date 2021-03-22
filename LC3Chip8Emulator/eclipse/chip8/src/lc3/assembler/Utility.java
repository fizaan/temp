package lc3.assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	 * Used by Parser.
	 */
	public static ArrayList<String> readTextAssemblyFile(File file) throws Exception
	{	BufferedReader reader=new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line;
		ArrayList<String> arr= new ArrayList<String>();
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
			
			//A comment	
			if(line.startsWith("//")||line.startsWith(";")) continue;
			//get string between line start and ;
			if(line.indexOf(";")>-1) {
				int end = line.indexOf(";");
				line=line.substring(0,end);
			}
			//add to arr
			arr.add(line.substring(0,line.length()));
		}
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