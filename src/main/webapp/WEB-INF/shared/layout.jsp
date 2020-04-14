<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Social Media Analysis Master Thesis</title>

<script src="<c:url value="/resources/js/jquery.min.js" />"></script>
<script src="<c:url value="/resources/js/jquery.dataTables.js" />"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/highcharts-3d.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="http://code.highcharts.com/modules/heatmap.js"></script>
<script src="https://code.highcharts.com/modules/data.js"></script>
<%-- <link href="<c:url value="/resources/css/bootstrap-datetimepicker.min.css" />" rel="stylesheet" type="text/css" /> --%>
<link href="<c:url value="/resources/css/navigation-bar.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/manage-categories.css" />"	rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/fonts/montserrat.css" />"	rel="stylesheet" type="text/css" />
<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
<link href="<c:url value="/resources/css/bootstrap.min.css" />"	rel="stylesheet" />
<link href="<c:url value="/resources/css/bla2.css" />" rel="stylesheet" />

<script
	src="<c:url value="/resources/js/bootstrap-datetimepicker.js" />"></script>
<!-- <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" /> -->

<link rel="stylesheet" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css" />

<link href='https://fonts.googleapis.com/css?family=Roboto:400,300,700' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">


<script>
$(function () {
	 $('.toggle-menu').click(function(){
		$('.exo-menu').toggleClass('display');
		
	 });
	 
	});
</script>

</head>
<body class="background">
	<nav class="navbar navbar-default">
	<div class="content">
	 
		<ul class="exo-menu">
			<li><a class="active" href="${home}home"><i class="fa fa-home"></i> Home</a></li>
			<li  class="drop-down"><a  href="#"><i class="fa fa-photo"></i> Charts</a>
				<ul class="drop-down-ul">
					<li><a href="${home}home">Heat map & Bar - line charts</a></li>
					<li><a href="${home}pie-charts">Pie charts</a></li>
				</ul>
			</li>
			<li class="drop-down"><a href="#"><i class="fa fa-list"></i> Categories</a>
				<ul class="drop-down-ul">
					<li><a href="${home}active-categories">View category</a></li>
					<li id="deletedCategories"><a href="${home}deleted-categories">Restore category</a></li>
				</ul>
			</li>
			<li class="drop-down"><a href="#"><i class="fa fa-bullhorn"></i> Accounts</a>
				<ul class="drop-down-ul">
					<li id="activeAccounts"><a href="${home}datasources">Active	accounts</a></li>
					<li id="inactiveAccounts"><a href="${home}inactive-accounts">Inactive accounts</a></li>
				</ul>
			</li>
			<li><a href="${home}tweets"><i class="fa fa-envelope"></i> Tweets</a>
				<div class="contact">
				</div>
			</li>
			<li class="navbar-right"><a href="${home}logout"><i class="fa fa-cogs"></i> Logout</a></li>		
	</ul>
	 
	 
	 </div>
	</nav>
	<decorator:body />
</body>
</html>