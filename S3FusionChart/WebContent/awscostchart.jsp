<%@ page import="java.util.*, com.eightkmiles.parse.*, com.amazonaws.services.s3.*"%>
	
<HTML>
	<HEAD>
		<TITLE>AWS Costs Stacked Column
		Chart</TITLE>
		<%
				/*You need to include the following JS file, if you intend to embed the chart using JavaScript.
				Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
				When you make your own charts, make sure that the path to this JS file is correct. Else, you would get JavaScript errors.
				*/
			%>
		<SCRIPT LANGUAGE="Javascript" SRC="FusionCharts/FusionCharts.js"></SCRIPT>
		<style type="text/css">
			<!--
			body {
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
			}
			-->
		</style>
	</HEAD>
	<BODY>
		<div style="text-align:center">
		<h2>AWS Costs Allocation Stacked Column Chart</h2>
			<%
				/*
				In this example, we plot a Stacked chart from data contained
				in an array. The array will have three columns - first one for Quarter Name
				and the next two for data values. The first data value column would store sales information
				for Product A and the second one for Product B.
				*/
				
				ArrayList<ChartCategory> months = new ArrayList<ChartCategory>();
				ChartCategory month;
				AmazonS3 s3;
				S3CSVParser p = new S3CSVParser();
				s3 = p.selectS3();
				String bucket = p.selectBucket(s3);
				ArrayList<String> keys = p.selectFiles(s3, bucket);
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
					System.out.println(key);
					month = p.parseCSV(s3, bucket, key);
					months.add(month);
				}
				
				//Initialize FusionChart variables
				String[][] arrData = new String[p.monthCounter][p.productList.size()];
				String[] colors = {"AFD8F8","F6BD0F","8BBA00","FF8E46","008E8E","8E468E","588526",
								   "B3AA00","008ED6","9D080D","A186BE"};	
				int index = 0;
				
				Iterator<ChartCategory> itCC = months.iterator();
				while (itCC.hasNext()) {
					ChartCategory key = itCC.next();
					arrName[index][] = key.getName();
					arrData[][] = key.getData().toString();
					index++;
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
			    strXML = "<graph caption='Costs' numberPrefix='$' formatNumberScale='0' decimalPrecision='2'>";
			    
			    //Initialize <categories> element - necessary to generate a stacked chart
			    String strCategories = "<categories>";
			    
			    //Initiate <dataset> elements
			    String strDataProd[] = new String[p.productList.size()];
			    
			    for (int i = 0; i < p.productList.size(); i++) {
			    	strDataProd[i] = "<dataset seriesName='" + arrName[i] + "' color='" + colors[i] +"'>";
			    }
		
		  		strCategories += "<category name='JUNE' />";
			    //Iterate through the data	
			    //for(int i=0;i<arrData.length;i++){
			    	//Append <category name='...' /> to strCategories
			    	//Add <set value='...' /> to both the datasets
			    for (int i = 0; i < strDataProd.length; i++) 
			    	strDataProd[i] += "<set value='" + arrData[i] + "' />";	
			     //}
			    
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
			
			//Create the chart - Stacked Column 3D Chart with data contained in strXML
			%>
			<jsp:include page="Includes/FusionChartsRenderer.jsp" flush="true"> 
				<jsp:param name="chartSWF" value="FusionCharts/FCF_StackedColumn3D.swf" /> 
				<jsp:param name="strURL" value="" /> 
				<jsp:param name="strXML" value="<%=strXML %>" /> 
				<jsp:param name="chartId" value="productSales" /> 
				<jsp:param name="chartWidth" value="600" /> 
				<jsp:param name="chartHeight" value="600" />
				<jsp:param name="debugMode" value="false" /> 	
				<jsp:param name="registerWithJS" value="false" /> 
			</jsp:include> 
			<BR>
			<BR>
		</div>
	</BODY>
</HTML>
