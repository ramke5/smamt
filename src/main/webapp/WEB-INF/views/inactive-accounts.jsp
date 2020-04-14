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
	<div class="btn-group">
 	</div>
		<table class="table table-bordered table-hover" id="dataSourceTable">
			<thead>
				<tr>
					<th style="margin: 0 auto;">Account Name</th>
					<th style="margin: 0 auto;">Account URL</th>
					<th style="margin: 0 auto;">Activate</th>
				</tr>
			</thead>
		</table>
</div>


						<!-- Restore Page Modal -->
	
	<div class="modal fade" id="restorePageModal" role="dialog">
		<div class="modal-dialog">
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">Activate</h4>
					</div>
					<div class="modal-body">
						Are you sure you want to activate this account?
					</div>
					
					<div class="modal-footer">
						<div class="btn-group">
							<button type="submit" class="btn btn-default" id="btnRestorePage">Yes</button>
							<button type="button" class="btn btn-danger"data-dismiss="modal">No</button>
						</div>
					</div>
				</div>
			</div>
		</div>

<script type="text/javascript">

	$( document ).ready(function() {
		populateTableWithInactiveTwitterAccounts();
	});

	
	function populateTableWithInactiveTwitterAccounts(){
		
		$.ajax({
			type : "GET",
			url : "${home}deletedtwitterpages?userId=${userId}",
			timeout : 100000,
			success : function(data) {
				var trHTML = '';
					for (var j = 0; j < data.length; j++) {
						for(var i = 0; i < data[j].twitterPages.length; i++){
							trHTML += '<tr><td>' + data[j].twitterPages[i].name + '</td><td>' + '<a href="' + data[j].twitterPages[i].url  + '">' + data[j].twitterPages[i].url  + '</a>'
							+ '</td><td><center><a href="#" title="Restore" data-toggle="modal" data-target="#restorePageModal" onclick="restorePage(\''+data[j].twitterPages[i].pageId+'\')" class="glyphicon glyphicon-ok"></a></center>'
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

	
	function restorePage(id){
		$("#btnRestorePage").click(function(){
			if (this.id == 'btnRestorePage') {
			$.ajax({
				type : "POST",
				url : "${home}restoretwitterpage?userId=${userId}&pageId=" + id,
				timeout : 100000,
				success : function(data) {
				$('#restorePageModal').modal('hide');
				populateTableWithInactiveTwitterAccounts();
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