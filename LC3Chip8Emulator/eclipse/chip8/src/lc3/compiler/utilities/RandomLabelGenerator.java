package lc3.compiler.utilities;

import java.util.ArrayList;
import java.util.Collections;

public class RandomLabelGenerator {
	private ArrayList<Character> chars;
	private ArrayList<String> list;
	
	public static void main(String args[]) {
		RandomLabelGenerator rg = new
				RandomLabelGenerator();
		rg.generateLabel();
		rg.generateLabel();
		rg.generateLabel();
		rg.printList();
	}
	
	public RandomLabelGenerator() {
		list = new ArrayList<String>();
		chars = new ArrayList<>();
		chars.add('A');
		chars.add('B');
		chars.add('C');
		chars.add('D');
		chars.add('E');
		chars.add('F');
		chars.add('G');
		chars.add('H');
		chars.add('I');
		chars.add('J');
		chars.add('K');
		chars.add('L');
		chars.add('M');
		chars.add('N');
		chars.add('O');
		chars.add('P');
		chars.add('Q');
		chars.add('R');
		chars.add('S');
		chars.add('T');
	}
	
	public String generateLabel() {
		Collections.shuffle(chars);
		chars.trimToSize();
		char[] c = new char[chars.size()];
		int i = 0;
		for(char e:chars) 
			c[i++] = e;
		String s = String.copyValueOf(c);
		while(contains(s))
			generateLabel();
		list.add(s);
		return s;
	}
	
	private boolean contains(String s) {
		for(String k:list)
			if(s.equals(k))
				return true;
		return false;
	}
	
	public void printList() {
		System.out.println(list);
	}
}
