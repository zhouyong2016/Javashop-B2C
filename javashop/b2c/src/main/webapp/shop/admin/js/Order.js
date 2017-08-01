//金额
function forMoney(value, row, index) {
	var val = "￥" + value;
	return val;
}
//日期
function formatDate(value,row,index){
	return getFormatDateByLong(value, "yyyy-MM-dd");
}

//订单状态
function forStruts(value, row, index) {
	var val;
	val = getType(${status_Json},value);
	return val;
}

//付款状态
function forpay(value,row,index){
	var val;
	val = getType(${payStatus_Json},value);
	return val;
}

//发货状态
function forship(value,row,index){
	var val;
	val = getType(${ship_Json},value);
	return val;
}


function getType(exMap,value){
	var val;
	$.each(exMap,function(key,values){ 
	    if(value==key){
	    	val=values;
	    }
	});
	return val;
}
$(function(){
	$("#cleanBtn").click(function(){
		var rows = $('#orderdata').datagrid("getSelections");
		if (rows.length < 1) {
			$.Loading.error("请选择要删除的订单");
			return;
		}
		if (!confirm("确认要将这些订单彻底删除吗？删除后将不可恢复？")) {
			return;
		}
		var options = {
				url : "order!clean.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {
					if (result.result == 1) {
						$('#orderdata').datagrid("reload");
					}
					if (result.result == 0) {
						$.Loading.success(result.message);
					}
				},
				error : function(e) {
					$.Loading.error("出现错误 ，请重试");
				}
			};

		$('#orderform').ajaxSubmit(options);
	});
	$("#revertBtn").click(function(){
		var rows = $('#orderdata').datagrid("getSelections");
		if (rows.length < 1) {
			$.Loading.error("请选择要还原的订单");
			return;
		}
		if (!confirm("确认要将这些订单还原吗？")) {
			return;
		}
		var options = {
				url : "order!revert.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {
					if (result.result == 1) {
						$('#orderdata').datagrid("reload");
					}
					if (result.result == 0) {
						$.Loading.success(result.message);
					}
				},
				error : function(e) {
					$.Loading.error("出现错误 ，请重试");
				}
			};

			$('#orderform').ajaxSubmit(options);
	});
})