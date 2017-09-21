$(function(){
	
	//添加楼层点击事件
	$("#add_floor_button").click(function(){
		append();	
	});
	
	//初始化楼层列表
	$("#pcfloordata").treegrid({
		fitColumns:'true',
	    url:ctx+'/core/admin/floor/get-list-by-parentid-json.do?parentid=0&pageid='+pageid,
	    animate:false,
	    idField:'id',
	    treeField:'text',
	    columns:[[
			{field:'id',title:'ID',width:80},
	        {field:'text',title:'名称',width:200},
	        {field:'ordernum',title:'排序',width:80,formatter:formatFloorcount},
	        {field:'is_display',title:'是否显示',width:80,formatter:formatShow},
	        {field:'add',title:'添加子',width:38,formatter:formatAdd},
	        {field:'edit',title:'编辑',width:36,formatter:formatEdit},
	        {field:'delete',title:'删除',width:36,formatter:formatDelete},
	        {field:'edit_template',title:'模板编辑',width:36,formatter:formatEditTemplate}
	    ]],
	    onBeforeExpand:function(row,param){
	        if(row){
	        	$(this).treegrid('options').url=ctx+"/core/admin/floor/get-list-by-parentid-json.do?parentid="+row.id;
	    	}
	    }
	});
	
	
	//保存排序
	$("#save_sort_button").click(function(){
		var floor_ids=$("input[name='floor_ids']");
		if(floor_ids.length<1){
			$.Loading.error('当前没有需要保存的数据');
			return;
		}
		$.Loading.show('正在保存排序，请稍侯...');
		var options = {
				url :ctx+"/core/admin/floor/save-sort.do",
				type : "POST",
				dataType : 'json',
				success : function(result) {				
				 	if(result.result==1){
				 		$.Loading.success(result.message);
				 		location.reload();
				 	}else{
				 		alert(result.message);
				 	}
				},
				error : function(e) {
					$.Loading.hide();
					alert("出错啦:(");
 				}
 		};
 
		$("#floorform").ajaxSubmit(options);
	})
});


/**
 * 根据操作类型对弹出的dialog进行基本配置
 * @param id 楼层id 当id为空时,是添加楼层事件
 * @param obj 弹窗类型
 * @returns
 */
function append(id, obj) {
	var map = {}; // Map map = new HashMap();
	if (!id) {
		map["href"] = ctx+"/core/admin/floor/add-floor.do?id="+pageid;
		map["formId"] = "#addForm";
		map["url"] = ctx+"/core/admin/floor/save-add.do?ajax=yes";
		map["title"] = "添加楼层";
		map["loadshow"] = "正在添加......";
		map["width"]=726;
		map["height"]=300;
	} else {
		if (obj == 1) {
			map["href"] = ctx+"/core/admin/floor/add-children.do?floorid=" + id;
			map["formId"] = "#addchildrenForm";
			map["url"] = ctx+"/core/admin/floor/save-add.do?ajax=yes";
			map["title"] = "添加子楼层";
			map["loadshow"] = "正在添加......";
			map["width"]=750;
			map["height"]=360;
		} else {
			map["href"] = ctx+"/core/admin/floor/edit.do?floorid=" + id;
			map["formId"] = "#editForm";
			map["url"] = ctx+"/core/admin/floor/save-edit.do?ajax=yes";
			map["title"] = "修改楼层";
			map["loadshow"] = "正在修改......";
			map["width"]=750;
			map["height"]=360;
		}

	}
	map["divDialog"] = "#divdia";
	map["gridreload"] = "#pcfloordata";
	addDialog(map);
}

/**
 * 根据配置信息弹出窗口
 * @param map 配置信息
 * @returns
 */
function addDialog(map) {
	$(map["divDialog"]).show();
	$(map["divDialog"]).dialog({
		title : map["title"],
		width : map["width"],
		height : map["height"],
		closed : false,
		cache : false,
		href : map["href"],
		modal : true,
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				var savebtn = $(this);
　　				var disabled=savebtn.hasClass("l-btn-disabled");
　　				if(!disabled){
					 submitForm(map,savebtn);
				}
			}
		}, {
			text : '取消',
			handler : function() {
				$(map["divDialog"]).dialog('close');
			}
		} ]
	});
}

/**
 * 提交表单
 * @param map 配置信息
 * @param savebtn 按钮对象
 * @returns
 */
