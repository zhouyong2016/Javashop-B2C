var Products=[];
var Spec={
	init:function(haveSpec){
		var self = this;
		
		$("#buyNow").click(function(){	
			var $this= $(this); 
			self.addToCart($this);
			
		});
		$("#addCart").click(function(){	
			var $this= $(this); 
			self.addToCart($this);
		});
		$(".addGroupbuyGoods").click(function(){
			var $this=$(this);
			self.addGroupBuy($this);
		});
		
		this.loadProduct(goodsid);
		
	},
	loadProduct:function(goodsid){
		var self= this;
		$.ajax({
			url:ctx+"/api/shop/goods!productList.do?goodsid="+goodsid,
			dataType:"json",
			success:function(json){
				if(json.result==1){
					Products = json.data;
					 
					self.refresh();
					$("a[name='goods_spec'][specvid!='']").click(function(){
						var link = $(this);
						if(link.attr("class")!='hovered' && link.attr("class")!='disabled' ){
							self.specClick($(this));
						}
						return false;
					});
					$("#goodsform [name='action']").val("addProduct");
					
				}else{
					alert(json.message);
				}
			},
			error:function(){
				alert("意外错误")
			}
		});
	},
	specClick:function(specLink){
		specLink.parents("ul").find("a[specvid!='']").removeClass("selected");
		specLink.parent().parent().parent().parent().find("em").addClass("checked");
		specLink.addClass("selected");
 		
		this.refresh(specLink);
	},
	findGoodsImg:function(vid){
		for(i in  spec_imgs){
			var specimg = spec_imgs[i];
			if(specimg.specvid==parseInt(vid)){
				return specimg.goods_img;
			}
		}
	},
	//根据当前选择的规格找到货品
	findProduct:function(vidAr){
		var pros =[];
		//判断两个数组元素值是否相同，不判断位置情况
		function arraySame(ar1,ar2){
			//if(ar1.length!=ar2.length) return false;
			
			for(i in ar1){
				if($.inArray(ar1[i],ar2)==-1){ //不存在
					return false;
				}
			}
			return true;
		}
		
		var self = this;
	 
		for(i in Products){
			var product= Products[i];
			if(arraySame(vidAr,product.specs)){
				pros[pros.length] =product; 
			}
		}	
		 
		return pros;
	}
	,
	refresh:function(specLink){
		var self = this;
		var product_ar=[];
 
		$("#goods-spec .spec-item a.selected").each(function(){
			var link = $(this);
			product_ar[product_ar.length]=parseInt(link.attr("specvid"));
		});
				
		var pro =this.findProduct(product_ar);
		
		for(i in Refresh){
			Refresh[i].refresh(pro,specLink,product_ar);
		}
		if(pro.length==1){
			$("strong[nctype='goods_stock']").html(pro[0].enable_store);
			$("#productid").val(pro[0].product_id);
			$(".gnotifybtn").attr("productid",pro[0].product_id);
		}
	 
	}

};
var StateRefresh={
	ArrrRemove:function( ar,obj) {  
		var new_ar =[];
		for( var i in ar ){
			if(obj!= ar[i]){
				new_ar.push(ar[i]);
			}
		}
		
		return new_ar;
	},


	refresh:function(pro,specLink,product_ar){
		//pro:找到的product [{sprc:{}},{}]
		//product_ar:选中的规格[1,2]
		
		var self  = this;
		if(product_ar.length>0){
		//从目前未选中的规格中循环
			$(".spec-item").not( specLink.parents(".spec-item") ).find("a").each(function(){
				var link = $(this);
				var proar=product_ar;
				link.parents(".spec-item").find("a").not(this).each(function(){
					var specvid = parseInt($(this).attr("specvid"));
					proar= self.ArrrRemove(proar,specvid);
				});
				
				var specvid = parseInt(link.attr("specvid"));
				proar.push(specvid);
				
				var result =Eop.Spec.findProduct(proar);
				if(!result || result.length==0){
					link.addClass("disabled");
				}else{
					link.removeClass("disabled");
				}
				proar.pop();
				
			});
		}
	},
	addToCart:function(btn){
		var self = this;
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var id = btn.attr("id");
		var action = $("#goodsform [name='action']").val();
		var options={
			url:ctx+"/api/shop/cart!" + action + ".do?ajax=yes&store_id="+$("#storeid").val(),
			dataType:"json",
			async:false,
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
	},
	showCartCount:function(){
		$.ajax({
			url:ctx+"/api/shop/cart!getCartData.do",   //获取购物车数据api
			dataType:"json",
			cache: false,             //清楚缓存，暂时测试，如果产生冲突，请优先考虑是否是这条语句。
			success:function(result){
				if(result.result==1){				
						$(".addcart_num").text(result.data.count);   //将得到的结果放入到头部的购物车数量中。
					 
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
	
	showAddSuccess:function(){
		var myself = this;
		var html = $(".add_success_msg").html();
		$.dialog({ title:'提示信息',content:html,lock:true,init:function(){
			var self = this;
			$(".ui_content .btn").jbtn();
			$(".ui_content .returnbuy_btn").click(function(){
				self.close();     //关闭自己
				myself.showCartCount();
			});
	
			$(".ui_content .checkout_btn").click(function(){
				location.href=ctx+"/cart.html";

			});
			
			$(".ui_close").click(function(){
				myself.showCartCount();
			})
			
		}});
	},addGroupBuy:function(btn){
		var self = this;
		$.Loading.show("请稍候...");
		btn.attr("disabled",true);
		var options={
			url:ctx+"/api/shop/cart!addGoods.do?ajax=yes&store_id="+$("#storeid").val(),
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
}
var SelectTipRefresh={
	refresh:function(pro){
			var i=0;
			var specHtml="";
			$("#goods-spec .spec-item a.selected").each(function(){
				if(i==0) specHtml="";
				if(i!=0) specHtml+="、";
				specHtml +="<font color='red'>"+$(this).attr("title")+"</font></span>";
				i++;
			});	
			
			if(i>0){
				specHtml="<em>您已选择：</em><span>"+specHtml;
			}else{
				specHtml="<em>请选择：</em><span>下列规格</span>";
			}
			
			$(".spec-tip").html(specHtml);
		 
	}
};
var PriceRefresh={
	refresh:function(pro){
		if(pro.length==1){
			$(".summary .price b").text("￥"+pro[0].price );
			$(".summary .sn label").text(pro[0].sn );
			$("#productid").val(pro[0].product_id);
		}
		else{
			var maxPrice=0,minPrice=-1;
			for(i in pro){
				if( maxPrice<pro[i].price){
					maxPrice = pro[i].price;
				}
				if(minPrice==-1|| minPrice>pro[i].price){
					minPrice = pro[i].price;
				}
			}	
			$(".summary .price b").text("￥"+minPrice+"-￥" +maxPrice);
			
		}
	}
};
var VipPriceRefresh={
	refresh:function(pro){
		if(pro.length==1){
			$.ajax({
				url:"api/shop/vipprice!showVipPrice.do?productid="+pro[0].product_id+"&ajax=yes",
				dataType:"json",
				success:function(result){
					if(result.result==1){
						$(".summary .vip_price strong").text("￥"+result.data.vipprice );
						$(".weight span").text("重量："+result.data.weight +"g");
					}else{
						$.alert("获取vip价格出错["+message+"]");
					}
				},
				error:function(){
					$.alert("获取vip价格意外出错");
				}
				
			});
		}	
	}
};
function canBuy(){
	$(".buynow_btn").unbind('click');
	$(".buynow_btn").bind('click',function(){
		Goods.addToCart($(this));
	});
	
	$(".addtocart_btn").unbind('click');
	$(".addtocart_btn").bind('click',function(){
		Goods.addToCart($(this));
		return false;
	});
	
	
	$(".buynow_btn").css("cursor","pointer");
	$(".buynow_btn").tip({'disable':true});
	
	$(".addtocart_btn").css("cursor","pointer");
	$(".addtocart_btn").tip({'disable':true});
	
	
	
	$(".gnotifybtn").unbind('click');
	$(".gnotifybtn").bind('click',function(){
		var currgoodsid = $(this).attr("goodsid");
		var currproductid = $(this).attr("productid");
		Favorite.gnotify(currgoodsid,currproductid);	
		return false;
	});
	
	$(".gnotifybtn").css("cursor","pointer");
	$(".gnotifybtn").tip({'disable':true});
}


var BtnTipRefresh = {
	refresh:function(pro){
		$(".buynow_btn").attr('tip','');
		$(".gnotifybtn").attr('tip','');
		$(".addtocart_btn").attr('tip','');
		if(pro.length==1){
			if(pro[0].store==0){
				
				canBuy();
				$(".gnotifybtn").show();
				$(".buynow_btn").hide();
				$(".addtocart_btn").hide();
				
			}else{
				canBuy();
				$(".gnotifybtn").hide();
				$(".buynow_btn").show();
				$(".addtocart_btn").show();
			}
		}else{
			$(".buynow_btn").unbind('click');
			$(".buynow_btn").bind('click',function(){return false;});
			$(".buynow_btn").css("cursor","not-allowed");
			
			$(".addtocart_btn").unbind('click');
			$(".addtocart_btn").bind('click',function(){return false;});
			$(".addtocart_btn").css("cursor","not-allowed");
			
			
			var i=0;
			var tip='';
			$("#goods-spec .spec-item em").each(function(){
				var em = $(this);
				
				if(em.hasClass("checked")){
					if(i!=0)tip+="、";
					tip+=em.text();
					i++;
				}
				
			});
			 
			$(".buynow_btn,.addtocart_btn").tip({'disable':false,className:"cantbuy",text:"请选择:"+tip});
		}
	}	
};
var Refresh=[SelectTipRefresh,PriceRefresh,BtnTipRefresh,StateRefresh];		//暂时去掉刷新会员价VipPriceRefresh，因与页面有冲突

//tip插件
(function($) {
	$.fn.tip = function(options) {
		 
		var opts = $.extend({}, $.fn.tip.defaults, options);
		var tipEl= $(".tipbox");
	
		if(tipEl.size()==0){
			var html="<div class='tipbox' style='position: absolute;'>";
			html+='<div class="tip-top"></div>';
			html+='<div class="tip">';
			html+='<div class="tip-text"></div>';
			html+='</div>';
			html+='<div class="tip-bottom"></div>';
			html+='</div>';
			tipEl=$(html).appendTo($("body"));
			tipEl.addClass(opts.className);
			tipEl.hide();
		}
		 
		 tipEl.find(".tip>.tip-text").html(opts.text);
		 if( opts.disable){
			 $(this).unbind("mouseover").unbind("mousemove").unbind("mouseout");
		 }else{

			 $(this).bind("mouseover",function(e){
				 tipEl.show(); 
			 }).bind("mousemove",function(e){
				 tipEl.css('top',e.pageY+15).css('left',e.pageX+15);
			 }).bind("mouseout",function(){
				tipEl.hide();
			 });
		 }
	};
	
 
	
    $.fn.tip.defaults = {
    	className:"tip",
        text:"", 
        disable:false
    };
    
})(jQuery);





