Favorite={
	init:function()	{
	 
		var self =this;
		//收藏
		$(".favorite").click(function(){
			var currgoodsid = $(this).attr("goodsid");
	 
			if(!isLogin){
				$.LoginDialog.open({loginSuccess:function(){
				   self.doFavorite(currgoodsid);
				}});
			}else{
				 self.doFavorite(currgoodsid);
			}
			return false;
		});
		
		$(".gnotifybtn").click(function(){
			var currgoodsid = $(this).attr("goodsid");
			var currproductid = $(this).attr("productid");
			Favorite.gnotify(currgoodsid,currproductid);			
			return false;			
		});
	},
	gnotify:function(currgoodsid,currproductid){
		var self =this;
		if(!isLogin){
			alert("请先登录，在进行登记");
			//$.LoginDialog.open({loginSuccess:function(){
			//   self.doGnotify(currgoodsid);
			//}});
		}else{
			 self.doGnotify(currgoodsid,currproductid);
		}
	},
	doGnotify:function(id,productid){
		$.Loading.show("正在登记......");
		$.ajax({ 
			url:ctx+'/api/shop/gnotify!add.do?goodsid='+id+'&productid='+productid,
			dataType:'json',
			success:function(result){
				$.Loading.hide();
				alert(result.message);
			}
		});
	}
	,
	doFavorite:function(currgoodsid){
		$.Loading.show("正在收藏...");
		$.ajax({ 
			url:ctx+'/api/shop/collect!addCollect.do?goods_id='+currgoodsid,
			dataType:'json',
			success:function(result){
				$.Loading.hide();
				alert(result.message);
			} 
		});
	}
};