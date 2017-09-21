var Eop = Eop||{};
Eop.Spec={
	init:function(haveSpec){
		if(haveSpec==1){
			var self = this;
			this.refresh();
			$("#goods-spec .spec-item a[specvid!='']").click(function(){
				var link = $(this);
				if(link.attr("class")!='selected' && link.attr("class")!='disabled' ){
					self.specClick($(this));
				}
				return false;
			});
		}else{
	//			canBuy();
	//			var pro =this.findProduct(product_ar);
			BtnTipRefresh.refresh(Products);
		}
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
		if(pro){
			  
		}
		else{
			alert("缺货");
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
			$(".spec-item").not($(".spec-item") ).find("a").each(function(){
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
			$(".kaben-price span").text("￥"+pro[0].price );
			$("#productid").val(pro[0].product_id);
			$(".enable_store").html(pro[0].specs);
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
			$(".kaben-price span").text("￥"+minPrice+"-￥" +maxPrice);
			
		}
	}
};
var VipPriceRefresh={
	refresh:function(pro){
		if(pro.length==1){
			$.ajax({
				url:ctx + "/api/shop/vipprice/show-vip-price.do?productid="+pro[0].product_id+"&ajax=yes",
				type : "POST",
				dataType:"json",
				success:function(result){
					if(result.result==1){
						//$(".summary .vip_price strong span").removeClass("no-choose");
						$(".summary .vip_price strong").html("<span>￥"+result.data.vipprice+"</span>元");
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
	$(".buynow-btn").unbind('click');
	$(".buynow-btn").bind('click',function(){
		Goods.addToCart($(this));
	});
	
	$(".btn-box .addtocart-btn").unbind('click');
	$(".btn-box .addtocart-btn").bind('click',function(){
		Goods.addToCart($(this));
		return false;
	});
	
	$(".content-main-tit .addtocart-btn").unbind('click');
	$(".content-main-tit .addtocart-btn").bind('click',function(){
		Goods.addToCart($(this));
		return false;
	});
	
	$(".buynow-btn").css("cursor","pointer");
	$(".buynow-btn").tip({'disable':true});
	
	$(".btn-box .addtocart-btn").css("cursor","pointer");
	$(".btn-box .addtocart-btn").tip({'disable':true});
	
	$(".buynow-btn").attr("disabled",false);   
	$(".addtocart-btn").attr("disabled",false); 
	
	$(".gnotifybtn").unbind('click');
	$(".gnotifybtn").bind('click',function(){
		var currgoodsid = $(this).attr("goodsid");
		Favorite.gnotify(currgoodsid);
		return false;
	});
	
	$(".gnotifybtn").css("cursor","pointer");
	$(".gnotifybtn").tip({'disable':true});
}


var BtnTipRefresh = {
	refresh:function(pro){
		$(".buynow-btn").attr('tip','');
		$(".gnotifybtn").attr('tip','');
		$(".btn-box .addtocart-btn").attr('tip','');
		if(pro.length==1){
			if(pro[0].store==0){
				
				canBuy();
				$(".gnotifybtn").show();
				$(".buynow-btn").hide();
				$(".btn-box .addtocart-btn").hide();
				
			}else{
				canBuy();
				$(".gnotifybtn").hide();
				$(".buynow-btn").show();
				$(".btn-box .addtocart-btn").show();
			}
		}else{
			$(".buynow-btn").unbind('click');
			$(".buynow-btn").bind('click',function(){return false;});
			$(".buynow-btn").css("cursor","not-allowed");
			
			$(".btn-box .addtocart-btn").unbind('click');
			$(".btn-box .addtocart-btn").bind('click',function(){return false;});
			$(".btn-box .addtocart-btn").css("cursor","not-allowed");
			
			$(".buynow-btn").attr("disabled","disabled");
			$(".addtocart-btn").attr("disabled","disabled");	
			
			var i=0;
			var tip='';
			$("#goods-spec .spec-item em").each(function(){
				var em = $(this);
				
				if(em.attr("class")!='checked'){
					if(i!=0)tip+="、";
					tip+=em.text();
					i++;
				}
				
			});
			 
			$(".buynow-btn,.btn-box .addtocart-btn").tip({'disable':false,className:"cantbuy",text:"请选择:"+tip});
		}
	}	
};
var Refresh=[SelectTipRefresh,PriceRefresh,BtnTipRefresh,VipPriceRefresh,StateRefresh];

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





