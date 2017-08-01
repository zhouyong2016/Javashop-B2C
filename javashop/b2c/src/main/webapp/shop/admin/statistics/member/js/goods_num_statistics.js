/**
 * 会员下单商品数量统计js
 * 
 * @author Sylow
 * @version v1.0,2015-09-24
 * @since v4.0
 */

var topNum = 10;	//排行数

$(function() {
	
	getGoodsNum();
	
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
 * 获取json数据
 * @param startDate 开始时间
 * @param endDate 结束时间
 */
function getGoodsNum(){
	
	var dateWhere = getDateWhere();
	
	// ajax配置
	var options = {
		url : ctx + "/shop/admin/memberStatistics/get-goods-num-top.do",
		data : {"top_num" : topNum, 'start_date' : dateWhere[0], 'end_date' : dateWhere[1]},
		type : "post",
		dataType:"json",
		success:function(data){
			
			//如果获得正确的数据
			if (data.result == 1) {
				
				if(data.data && data.data.length < 1) {
					// alert("当前条件下没有统计数据");
				}
				
				// 1.获取到统计图相关配置
				var conf = getGoodsNumConfig(data.data);
				
				// 2.初始化统计图
				initHistogram("goods_num_statistics",conf);
				
				// 3.初始化datagrid
				$("#goods_num_dg").datagrid();
				
				// 4.将数据绑定到datagrid  
				$("#goods_num_dg").datagrid('loadData', data.data); 
			} else {
				alert("调用action出错：" + data.message);
			} 
		},
		error:function(){
			alert("系统错误，请稍候再试");
		}
	};
	$.ajax(options);
}

/**
 * 根据服务返回的数据 生成统计图所需要的配置
 * @param json 数据
 * @returns json格式的配置
 */
function getGoodsNumConfig(json){
	var conf = {};			//配置
	var num = topNum;								// top几
	var colors = Highcharts.getOptions().colors;	// 颜色

	var data = [];	// Y轴 排名数据
	var categories = []; //X轴 名次数据
	
	// 遍历生成 data,categories
	for(var i in json) {
		var member = json[i];
		var temp = {
			name:member.name,
			color: colors[0],
	        y: member.num	
		};
		data.push(temp);
		categories.push(parseInt(i) + 1);
	}
	
	var conf = {
		title : "买家排行Top" + num ,		//统计图标题
		yDesc : "商品数量（个）" ,			//y轴 描述
										//X 轴数据 [数组]
		categories : categories,				
            							//Y轴数据 [数组]
		series : [
			{
				name : '购买量', 
				data: data,
            	color: 'white'
			}
		]						

	};
	return conf;
}

