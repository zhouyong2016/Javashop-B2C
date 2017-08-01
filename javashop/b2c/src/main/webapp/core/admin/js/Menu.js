var currimg;
var this_img;
function onClick(event, treeId, treeNode, clickFlag){
	$("#detail_wraper").load(ctx+"/core/admin/menu/edit.do?id="+treeNode.menuid+"&"+new Date().getTime(),function(){
		//$("#editForm").validate();
		$("#saveEditBtn").click(function(){ 
			saveEdit();
		});
		$(".icon_wrap img").click(function(){ 
			this_img= $(this);
			openImgDlg(setSrc);
		});
	});
}
function saveAdd(){
	
	/*if( !$("form").checkall() ){
		return ;
	}*/
	var name = $("#name").val();
	if(name == ''){
		alert("菜单名称不能为空！");
		return;
	}
	
	var options = {
			url :ctx+"/core/admin/menu/save-add.do",
			type : "POST",
			dataType : 'json',
			success : function(result) {	
			 	if(result.result==1){
			 		$.Loading.success("保存成功");
			 		$("#menuid").attr("disabled",false).val( result.data );
					$("#saveAddBtn").unbind().bind("click",function(){
						saveEdit();
					});
			 	}else{
			 		$.Loading.hide();
			 		$.Loading.error(result.message);
			 	}
			 	
			},
			error : function(e) {
				$.Loading.hide();
				alert("出错啦:(");
			}
		};
	if ($('#addForm').form('validate')) {
		$.Loading.show('正在保存，请稍侯...');
		$("form").ajaxSubmit(options);	
	}
	
}


function saveEdit(){
//	if( !$("form").checkall() ){
//		return ;
//	}
	var options = {
			url :ctx+"/core/admin/menu/save-edit.do",
			type : "POST",
			dataType : 'json',
			success : function(result) {	
				 
			 	if(result.result==1){
			 		
			 		$.Loading.success("保存成功");
			 		 
			 	}else{
			 		$.Loading.hide();
			 		$.Loading.error(result.message);
			 	}
			 	
			},
			error : function(e) {
				$.Loading.hide();
				alert("出错啦:(");
				}
		};

	if ($('#editForm').form('validate')) {
		$.Loading.show('正在保存，请稍侯...');
		$("form").ajaxSubmit(options);
	}
}


function setSrc(path){
	this_img.attr("src",path);
	$("#menu_icon").val(path);
	this_img.prev(".icon").val(path);
}


function deleteMenu(id){
	$.Loading.show('请稍侯...');
	$.ajax({
		url:ctx+'/core/admin/menu/delete.do?id='+id,
		type:'post',
		dataType:'json',
		success:function(result){
			 
		 	if(result.result==1){
		 		$.Loading.success("删除成功");
		 	 
		 	}else{
		 		$.Loading.hide();
		 		$.Loading.error(result.message);
		 	}			
		},
		error:function(){
			$.Loading.hide();
			alert("出错啦:(");
		}
	});
}


function beforeRemove(treeId, treeNode) {
	if ((treeNode.children == undefined)) {
		return confirm("确认删除菜单 " + treeNode.name + " 吗？");
	} else {
		alert("不能删除有子菜单的选项！");
		return false;
	}
}

function onRemove(e, treeId, treeNode) {
	deleteMenu(treeNode.menuid);
}


function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy){
	var node  = treeNodes[0];
	// alert(moveType+"  "+ node.parentTId +"　　" +targetNode.parentTId);
	moveNode(node.menuid,targetNode.menuid,moveType);
}


function moveNode(menuid,target_menu_id,moveType){
	
	$.Loading.show('请稍侯...');
	$.ajax({
		url: ctx+'/core/admin/menu/move.do?id='+menuid+"&targetid="+target_menu_id+"&movetype="+moveType,
		type:'post',
		dataType:'json',
		success:function(result){
			 
		 	if(result.result==1){
		 		$.Loading.success("菜单移动成功");
		 		
		 	}else{
		 		$.Loading.error(result.message);
		 	}	
		 	
		 	
		},
		error:function(){
			$.Loading.hide();
			alert("出错啦:(");
		}
	});	
	
}

 



$(function(){
	
	var setting = {
			async: {
				enable: true,
				url:ctx+"/core/admin/menu/json.do",
				autoParam:["menuid"]
			},
			callback: {
				onClick: onClick,
				beforeRemove: beforeRemove,
				onRemove: onRemove,
				onDrop:onDrop
			},
			edit:{
				drag:{
					isCopy:false
				},
				enable:true,
				showRenameBtn:false
			}
		};	
	
	$.fn.zTree.init($(".ztree"), setting);
 
	$("#add-menu-btn").click(function(){
		$("#detail_wraper").load(ctx+"/core/admin/menu/add.do?parentid=0",function(){
			$("#saveAddBtn").click(function(){
				saveAdd();
			});
			$(".icon_wrap img").click(function(){
				this_img= $(this);
				openImgDlg(setSrc);
			});
		});
	});
	
});

