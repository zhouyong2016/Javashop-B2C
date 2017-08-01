/**
 * 商品操作js
 */
var Goods={
	init:function(scroll){
		var self = this;
		$(".Numinput").numinput({name:"num"});	
		Favorite.init();
		
//		$(".addtocart-btn").click(function(){	
//			var $this= $(this); 
//			self.addToCart($this);
//			
//		});
//		 $(".buynow-btn").click(function(){
//			var $this= $(this); 
//			self.addToCart($this);
//			return false;
//		 });
		 $(".addGroupbuyGoods").click(function(){
				var $this=$(this);
				self.addGroupBuy($this);
		 });
		//相册缩略图滑动
		if(scroll){
			$("#detail-wrapper .gallery .thumblist").jCarouselLite({
		        btnNext: ".left-control",
		        btnPrev: ".right-control",
				visible:5
		    });
		}
		
		//相册放大切换
		$("#detail-wrapper .gallery .thumblist li").click(function(){
			var $this = $(this);
			var img  = $this.children("img");
			var zoom = document.getElementById('zoom'); //get the reference to our zoom object
		    MagicZoom.update(zoom, img.attr("big"), img.attr("small"), 'show-title: false'); 
			$this.addClass("selected").siblings().removeClass("selected");
		});	 

		
	},
	addToCart:function(btn){
		//添加积分适配
		var checkState = $("#is_exchange").attr("checked");
		if(checkState){
			//积分兑换选中,判断登录状态,弹出登录框或者跳转登录页面
			if(!isLogin){
				var html = $("#login_tip").html();
				$.dialog({ title:'提示信息',content:html,lock:true,init:function(){
					
					$(".button-wrapper .btn").jbtn();
					$(".button-wrapper .to-login-btn").click(function(){
						 location.href="login.html?forward=goods-"+goodsid+".html";
					});

					//
					$(".button-wrapper .to-checkout-btn").click(function(){
						location.href="register.html?forward=goods-"+goodsid+".html";
					});
					
//					// 跳转到结算页  取消 直接购买  
//					$(".button-wrapper .to-register-btn").click(function(){
//						location.href="register.html?forward=checkout.html";
//					});
				}});	
				return;
			}else{
				//登录了就检查积分
				if(!checkPoint){
					var html = $("#point_tip").html();
					$.dialog({ title:'提示信息',content:html,lock:true,init:function(){}});
					return;
				}
			}
		}		
				
		
		var self = this;
		var id = btn.attr("id");
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var action = $("#goodsform [name='action']").val();
		var options={
			url:"api/shop/cart/" + action + ".do?showCartData=0",
			dataType:"json",
			cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
			success:function(result){
				$.Loading.hide();
				if(result.result==1){
					if(id!="buyNow"){
						self.showAddSuccess();
					}else{
						window.location.href="cart.html";
					}
				}else{
					$.dialog.alert("发生错误:"+result.message); 
				}
				btn.attr("disabled",false);
			},
			error:function(){
				$.Loading.hide();
				$.alert("抱歉,发生错误");
				btn.attr("disabled",false);
			}
		};
		$("#goodsform").ajaxSubmit(options);		
	},
	
	showAddSuccess:function(){
		var html = $(".add-success-msg").html();
		$.dialog({ title:'提示信息',content:html,lock:true,init:function(){
			var self = this;
			$(".ui_content .btn").jbtn();
			$(".ui_content .returnbuy_btn").click(function(){
				self.close();     //关闭自己
				$.ajax({
					url:"api/shop/cart/get-cart-data.do",   //获取购物车数据api
					dataType:"json",
					cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
					success:function(result){
						if(result.result==1){				
								$(".num").text(result.data.count);   //将得到的结果放入到头部的购物车数量中。
						}else{
							$.alert(result.message);
						}	
					},
					error:function(){
						$.Loading.hide();
						$.alert("出错了:(");
					}
				});
			});

			$(".ui_content .checkout_btn").click(function(){
				location.href="cart.html";
			});
			
		}});
	},addGroupBuy:function(btn){
		var self = this;
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var options={
			url:Cluster.goodsApi+"/api/groupbuy/cart/add-goods.do",
			dataType:"json",
			async:false,
			cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
			success:function(result){
				$.Loading.hide();
				if(result.result==1){
					self.showAddSuccess();
				}else{
					$.dialog({title:'提示',content: "发生错误:"+result.message,lock: true});
				}
				btn.attr("disabled",false);
			},
			error:function(){
				$.Loading.hide();
				$.dialog({title:'提示',content: "抱歉,发生错误",lock: true});
				btn.attr("disabled",false);
			}
		};
		$("#goodsform").ajaxSubmit(options);		
	}

};
