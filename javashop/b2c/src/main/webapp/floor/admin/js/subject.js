$(function(){
	//绑定添加按钮
	$("#add-subject-button").click(function(){
		append();
	});
	//绑定排序按钮
	$("#save-subject-sort").click(function(){
		var subject_ids=$("input[name='subject_ids']");
		if(subject_ids.length<1){
			$.Loading.error('当前没有需要保存的数据');
			return;
		}
		$.Loading.show('正在保存排序，请稍侯...');
		var options = {
				url :ctx+"/core/admin/subject/save-sort.do",
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
 
		$("#gridform").ajaxSubmit(options);
	});
});

/**
 * 编辑按钮弹出dialog
 * @param id
 * @returns
 */
function edit(id){
	append(id);
}
/**
 * 根据操作类型对弹出的dialog进行基本配置
 * @param id 专题id 当id为空时,是添加专题事件
 * @param obj 弹窗类型
 * @returns
 */
function append(id) {
	var map = {}; // Map map = new HashMap();
	if (!id) {
		map["href"] = ctx+"/core/admin/subject/add-subject.do";
		map["formId"] = "#addForm";
		map["url"] = ctx+"/core/admin/subject/save-add.do?ajax=yes";
		map["title"] = "添加专题";
		map["loadshow"] = "正在添加......";
		map["width"]=726;
		map["height"]=300;
	} else {
		map["href"] = ctx+"/core/admin/subject/edit-subject.do?id="+id;
		map["formId"] = "#editForm";
		map["url"] = ctx+"/core/admin/subject/save-edit.do?ajax=yes";
		map["title"] = "编辑专题";
		map["loadshow"] = "正在修改......";
		map["width"]=726;
		map["height"]=300;
	}
	map["divDialog"] = "#divdia";
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
					loadData();
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
 * 格式化专题列表'排序'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatSort(value,row,index){
	var val = '<input type="text" class="receiptsInputText" autocomplete="off" onblur="onlyNumber(this,'+row.sort+','+row.id+')" value="'+row.sort+'" style="width:70px" name="subject_sorts" maxlength="9">';
	val+='<input type="hidden" name="subject_ids" value="'+row.id+'" > '
	return val;
}
/**
 * 格式化专题列表'是否显示'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatIsDisplay(value,row,index){
	var val = "";
	if(value == 0){
		val = '<p id="subjectstatus'+row.id+'" style="display:inline;margin:0px 10px">已显示</p>'
			+'<input class="stop button" type="button" value="停用" floorid="'+row.id+'" id=subjectimg'+row.id+' onclick="changeshow('+row.id+')" showvalue="0" changevalue="1" >';
	}else if(value == 1){
		val = '<p id="subjectstatus'+row.id+'" style="display:inline;margin:0px 10px">未显示</p>'
			+'<input class="start button" type="button" value="启用" floorid="'+row.id+'" id=subjectimg'+row.id+' onclick="changeshow('+row.id+')" showvalue="1" changevalue="0" >';
	}
	return val;
}
/**
 * 格式化专题列表'编辑'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatEdit(value,row,index){
	var val="";
	val= "<a href='javascript:void(0);' class='edit' onclick='edit(" 
		+ row.id
		+ ")'></a>";
	return val;
}
/**
 * 格式化专题列表'专题设计'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatDesign(value,row,index){
	var val="";
	val= "<a href='javascript:void(0);' class='edit' onclick='design(" 
		+ row.id
		+ ")'></a>"; 
	return val;
}
/**
 * 格式化专题列表'删除'列
 * @param value
 * @param row
 * @param index
 * @returns
 */
function formatDelete(value,row,index){
	var val="";
	val+=val = '<a href="javascript:;" class="delete" onclick="del('
		+ row.id
		+ ')"><img floorid="'+row.id+'" src="'+ctx+'/shop/admin/images/transparent.gif"></a>';
	return val;
}
/**
 * 对输入的排序进行校验
 * @param obj
 * @param sort
 * @param id
 * @returns
 */
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
 * 更改显示状态
 * @param id
 * @returns
 */
function changeshow(id){
	var value=$("#subjectimg"+id).attr("showvalue");
	var changevalue=$("#subjectimg"+id).attr("changevalue");
	$.ajax({
		url:ctx+"/core/admin/subject/save-display.do?id=" + id+"&is_display="+changevalue,
		type : "POST",
		dataType : 'json',
		success : function(result) {
			if (result.result == 1) {
				$.Loading.success(result.message);
				if(changevalue==1){
						$("#subjectimg"+id).removeClass("stop");
						$("#subjectimg"+id).addClass("start");
						$("#subjectimg"+id).val("启用");
						$("#subjectstatus"+id).html("未显示");
						$("#subjectimg"+id).attr("showvalue",changevalue);
						$("#subjectimg"+id).attr("changevalue",value);
					
				}else{
						$("#subjectimg"+id).removeClass("start");
						$("#subjectimg"+id).addClass("stop");
						$("#subjectimg"+id).val("停用");
						$("#subjectstatus"+id).html("已显示");
						$("#subjectimg"+id).attr("showvalue",changevalue);
						$("#subjectimg"+id).attr("changevalue",value);
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
}
/**
 * 删除专题
 * @param id
 * @returns
 */
function del(id) {
	if (!confirm("删除专题会删除专题所有数据,确定删除吗？")) {
		return;
	}
	$.Loading.show("正在删除......");
	$.ajax({
		url : ctx+"/core/admin/subject/delete.do?id=" + id,
		type : "POST",
		dataType : 'json',
		success : function(result) {
			if (result.result == 1) {
				loadData();
				$.Loading.success(result.message);
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
/**
 * 重新加载数据
 * @returns
 */
function loadData(){
	$.ajax({
		url : ctx+"/core/admin/subject/list-json.do",
		type : "POST",
		dataType : 'json',
		success : function(result) {
			$("#subjectdata").datagrid('reload',result);
		},
		error : function(e) {
			$.Loading.error("出现错误 ，请重试");
		}
	});
}
/**
 * 新增专题设计tab页
 * @param id
 * @returns
 */
function design(id){
	parent.addTab1("专题设计",ctx+"/core/admin/subject/design.do?id="+id);
}