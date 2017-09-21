$(function(){
	$("#save_showcasesort_button").click(function(){
		var check = $("input[name='id']").val();
		if(check == false){
			alert("请选择要排序的橱窗！！！");
			return false;
		}
		$.Loading.show('正在保存排序，请稍侯...');
		var options = {
				url :ctx+"/core/admin/showcase/save-sort.do",
				type : "POST",
				dataType : 'json',
				success : function(result) {				
				 	if(result.result==1){
				 		$.Loading.success(result.message);
				 		table.ajax.url(ctx+"/core/admin/showcase/list-json.do").load();
				 	}else{
				 		alert(result.message);
				 	}
				},
				error : function(e) {
					alert("出错啦:(");
 				}
 		};
 
		$("#gridform").ajaxSubmit(options);
	});
});


/**
 * 格式化橱窗列表'排序'列
 * @param value
 * @param row
 * @param index
 * @returns {String}
 */
function formatSort(value,row,index){
	var val = '<input type="text" class="receiptsInputText" autocomplete="off" onblur="onlyNumber(this,'+row.sort+','+row.id+')" value="'+row.sort+'" style="width:70px" name="showcase_sorts" maxlength="9">';
	val+='<input type="hidden" name="showcase_ids" value="'+row.id+'" > '
	return val;
}
/**
 * 格式化橱窗列表'是否显示'列
 * @param value
 * @param row
 * @param index
 * @returns {String}
 */
function formatIsDisplay(value,row,index){
	var val = "";
	if(value == 0){
		val = '<p id="showcasestatus'+row.id+'" style="display:inline;margin:0px 10px">已显示</p>'
			+'<input class="stop button" type="button" value="停用" floorid="'+row.id+'" id=showcaseimg'+row.id+' onclick="changeshow('+row.id+')" showvalue="0" changevalue="1" >';
	}else if(value == 1){
		val = '<p id="showcasestatus'+row.id+'" style="display:inline;margin:0px 10px">未显示</p>'
			+'<input class="start button" type="button" value="启用" floorid="'+row.id+'" id=showcaseimg'+row.id+' onclick="changeshow('+row.id+')" showvalue="1" changevalue="0" >';
	}
	return val;
}
/**
 * 格式化橱窗列表'编辑'列
 * @param value
 * @param row
 * @param index
 * @returns {String}
 */
function formatEdit(value,row,index){
	var val="";
	val= "<a href='javascript:void(0);' class='edit' onclick='edit(" 
		+ row.id
		+ ")'></a>";
	return val;
}
/**
 * 格式化橱窗列表'删除'列
 * @param value
 * @param row
 * @param index
 * @returns {String}
 */
function formatDelete(value,row,index){
	var val="";
	val+=val = '<a href="javascript:;" class="delete" onclick="del('
		+ row.id
		+ ')"><img floorid="'+row.id+'" src="'+ctx+'/shop/admin/images/transparent.gif"></a>';
	return val;
}
/**
 * 对输入的排序值进行校验
 * @param obj
 * @param sort
 * @param id
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
//显示.停用的状态切换
function changeshow(id){
	var value=$("#subjectimg"+id).attr("showvalue");
	var changevalue=$("#subjectimg"+id).attr("changevalue");
	$.ajax({
		url:ctx+"/core/admin/showcase/save-display.do?id=" + id+"&is_display="+changevalue,
		type : "POST",
		dataType : 'json',
		success : function(result) {
			if (result.result == 1) {
				if(changevalue==1){
						$("#subjectimg"+id).removeClass("stop layui-btn-danger");
						$("#subjectimg"+id).addClass("start layui-btn-normal");
						$("#subjectimg"+id).val("启用");
						$("#subjectstatus"+id).html("未显示");
						$("#subjectimg"+id).attr("showvalue",changevalue);
						$("#subjectimg"+id).attr("changevalue",value);
					
				}else{
						$("#subjectimg"+id).removeClass("start layui-btn-normal");
						$("#subjectimg"+id).addClass("stop layui-btn-danger");
						$("#subjectimg"+id).val("停用");
						$("#subjectstatus"+id).html("已显示");
						$("#subjectimg"+id).attr("showvalue",changevalue);
						$("#subjectimg"+id).attr("changevalue",value);
				}
			}
			if (result.result == 0) {
				alert(result.message);
			}
		},
		error : function(e) {
			alert("出现错误 ，请重试");
		}
	});
}
/**
 * 打开新的编辑tab页
 * @param id
 */
function edit(id){
	parent.addTab("橱窗编辑"+"<input type='hidden' name='id' value='"+id+"'",ctx+"/core/admin/showcase/edit-showcase.do?id="+id);
}

/**
 * 删除橱窗
 * @param id
 */
function del(id) {
	if (!confirm("删除橱窗会删除橱窗所有数据,确定删除吗？")) {
		return;
	}
	$.Loading.show("正在删除......");
	$.ajax({
		url : ctx+"/core/admin/showcase/delete.do?id=" + id,
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
 * 加载橱窗数据
 */
function loadData(){
	$.ajax({
		url : ctx+"/core/admin/showcase/list-json.do",
		type : "POST",
		dataType : 'json',
		success : function(result) {
			$("#showcasedata").datagrid('reload',result);
		},
		error : function(e) {
			$.Loading.error("出现错误 ，请重试");
		}
	});
}