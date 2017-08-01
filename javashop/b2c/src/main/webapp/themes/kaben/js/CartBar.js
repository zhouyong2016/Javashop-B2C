/**
 * 购物车Bar
 * @author kingapex
 * 提供购物车Bar数量加载及悬停效果
 * 并暴露加载数量接口供其它程序使用
 */
var CartBar={
		/**
		 * 初始化方法 
		 * 1.加载数量
		 * 2.绑定hover事件
		 */
		init:function(){
			 var self = this;
			 this.barWrapper = $("#cart_bar_wrapper"); //购物车bar主元素
			 this.numBox = this.barWrapper.find(".num"); //数量元素
	//		 var contentBox = this.barWrapper.find(".content");  //购物列表元素
			 var listBox = this.barWrapper.find(".cart_content02");  //购物列表元素
			 this.loadNum();
			 this.barWrapper.hover(
				 function(){
					 if($(this).hasClass("hover")){return false;}
					 $(this).addClass("hover");
					 
					 //重新加载购物车数量
					 CartBar.loadNum();
					 
					 listBox.show();
					//显示loading 图标
					 $.ajaxSetup ({ cache: false });   //jquery ajax 的load()方法IE下无法获取页面内容
					 listBox.load(ctx+"/cart/cart_bar.html",function(){
						 
						 //加载完列表绑定删除事件
						 $(this).find(".delete").click(function(){
							 var itemid = $(this).attr("itemid");
							 self.deleteItem(itemid);
						 });
					 });
				 },function(){
					 $(this).removeClass("hover");
					 listBox.hide();
				 }
			 );
		},
		
		/**
		 * 购物车项删除
		 * @param itemid
		 */
		deleteItem:function(itemid){
			var self=this;
			$.Loading.show("请稍候...");
			$.ajax({
				url:ctx+"/api/shop/cart/delete.do",
				data:"cartid="+itemid,
				dataType:"json",
				success:function(result){
					$.Loading.hide();
					if(result.result==1){				
						self.loadNum();
						self.barWrapper.find(".item[itemid="+itemid+"]").remove();
						$(".common-cart-checkout").load(ctx+"/cart/common_totle.html");
						
					}else{
						$.alert(result.message);
					}	
				},
				error:function(){
					$.Loading.hide();
					$.alert("出错了:(");
				}
			});
		},
		/**
		 * 加载购物车中的商品数量
		 * 
		 */
		loadNum:function(){
		 	 var self = this;
			 $.ajax({
				 url:ctx+"/api/shop/cart/get-cart-data.do",
				 dataType:'json',
				 cache:false,
				 success:function(result){
					 if(result.result==1){
						 $(".index-go-cart .num").text(result.data.count);
						 if(result.data.count<=3){
							 $(".jscroll-e").hide();
							 $(".my_cartlist_all").css("height","auto");
						 }
						 if(result.data.count=="0"){
							$(".cart_content02").empty();
							$(".cart_content02").html("<p class='no_cartlist' style='border:0px!important;'>您的购物车中还没有任何商品</p>");
						 }
					 }
				 }
			 });
		}
};

