package de.htwg_konstanz.in.trimmed;

import java.io.IOException;

public class FileConverter {
	
	
	public static void main(String[] args) {
		try {
			System.out.println(args[0] +"to " + args[1]);
			String p = FileChecker.convertIt(args[0],args[1] );
			System.out.println("File converted path: " + p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