function submitForm(map,savebtn) {
	var formflag = $(map["formId"]).form().form('validate');
	if (formflag) {
		$.Loading.show("正在保存请稍候...");
		savebtn.linkbutton("disable");	
		var options = {
			url : map["url"],
			type : "POST",
			dataType : 'json',
			success : function(result) {
				if (result.result == 1) {
					$(map["divDialog"]).dialog('close');
					//$(map["gridreload"]).treegrid('reload');
					loadData1(pageid);
					$.Loading.success(result.message);
				}
				if (result.result == 0) {
					$.Loading.error(result.message);
				}
				savebtn.linkbutton("enable");
			},
			error : function(e) {
				$.Loading.error("出现错误 ，请重试");
				savebtn.linkbutton("enable");
			}
		};
		$(map["formId"]).ajaxSubmit(options);
	}else{
		savebtn.linkbutton("enable");
		$.Loading.hide();
	}
}
/**
 * 格式化列表'添加子'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatAdd(value, row, index) {
	var val="";
	if(row.parent_id==0){
		 val= "<a href='javascript:void(0);' class='add' onclick='append(" + row.id
			+ ",1)'><img src='"+ctx+"/shop/admin/images/transparent.gif'></a>";
	}
	return val;
}
/**
 * 格式化列表'编辑'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatEdit(value, row, index) {
	var val = "<a class='edit' title='修改' href='javascript:void(0);' onclick='append("
			+ row.id + ",2)' ></a>";
	return val;
}
/**
 * 格式化列表'删除'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatDelete(value, row, index) {
	var val = '<a href="javascript:;" class="delete" onclick="del('
			+ row.id
			+ ')"><img floorid="'+row.id+'" src="'+ctx+'/shop/admin/images/transparent.gif"></a>';
	return val;
}
/**
 * 格式化列表'编辑模板'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatEditTemplate(value, row, index) {
	
	var val="";
	if(row.parent_id==0){
		var val = '<a href="javascript:;" class="edit" onclick="'
		+"newTab('模板编辑','"+ctx+"/core/admin/floor/edit-template.do?floor_id="+row.id+"&page_id="+pageid+"')"
		+'" ></a>';
	}
	return val;
}
/**
 * 格式化列表'是否显示'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatShow(value, row, index){
	var val = "";
	if(value == 0){
		val = '<p id="floorstatus'+row.id+'" style="display:inline;margin:0px 10px">已显示</p>'
			+'<input class="stop button" type="button" value="停用" floorid="'+row.id+'" id=floorimg'+row.id+' onclick="changeshow('+row.id+','+row.parent_id+')" showvalue="0" changevalue="1" >';
	}else if(value == 1){
		val = '<p id="floorstatus'+row.id+'" style="display:inline;margin:0px 10px">未显示</p>'
			+'<input class="start button" type="button" value="启用" floorid="'+row.id+'" id=floorimg'+row.id+' onclick="changeshow('+row.id+','+row.parent_id+')" showvalue="1" changevalue="0" >';
	}
	return val;
}


/**
 * 格式化列表'排序'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatFloorcount(value, row, index) {
	var val = '<input type="text" class="receiptsInputText" autocomplete="off" onblur="onlyNumber(this,'+row.sort+')" value="'+row.sort+'" style="width:70px" name="floor_sorts" maxlength="9">';
	val+='<input type="hidden" name="floor_ids" value="'+row.id+'" > '
	return val;
}


/**
 * 更改显示状态
 * @param id
 * @returns
 */
function changeshow(id,pid){
	var flag=true;
	if(pid != 0){
		var pstatus=$("#floorimg"+pid).val();
		if(pstatus =='启用'){
			flag=false;
		}
	}
	var value=$("#floorimg"+id).attr("showvalue");
	var changevalue=$("#floorimg"+id).attr("changevalue");
	if(flag){
		$.ajax({
			url:ctx+"/core/admin/floor/save-display.do?id=" + id+"&is_display="+changevalue,
			type : "POST",
			dataType : 'json',
			success : function(result) {
				if (result.result == 1) {
					$.Loading.success(result.message);
					//loadData1(1);
					if(changevalue==1){
						for(var i=0;i<result.data.length;i++){
							var imgid=result.data[i];
							$("#floorimg"+imgid).removeClass("stop");
							$("#floorimg"+imgid).addClass("start");
							$("#floorimg"+imgid).val("启用");
							$("#floorstatus"+imgid).html("未显示");
							$("#floorimg"+imgid).attr("showvalue",changevalue);
							$("#floorimg"+imgid).attr("changevalue",value);
						}
					}else{
						for(var i=0;i<result.data.length;i++){
							var imgid=result.data[i];
							$("#floorimg"+imgid).removeClass("start");
							$("#floorimg"+imgid).addClass("stop");
							$("#floorimg"+imgid).val("停用");
							$("#floorstatus"+imgid).html("已显示");
							$("#floorimg"+imgid).attr("showvalue",changevalue);
							$("#floorimg"+imgid).attr("changevalue",value);
						}
					}
				}
				if (result.result == 0) {
					$.Loading.error(result.message);
				}
			},
			error : function(e) {
				$.Loading.error("出现错误 ，请重试");
			}
		});
	}else{
		$.Loading.error("请启用上一级楼层");
	}

}

/**
 * 重新加载数据
 * @param pageId
 * @returns
 */
function loadData1(pageId){
	$.ajax({
		url : ctx+"/core/admin/floor/get-list-by-parentid-json.do?parentid=0&pageid="+pageId,
		type : "POST",
		dataType : 'json',
		success : function(result) {
			$("#pcfloordata").treegrid('loadData',result);
		},
		error : function(e) {
			$.Loading.error("出现错误 ，请重试");
		}
	});
}



//验证排序值是否输入有误 
function onlyNumber(obj,sort,id){
	var z = /^[0-9]*$/;
	if(!z.test(obj.value)){
	   	alert("排序值输入有误！");
	   	obj.value = "" + sort;
    }else{
    	if(obj.value.length > 1){
    		if(obj.value.charAt(0) == "0"){
    			var s = "";
       			for(var i = 0; i < obj.value.length; i++){
       				if(obj.value.charAt(i) == "0"){
       					s = obj.value.substring(i+1);
       				}else{
       					break;
       				}
       			}
       			if(s == ""){
       				s = 0;
       			}
       			obj.value = "" + s;
    		}
    	}
    }
}
/**
 * 删除楼层
 * @param id 楼层id
 * @returns
 */
function del(id) {
	if (!confirm("删除楼层会删除楼层所有数据,确定删除吗？")) {
		return;
	}
	$.Loading.show("正在删除......");
	$.ajax({
		url : ctx+"/core/admin/floor/delete.do?floor_id=" + id,
		type : "POST",
		dataType : 'json',
		success : function(result) {
			if (result.result == 1) {
				$.Loading.success(result.message);
				loadData1(pageid);
			}
			if (result.result == 0) {
				$.Loading.error(result.message);
			}
		},
		error : function(e) {
			$.Loading.error("出现错误 ，请重试");
		}
	});
}