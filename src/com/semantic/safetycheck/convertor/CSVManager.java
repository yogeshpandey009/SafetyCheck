package com.semantic.safetycheck.convertor;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class CSVManager {

	public static void main(String[] args) {
		loadCSV();
	}
	
	private static final String FILE_PATH = "./files/all_month.csv";
	
	private static void loadCSV(){
		try {
			ArrayList<ArrayList<String>> allRowAndColData = null;
	        ArrayList<String> oneRowData = null;
	        String currentLine;
	        FileInputStream fis = new FileInputStream(FILE_PATH);
	        DataInputStream myInput = new DataInputStream(fis);
	        int i = 0;
	        allRowAndColData = new ArrayList<ArrayList<String>>();
	        while ((currentLine = myInput.readLine()) != null) {
	            oneRowData = new ArrayList<String>();
	            String oneRowArray[] = currentLine.split(",");
	            for (int j = 0; j < oneRowArray.length; j++) {
	                oneRowData.add(oneRowArray[j]);
	                //System.out.println(oneRowArray[j]);
	            }
	            allRowAndColData.add(oneRowData);
	            System.out.println();
	            i++;
	        }
		} catch (Exception e) {
			System.out.println("Error while loading CSV file");
			e.printStackTrace();
		}
	}
	
}
