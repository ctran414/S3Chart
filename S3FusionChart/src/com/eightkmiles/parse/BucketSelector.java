package com.eightkmiles.parse;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class BucketSelector {
	private List<Bucket> buckets = new ArrayList<Bucket>();
	private List<String> bucketNames = new ArrayList<String>();
	private List<String> keyNames = new ArrayList<String>();
	private String accessKey;
	private String secretKey;

	public String[] select() {
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
		
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName((String) bucket);
		ObjectListing objectListing;            
        do {
            objectListing = s3.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries())
                keyNames.add(objectSummary.getKey());
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
		
        Object[] keyOptions = keyNames.toArray();
		Object key = JOptionPane.showInputDialog(null, 
		                                           "Select Key", 
		                                           "Bucket Selector", 
		                                            JOptionPane.QUESTION_MESSAGE, 
		                                            null,
		                                            keyOptions, 
		                                            keyOptions[0]);

		return new String[] {accessKey, secretKey, (String) bucket, (String) key};
	}
}
