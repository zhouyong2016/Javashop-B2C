(function($){
 
	
	$.fn.jbtn = function(options){	
	
		
	    return this.each(function() {    
		
			$(this).unbind("hover").hover(

				function ()
				{	
					var $this = $(this);   
					$this.addClass("hover");
				},
				function ()
				{	
					 
					var $this = $(this);   
					$this.removeClass("hover");		
	
				}
				
			);	
		});    
 		
	}; 
	
 

	$.Loading = $.Loading || {};
	$.Loading.show=function(text){
		$.blockUI({ 
	         css: { top: '10px'} , 
			 message:text,
			 showOverlay:false
	    }); 
	};

	$.Loading.hide=function(){
		$.unblockUI();
	};
	
	
	$.alert=function(text){
		$.dialog.alert(text);
	};
	
	$.confirm=function(text,yes,no){	 
		$.dialog.confirm(text,yes,no);
	};
}
)(jQuery);


//购物车导航条
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
			 var contentBox = this.barWrapper.find(".content");  //购物列表元素
			 var listBox = this.barWrapper.find(".cart_list");  //购物列表元素
			 this.loadNum();
			 this.barWrapper.hover(
				 function(){
					 contentBox.show();
					//显示loading 图标
					 listBox.load(ctx+"/cart/cart_bar.html",function(){
						 //加载完列表绑定删除事件
						 $(this).find(".delete").click(function(){
							 var itemid = $(this).attr("itemid");
							 self.deleteItem(itemid);
						 });
					 });
				 },function(){
					 contentBox.hide();
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
				url:"api/shop/cart!delete.do?ajax=yes",
				data:"cartid="+itemid,
				dataType:"json",
				success:function(result){
					$.Loading.hide();
					if(result.result==1){				
						self.loadNum();
						self.barWrapper.find(".item[itemid="+itemid+"]").remove();
						//如果是在购物车页面更新购物车相应信息
						if(typeof Cart != "undefined"){
							Cart.removeItem(itemid);
							Cart.refreshTotal();
						}
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
				 url: ctx+"/api/shop/cart!getCartData.do?ajax=yes",
				 dataType:'json',
				 cache:false,
				 success:function(result){
					 if(result.result==1){
						 self.numBox.text(result.data.count);
					 }
				 }
			 });
		}
};



$(function(){
	$(".btn").jbtn();	
	CartBar.init();
	$("form.validate").validate();
});


