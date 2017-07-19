<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
    String path = request.getContextPath(); 
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<link href="<%=basePath %>css/bootstrap.css" rel="stylesheet">
<link href="<%=basePath %>css/bootstrap.min.css" rel="stylesheet">

<script type="text/javascript"
	src="<%=basePath %>js/jquery-1.9.1.min.js"></script>
<script type="text/javascript">
	$(function(){
		$("#button").click(function(){ 
			// var test = document.getElementById("tt").checked;
			//  alert(test);
			 //alert($("#checkbox").attr("checked"));
		//	 alert($("[name='checkbox']").prop("checked"));
			$("[name='checkbox']").prop("checked", function(i,val){   
				
			})
				//alert($(this).val());  
		})
	})
</script>
</head>
<body>

	<div>
		<input type="checkbox" name="checkbox" class="rr1">
	</div>
	
	<div>
		<input type="checkbox" name="checkbox" class="rr2">
	</div>
	
	<div>
		<input type="checkbox" name="checkbox" class="rr3">
	</div>
	 
	 
			<input type="button" id="button" value="Click Me">
	 

</body>
</html>