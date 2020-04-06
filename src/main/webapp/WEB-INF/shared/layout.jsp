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
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/highcharts-3d.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="http://code.highcharts.com/modules/heatmap.js"></script>
<script src="https://code.highcharts.com/modules/data.js"></script>
<link href="<c:url value="/resources/css/bootstrap-datetimepicker.min.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/navigation-bar.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/manage-categories.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/fonts/montserrat.css" />" rel="stylesheet" type="text/css" />
<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
<link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet" />
<link href="<c:url value="/resources/css/bla.css" />" rel="stylesheet" />

<script src="<c:url value="/resources/js/bootstrap-datetimepicker.js" />"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" />
<script>
	const buttons = document.querySelectorAll("header button");
	
	buttons.forEach(el => {
	  const span = el.lastElementChild;
	  const width = span.offsetWidth;
	  span.style.width = 0;
	  
	  el.addEventListener("mouseenter", () => {
	    span.style.width = `${width}px`;
	  });
	  
	  el.addEventListener("mouseleave", () => {
	    span.style.width = 0;
	  });
	});
</script>

</head>
<body>
    <nav class="navbar navbar-default">
  	<div>
  		<header>
		  <h5 style="color: var(--white)">Welcome <b>${username}</b></h5>
      		<p id="userID" hidden>${userId}</p>
		  <ul>
		    <li>
		      <button onclick="window.location.href = '${home}home';" >
		        <svg width="24" height="24" viewBox="0 0 16 16">
		          <rect x="4" width="12" height="12" transform="translate(20 12) rotate(-180)"/><polygon points="2 14 2 2 0 2 0 14 0 16 2 16 14 16 14 14 2 14"/>
		        </svg>
		        <span style="color: var(--white)">Home</span>
		      </button>
		    </li>
		    <li>
		    <div class="dropdown">
			    <button data-toggle="dropdown">
			    	<svg width="24" height="24" viewBox="0 0 16 16">
		          		<path d="M 15.015625 0.585938 C 14.316406 -0.113281 13.179688 -0.113281 12.480469 0.585938 L 5.363281 7.699219 C 5.316406 7.746094 5.28125 7.808594 5.261719 7.875 L 4.328125 11.25 C 4.289062 11.390625 4.328125 11.539062 4.429688 11.640625 C 4.53125 11.742188 4.679688 11.78125 4.816406 11.742188 L 8.195312 10.808594 C 8.261719 10.789062 8.324219 10.753906 8.371094 10.703125 L 15.484375 3.589844 C 16.183594 2.890625 16.183594 1.753906 15.484375 1.054688 Z M 6.234375 7.957031 L 12.054688 2.136719 L 13.933594 4.011719 L 8.113281 9.835938 Z M 5.859375 8.710938 L 7.359375 10.210938 L 5.285156 10.785156 Z M 14.917969 3.027344 L 14.496094 3.449219 L 12.621094 1.574219 L 13.042969 1.152344 C 13.433594 0.761719 14.0625 0.761719 14.449219 1.152344 L 14.917969 1.617188 C 15.308594 2.007812 15.308594 2.636719 14.917969 3.027344 Z M 14.917969 3.027344"/>
		        	</svg>
		        	<span style="color: var(--white)">Manage Categories</span>
			    	<span class="caret" style="color: var(--white)"></span>
			    </button>
			    <ul class="dropdown-menu">
			      <li ><a href="${home}active-categories">Add Category</a></li>
			      <li id="deletedCategories"><a href="${home}deleted-categories">Restore Category</a></li>
			    </ul>
		  </div>
		    </li>
		    <li>
		      <button onclick="window.location.href = '${home}datasources';">
		        <svg width="24" height="24" viewBox="0 0 16 16">
		          <path d="M 4.370032 0.866708 L 20.629968 0.866708 C 21.185394 0.866708 21.630955 1.318373 21.630955 1.867695 L 21.630955 23.132568 C 21.630955 23.68189 21.185394 24.133555 20.629968 24.133555 L 4.370032 24.133555 C 3.814606 24.133555 3.369045 23.68189 3.369045 23.132568 L 3.369045 1.867695 C 3.369045 1.318373 3.814606 0.866708 4.370032 0.866708 Z M 4.370032 0.866708"/>
		        </svg>
		        <span style="color: var(--white)">My Pages</span>
		      </button>
		    </li>
		    <li>
		      <button onclick="window.location.href = '${home}tweets';">
		        <svg>
		          <img src="/resources/img/logout.svg" width="16" height="16"/>
		        </svg>
		        <span style="color: var(--white)">Tweets</span>
		      </button>
		    </li>
		    <li>
		      <button onclick="window.location.href = '${home}logout';">
<!-- 		        <svg width="24" height="24" viewBox="0 0 16 16"> -->
					<img src="/resources/img/logout-1.png" width="24" height="24"/>
<!-- 		        </svg> -->
		        <span style="color: var(--white)">Logout</span>
		      </button>
		    </li>
		  </ul>
		</header>
  	</div>
   	</nav>
<decorator:body />
</body>
</html>