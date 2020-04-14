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
<link href="<c:url value="/resources/css/bla.css" />" rel="stylesheet" />

<script
	src="<c:url value="/resources/js/bootstrap-datetimepicker.js" />"></script>
<!-- <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" /> -->

<link rel="stylesheet" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css" />



<script>
// 	const buttons = document.querySelectorAll("header button");
	
// 	buttons.forEach(el => {
// 	  const span = el.lastElementChild;
// 	  const width = span.offsetWidth;
// 	  span.style.width = 0;
	  
// 	  el.addEventListener("mouseenter", () => {
// 	    span.style.width = `${width}px`;
// 	  });
	  
// 	  el.addEventListener("mouseleave", () => {
// 	    span.style.width = 0;
// 	  });
// 	});
</script>

</head>
<body>
	<nav class="navbar navbar-default">
	<div>
		<header>
		<h5 style="color: var(- -white)">
			Welcome <b>${username}</b>
		</h5>
		<p id="userID" hidden>${userId}</p>
		<ul>
			<li>
				<button onclick="window.location.href = '${home}home';">
					<img src="/resources/img/home.png" width="24" height="24" /> <span style="color: var(- -white)">Home</span>
				</button>
			</li>
			<li>
				<div class="dropdown">
					<button data-toggle="dropdown">
						<img src="/resources/img/edit.png" width="24" height="24" /> <span style="color: var(- -white)">Charts</span> <span class="caret" style="color: var(- -white)"></span>
					</button>
					<ul class="dropdown-menu">
						<li><a href="${home}home">Heat map & Bar - line charts</a></li>
						<li><a href="${home}pie-charts">Pie charts</a></li>
					</ul>
				</div>
			</li>
			<li>
				<div class="dropdown">
					<button data-toggle="dropdown">
						<img src="/resources/img/edit.png" width="24" height="24" /> <span style="color: var(- -white)">Categories</span> <span	class="caret" style="color: var(- -white)"></span>
					</button>
					<ul class="dropdown-menu">
						<li><a href="${home}active-categories">View category</a></li>
						<li id="deletedCategories"><a href="${home}deleted-categories">Restore category</a></li>
					</ul>
				</div>
			</li>
			<li>
				<div class="dropdown">
					<button data-toggle="dropdown">
						<img src="/resources/img/pages.png" width="24" height="24" /> <span	style="color: var(- -white)">Twitter accounts</span> <span class="caret" style="color: var(- -white)"></span>
					</button>
					<ul class="dropdown-menu">
						<li id="activeAccounts"><a href="${home}datasources">Active	accounts</a></li>
						<li id="inactiveAccounts"><a href="${home}inactive-accounts">Inactive accounts</a></li>
					</ul>
				</div>
			</li>
			<li>
				<button onclick="window.location.href = '${home}tweets';">
					<img src="/resources/img/tweets.png" width="24" height="24" /> <span style="color: var(- -white)">Tweets</span>
				</button>
			</li>
			<li>
				<button onclick="window.location.href = '${home}logout';">
					<img src="/resources/img/logout-1.png" width="24" height="24" /> <span style="color: var(- -white)">Logout</span>
				</button>
			</li>
		</ul>
		</header>
	</div>
	</nav>
	<decorator:body />
</body>
</html>