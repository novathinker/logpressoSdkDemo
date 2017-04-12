/**
 * 
 */
google.charts.load("current", {packages:["corechart"]});
google.charts.setOnLoadCallback(ajaxGc);
var dummy  = [], diskTrendYkey = [];   

$(document).ready(function() { 
	createChart();
	setInterval("ajaxChartData()", 5000);
});

function createChart() {
	logTrendChart = new Morris.Area({
		element: 'logTrend',
		data: dummy,
		xkey: '_time',
		ykeys: ['speed'],
		fillOpacity:0.5,
		labels: ['speed'],
		hideHover: 'auto',
		smooth:false,
		behaveLikeLine :true,
		pointSize : 3,
		resize:true
	});   
	
	inputTableChart = new Morris.Bar({
		element: 'inputTable',
		data: dummy,
		xkey: 'table',
		ykeys: ['count'],
		labels: ['건수'],
		hideHover: 'auto',
		smooth:false,
		behaveLikeLine :true,
		resize:true
	});  
	
	threadsChart = new Morris.Donut({
		element: 'threads',
		data: [
		    {value: 0, label: 'WATING'},
		    {value: 0, label: 'TIMED_WAITING'},
		  ],
		formatter: function (x) { return x + "%"}
	}); 
	
	cpuChart = new Morris.Area({
		element: 'cpuinfo',
		data: dummy,
		xkey: '_time',
		ykeys: ['total'],
		ymax : 100, 
		fillOpacity:0.5,
		labels: ['total'],
		hideHover: 'auto',
		smooth:false,
		behaveLikeLine :false,
		pointSize : 3,
		resize:true
	}); 
	
	javaHeapChart = new Morris.Bar({
		element: 'javaheap',
		data: dummy,
		xkey: 'category',
		ykeys: ['value'],
		labels: ['value'],
		hideHover: 'auto',
		smooth:false,
		behaveLikeLine :true,
		pointSize : 3,
		resize:true
	}); 
	
	diskChart = new Morris.Bar({
		element: 'disk',
		data: dummy,
		xkey: 'partition',
		ykeys: ['used', 'free'],
		labels: ['used', 'free'],
		stacked : true,
		hideHover: 'auto',
		smooth:false,
		behaveLikeLine :true,
		pointSize : 3,
		resize:true
	});  
	
	diskTrendChart = new Morris.Line({
		element: 'disk-trend',
		data: dummy,
		xkey: '_time',
		ykeys: diskTrendYkey,
		labels: diskTrendYkey,
		hideHover: 'auto',
		smooth:false,
		behaveLikeLine :true,
		pointSize : 3,
		resize:true
	}); 
	
	ajaxChartData();
}

function gcChart(dataFromAjax) {
	var result = google.visualization.arrayToDataTable(dataFromAjax);
	var options = {
			legend:'none',
			colors: ['#0B62A4']
	};
	var gcCandle = new google.visualization.CandlestickChart(document.getElementById('gc'));
	gcCandle.draw(result, options);
}

function ajaxGc(){
	$.ajax ({
		url:'/home/gc',
		type:'GET',
		contentType: 'application/json',
		dataType: 'json',
		success:function(list) {
			var data = [];
			data.push(['gc', 'min', 'iqr1', 'iqr3', 'max']);
			$.each(list, function(k, v){
				var values = [];
				values.push(v.type);
				values.push(v.min);
				values.push(v.iqr1);
				values.push(v.iqr3);
				values.push(v.max);
				data.push(values);
			})
			gcChart(data);
		}
	});
}

function ajaxChartData(){

	$.ajax ({
		url:'/home/speed',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			$("#speed").text(list[0].rate);
		}
	});	

	$.ajax ({
		url:'/home/logTrend',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			logTrendChart.setData(list);
		}
	});	
	
	$.ajax ({
		url:'/home/inputTable',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			inputTableChart.setData(list);
		}
	});	
	
	$.ajax ({
		url:'/home/threads',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			threadsChart.setData(list);
		}
	});	
	
	$.ajax ({
		url:'/home/cpuInfo',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			cpuChart.setData(list);
		}
	});    
	
	$.ajax ({
		url:'/home/javaHeap',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			javaHeapChart.setData(list);
		}
	});  
	
	$.ajax ({
		url:'/home/disk',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			diskChart.setData(list);
		}
	});   
	
	$.ajax ({
		url:'/home/diskTrend',
		type:'GET',
		contentType:'application/json',
		dataType: 'json',
		success:function(list) {
			if($.isEmptyObject(diskTrendYkey)) {
				$.each(list[0], function(key, value){
					if(key!= "_time" ){
						diskTrendYkey.push(key);
					}
				});
			}
			diskTrendChart.setData(list);
		}
	});   
	
	ajaxGc();
}

