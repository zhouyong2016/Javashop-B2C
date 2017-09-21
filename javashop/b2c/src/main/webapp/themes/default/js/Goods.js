/**
 * 商品操作js
 */
var Goods={
	init:function(scroll){
		var self = this;
		$(".Numinput").numinput({name:"num"});	
		Favorite.init();
		
		$(".addtocart_btn").click(function(){	
			var $this= $(this); 
			self.addToCart($this);
			
		});
		 $(".buynow_btn").click(function(){
			var $this= $(this); 
			self.addToCart($this);
			return false;
		 });
		//相册缩略图滑动
		if(scroll){
			$("#detail_wrapper .gallery .thumblist").jCarouselLite({
		        btnNext: ".left-control",
		        btnPrev: ".right-control",
				visible:4
		    });
		}
		
		//相册放大切换
		$("#detail_wrapper .gallery .thumblist li").click(function(){
			var $this = $(this);
			var img  = $this.children("img");
			var zoom = document.getElementById('zoom'); //get the reference to our zoom object
		    MagicZoom.update(zoom, img.attr("big"), img.attr("small"), 'show-title: false'); 
			$this.addClass("selected").siblings().removeClass("selected");
		});	 

		
	},
	addToCart:function(btn){
		var self = this;
		var id = btn.attr("id");
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var action = $("#goodsform [name='action']").val();
		var options={
			url:"api/shop/cart!" + action + ".do?ajax=yes",
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
		var html = $(".add_success_msg").html();
		$.dialog({ title:'提示信息',content:html,lock:true,init:function(){
			var self = this;
			$(".ui_content .btn").jbtn();
			$(".ui_content .returnbuy_btn").click(function(){
				self.close();     //关闭自己
				$.ajax({
					url:ctx+"/api/shop/cart!getCartData.do",   //获取购物车数据api
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
	}

};
