/**
 * 购买分析页面
 */

var topNum = 30;

$(function(){
	
	//初始化下拉菜单中的时间
	var currentYear = new Date().getFullYear();
	var historyYear = currentYear-10;
	var currentmonth = new Date().getMonth();
	currentmonth+=1;
	
	//for循环得到年数
	for(var i=0;i<20;i++){
		
		//选中当前年
		if(currentYear==historyYear){
			$("#year").append("<option value='"+historyYear+"' selected >"+historyYear+"年</option>" );
		}else{
			$("#year").append("<option value='"+historyYear+"' >"+historyYear+"年</option>" );
		}
		historyYear++;
	}
	
	//for循环得到月份
	for(var i=1;i<=12;i++){
		
		// 选中当前月
		if(currentmonth==i){
			$("#month").append("<option value='"+i+"' selected >"+i+"月</option>" );
		}else{
			$("#month").append("<option value='"+i+"' >"+i+"月</option>" );
		}
	}
	
	var dateWhere = getDateWhere();
	initGoodsFlowStatistics(dateWhere);
	//搜索按钮单击事件
	$("#search_statis").click(function(){
		var dateWhere = getDateWhere();
		
		//如果没有时间条件 就不用刷新
		if(!dateWhere) {
			return;
		}
		
		initGoodsFlowStatistics(dateWhere);
	});
	

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
 * 初始化商品访问量统计
 */
function initGoodsFlowStatistics(dateWhere) {

	// ajax配置
	var options = {
		url : ctx + "/shop/admin/flowStatistics/get-goods-flow-statistics.do" ,
		data : {'start_date' : dateWhere[0], 'end_date' : dateWhere[1],'top_num':topNum},
		type : "post",
		dataType:"json",
		success:function(data){
			//如果获得正确的数据
			if (data.result == 1) {
				//alert(JSON.stringify(data.data));
				if(data.data && data.data.length < 1) {
					//alert("当前条件下没有统计数据");
				}
				
				// 1.获取到统计图相关配置
				var conf = getGoodsFlowConfig(data.data);
				
				// 2.初始化统计图
				initHistogram("flow_statistics",conf);

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
 * 获取日期条件
 * @return dateWhere[] 下标0=开始时间  下标1=结束时间
 */
function getDateWhere(){
	
	var c_type = $("#cycle_type").val();
	var startDate, endDate; //开始时间和结束时间
	
	if (c_type == 0) {
		alert("请先选择查询方式!");
		return;
	}
	
	//如果是按照年来筛选订单的
	if(c_type == 2) {
		
		var year = $("#year").val();
		
		if(year == 0) {
			var dateWhere = [];
			dateWhere[0] = "";
			dateWhere[1] = "";
			return dateWhere;
		}
		
		startDate = year + "-01-01 0:0:00";
		endDate = year + "-12-31  23:59:59";
		
	} else {
		
		var year = parseInt($("#year").val());
		var month = parseInt($("#month").val());

		if(year == 0) {
			alert("请选择年份");
			return;
		}
		if(month == 0) {
			alert("请选择月份");
			return;
		}
		
		//得到一个月最后一天
		var lastDate = new Date(year, month, 0);
		var lastDay = lastDate.getDate();
		
		startDate = year + "-" + month + "-01 0:0:00";
		endDate = year + "-" + month + "-" + lastDay + " 23:59:59";
	}
	var dateWhere = [];
	dateWhere[0] = startDate;
	dateWhere[1] = endDate;
	return dateWhere;
}

/**
 * 根据服务返回的数据 生成统计图所需要的配置
 * @param json 数据
 * @returns json格式的配置
 */
function getGoodsFlowConfig(json){
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
		
		//添加到数组
		data.push(temp);
		categories.push(parseInt(i) + 1);
	}
	
	var conf = {
		title : "商品访问量Top" + num ,		//统计图标题
		yDesc : "访问量（次）" ,			//y轴 描述
										//X 轴数据 [数组]
		categories : categories,				
            							//Y轴数据 [数组]
		series : [
			{
				name : '访问量', 
				data: data,
            	color: 'white'
			}
		]						

	};
	return conf;
}
/**
 * 初始化柱状图
 * @param id	html 初始化div的id
 * @param conf	相关配置
 */
function initHistogram(id,conf){

	var options = {
			credits: {
	             //text: 'Javashop',
	             //href: 'http://www.javamall.com.cn'
				enabled:false
	        },
			chart : {
				type : 'column'
			},
			title : {
				text : conf.title
			},
			xAxis : {
				categories : conf.categories
			},
			yAxis : {
				min : 0,
				title : {
					text : conf.yDesc
				}
			},
			plotOptions: {
	            column: {
	                
	                dataLabels: {
	                    enabled: true,
	                    style: {
	                        fontWeight: 'bold'
	                    },
	                    formatter: function() {
	                        return this.y +'次';
	                    }
	                }
	            }
	        },
	        tooltip: {//鼠标放在图形上时的提示信息  
                formatter: function() {  
                        return '<b>'+ this.key +'</b><br/>';  
                }
	        },
			series : conf.series
		};
	$("#" + id).highcharts(options);
};