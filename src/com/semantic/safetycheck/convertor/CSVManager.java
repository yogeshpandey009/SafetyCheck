package com.semantic.safetycheck.convertor;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class CSVManager {

	private static final String FILE_PATH = "./files/all_month.csv";
	
	public static ArrayList<ArrayList<String>> loadCSV(){
		ArrayList<ArrayList<String>> allRowAndColData = null;
		try {
	        ArrayList<String> oneRowData = null;
	        String currentLine;
	        FileInputStream fis = new FileInputStream(FILE_PATH);
	        DataInputStream myInput = new DataInputStream(fis);
	        allRowAndColData = new ArrayList<ArrayList<String>>();
	        while ((currentLine = myInput.readLine()) != null) {
	            oneRowData = new ArrayList<String>();
	            String oneRowArray[] = currentLine.split(",");
	            for (int j = 0; j < oneRowArray.length; j++) {
	                oneRowData.add(oneRowArray[j]);
	                //System.out.println(oneRowArray[j]);
	            }
	            allRowAndColData.add(oneRowData);
	        }
	        fis.close();
	        myInput.close();
		} catch (Exception e) {
			System.out.println("Error while loading CSV file");
			e.printStackTrace();
		}
		return allRowAndColData;
	}
	
}
