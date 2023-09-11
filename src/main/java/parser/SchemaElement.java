package parser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SchemaElement {
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static boolean FullMatch=true;
	private static int maxRecordCount=4500;

	private static String reverseBaseName="Description_To_ID";
	private static String forwardBaseName="ID_To_Description";
	private static String packageHeader="com.cleo.labs.x12decoder";
	private static String preamble="def doubleInputCodeTable %%basename%%_%%version%%_CT {\n"
			+ "    def init() {\n"
			+ "       defaultValue = \"Unknown\"\n\n";
	private static String epilog="    }\n"
			+ "}";
			
	public static ArrayList<SchemaElement> elements = new ArrayList<SchemaElement>();

	public static void printTable(Comparator<SchemaElement> comparator, String baseName, String ediVersion, boolean forwardLookup) throws IOException {
		Collections.sort(elements, comparator);
		int part=0;
		int recordCount=0;
		Writer fileWriter=null;
		for(int i=0;i<elements.size();i++) {
			if(recordCount == 0) {
				if(part!= 0) {
					fileWriter.write(epilog);
					fileWriter.close();
				}
				fileWriter = new FileWriter(baseName + "_" + ediVersion +"_" + Integer.toString(part) + "_CT.codeTable", false);
				fileWriter.write("package " + SchemaElement.packageHeader + "." + ediVersion + ";\n");
				fileWriter.write(SchemaElement.preamble.replace("%%version%%", ediVersion+ "_" + Integer.toString(part)).replace("%%basename%%", baseName));
				part++;
			}
			if(forwardLookup) {
				elements.get(i).printForwardTable(fileWriter);
			} else {
				elements.get(i).printReverseTable(fileWriter);
			}
			recordCount++;
			if(recordCount > SchemaElement.maxRecordCount) {
				recordCount=0;
			}
		}
		fileWriter.write(SchemaElement.epilog);
		fileWriter.close();	
	}
	
	public static void printAllReverseTable(String ediVersion) throws IOException {
		Comparator<SchemaElement> schemaComparator = Comparator.comparing(SchemaElement::getReferenceNo)
                .thenComparing(SchemaElement::getLabel);
		Collections.sort(elements, schemaComparator);
		printTable(schemaComparator, SchemaElement.reverseBaseName, ediVersion, false);
	}
	public static void printAllForwardTable(String ediVersion) throws IOException {
		Comparator<SchemaElement> schemaComparator = Comparator.comparing(SchemaElement::getNum)
                .thenComparing(SchemaElement::getReferenceNo);
		Collections.sort(elements, schemaComparator);
		printTable(schemaComparator, SchemaElement.forwardBaseName, ediVersion, true);
	}
	
	public static void addIfNotPresent(SchemaElement element) {
		SchemaElement.FullMatch=false;
		if(elements.contains(element))
			element.duplicateReverseLookup=true;
		SchemaElement.FullMatch=true;
			if(!elements.contains(element))
			elements.add(element);
	}
	
	private String referenceNo;
	private String num;
	private String label;
	public boolean duplicateReverseLookup;

	public SchemaElement(String referenceNo, String num, String label) {
		this.referenceNo = referenceNo;
		this.num=num;
		this.label=label;
		this.duplicateReverseLookup=false;
	}
	
	@Override
	public boolean equals(Object object) {
		boolean same=false;
		if(object != null && object instanceof SchemaElement) {
			if(SchemaElement.FullMatch) {
				same= this.referenceNo.equals(((SchemaElement)object).referenceNo) && 
					this.label.equals(((SchemaElement)object).label) && 						
					this.num.equals(((SchemaElement)object).num);
			}
			else {
 				same= this.referenceNo.equalsIgnoreCase(((SchemaElement)object).referenceNo) && 
				this.label.equalsIgnoreCase(((SchemaElement)object).label);
			}
		}
		return same;
	}

	public void printForwardTable(Writer fileWriter) throws IOException {
		fileWriter.write("row (\"" + num + "\", \"" + referenceNo + "\", \"" + label + "\")\n");
	}

	public void printReverseTable(Writer fileWriter) throws IOException {
		if(!duplicateReverseLookup)
			fileWriter.write("row (\"" + label + "\", \"" + referenceNo + "\", \"" + num + "\")\n");
	}
}
