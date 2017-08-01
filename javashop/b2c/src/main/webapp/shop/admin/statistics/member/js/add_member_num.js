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

	var dateWhere = getDateWhere();
	var lastDateWhere = getLastDateWhere();
	// ajax配置
	var options = {
		url : ctx + "/shop/admin/memberStatistics/get-add-member-num.do",
		data : {'start_date' : dateWhere[0], 'end_date' : dateWhere[1], 'lastStart_date' : lastDateWhere[0], 'lastEnd_date' : lastDateWhere[1],'type': dateWhere[2]},
		type : "post",
		dataType:"json",
		success:function(data){
			
			//如果获得正确的数据
			if (data.result == 1) {
				if(data.data.list && data.data.list.length < 1) {
					 alert("当前条件下没有统计数据");
				}
				// 1.获取到统计图相关配置
				var conf = getOrderNumConfig(data.data.list);
				
				// 2.初始化统计图
				initHistogram("order_num_statistics",conf);
				
				// 3.初始化datagrid
				$("#order_num_dg").datagrid();
				//计算上月数量
				var allList = [];
				var nowList = data.data.list;
				var lastList = data.data.lastList;
				for (var l = 0; l < lastList.length; l++) {
					//上月日期
					var lastDay = lastList[l].membertime;
					
					var result = false;
					
					for(var i = 0; i < nowList.length; i++) {
						
						var nowDay = nowList[i].membertime;
						
						if (nowDay == lastDay) {
							result = true;
							break;
						}
					}
					
					if (!result) {
						var temp = {"membertime":lastDay,"membernum":0,"last_member_num":lastList[l].last_member_num};
						nowList.push(temp);
					}
				}
				
				//计算当月会员数量，当本月有数量，而上月无数量，则，上个月添加0
				
				for(var i = 0; i < nowList.length; i++) {
					//当月数量
					var memberNum = nowList[i].membernum;
					 //当月日期
					var day = nowList[i].membertime;
					//数组
					var tempData = {member_num:memberNum,membertime:day};
					tempData["last_member_num"] = 0;
					tempData["member_an"] = "0.00%";
					for(var j = 0; j < lastList.length; j++) {
						//上月时间
						var lastDay = lastList[j].membertime;
						
						if (day == lastDay) {
							var lastMemberNum = lastList[j].membernum;
							tempData["last_member_num"] = lastMemberNum;
							if(parseInt(memberNum) == 0){
								memberNum=parseInt(lastMemberNum);
							}
							
							//计算同比率
						//	var memberAn = parseInt(memberNum)/parseInt(lastMemberNum);
							var memberAn = (parseInt(memberNum)-parseInt(lastMemberNum))/parseInt(lastMemberNum);
							//1.先转换成4位小数
						//	memberAn = memberAn.toFixed(4);
						//	memberAn = memberAn.slice(2,4)+'.'+memberAn.slice(4,6)+'%';
							memberAn = memberAn.toFixed(2)+"%";
							//2.增加字段
							tempData["member_an"] = memberAn;
							break;
						}
					}
					allList.push(tempData);
				}
				//按membertime排序
				allList.sort(getSortFun('asc', 'membertime'));
				
				// 4.将数据绑定到datagrid  
				$("#order_num_dg").datagrid('loadData', allList); 

				
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
//排序
function getSortFun(order, sortBy) {
    var ordAlpah = (order == 'asc') ? '>' : '<';
    var sortFun = new Function('a', 'b', 'return a.' + sortBy + ordAlpah + 'b.' + sortBy + '?1:-1');
    return sortFun;
}


/**
 * 根据服务返回的数据 生成统计图所需要的配置
 * @param json 数据
 * @returns json格式的配置
 */
function getOrderNumConfig(json){
	var conf = {};			//配置
	var categories = []; //X轴 时间
	var data = [];	// Y轴 增加数量
	//alert(JSON.stringify(json));
	// 遍历生成 data,categories
	for(var i in json) {
		var member = json[i];
		//添加到数组
		categories.push(member.membertime + "");
		data.push(member.membernum);
	}
	var conf = {
			title : "新增会员数量",		//统计图标题
			yDesc : "新增会员数量" ,			//y轴 描述
											//X 轴数据 [数组]
			categories : categories,				
	            							//Y轴数据 [数组]
			 series: [{
		            name: '新增会员数量',
		            data: data
		        }]						

		};
	return conf;
}

