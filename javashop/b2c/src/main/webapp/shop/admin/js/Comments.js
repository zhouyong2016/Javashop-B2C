var Comments=$.extend({
	init:function(){
		var self =this;
		$("#chide").click(function(){var comment_id=$(this).attr("comment_id");self.doHide(comment_id);});
		$("#cshow").click(function(){var comment_id=$(this).attr("comment_id");self.doShow(comment_id);});
		$("#delBtn").click(function(){self.doDelete();});
		$("#cleanBtn").click(function(){self.doClean();	});
		$("#revertBtn").click(function(){self.doRevert();});
		$("#toggleChk").click(function(){
			self.toggleSelected(this.checked);}
		);
	},
	doDelete:function(){
		
		if(!this.checkIdSeled()){
			alert("请选择要删除的评论");
			return ;
		}
	 
		if(!confirm("确认要删除这些评论吗？")){	
			return ;
		}
		
		
		this.deletePost(ctx+"/shop/admin/comments/delete.do");
			
	},
	doClean:function(){
		if(!this.checkIdSeled()){
			alert("请选择要删除的评论");
			return ;
		}
	 
		if(!confirm("确认要将这些评论彻底删除吗？删除后将不可恢复")){	
			return ;
		}
		this.deletePost(ctx+"/shop/admin/comments/clean.do");
	},
	
	doRevert:function(){
		if(!this.checkIdSeled()){
			alert("请选择要还原的评论");
			return ;
		}
	 
		this.deletePost("comments!revert.do","选择的评论已被成功还原至赠品列表中");		
	},
	//拒绝
	doHide:function(comment_id){

		if (!confirm("确认要拒绝通过审核吗？")) {
			return false;
		}
		var type=$(".type").val();
		var that =this;
		var options = {
			url : ctx+"/shop/admin/comments/hide.do?commentId="+comment_id,
			type : "POST",
			dataType : 'json',
			success : function(date) {
				if(date.result==1){
					$.Loading.success(date.message);
					parent.layer.close(index);
					parent.table.ajax.url(ctx+"/shop/admin/comments/list-json.do?type="+type).load();
				}else{
					$.Loading.error(date.message);
				}
			},
			error : function(e) {
				$.Loading.error("出现错误 ，请重试");
			}
		};
		$.ajax(options);
	},
	//通过
	doShow:function(comment_id){
		
		if (!confirm("确认要同意通过审核吗？")) {
			return false;
		}
		var typel=$(".type").val();
		var that =this;
		var options = {
			url : ctx+"/shop/admin/comments/show.do?commentId="+comment_id,
			type : "POST",
			dataType : 'json',
			success : function(date) {	
				if(date.result==1){
					$.Loading.success(date.message);
					parent.layer.close(index);
					parent.table.ajax.url(ctx+"/shop/admin/comments/list-json.do?type="+typel).load();
				}else{  
					$.Loading.error(date.message); 
				}
			},
			error : function(e) {
				$.Loading.error("出现错误 ，请重试"); 
			}
		};
		$.ajax(options);
	}
});

$(function(){
	Comments.init();
});