/**
 * 会员增加数量统计
 * 
 * @author whj
 * @version v1.0,2015-09-28
 * @since v4.0
 */

/**
 * 封装js
 */
var addMenber={
		addMenberJson:function(){
		getOrderNum();
	}
}


$(function() {
	
	getOrderNum();
	

	var type = $("#cycle_type").val();
	
	// 如果统计类型 是按年统计
	if(type == "2") {
		$("#month").hide();
	} else {
		$("#month").show();
	}
	
	// 统计类型变更事件
	$("#cycle_type").change(function(){
		
		var type = $(this).val();
		
		// 如果统计类型 是按年统计
		if(type == "2") {
			$("#month").hide();
		} else {
			$("#month").show();
		}
		
	});
	
});

/**
 * 获取json数据完成线装图
 * @param startDate 开始时间
 * @param endDate 结束时间
 */
function getOrderNum(){

}


