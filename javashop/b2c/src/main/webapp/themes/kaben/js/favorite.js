Favorite={
	init:function()	{
	 
		var self =this;
		//收藏
		$(".favorite").click(function(){
			var currgoodsid = $(this).attr("goodsid");
			if(!isLogin){
				$.LoginDialog.open({loginSuccess:function(){
				   self.doFavorite(currgoodsid);
				   location.reload();
				}});
			}else{
				 self.doFavorite(currgoodsid);
			}
			return false;
		});
		
		$(".gnotifybtn").click(function(){
			var currgoodsid = $(this).attr("goodsid");
			Favorite.gnotify(currgoodsid);			
			return false;			
		});
	},
	gnotify:function(currgoodsid){
		var self =this;
		if(!isLogin){
			alert("请先登录，在进行登记");
			//$.LoginDialog.open({loginSuccess:function(){
			//   self.doGnotify(currgoodsid);
			//}});
		}else{
			 self.doGnotify(currgoodsid);
		}
	},
	doGnotify:function(id){
		$.Loading.show("正在登记......");
		$.ajax({ 
			url:ctx+'/api/shop/gnotify/add.do?goodsid='+id,
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
			url:ctx+'/api/shop/collect/add-collect.do?goods_id='+currgoodsid,
			dataType:'json',
			success:function(result){
				$.Loading.hide();
				alert(result.message);
			} 
		});
	}
};