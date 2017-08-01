var Smtp=$.extend({},Eop.Grid,{
	init:function(){
		var self =this;
		$("#delBtn").click(function(){self.doDelete();});
		$("#toggleChk").click(function(){
			self.toggleSelected(this.checked);}
		);
	},
	doDelete:function(){
		
		if(!this.checkIdSeled()){
			alert("请选择要删除的记录");
			return ;
		}
		this.deletePost("smtp!delete.do");
	} 
	
});

$(function(){
	Smtp.opation("idChkName","idAr");
	Smtp.init();
});