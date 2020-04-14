<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
								<!-- Main Table -->

<div class="tocenter">
	<button style="float: right;" id="btnPage" class="btn btn-info btn-sm" data-toggle="modal" data-target="#dataSourcesModal">
		<span class="glyphicon glyphicon-plus-sign"></span> 
		Add Account
		</button>
	<div class="btn-group">

 	</div>
		<table class="table table-bordered table-hover" id="dataSourceTable">
			<thead>
				<tr>
					<th style="margin: 0 auto;">Account Name</th>
					<th style="margin: 0 auto;">Account URL</th>
					<th style="margin: 0 auto;">Make inactive</th>
				</tr>
			</thead>
		</table>
</div>

							<!-- Add New Page Modal -->
							
	<div class="modal fade normal" id="dataSourcesModal" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<form:form method="post" name="addPageForm" id="addPageForm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">Add New Account</h4>
					</div>
					<div class="modal-body">
						<div class="category-block">
							<div class="form-group">
								<label for="page">Title</label> <textarea rows="4" cols="70" placeholder="Enter URL of account
								https://www.twitter.com/ramke5/" class="form-control" id="page" required="required"></textarea>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<div class="btn-group">
							<button type="submit" class="btn btn-info" id="btnAddPage" >Save</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
						</div>
					</div>

				</div>
			</form:form>
		</div>
	</div>
	
								<!-- Delete Page Modal -->
	
	<div class="modal fade" id="deletePageModal" role="dialog">
		<div class="modal-dialog">
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">Make inactive</h4>
					</div>
					<div class="modal-body">
						Are you sure you want to make this account inactive?
					</div>
					
					<div class="modal-footer">
						<div class="btn-group">
							<button type="submit" class="btn btn-default" id="btnDeletePage">Yes</button>
							<button type="button" class="btn btn-danger"data-dismiss="modal">No</button>
						</div>
					</div>
				</div>
			</div>
		</div>

<script type="text/javascript">

	$( document ).ready(function() {
		populateTableWithActiveTwitterAccounts();
	});

	jQuery(document).ready(function($) {
		$("#addPageForm").submit(function(pageEvent) {
			pageEvent.preventDefault();
			addPage();
		});
	});
	
	function populateTableWithActiveTwitterAccounts(){
		$.ajax({
			type : "GET",
			url : "${home}twitterpages?userId=${userId}",
			timeout : 100000,
			success : function(data) {
				var trHTML = '';
					for (var j = 0; j < data.length; j++) {
						for(var i = 0; i < data[j].twitterPages.length; i++){
							trHTML += '<tr><td>' + data[j].twitterPages[i].name + '</td><td>' + '<a href="' + data[j].twitterPages[i].url  + '">' + data[j].twitterPages[i].url  + '</a>'
							+ '</td><td><center><a data-toggle="modal" data-target="#deletePageModal" href="" onclick="deletePage(\''+data[j].twitterPages[i].pageId +'\')" id ="deletePageTable" class="glyphicon glyphicon-trash"></a></center>'
							+ '</td></tr>';
						}
				}
				$("#dataSourceTable > tbody").html("");
				$('#dataSourceTable').append(trHTML);
				$('#dataSourceTable').DataTable();
			},
			error : function(e) {
				console.log("ERROR: ", e);
			},
			done : function(e) {
				console.log("DONE");
			}
		});
	}
	
	function addPage(){
		var pageUrl = $("#page").val();
		$.ajax({
			type : "POST",
			url : "${home}addtwitteraccount?userId=${userId}&pageUrl=" + pageUrl,
			timeout : 100000,
			success : function(data) {
				console.log("SUCCESS: ", data);
				$('#dataSourcesModal').modal('hide');
				$("#dataSourcesModal").trigger('reset');
				populateTableWithActiveTwitterAccounts();
				alert("You have succesefully added new page.");
			},
			error : function(e) {
				console.log("ERROR: ", e.responseText);
				alert("URL that you have entered does not represent a real page. Please go to home page of portal you want to follow and enter URL in form\nwww.twitter.com/accountyouwanttoadd");
			},
			done : function(e) {
				alert("DONE");
				console.log("DONE");
			}
		});
	}
	
	function deletePage(id){
		$("#btnDeletePage").click(function(){
			if (this.id == 'btnDeletePage') {
			$.ajax({
				type : "POST",
				url : "${home}deletetwitteraccount?userId=${userId}&pageId=" + id,
				timeout : 100000,
				success : function(data) {
				$('#deletePageModal').modal('hide');
				populateTableWithActiveTwitterAccounts();
				},
				error : function(e) {
					console.log("ERROR: ", e);
				},
				done : function(e) {
					alert("DONE");
				}
				});
			}
		});
	}

</script>

</body>
</html>