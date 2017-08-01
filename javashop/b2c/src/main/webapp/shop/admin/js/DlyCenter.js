var DlyCenter=$.extend({},Eop.Grid,{
	init:function(){
		var self =this;
		$("#delBtn").click(function(){self.doDelete();});
		$("#toggleChk").click(function(){
			self.toggleSelected(this.checked);}
		);
	},
	doDelete:function(){
		
		if(!this.checkIdSeled()){
			alert("请选择要删除的发货地址");
			return ;
		}
		
		if( !confirm("确定要删除这些发货地址吗") ){	
			return ;
		}
	 
		$.Loading.show("正在删除发货地址...");
		
		this.deletePost("dlyCenter!delete.do");
			
	}
});

$(function(){
	DlyCenter.init();
});