package com.eightkmiles.parse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import au.com.bytecode.opencsv.CSVReader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3CSVParser {
	private static final int cutoff = 1;
	public static int monthCounter = 0;
	public ArrayList<String> productList = new ArrayList<String>();
	
	public S3CSVParser() {}
	
	public AmazonS3 selectS3() {
		String accessKey;
		String secretKey;
		JTextField xField = new JTextField(15);
		JTextField yField = new JTextField(30);

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Access Key:"));
		myPanel.add(xField);
		myPanel.add(new JLabel("Secret Key:"));
		myPanel.add(yField);

		int result = JOptionPane.showConfirmDialog(null, myPanel, 
				"Please Enter Key Values", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
	    	accessKey = xField.getText();
	    	secretKey = yField.getText();
		}
		else
			return null;
	      
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonS3 s3 = new AmazonS3Client(credentials);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		s3.setRegion(usEast1);
		
		return s3;
	}
	
	public String selectBucket(AmazonS3 s3) {
		List<Bucket> buckets = new ArrayList<Bucket>();
		List<String> bucketNames = new ArrayList<String>();
		
		for (Bucket bucket : s3.listBuckets()) {
            buckets.add(bucket);
            bucketNames.add(bucket.getName());
		}
		
		Object[] options = bucketNames.toArray();
		Object bucket = JOptionPane.showInputDialog(null, 
		                                           "Select S3 Bucket", 
		                                           "Bucket Selector", 
		                                            JOptionPane.QUESTION_MESSAGE, 
		                                            null,
		                                            options, 
		                                            options[0]);
		return (String)bucket;
	}
	
	public ArrayList<String> selectFiles(AmazonS3 s3, String bucket) {
		ArrayList<String> keyList = new ArrayList<String>();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket);
		ObjectListing objectListing;  
		
        do {
            objectListing = s3.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries())
                keyList.add(objectSummary.getKey());
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
        
		return keyList;
	}
	
	public ChartCategory parseCSV(AmazonS3 s3, String bucket, String key) throws IOException {	
	    int productIndex = 0, costIndex = 0, monthIndex = 0, month = 0;
	    String monthName = null;
	    Map<String, Double> map = new HashMap<String, Double>();
		
        S3Object object = s3.getObject(new GetObjectRequest(bucket, key));
		CSVReader csvReader = new CSVReader(new InputStreamReader(object.getObjectContent()));
		String[] col = null;
		
		col = csvReader.readNext();
		for (int i = 0; i < col.length; i++) {
			if (col[i].equals("ProductCode"))
				productIndex = i;
			else if (col[i].equals("TotalCost"))
				costIndex = i;
			else if (col[i].equals("BillingPeriodEndDate"))
				monthIndex = i;
		}
		
		while((col = csvReader.readNext()) != null) {
			double d = Double.parseDouble(col[costIndex]);
			
			if (!col[productIndex].isEmpty() && d > cutoff) {
				if(map.containsKey(col[productIndex]))
					map.put(col[productIndex], map.get(col[productIndex]) + d);
				else 
					map.put(col[productIndex], d);
				
				//Add all unique product names
				if (!productList.contains(col[productIndex]))
					productList.add(col[productIndex]);
				
				//Convert Billing date to Month string
				if(month == 0) {
					String[] temp = col[monthIndex].split("[ :/]");
					if (Integer.parseInt(temp[0]) < 2000)
						month = Integer.parseInt(temp[0]);
					else
						month = Integer.parseInt(temp[1]);
					monthName = getMonth(month);
				}
			}
		}
		csvReader.close();

		ChartCategory chartCat = new ChartCategory(monthName);
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String n = it.next();
			ChartObject obj = new ChartObject(n, map.get(n));
			chartCat.add(obj);
		}
		monthCounter++;
		return chartCat;
	}
	
	public String getMonth(int month) {
		String monthString;
        switch (month) {
            case 1:  monthString = "January";
                     break;
            case 2:  monthString = "February";
                     break;
            case 3:  monthString = "March";
                     break;
            case 4:  monthString = "April";
                     break;
            case 5:  monthString = "May";
                     break;
            case 6:  monthString = "June";
                     break;
            case 7:  monthString = "July";
                     break;
            case 8:  monthString = "August";
                     break;
            case 9:  monthString = "September";
                     break;
            case 10: monthString = "October";
                     break;
            case 11: monthString = "November";
                     break;
            case 12: monthString = "December";
                     break;
            default: monthString = "Invalid month";
                     break;
        }
        return monthString;
	}
}
