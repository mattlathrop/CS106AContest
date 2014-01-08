import java.util.ArrayList;

import acm.program.*;

public class test extends ConsoleProgram{
	private ArrayList<String> figureList = new ArrayList<String>();
	
	public void run() {
		figureList.add("1");
		figureList.add("2");
		figureList.add("3");
		figureList.add("4");
		figureList.add("5");
		boolean t;
		for (String str: figureList) {
			println(str);
			t = figureList.remove(str);
		}
		for (String str: figureList) {
			println(str);
		} 
		
	}
	
}
