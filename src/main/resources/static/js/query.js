/**
 * 
 */
var queryID = 0;
var queryStatus;
var $table = $('#result-table');   
var refreshIntervalId;

$(document).ready(function(){
	$(".table-responsive").css('height', $(window).height()-345);
	$(window).resize(function() { 
		$(".table-responsive").css('height', $(window).height()-345);
	});
	$("#qcount").text("0");
	$("#qid").text("0");
});

$(window).bind("beforeunload", function ()		{
	if(queryID != "0" && queryStatus !="Ended" && queryStatus != "Cancelled") {
		$.ajax({
			url:"/query/remove/"+queryID,
			type:"GET"
		});
	}
});


function initQuery(){
	$("#result-table").bootstrapTable('destroy');
	$("#result-table thead tr").html('');
	if(queryID != "0") {
		$("#qid").text("0");
		$("#query-text").val('');
		$("#qcount").text("0");
		$("#page-move").val('');
		$("#qnum").text("");
        $("#qstart").text("");
        $("#qbranch").empty();	
		queryID = 0;
		queryStatus = "";
		$("#qstatus").text("");
		$("#qpage-count").text("0");
		$.ajax({
			url:"/query/remove/"+queryID,
			type:"GET"
		});
	}
}

function clear() {
	if(queryID != "0") {
		$("#qid").text("0");
		$("#query-text").val('');
		$("#qcount").text("0");
		$("#page-move").val('');
		$("#qnum").text("");
        $("#qstart").text("");
        $("#qbranch").empty();	
		queryID = 0;
		queryStatus = "";
		$("#qstatus").text("");
		$("#qpage-count").text("0");
		$.ajax({
			url:"/query/remove/"+queryID,
			type:"GET"
		});
	}
}

function getCount() {
	var request = { queryId: queryID };
	$.ajax({
		url:"/query/count",
		dataType: 'json',
		type:"GET",
		data: request,
		success:function(list)
		{
			var count = list["count"];
			$("#qcount").text(count);
			queryStatus = list["status"];
			var page = Math.ceil(count/50);
			$("#qpage-count").text(page);
			$("#qstatus").text(queryStatus);
		}
	});
	getTree();
	if(queryStatus == "Ended" || queryStatus == "Cancelled") {
		clearInterval(refreshIntervalId);
	}
}

function getSubResult(page) {
	var page = $("#page-move").val();
	$("#result-table").bootstrapTable('destroy');
	$.ajax({
		url:"/query/result/"+queryID+"/"+page,
		contentType:'application/json;charset=UTF-8',
		dataType: 'json',
		type:"GET",
		success:function(list)
		{
			$("#result-table").bootstrapTable({data: list});
		}
	});
	
}

function stopQuery() {
	clearInterval(refreshIntervalId);
	if(queryID != "0" && queryStatus !="Ended" && queryStatus != "Cancelled") {
		$.ajax({
			url:"/query/stop/"+queryID,
			dataType: 'text',
			type:"GET",
			success : function(list) {
				queryStatus = list;
				$("#qstatus").text(queryStatus);
			}
		});
	}
	
}

function getResult(){
	var request = { queryText: $("#query-text").val()};
    initQuery();
	$.ajax({
		url:"/query/result",
		contentType:'application/json;charset=UTF-8',
		dataType: 'json',
		type:"POST",
		data: JSON.stringify(request),
		success:function(list)
		{
			var result = list["result"];
			queryID = list["queryID"];
			$("#qcount").text(list["count"]);
			$("#qid").text(queryID);
			queryStatus = list["status"];
			$("#qstatus").text(queryStatus);
			$("#result-table thead tr").html('');
			refreshIntervalId = setInterval("getCount()", 1000);
			var index;
			$.each(result[0], function(key, value) {
				var tr = $("<th data-field=\'"+key+"' class=\"table-header\">"+key+"</th>");
				$("#result-table thead tr").append(tr);
				index++;
			});
			$("#result-table").bootstrapTable({data: result});
			getTree();
		}
	});
}

function getTree() {
	var request = { queryId: queryID };
	$.ajax({
		url:"/query/tree/"+queryID,
		contentType:'application/json;charset=UTF-8',
		dataType: 'json',
		type:"GET",
		success:function(list)
		{
			$("#qnum").text("─  쿼리 " + queryID);
            $("#qstart").text(list[0]["last_started"]+"에 실행됨, "+list[0]["elapsed"]+"ms 소요됨");
            $("#qbranch").empty();
            $.each(list, function(index, value) {
            	var li = $("<li class=\"qbran\">┗<span class=\"qbname\">" + value["command"] + "</span><span class=\"qbcount\">" + value["push_count"] + "건의 데이터를 넘김</span></li>");
            	$("#qbranch").append(li);         	        	
            });
		}
	});
}
