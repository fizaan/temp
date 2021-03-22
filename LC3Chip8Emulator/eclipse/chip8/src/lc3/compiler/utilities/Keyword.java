package lc3.compiler.utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Keyword {
	public static ArrayList<String> keywords;
	
	public Keyword() {
		keywords = new ArrayList<String>();
	}
	
	public ArrayList<String> getKeywords() {
		return keywords;
	}
	
	/*
	 * loadKeywords:
	 * One time operation
	 */
	public void loadKeywords(File file) throws IOException {
		BufferedReader reader=new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line;
		while((line=reader.readLine())!=null) {
			line=line.trim();
			if(line.length()==0) continue;
			String[] input = line.split(",");
			for(String s: input) 
				keywords.add(s.trim());
		}
		
		reader.close();
	}
	
	public static boolean isKeyword(String s) {
		for(String k:keywords)
			if(s.equals(k))
				return true;
		return false;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(String s:keywords)
			sb.append(s + "\n");
		return sb.toString();
	}
}
