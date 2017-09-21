/**
 * 总流量统计页面
 */


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
	initFlowStatistics(dateWhere);
	//搜索按钮单击事件
	$("#search_statis").click(function(){
		var dateWhere = getDateWhere();
		
		//如果没有时间条件 就不用刷新
		if(!dateWhere) {
			return;
		}
		
		initFlowStatistics(dateWhere);
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
 * 初始化购买时段分布统计图
 */
function initFlowStatistics(dateWhere) {

	// ajax配置
	var options = {
		url : ctx + "/shop/admin/flowStatistics/get-flow-statistics.do" ,
		data : {'start_date' : dateWhere[0], 'end_date' : dateWhere[1],'statistics_type':dateWhere[2]},
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
				
				var conf = getFlowConfig(data.data);
				// 2.初始化统计图
				initHistogram(conf);
				

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
	var startDate, endDate,statisticsType; //开始时间和结束时间 和统计类型
	
	if (c_type == 0) {
		alert("请先选择查询方式!");
		return;
	}
	
	//如果是按照年来筛选订单的
	if(c_type == 2) {
		statisticsType = "1";
		var year = $("#year").val();
		
		if(year == 0) {
			var dateWhere = [];
			dateWhere[0] = "";
			dateWhere[1] = "";
			return dateWhere;
		}
		
		startDate = year + "-01-01 0:0:01";
		endDate = year + "-12-31 23:59:59";
		
	} else {
		statisticsType = "0";
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
		
		startDate = year + "-" + month + "-01 0:0:01";
		endDate = year + "-" + month + "-" + lastDay +" 23:59:59";
	}
	var dateWhere = [];
	dateWhere[0] = startDate;
	dateWhere[1] = endDate;
	dateWhere[2] = statisticsType;
	return dateWhere;
}

/**
 * 获得总流量统计相关配置
 * @param json 数据
 */
function getFlowConfig(json){
	
	var conf = {};			//配置

	var data = [];	// Y轴 排名数据
	var categories = []; //X轴 名次数据
	
	// 遍历生成 data,categories
	for(var i in json) {
		var order = json[i];
		
		//添加到数组
		data.push(order.num);
		categories.push(" " +order.day_num);
	}
	
	var conf = {
		title : "访问量统计" ,		//统计图标题									
		categories : categories,	//X 轴数据 [数组]			
        data: data                 //Y轴数据 [数组]
	};
	return conf;
	
};


function initHistogram(conf){
	var myChart = echarts.init(document.getElementById('main'));
	option = {
			color: ['#7cb5ec'],
		    title: {
		    	x:'center',
		        text: conf.title  //统计图标题
		    },
		    tooltip: {
		        trigger: 'axis'
		    },
		    toolbox: {
		        show : true,
		        feature : {
		            mark : {show: true},
		            magicType : {show: true, type: ['line', 'bar']},
		            restore : {show: true},
		            saveAsImage : {show: true}
		        }
		    },
		    legend: {
		    	x:'center',y:'bottom',
	            data:['访问量']   // y轴数据所代表的意思
	        },
		    xAxis:  {
		        type: 'category',
		        boundaryGap: true,
		        data: conf.categories
		    },
		    yAxis: {
		        type: 'value',
		        name:'访问量（次）',
		    	axisLabel : {
	            formatter: '{value} 次'
	        },
	        	boundaryGap : true
	    
		    },
		    series: [
		        {
		            name:'访问量',   //点击悬浮框中的内容
		            type:'line',
		            data:conf.data,
		            markPoint: {
		                data: [
		                    {type: 'max', name: '最大值'},   //最大值和最小值的设置
		                    {type: 'min', name: '最小值'}
		                ]
		            },
		            markLine: {
		                data: [
		                    {type: 'average', name: '平均值'}   //平均值的设置
		                ]
		            }
		        },
		    ]
		};

	myChart.setOption(option);

	}









