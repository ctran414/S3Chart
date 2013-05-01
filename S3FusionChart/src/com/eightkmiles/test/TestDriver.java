package com.eightkmiles.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.amazonaws.services.s3.AmazonS3;
import com.eightkmiles.parse.ChartCategory;
import com.eightkmiles.parse.ChartObject;
import com.eightkmiles.parse.S3CSVParser;

public class TestDriver {

	public static void main(String[] args) throws IOException {
		ArrayList<ChartCategory> months = new ArrayList<ChartCategory>();
		ChartCategory month;
		AmazonS3 s3;
		S3CSVParser p = new S3CSVParser();
		s3 = p.selectS3();
		String b = p.selectBucket(s3);
		ArrayList<String> keys = p.selectFiles(s3, b);
		
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String tom = it.next();
			System.out.println(tom);
			month = p.parseCSV(s3, b, tom);
			months.add(month);
		}
		String[] colors = {"AFD8F8","F6BD0F","8BBA00","FF8E46","008E8E","8E468E","588526",
				   "B3AA00","008ED6","9D080D","A186BE"};	
		
		String[][] arrData = new String[months.size()][p.productList.size() + 1];

		for (int i = 0; i < months.size(); i++) {
	    	arrData[i][0] = months.get(i).getName();
	    	System.out.println(arrData[i][0]);
	    }

		for (int i = 0; i < months.size(); i++) {
			System.out.println(months.get(i).getData().size());
			for (int j = 0, k = 1; j < months.get(i).getData().size(); j++, k++) {
				arrData[i][k] = Double.toString(months.get(i).getData().get(j).getValue());
				System.out.println("arrData[" + i + "][" + j + "] = " + arrData[i][k]);
			}
	    }
		
	    String strXML;
	    /*
	    Now, we need to convert this data into multi-series XML. 
	    We convert using string concatenation.
	    strXML - Stores the entire XML
	    strCategories - Stores XML for the <categories> and child <category> elements
	    strDataProdA - Stores XML for current year's sales
	    strDataProdB - Stores XML for previous year's sales
	    */
	    
	    
	    //Initialize <graph> element
	    strXML = "<graph caption='Sales' numberPrefix='$' formatNumberScale='0' decimalPrecision='0'>";
	    
	    //Initialize <categories> element - necessary to generate a stacked chart
	    String strCategories = "<categories>";
	    
	    //Initiate <dataset> elements
	    String[] strDataProd = new String[p.productList.size()];    
	    for (int i = 0; i < months.size(); i++) {
	    	for (int j = 0; j < p.productList.size(); j++)
	    		strDataProd[j] = "<dataset seriesName='" + p.productList.get(j) + "' color='" + colors[j] + "'>";
	    }
	    
	    //Iterate through the data	
	     for(int i=0;i<arrData.length;i++){
	    	//Append <category name='...' /> to strCategories
	    	strCategories += "<category name='" + arrData[i][0] + "' />";
	    	//Add <set value='...' /> to both the datasets
	    	for (int j = 0, k = 1; j < strDataProd.length; j++, k++)
	    		strDataProd[j] += "<set value='" + arrData[i][k] + "' />";
	     }
	    
	    //Close <categories> element
	    strCategories += "</categories>";
	    
	    //Close <dataset> elements
	    for (int i = 0; i < strDataProd.length; i++)
	    	strDataProd[i] += "</dataset>";
	    
	    //Assemble the entire XML now
    	strXML += strCategories;
    	for (int i = 0; i < strDataProd.length; i++)
    		strXML += strDataProd[i];
    	strXML += "</graph>";
	    System.out.println(strXML);
		
	}
}
