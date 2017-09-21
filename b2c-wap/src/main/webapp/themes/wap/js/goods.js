var goodsjs = {
//		//增加减少数量
//		goods_stock:function(){
//			var store = parseInt($(".goods_add").attr("rel"));
//			$(".goods_cut").click(function(){
//				var goodsnum = parseInt($(".goodsnum").val());
//				var r = /^[0-9]*[1-9][0-9]*$/;
//				if(r.test(goodsnum) == false){
//					alert("商品数量必须为正整数");
//					return false;
//				}
//				if(goodsnum>1){
//					$(".goodsnum").val(goodsnum-1);
//				}
//				else{
//					alert("抱歉，最少需要一件商品");
//				}
//			});
//			
//			$(".goods_add").click(function(){
//				var goodsnum = parseInt($(".goodsnum").val());
//				var r = /^[0-9]*[1-9][0-9]*$/;
//				if(r.test(goodsnum) == false){
//					alert("商品数量必须为正整数");
//					return false;
//				}
//				
//				if(goodsnum<store){
//					$(".goodsnum").val(goodsnum+1);
//				}
//				else{
//					alert("抱歉，购买商品不能大于库存");
//				}
//			});
//			$(".goodsnum").blur(function(){
//				var goodsnums = parseInt($(this).val());
//				var r = /^[0-9]*[1-9][0-9]*$/;
//				if(r.test(goodsnums) == false){
//					alert("商品数量必须为正整数");
//					return false;
//				}
//				if(goodsnums>store){
//					alert("请输入小于库存的数量");
//				}
//				if(goodsnums<1){
//					alert("最少买一件哦");
//					$(this).val(parseInt(1));
//				}
//			});
//			
//		},
		
		//立即购买
		buy:function(){
			$(".buy_now").click(function(){
		//		var gnum = $(".goodsnum").val();
				var gid = $(this).attr("rel");  //商品ID
				var productid = $("#productid").val();    //货品ID
				var action = $(".cart_way [name='action']").val();   
				var spec = $(".cart_way [name='havespec']").val();  //规格
		//		var inventory = parseInt($(this).attr("inventory")); //库存
				if(spec==1){
					var specLi = parseInt($(".spec_item ul").length);
					var specLiSelected = parseInt($(".same-spec-li .selected").length);
					if(specLi != specLiSelected){
						alert("请选择规格");
						show.close_cover();       //增加遮罩。来阻止在提交ajax时进行其他操作。
						return false;
					}
					
					
					$.ajax({
						url:ctx+"/api/shop/cart/add-product.do",
						data:{"goodsid":gid,"productid":productid,"num":1,"showCartData":0,"spec":1},
						dataType:"json",
						success : function(data) {	
							if(data.result==1){																
								location.href="cart.html";
							}
							else{							
								alert(data.message);
							}
						}
					});
					
				}else{
					$.ajax({
						url:ctx+"/api/shop/cart/add-goods.do",
						data:{"goodsid":gid,"productid":productid,"num":1,"showCartData":0,"spec":1},
						dataType:"json",
						success : function(data) {	
							if(data.result==1){																
								location.href="cart.html";
							}
							else{							
								alert(data.message);
							}
						}
					});
				}
				
			});
		},
		//加入购物车
		incart:function(){
			$(".in_cart").click(function(){
		//		var gnum = $(".goodsnum").val();
				var gid = $(this).attr("rel");
				var productid = $("#productid").val();
				var action = $(".cart_way [name='action']").val();
				var spec = $(".cart_way [name='havespec']").val();
				if(spec==1){
					
					var specLi = parseInt($(".spec_item ul").length);
					var specLiSelected = parseInt($(".same-spec-li .selected").length);
					if(specLi != specLiSelected){
						alert("请选择规格");
						show.close_cover();       //增加遮罩。来阻止在提交ajax时进行其他操作。
						return false;
					}
					$.ajax({
						url:ctx+"/api/shop/cart/add-goods.do",
						data:{"goodsid":gid,"productid":productid,"num":1,"showCartData":0,"spec":1},
						dataType:"json",
						success : function(data) {	
							if(data.result==1){	
								alert("加入购物车成功");
								$.ajax({
									url:ctx+"/api/shop/cart/get-cart-data.do",
									data:{"goodsid":gid,"productid":productid,"num":1,"showCartData":0,"spec":1},
									dataType:"json",
									success:function(result){
										if(result.result==1){
											var goodscartnum = result.data.count;
											$(".my_cart span").empty();
											$(".my_cart span").text(goodscartnum);
										}else{
											alert(result.message);
										}
									},error:function(){
										alert("抱歉，收藏出现意外错误");
									}
								});
							}
							else{							
								alert(data.message);
							}
						}
					});
				}else{
					$.ajax({
						url:ctx+"/api/shop/cart/add-goods.do",
						data:{"goodsid":gid,"productid":productid,"num":1,"showCartData":0,"spec":1},
						dataType:"json",
						success : function(data) {	
							if(data.result==1){	
								alert("加入购物车成功");
								$.ajax({
									url:ctx+"/api/shop/cart/get-cart-data.do",
									data:{"goodsid":gid,"productid":productid,"num":1,"showCartData":0,"spec":1},
									dataType:"json",
									success:function(result){
										if(result.result==1){
											var goodscartnum = result.data.count;
											$(".my_cart span").empty();
											$(".my_cart span").text(goodscartnum);
										}else{
											alert(result.message);
										}
									},error:function(){
										alert("抱歉，收藏出现意外错误");
									}
								});
							}
							else{							
								alert(data.message);
							}
						}
					});
				}
				
			});
		},
//		//更新购物车数量
//		firstnum:function(){
//			$.ajax({
//				url:"api/shop/cart!getCartData.do",
//				dataType:"json",
//				success:function(result){
//					if(result.result==1){
//						var goodscartnum = result.data.count;
//						$(".my_cart span").empty();
//						$(".my_cart span").text(goodscartnum);
//					}else{
//						alert(result.message);
//					}
//				},error:function(){
//					alert("抱歉，收藏出现意外错误");
//				}
//			});
//		},
		//加入收藏
		goodscollect:function(){
			$(".goods_collect").click(function(){
				var collect = $(this).attr("rel");
				$.ajax({
					url:ctx+"/api/shop/collect/add-collect.do?goods_id="+collect,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							alert("收藏成功");
							location.reload();
						}else{
							alert(result.message);
						}
					},error:function(){
						alert("抱歉，收藏出现意外错误");
					}
				});
				
			});
		},
		//图片滚动
		goodsimg:function(){
			var goodsnum= $(".goods_images li").length;    //获取大幅图片中li的总个数。
			for(i=1;i<=goodsnum;i++){    //for循环，定i=1,每次循环就加1; 当i的值由1循环到等于result的值一样时（即“文本框的name='text'的值”）就停止循环
				var createobj = $("<a></a>"); //把div定义为变量createobj
				$(".goods_circle").append(createobj); //把createobj这个变量追加到html中的'body'里
				};
				var active=0,  
				as=$(".goods_circle a");
			//	as=document.getElementById('goods_circle').getElementsByTagName('a');	
				for(var i=0;i<as.length;i++){
					(function(){
						var j=i;
						as[i].onclick=function(){
							t2.slide(j);
							return false;
						}
					})();
				};
				var t2=new TouchSlider({id:'slider', speed:600, timeout:4000, before:function(index){
						as[active].className='';
						active=index;
						as[active].className='active';
					}});
			  /*循环滚动结束*/
		},
};