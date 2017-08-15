var Address=["shipAddr","shipZip","shipName","shipTel","shipMobile"];

var Bonus={
		
	init:function(){
		var self = this;
		
	
		 var self  = this;
		 this.bindUseEvent();
		 //关闭按钮事件
		 $(".cartPop .c").click(function(){
				$(".cartPoint").empty();
				Bonus.refreshTotal();
		 		
		});
		 //添加一个新输入框
		 $(".bonusADD").click(function(){
			 
			 if($(".bonusSPAN>div").size()>3){
				 alert("最多使用3个红包");
				 return false;
			 }
			 
			 var newInput = $(".bonuscopy").html();   //复制html片段模板
//			 newInput.removeAttr("id").find("input").removeAttr("disabled").val("");
//			 newInput.find(".userY").show();
			 $(".bonusSPAN").append("<div class=\"bonusC01\">"+newInput+"</div>");
			 
			 self.bindUseEvent();
		 });
		 
		 $("#electronic").change(function(){
			 	var regionid = $("#region_id").attr("rel");
				var typeid = $('input:radio[name="typeId"]:checked').val();
			 
				var bonusid = $(this).val();
					if(regionid==null){
						regionid=0;
					}
					if(typeid==null){
						typeid=0;
					}
				self.changeBonus(bonusid);
			 })
	 }
	,
	bindUseEvent:function(){
		var self = this;
		$(".bonusSPAN .userY").unbind("click").bind("click",function(){
			var ipt = $(this).prev("input");
			var sn  = ipt.val();
			if(sn==""){
				alert( "请输入优惠券号码" );
				return ;
			}
			var count =0; 
			$(".bonusSPAN input").each(function(i,v){
				if(v.value== sn){
					count++;
				}
			});
			
			if(count>1 ){
				alert("输入的号码重复");
				ipt.select();
				return ;
			}
			//获取当前订单需要支付的总金额 add by DMRain 2016-4-28
			var need = $("input[name='needPay']").val();
			self.useBonus(sn,ipt,need);
		});
		
		$(".bonusSPAN .userQ").unbind("click").bind("click",function(){
			var ipt =  $(this).siblings("input");
			var sn  = ipt.val();
			var box = $(this).parent();
			self.cancelBonus(sn,box);
		});
	},
	//实体券
	useBonus:function(sn,ipt,need){
		
		var regionid = $(".order_region").attr("rel");
		var typeid = $('input:radio[name="typeId"]:checked').val();
		if(regionid==null){
			regionid=1;
		}
		if(typeid==null){
			typeid=0;
		}
//		var need = $("input[name='needPay']").val();
		$.ajax({
			url:"api/shop/bonus/use-sn.do?sn="+sn+"&regionid="+regionid+"&typeid="+typeid+"&needPay="+need,
			dataType:"json",
			success:function(res){
				if(res.result==1){
					 ipt.attr("disabled",true);
					 ipt.next().hide();
					 Bonus.refreshTotal();
					 
				}else{
					alert(res.message);
				}
			},
			error:function(){
				alert("糟糕，使用优惠券发生意外错误");
			}
		});
	},
	cancelBonus:function(sn,box){
		if(sn==""){
			if($(".bonusSPAN>div").size()>1){
				box.remove();
			}else{
				box.find("input").removeAttr("disabled").val("");
				box.find(".userY").show();
			}
			return false;
		}
			
		$.ajax({
			url:ctx+"/api/shop/bonus/cancel-sn.do?sn="+sn,
			dataType:"json",
			success:function(res){
				if(res.result==1){
					if($(".bonusSPAN>div").size()>1){
						box.remove();
					}else{
						box.find("input").removeAttr("disabled").val("");
						box.find(".userY").show();
					}
					Bonus.refreshTotal();
				}else{
					alert(res.message);
				}
			},
			error:function(){
				alert("糟糕，使用优惠券发生意外错误");
			}
		});
		
	},
		
	//加载结算金额
	
	refreshTotal:function(){
		var regionid = $(".order_region").attr("rel");
	 		var dlytype = $("[name=typeId]:checked");
	 		if( dlytype.size()== 0 ){
	 			$.alert("请选择配送方式");
	 			return ;
	 		}
	 		var typeId = dlytype.val();
	 		$(".cleckout-float-price").load("checkout_total.html?regionId="+regionid+"&typeId="+typeId);
	},
	
	
	
	
	
	
	
	//加载电子券
	changeBonus:function (bonusid){
		var regionid = $(".order_region").attr("rel");
		var typeid = $('input:radio[name="typeId"]:checked').val();
		if(regionid==null){
			regionid=0;
		}
		if(typeid==null){
			typeid=0;
		}
		$.ajax({
			url:ctx+"/api/shop/bonus/use-one.do?bonusid="+bonusid+"&regionid="+regionid+"&typeid="+typeid,
			dataType:"json",
			success:function(res){
				if(res.result==1){
					var regionid = $(".order_region").attr("rel");
			 		var dlytype = $("[name=typeId]:checked");
			 		if( dlytype.size()== 0 ){
			 			$.alert("请选择配送方式");
			 			return ;
			 		}
			 		var typeId = dlytype.val();
			 		$(".cleckout-float-price").load("checkout_total.html?regionId="+regionid+"&typeId="+typeId);
				}else{
					alert(res.message);
				}
			},
			error:function(){
				alert("糟糕，使用优惠券发生意外错误");
			}
		});
	}


};
$(function(){
	
	Bonus.init();
});