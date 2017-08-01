var GoodsTag=$.extend({},Eop.Grid,{
	init:function(){
		var self =this;
		$("#btnAdd").click(function(){self.doAdd();});
	},
	
	doAdd:function(){
		
		if(!this.checkIdSeled()){
			alert("请选择要添加的商品！");
			return ;
		}
		
		$.Loading.show("正在添加商品...");
		
		this.addPost1(basePath+"goodsShow!batchAdd.do");
			
	},
	addPost1:function(url,msg){
		var self =this;
		url=url.indexOf('?')>=0?url+"&":url+"?";
		url+="ajax=yes";
	//	url=basePath+url;
		var options = {
				url : url,
				type : "POST",
				dataType : 'json',
				success : function(result) {	
					$.Loading.hide();
					if(result.result==0){
						alert(result.message);
					}else{
						alert(result.message);
					}
				},
				error : function(e) {
					$.Loading.hide();
					alert("出现错误 ，请重试");
				}
			};

			$('#gridform').ajaxSubmit(options);		
	}
	
});

$(function(){
	GoodsTag.init();
});