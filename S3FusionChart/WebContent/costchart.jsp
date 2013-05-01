<%@ page import="com.fusioncharts.*, java.util.*, com.eightkmiles.parse.*"%>

<%
	//Initialize FusionChart variables
	FusionGraph graph = null;
	String errMsg = null;
	String[] bucketKey;
    
    BucketSelector bs = new BucketSelector();
    bucketKey = bs.select();
    
	Map<String, Double> map = new HashMap<String, Double>();
	Parser mp = new Parser(bucketKey[0], bucketKey[1], bucketKey[2], bucketKey[3]);
	map = mp.parse();
	try
	{
		//create graph object
		graph = new FusionGraph("pieGraph",ChartType.PIE3D,map.size(),request);
		
		//set chart properties and chart look
		graph.setWidth(800);
		graph.setHeight(600);
		graph.setChartProperties("showZeroPies","0");
		graph.setChartProperties("bgColor","FFFFFF,CCCC33");
		graph.setChartProperties("smartLineThickness","2");
		graph.setChartProperties("baseFont","Arial");
		graph.setChartProperties("baseFontSize","12");
		graph.setChartProperties("showToolTipShadow","1");
		graph.setChartProperties("toolTipBgColor","D9E5F1");
		graph.setChartProperties("pieRadius","180");
		    
		//create series
		Series series = graph.createSeries("AWS Services Cost");
		String[] seriesColors = {"AFD8F8","F6BD0F","8BBA00","FF8E46","008E8E","8E468E","588526"};
		int i = 0;
		
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			graph.setCategory(i, key);
			series.setValue(i, map.get(key));
			series.setColor(i, seriesColors[i]);
			i++;
		}
  	}
  	catch (Exception e)
  	{
    	errMsg = "Error: "+ e;
  	}
%>

<html>
<head>
  	<title>AWS Cost Allocation Graph</title>  
  	<SCRIPT LANGUAGE="Javascript" SRC="FusionCharts/FusionCharts.js"></SCRIPT>
</head>
<body>
	<!--  print chart -->
	<jsp:include page="tools/chartBuilder.jsp" flush="true">
		<jsp:param name="graphId" value="<%= graph.getGraphId() %>"/> 
	</jsp:include>
</body>
</html>