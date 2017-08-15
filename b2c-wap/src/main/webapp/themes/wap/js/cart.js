var Cart={
		init:function(staticserver){
			var self=this;			
			this.bindEvent();
		},
		bindEvent:function(){
			var self=this;
			
			//购物数量调整
			$(".Numinput .increase,.Numinput .decrease").click(function(){
				var $this = $(this),
					number = $this.parents(".Numinput"),
					itemid =number.attr("itemid"),
					productid =number.attr("productid"),
					objipt = number.find("input"),
					num	= parseInt(objipt.val());
				if (!isNaN(num)){
					if($this.hasClass("increase")){
						num++;
					}
					if($this.hasClass("decrease")){
						 if(num == 1 ){
							 self.deleteGoodsItem(itemid);
							 return false;
						} 
						num--;
					}
					 num = (num <=1 || num > 100000) ? 1 : num;
					 self.updateNum(itemid, num, productid,objipt);
				}
			});
			
			//购物数量手工输入
            $(".Numinput input").keydown(function(e){
                var kCode = $.browser.msie ? event.keyCode : e.which;
                //判断键值  
                if (((kCode > 47) && (kCode < 58)) 
                    || ((kCode > 95) && (kCode < 106)) 
                    || (kCode == 8) || (kCode == 39) 
                    || (kCode == 37)) { 
                    return true;
                } else{ 
                    return false;  
                }
            }).focus(function() {
                this.style.imeMode='disabled';// 禁用输入法,禁止输入中文字符
            }).keyup(function(){
                var pBuy   = $(this).parent();//获取父节点
                var itemid  = pBuy.attr("itemid");
                var productid  = pBuy.attr("productid");
                var numObj = pBuy.find("input[name='num']");//获取当前商品数量
                var num    = parseInt(numObj.val());
                if (!isNaN(num)) {
                    var numObj = $(this);
                    var num    = parseInt(numObj.val());
                    num = (num <=1 || num > 100000) ? 1 : num;
                    self.updateNum(itemid, num, productid,numObj);
                }
            });
			
			//去结算
			$(".go-checkout").click(function(){
				
					if ($('.cart-list-box input[name="product_id"]:checked').length == 0) {
						alert('请至少选择一件商品');
						return;
					}
					location.href="./checkout/checkout.html";
				
			});
			
			
			//选择货品
			$("input[name='product_id']").click(function(){
				if($(this).is(':checked') == true){
					$(this).parent().addClass("selected");
				}else{
					$(this).parent().removeClass("selected");
				}
				
				self.checkProduct(this);
			});
			//全选货品
			$("input[name='select_all']").click(function(){
				self.checkAllProduct(this);
			});
			
		},
		
		
		
		//删除一个购物项
		deleteGoodsItem:function(itemid){
			var self=this;
			if(confirm("确定要移除该商品吗？")){
				$.ajax({
					url:ctx+"/api/shop/cart/delete.do",
					data:{"cartid":itemid},
					dataType:"json",
					success:function(result){
						if(result.result==1){
							self.refreshTotal();
							self.removeItem(itemid);
						}else{
							$.alert(result.message);
						}
						//	$.Loading.hide();
					},
					error:function(){
						$.Loading.hide();
						$.alert("出错了:(");
					}
				});
			}
		},
		//移除商品项
		removeItem:function(itemid){
			$(".cart-list-box li[itemid="+itemid+"]").remove();
			var cart_tiem_num = $(".cart-list-box ul li").length;  //判断购物车购物项数量
			if(cart_tiem_num == "0"){
				$(".cart-list-box").empty();
				$(".cart-list-box").load("cart/no_cart.html");
			}
		},
		
		//清空购物车
		clean:function(){
			$.Loading.show("请稍候...");
			var self=this;
			$.ajax({
				url:ctx+"/api/shop/cart/clean.do",
				dataType:"json",
				success:function(result){
					$.Loading.hide();
					if(result.result==1){
						location.href='cart.html';
					}else{
						$.alert("清空失败:"+result.message);
					}				 
				},
				error:function(){
					$.Loading.hide();
					$.alert("出错了:(");
				}
			});		
		},
		
		//更新数量
		updateNum:function(itemid,num,productid,num_input){
			var self = this;
			$.ajax({
				url:ctx+"/api/shop/cart/update-num.do",
				data:"cartid="+itemid +"&num="+num +"&productid="+productid,
				dataType:"json",
				success:function(result){
					if(result.result==1){
						if(result.data.store>=num){
							self.refreshTotal();
							var price = parseFloat($("li[itemid="+itemid+"]").attr("price"));
							//price =price* num;
							price =self.changeTwoDecimal_f(price* num);
							$("tr[itemid="+itemid+"] .itemTotal").html("￥"+price);
							num_input.val(num);
						}else{
							num_input.val(result.data.store);
							alert("抱歉！您所选择的货品库存不足。");
						}
					}else{
						alert("更新失败");
					}
				},
				error:function(){
					alert("出错了:(");
				}
			});		
		},
		
		//刷新价格
		refreshTotal:function(){
			var self = this;
			$.ajax({
				url:"cart/cartTotal.html",
				dataType:"html",
				success:function(html){
					 $(".cart-float-price").html(html);
				},
				error:function(){
					alert("糟糕，出错了:(");
				}
			});
		},
		
		changeTwoDecimal_f:function(x) {
	        var f_x = parseFloat(x);
	        if (isNaN(f_x)) {
	            alert('参数为非数字，无法转换！');
	            return false;
	        }
	        var f_x = Math.round(x * 100) / 100;
	        var s_x = f_x.toString();
	        var pos_decimal = s_x.indexOf('.');
	        if (pos_decimal < 0) {
	            pos_decimal = s_x.length;
	            s_x += '.';
	        }
	        while (s_x.length <= pos_decimal + 2) {
	            s_x += '0';
	        }
	        return s_x;
	    },
		
		
	    checkProduct:function(product){
	    	var self = this;
	    	
	    	
	    	$.ajax({
				url:ctx+"/api/shop/cart/check-product.do",
				data:{"checked":product.checked,"product_id":$(product).val()},
				dataType:"json",
				success:function(result){
					self.refreshTotal();
				},
				error:function(){
					alert("出错了:(");
				}
			});		
	    },
	    checkAllProduct:function(product){
	    	var self = this;
	    	if(product.checked){
	    		$("input[name='product_id']").prop("checked", true);
	    		$(".noselect_cartlist").addClass("selected");
	    	}else{
	    		$("input[name='product_id']").prop("checked", false);
	    		$(".noselect_cartlist").removeClass("selected");
	    	}
	    	$.ajax({
				url:ctx+"/api/shop/cart/check-all.do",
				data:{"checked":product.checked},
				dataType:"json",
				success:function(result){
					self.refreshTotal();
				},
				error:function(){
					alert("出错了:(");
				}
			});		
	    }
		
};

$(function(){
	Cart.init();
});