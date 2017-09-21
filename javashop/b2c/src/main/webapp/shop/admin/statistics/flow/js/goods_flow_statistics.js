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
				initHistogram(conf,dateWhere);
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
	var conf = {};		
	var num = topNum;	
	var data = [];	
	var categories = []; 
	var name = [];
	
	// 遍历生成 data,categories
	for(var i in json) {
		var member = json[i];
	     data.push(member.num);
		name.push(member.name)
		categories.push(parseInt(i) + 1);
		};
		var conf = {
			title : "商品访问量Top" + num ,
			categories : categories,				
        	data : data, 
        	name: name
	};
	return conf;
}
/**
 * 初始化柱状图
 * @param id	html 初始化div的id
 * @param conf	相关配置
 */
function initHistogram(conf,dateWhere){
	var myChart = echarts.init(document.getElementById('main'));
	$.ajax({
		type : "post",
		async : true,            //异步请求	（同步请求将会锁住浏览器，用户其他操作必须等待请求完成才可以执行）
		url : ctx + "/shop/admin/flowStatistics/get-goods-flow-statistics.do",
		data : {'start_date' : dateWhere[0], 'end_date' : dateWhere[1],'top_num':topNum},
		type : "post",
		dataType:"json",  //返回数据形式为json
		success : function(result) {
		    //请求成功时执行该函数内容，result即为服务器返回的json对象
		    if (result) {
		           myChart.setOption({        //加载数据图表
		        	   tooltip: {
		                    trigger: 'item',
		                    formatter: function(params){
		                    	var dataname = result.data[params.dataIndex].name;
		                    	return dataname + '<br/>' + params.seriesName + ' : ' +params.value +'次';
		                    }
		                }
		        	   
		           });
		    }
		},
		error : function(errorMsg) {
		    //请求失败时执行该函数
		alert("图表请求数据失败!");
		myChart.hideLoading();
		}		                    
	})
	
	var option = {
		    color: ['#7cb5ec'],
		    title: {
		    	x:'center',
		        text: conf.title
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
		    
		    tooltip: {
	            show: true,
	            lable:{
	            axisPointer :
		                      {
	              shadowColor: 'rgba(0, 0, 0, 0.5)',
	              shadowBlur: 10
		                      },         
	            } 
	        },
	        legend: {
	        	x:'center',y:'bottom',
	            data:['访问量']
	        },
	        xAxis : [
	            {
	                type : 'category',
	                boundaryGap: true,
	                data : conf.categories
	            }
	        ],
	        yAxis : [
	            {
            	type: 'value',
            	boundaryGap: true,
            	name:'访问量（次）',
    	    	axisLabel : {
                formatter: '{value} 次'
	                },
	            }
	        ],
	        series : [
	            {
	            	name :"访问量",
	                type:"bar",
	                data:conf.data
	            }
	        ]
	    };
		
	myChart.setOption(option);
};