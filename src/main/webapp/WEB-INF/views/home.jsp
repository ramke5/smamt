<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome user</title>
</head>
<body>
	<br>

	<div class="row" style="margin-right: 15px; margin-left: 15px;">
		<div class="panel panel-default">
			<div class="panel-heading">Tweet occurrences per week day by hour</div>
			<div class="panel-body">
				<div class="col-xs-12">
					<div id="heatMapContainer"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="becko"></div>

	<div class="row" style="margin-right: 15px; margin-left: 15px;">
		<div class="panel panel-default">
			<div class="panel-heading">Daily and hourly tweets</div>
			<div class="panel-body">
				<div class="col-xs-4">
					<div id="dayContainer"></div>
				</div>
				<div class="col-xs-8">
					<div id="hourContainer"></div>
				</div>
			</div>
		</div>
	</div>

	<div class="row" style="margin-right: 15px; margin-left: 15px;">
		<div class="panel panel-default">
			<div class="panel-heading">Overall tweet occurrences</div>
			<div class="panel-body">
				<div class="col-xs-6">Overall tweet occurrences
					<div id="overalContainer"></div>
				</div>
				<div class="col-xs-6">Tweets by gender
					<div id="genderContainer"></div>
				</div>
			</div>
		</div>
	</div>
	
		<div class="row" style="margin-right: 15px; margin-left: 15px;">
		<div class="panel panel-default">
			<div class="panel-heading">Overall tweet occurrences</div>
			<div class="panel-body">
				<div class="col-xs-6">Tweets by account
					<div id="accountContainer"></div>
				</div>
				
				<div class="col-xs-6">Tweets by location
					<div id="locationContainer"></div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="row" style="margin-right: 15px; margin-left: 15px;">
		<div class="panel panel-default">
			<div class="panel-heading">Tweet occurrences per category by date</div>
			<div class="panel-body">
				<div class="col-xs-12">
					<div class="form-inline" style="float: left;">
						<select id="category" class="form-control">
						</select>
						<button class="btn btn-default" onclick="validate()">Submit</button>
					</div>
					<div id="byCategoryChart"></div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="loadingModal" role="dialog" style="display: none; width: auto;">
		<div class="modal-dialog modal-sm">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h5 class="modal-title">Loading, please wait</h5>
				</div>
				<div class="modal-body">
					<img style="display: block; margin: 0 auto;" alt="Loading" src="/resources/img/loading7_blue.gif">
				</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		$(function() {
			$("#loadingModal").modal('show');
			$.ajax({
				type : "GET",
				url : "${home}heat-map",
				success : function(data) {
					drawChart(data);
					getDailyChart();
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});

		});

		function getDailyChart() {
			$.ajax({
				type : "GET",
				url : "${home}week-day",
				success : function(data) {
					drawDailyChart(data);
					getHourlyChart();
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});
		}

		function getHourlyChart() {
			$.ajax({
				type : "GET",
				url : "${home}day-hour",
				success : function(data) {
					drawHourChart(data);
					getOveralChart();
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});
		}

		function getOveralChart() {
			$.ajax({
				type : "GET",
				url : "${home}my-categories-stats?userId=${userId}",
				success : function(data) {
					drawOveralCategoryPieChart(data);
					getGenderChart();
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});
		}

		function getGenderChart() {
			$.ajax({
				type : "GET",
				url : "${home}gender-stats?userId=${userId}",
				success : function(data) {
					drawGenderPieChart(data);
					getAccountChart();
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});
		}

		function getAccountChart() {
			$.ajax({
				type : "GET",
				url : "${home}account-stats?userId=${userId}",
				success : function(data) {
					drawAccountPieChart(data);
					getLocationChart();
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});
		}

		function getLocationChart() {
			$.ajax({
				type : "GET",
				url : "${home}location-stats?userId=${userId}",
				success : function(data) {
					$("#loadingModal").modal('hide');
					drawLocationPieChart(data);
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});
		}

		function drawChart(data) {
			
			Highcharts.chart('heatMapContainer', {

			    chart: {
			        type: 'heatmap',
			        marginTop: 40,
			        marginBottom: 80,
			        plotBorderWidth: 1
			    },


			    title: {
			        text: ''
			    },

			    xAxis: {
			    	categories : [ '00:00', '01:00', '02:00','03:00', '04:00', '05:00', '06:00','07:00', '08:00', '09:00', '10:00','11:00', '12:00', '13:00', '14:00','15:00', '16:00', '17:00', '18:00','19:00', '20:00', '21:00', '22:00','23:00' ]
			    },

			    yAxis: {
			    	categories : [ 'Sunday', 'Monday', 'Tuesday','Wednesday', 'Thursday', 'Friday','Saturday' ],
					title : null
			    },

			    colorAxis: {
			    	min : 0,
					minColor : '#FFFFFF',
					maxColor : '#0000FF'
			    },

			    legend: {
			        align : 'right',
					layout : 'vertical',
					margin : 0,
					verticalAlign : 'top',
					y : 25,
					symbolHeight : 280
			    },

			    tooltip: {
			        formatter: function () {
			            return '<b>' + this.point.value + '</b> tweets were posted on <br><b>' + this.series.yAxis.categories[this.point.y] + 
			            ' at ' + this.series.xAxis.categories[this.point.x] + '</b>';
			        }
			    },

			    series: [{
			        name: 'Day - Hour heat map',
			        borderWidth: 1,
			        data: data,
			        dataLabels: {
			            enabled: true,
			            color: '#000000'
			        }
			    }]

			});
		}

		function drawDailyChart(data) {
			$('#dayContainer')
					.highcharts(
							{
								chart : {
									type : 'bar'
								},
								title : {
									text : ''
								},
								subtitle : {
									text : ''
								},
								xAxis : {
									categories : [ 'Sunday', 'Monday', 'Tuesday',
											'Wednesday', 'Thursday', 'Friday',
											'Saturday' ],
									title : {
										text : 'Week day',
										align : 'middle'
									}
								},
								yAxis : {
									min : 0,
									title : {
										text : 'Number of tweets',
										align : 'middle'
									},
									labels : {
										overflow : 'justify'
									}
								},
								tooltip : {
									valueSuffix : 'occurences'
								},
								plotOptions : {
									bar : {
										dataLabels : {
											enabled : true
										}
									}
								},
								credits : {
									enabled : false
								},
								series : [ {
									name : 'Tweets',
									color : '#37AEE6',
									data : data
								} ]
							});
		}

		function drawHourChart(data) {
			$(function() {
				$('#hourContainer').highcharts(
						{
							chart : {
								type : 'spline'
							},
							title : {
								text : ''
							},
							subtitle : {
								text : ''
							},
							xAxis : {
								categories : [ '00:00', '1:00', '2:00', '3:00',
										'4:00', '5:00', '6:00', '7:00', '8:00',
										'9:00', '10:00', '11:00', '12:00',
										'13:00', '14:00', '15:00', '16:00',
										'17:00', '18:00', '19:00', '20:00',
										'21:00', '22:00', '23:00' ]
							},
							yAxis : {
								title : {
									text : 'Number of tweets'
								}
							},
							plotOptions : {
								line : {
									dataLabels : {
										enabled : true
									},
									enableMouseTracking : false
								}
							},
							series : [ {
								name : 'Tweets',
								color : '#37AEE6',
								data : data
							} ]
						});
			});
		}

		function drawOveralCategoryPieChart(data) {
			$('#overalContainer')
					.highcharts(
							{
								chart : {
									plotBackgroundColor : null,
									plotBorderWidth : null,
									plotShadow : false,
									type : 'pie'
								},
								title : {
									text : ''
								},
								tooltip : {
									pointFormat : '<b>{point.name}</b>: {point.y} occurrences'
								},
								plotOptions : {
									pie : {
										allowPointSelect : true,
										cursor : 'pointer',
										dataLabels : {
											enabled : true,
											format : '<b>{point.name}</b>: {point.percentage:.1f} %',
											style : {
												color : (Highcharts.theme && Highcharts.theme.contrastTextColor)
														|| 'black'
											}
										}
									}
								},
								series : [ {
									name : 'Categories',
									colorByPoint : true,
									data : data
								} ]
							});
		}

		function drawGenderPieChart(data) {
			$('#genderContainer')
					.highcharts(
							{
								chart : {
									plotBackgroundColor : null,
									plotBorderWidth : null,
									plotShadow : false,
									type : 'pie'
								},
								title : {
									text : ''
								},
								tooltip : {
									pointFormat : '<b>{point.name}</b>: {point.y} occurrences'
								},
								plotOptions : {
									pie : {
										allowPointSelect : true,
										cursor : 'pointer',
										dataLabels : {
											enabled : true,
											format : '<b>{point.name}</b>: {point.percentage:.1f} %',
											style : {
												color : (Highcharts.theme && Highcharts.theme.contrastTextColor)
														|| 'black'
											}
										}
									}
								},
								series : [ {
									name : 'Categories',
									colorByPoint : true,
									data : data
								} ]
							});
		}

		function drawAccountPieChart(data) {
			$('#accountContainer')
					.highcharts(
							{
								chart : {
									plotBackgroundColor : null,
									plotBorderWidth : null,
									plotShadow : false,
									type : 'pie'
								},
								title : {
									text : ''
								},
								tooltip : {
									pointFormat : '<b>{point.name}</b>: {point.y} occurrences'
								},
								plotOptions : {
									pie : {
										allowPointSelect : true,
										cursor : 'pointer',
										dataLabels : {
											enabled : true,
											format : '<b>{point.name}</b>: {point.percentage:.1f} %',
											style : {
												color : (Highcharts.theme && Highcharts.theme.contrastTextColor)
														|| 'black'
											}
										}
									}
								},
								series : [ {
									name : 'Categories',
									colorByPoint : true,
									data : data
								} ]
							});
		}

		function drawLocationPieChart(data) {
			$('#locationContainer')
					.highcharts(
							{
								chart : {
									plotBackgroundColor : null,
									plotBorderWidth : null,
									plotShadow : false,
									type : 'pie'
								},
								title : {
									text : ''
								},
								tooltip : {
									pointFormat : '<b>{point.name}</b>: {point.y} occurrences'
								},
								plotOptions : {
									pie : {
										allowPointSelect : true,
										cursor : 'pointer',
										dataLabels : {
											enabled : true,
											format : '<b>{point.name}</b>: {point.percentage:.1f} %',
											style : {
												color : (Highcharts.theme && Highcharts.theme.contrastTextColor)
														|| 'black'
											}
										}
									}
								},
								series : [ {
									name : 'Categories',
									colorByPoint : true,
									data : data
								} ]
							});
		}

		$(function() {
			$.ajax({
				type : "GET",
				url : "${home}get-categories?userId=${userId}",
				success : function(data) {
					console.log(JSON.stringify(data));
					populateDropdown(data);
				},
				error : function(e) {
					alert("ERROR");
				},
				done : function(e) {
					alert("DONE");
				}
			});
		});

		function populateDropdown(data) {
			var select = document.getElementById("category");

			for (var i = 0; i < data.length; i++) {
				var opt = data[i];
				select.innerHTML += "<option id=" + data[i].id +" value=" + data[i].id + ">"
						+ data[i].name + "</option>";
			}
			validate();
		}

		function validate() {
			var category = document.getElementById("category");

			if (category.value == "") {
				category.style.borderColor = 'red';
			} else {
				category.style.borderColor = 'default';
				$.ajax({
					type : "GET",
					url : "${home}date-stats?userId=${userId}&categoryId="
							+ category.value,
					success : function(data) {
						console.log(JSON.stringify(data));
						drawByCategoryChart(data);
					},
					error : function(e) {
						alert("ERROR");
					},
					done : function(e) {
						alert("DONE");
					}
				});
			}
		}

		function drawByCategoryChart(data) {
			$(function() {
				// Create the chart
				$('#byCategoryChart')
						.highcharts(
								{
									chart : {
										type : 'column',
										zoomType : 'x'
									},
									title : {
										text : 'Statistics by date'
									},
									subtitle : {
										text : 'Click and drag in the plot area to zoom in'
									},
									xAxis : {
										type : 'category'
									},
									yAxis : {
										title : {
											text : 'Occurences per day'
										}

									},
									legend : {
										enabled : false
									},
									plotOptions : {
										series : {
											borderWidth : 0,
											dataLabels : {
												enabled : true,
												format : '<b>{point.y}</b>'
											}
										}
									},

									tooltip : {
										pointFormat : '{point.name}: <b>{point.y} occurences</b>'
									},

									series : [ {
										name : 'Category',
										colorByPoint : true,
										data : data
									} ],
								});
			});
		}
	</script>
</body>

</html>