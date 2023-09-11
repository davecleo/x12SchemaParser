package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchemaReader {
	public static void main(String args[]) {
		if(args.length < 2) {
			System.err.println("Please supply document version and one or more file names");
			return;
		}
		for(int i=1; i<args.length; i++) {
			processFile(args[i]);
		}
		try {
			SchemaElement.printAllReverseTable(args[0]);
			SchemaElement.printAllForwardTable(args[0]);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void processFile(String filename)  {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			String referenceNo="";
			SchemaElement element;
			while(line != null) {
				if(line.contains("def simpleElement")) {
					referenceNo=line.split(" ")[2];
				}
				if(line.contains("allowedValue")) {
					Pattern regex = Pattern.compile("[^(]+\\(\"([^\"]+)\\\", \"([^\"]+)\"\\)");
					Matcher regexMatcher = regex.matcher(line);
					while (regexMatcher.find()) {
						element=new SchemaElement(referenceNo, regexMatcher.group(1),regexMatcher.group(2));
						SchemaElement.addIfNotPresent(element);
					}
				}
				line=reader.readLine();
			}
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
