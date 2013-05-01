package com.eightkmiles.parse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class Parser {
	private static final double CUTOFF = 10.0;
    private String access, secret, bucket, key;
    
	public Parser(String access, String secret, String bucket, String key) {
		this.access = access;
		this.secret = secret;
		this.bucket = bucket;
		this.key = key;
	}

	public HashMap<String, Double> parse() throws IOException {	
	    int productIndex = 0, costIndex = 0;
		Map<String, Double> map = new HashMap<String, Double>();
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(access, secret));
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		s3.setRegion(usEast1);
		
        S3Object object = s3.getObject(new GetObjectRequest(bucket, key));
		CSVReader csvReader = new CSVReader(new InputStreamReader(object.getObjectContent()));
		String[] col = null;
		
		col = csvReader.readNext();
		for (int i = 0; i < col.length; i++) {
			if (col[i].equals("ProductCode"))
				productIndex = i;
			else if (col[i].equals("TotalCost"))
				costIndex = i;
		}
		
		while((col = csvReader.readNext()) != null) {
			double d = Double.parseDouble(col[costIndex]);
			if (!col[productIndex].isEmpty() && d >= CUTOFF) {
				if(map.containsKey(col[productIndex]))
					map.put(col[productIndex], map.get(col[productIndex]) + d);
				else 
					map.put(col[productIndex], d);
			}
		}
		csvReader.close();

		return (HashMap<String, Double>) map;
	}
}
