<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTDx HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Login</title>

<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
<%-- <link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet"> --%>
<script src="<c:url value="/resources/js/jquery.min.js" />"></script>
<%-- <link href="<c:url value="/resources/css/admin.css" />" rel="stylesheet"> --%>
<%-- <link href="<c:url value="/resources/fonts/montserrat.css" />" rel="stylesheet" type='text/css'> --%>
<link href="<c:url value="/resources/css/loginCSS.css" />" rel="stylesheet">

<script>
$(document).ready(function() {
    $("#do_login").click(function() { 
       closeLoginInfo();
       $(this).parent().find('span').css("display","none");
       $(this).parent().find('span').removeClass("i-save");
       $(this).parent().find('span').removeClass("i-warning");
       $(this).parent().find('span').removeClass("i-close");
       
        var proceed = true;
        $("#login_form input").each(function(){
            
            if(!$.trim($(this).val())){
                $(this).parent().find('span').addClass("i-warning");
            	$(this).parent().find('span').css("display","block");  
                proceed = false;
            }
        });
       
        if(proceed) //everything looks good! proceed...
        {
            $(this).parent().find('span').addClass("i-save");
            $(this).parent().find('span').css("display","block");
        }
    });
    
    //reset previously results and hide all message on .keyup()
    $("#login_form input").keyup(function() { 
        $(this).parent().find('span').css("display","none");
    });
 
  openLoginInfo();
  setTimeout(closeLoginInfo, 1000);
});

function openLoginInfo() {
    $(document).ready(function(){ 
    	$('.b-form').css("opacity","0.01");
      $('.box-form').css("left","-37%");
      $('.box-info').css("right","-37%");
    });
}

function closeLoginInfo() {
    $(document).ready(function(){ 
    	$('.b-form').css("opacity","1");
    	$('.box-form').css("left","0px");
      $('.box-info').css("right","-5px"); 
    });
}

$(window).on('resize', function(){
      closeLoginInfo();
});
</script>
</head>
<body>
	<form action="login" method="post">
<!-- 		<div class="login-block"> -->
<!-- 			<h1>Login</h1> -->
<%-- 			<div class="error">${error}</div> --%>
<!-- 			<input type="text" placeholder="username" id="name" name="name" />  -->
<!-- 			<input type="password" placeholder="password" id="password" name="password" /> -->

<!-- 			<button value="submit" type="submit">Login</button> -->
<!-- 			<div style="padding-top: 8px; padding-bottom: 8px;"> -->
<%-- 				<a href="${home}registration" style="color: black; float: right;"><span class=" glyphicon glyphicon-user"></span> Register</a> --%>
<!-- 			</div> -->
<!-- 		</div> -->
<div class='box'>
  <div class='box-form'>
    <div class='box-login-tab'></div>
    <div class='box-login-title'>
      <div class='i i-login'></div><h2>LOGIN</h2>
    </div>
    <div class='box-login'>
      <div class='fieldset-body' id='login_form'>
        <button onclick="window.location.href = '${home}login';" class='b b-form i i-more'></button>
        	<p class='field'>
          <label for='user'>E-MAIL</label>
          <input type='text' id="name" name="name" />
          <span id='valida' class='i i-warning'></span>
        </p>
      	  <p class='field'>
          <label for='pass'>PASSWORD</label>
          <input type='password'  id="password" name="password" />
          <span id='valida' class='i i-close'></span>
        </p>

        	<input type='submit' id='do_login' value='LOGIN'/>
      </div>
    </div>
  </div>
  <div class='box-info'>
	<div class='line-wh'></div>
    <button onclick="" class='b-cta' title='Sign up!'> CREATE ACCOUNT</button>
  	</div>
</div>

  
</form>
</body>
</html>