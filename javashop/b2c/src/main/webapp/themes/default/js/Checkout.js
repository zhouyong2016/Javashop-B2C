var Address=["shipAddr","shipZip","shipName","shipTel","shipMobile"];

var CheckOut={
		
	init:function(){
		var self = this;
		
		/**
		 * ====================================================
		 * 声明收货地址、付款方式、配送方式的Wrapper
		 * ====================================================
		 */
		//收货地址
		this.addrSelectedWrap =  $("#checkout_wrapper .address .selected"); //收货地址-选中状态的Wrapper
		this.addrModifyWrap   =  $("#checkout_wrapper .address .modify");   //收货地址-修改状态的Wrapper
		
		//付款方式
		this.paySelectedWrap  =  $("#checkout_wrapper .payment .selected"); //付款方式-选中状态的Wrapper
		this.payModifyWrap    =  $("#checkout_wrapper .payment .modify");   //付款方式-修改状态的Wrapper

		
		this.dlytypeSelectedWrap  =  $("#checkout_wrapper .dlytype .selected"); //配送方式-选中状态的Wrapper
		this.dlytypeModifyWrap    =  $("#checkout_wrapper .dlytype .modify");   //配送方式-修改状态的Wrapper
		
		
		self.bindAddrEvent();
		self.bindPaymentEvent();
		self.bindDlytypeEvent();
		
		//地区联动选择-选择最后一级（区）时自动设置邮编
		RegionsSelect.regionChange=function(regionid,name,zipcode){
			self.addrModifyWrap.find(".input [name='shipZip']").val(zipcode);
		};

		/**
		 * 优惠劵显示的切换
		 */
		$(".pucker input[type=checkbox]").click(function(){
			var parent = $(this).parent();
			
			if(this.checked){
				if(parent.parent().hasClass("coupon")){
					$("#coupondiv").load("checkout/bonus.html",function(){
						self.whenOpen();
					});
				}
				parent.siblings(".content").show();
			}else{
				parent.siblings(".content").hide();
				self.changeBonus(0);
			}
			
		});
		
		
		/**
		 * 显示或隐藏发票抬头
		 */
		$(".receipt [name=receiptType]").click(function(){
			var value = $(this).val();
			if(value=="1"){
				$(".receipt [name=receiptTitle]").hide();
			}else{
				$(".receipt [name=receiptTitle]").show();
			}
		});
		
		
		$("#createBtn").click(function(){
			self.createOrder();
		});
		
		
	},
	
	changeBonus:function(bonusid){
		var regionid = $("#region_id").val();
		var typeid = $('input:radio[name="typeId"]:checked').val();
		if(regionid==null){
			regionid=0;
		}
		if(typeid==null){
			typeid=0;
		}
		$.ajax({
			url:"api/shop/bonus!useOne.do?bonusid="+bonusid+"&regionid="+regionid+"&typeid="+typeid,
			dataType:"json",
			success:function(res){
				if(res.result==1){
					CheckOut.refreshTotal();
				}else{
					alert(res.message);
				}
			},
			error:function(){
				alert("糟糕，使用优惠券发生意外错误");
			}
		});
	},
	
	//绑定打开窗口事件
	 whenOpen:function(){
		 var self  = this;
		 this.bindUseEvent();
		 //关闭按钮事件
		 $(".cartPop .c").click(function(){
				$(".cartPoint").empty();
				
				Cart.refreshTotal(); 					
		});
		 //添加一个新输入框
		 $(".bonusADD").click(function(){
			 
			 if($(".bonusSPAN>div").size()>=5){
				 alert("最多使用5个红包");
				 return false;
			 }
			 
			 var newInput = $(".bonusSPAN>div:first").clone();
			 newInput.removeAttr("id").find("input").removeAttr("disabled").val("");
			 newInput.find(".userY").show();
			 $(".bonusSPAN").append(newInput);
			 self.bindUseEvent();
		 });
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
			
			self.useBonus(sn,ipt);
		});
		
		$(".bonusSPAN .userQ").unbind("click").bind("click",function(){
			var ipt =  $(this).siblings("input");
			var sn  = ipt.val();
			var box = $(this).parent();
			self.cancelBonus(sn,box);
		});
	},
	//实体券
	useBonus:function(sn,ipt){
		
		var regionid = $("#region_id").val();
		var typeid = $('input:radio[name="typeId"]:checked').val();
		if(regionid==null){
			regionid=0;
		}
		if(typeid==null){
			typeid=0;
		}
		
		$.ajax({
			url:"api/shop/bonus!useSn.do?sn="+sn+"&regionid="+regionid+"&typeid="+typeid,
			dataType:"json",
			success:function(res){
				if(res.result==1){
					 ipt.attr("disabled",true);
					 ipt.next().hide();
					 CheckOut.refreshTotal();
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
			url:"api/shop/bonus!cancelSn.do?sn="+sn,
			dataType:"json",
			success:function(res){
				if(res.result==1){
					if($(".bonusSPAN>div").size()>1){
						box.remove();
					}else{
						box.find("input").removeAttr("disabled").val("");
						box.find(".userY").show();
					}
					 CheckOut.refreshTotal();
				}else{
					alert(res.message);
				}
			},
			error:function(){
				alert("糟糕，使用优惠券发生意外错误");
			}
		});
		
	},
		
	refreshTotal:function(){
		$(".total_wrapper").empty();
		var regionid = $("#region_id").val();
 		var dlytype = $("[name=typeId]:checked");
 		if( dlytype.size()== 0 ){
 			$.alert("请选择配送方式");
 			return ;
 		}
 		var typeId = dlytype.val();
 		$(".total_wrapper").load("checkout/checkout_total.html?regionId="+regionid+"&typeId="+typeId);
	},
	
	showModifyUI:function(){
		var self = this;
		self.setAddressValue(  self.addrModifyWrap.find(".input") );
		self.addrSelectedWrap.hide();
		self.addrModifyWrap.show();
	}
	
	,
	/**
	 * ====================================
	 * 绑定地址操作事件
	 * ====================================
	 */
	bindAddrEvent:function(){
		
		var self = this;
		//显示地址修改界面
		this.addrSelectedWrap.find(".modify_btn").click(function(){
			self.showModifyUI();
		});
			
		
		
		//保存收货地址
		$("#saveAddrBtn").click(function(){
			
			var result = $("#checkoutform").checkall();
			if(!result ) return ;
			
			var modifyWrapper = self.addrModifyWrap.find(".input");
			
			//设置 hidden的值 
			self.setHiddenValue(modifyWrapper );
			
			//为显示选中的界面显示html
			var html = self.createAddressHtml( modifyWrapper );
			self.addrSelectedWrap.find("span").remove();
			self.addrSelectedWrap.append(html).show();
			self.addrModifyWrap.hide();
			
			
			//让用户重新选择支付方式 
			self.loadPayment();
			
		});
		
		
		//收货地址选择事件
		this.addrModifyWrap.find(".list input[name=addressId]").click(function(){
			var $this= $(this);
			for(index in Address){
				var name = Address[index];
				$("#"+ name).val( $this.attr(name)  );	
			}
			self.setAddressValue(  self.addrModifyWrap.find(".input") );
			RegionsSelect.load($this.attr("province_id"),$this.attr("city_id"),$this.attr("region_id"));
		});		
	},
	
	
	/**
	 *====================================
	 * 绑定付款方式事件
	 *==================================== 
	 */
	bindPaymentEvent:function(){
		var self = this;
		//显示付款方式修改界面
		this.paySelectedWrap.find(".modify_btn").click(function(){
			self.paySelectedWrap.hide();
			self.payModifyWrap.show();
		});
		
		
		

		//保存付款方式 
		$("#savePaymentBtn").click(function(){
			 var payment = self.payModifyWrap.find(".list [name='paymentId']:checked");
			 if(payment.size()==0){
				 $.alert("请选择支付方式");
				 return false;
			 }
			 var name = payment.attr("payment_name");
			 self.paySelectedWrap.find("span").remove();
			 self.paySelectedWrap.append("<span>"+name+"</span>").show();
			 self.payModifyWrap.hide();
			 //让用户重新选择配送方式
			 self.loadDlytype(); 
		});
		
	}
	,
	/**
	 *====================================
	 * 绑定配送方式事件
	 *==================================== 
	 */	
	bindDlytypeEvent:function(){
		var self = this;
		//显示配送方式修改界面
		this.dlytypeSelectedWrap.find(".modify_btn").click(function(){
			self.dlytypeSelectedWrap.hide();
			self.dlytypeModifyWrap.show();
		});
		

		//保存配送方式 
		$("#saveDlytypeBtn").click(function(){
			
			var dlytype= self.dlytypeModifyWrap.find(".list input:radio[name='typeId']:checked");
			if(dlytype.size()==0){
				$.alert("请选择配送方式");
				return false;
			}
			
			var name = dlytype.attr("type_name");
			self.dlytypeSelectedWrap.find("span").remove();
			self.dlytypeSelectedWrap.append("<span>"+name+"</span>").show();
			self.dlytypeModifyWrap.hide();
			
			self.loadOrderTotal();
		});		
	},
	
	/***
	 * 加载支付方式列表
	 */
	loadPayment:function(){
		var self = this;
		$("#savePaymentBtn").show();
		self.paySelectedWrap.hide();
		self.payModifyWrap.show();
		this.payModifyWrap.find(".list").load("checkout/payment_list.html",function(){
			self.payModifyWrap.find("input[name=paymentId]").click(function(){
				self.payModifyWrap.find(".biref").hide();
				$(this).parents("li").find(".biref").show();
			});
		});
	}
	,
	
	
	/**
	 * 加载配送方式
	 */
	loadDlytype:function(){
		var self = this;
		$("#saveDlytypeBtn").show();
		self.dlytypeSelectedWrap.hide();
		self.dlytypeModifyWrap.show();
		var regionid = $("#region_id").val();
		this.dlytypeModifyWrap.find(".list").load("checkout/dlytype_list.html?regionid="+regionid);
	}
	
	
	,
	/**
	 * 给定一个包装器
	 * @param wrapper
	 * @returns {String}
	 */
	createAddressHtml:function(wrapper){
		var shipAddr = wrapper.find("[name=shipAddr]").val();
		var shipZip = wrapper.find("[name=shipZip]").val();
		var shipName = wrapper.find("[name=shipName]").val();
		var shipTel = wrapper.find("[name=shipTel]").val();
		var shipMobile = wrapper.find("[name=shipMobile]").val();
		var html = "<span>"+shipAddr+","+shipZip+","+shipName+","+shipTel+","+shipMobile+"</span>";
		
		return html;
		
	},
	
	/**
	 * 设置hidden的value
	 */
	setHiddenValue:function(wrapper){
		
		$("#shipAddr").val( wrapper.find("[name=shipAddr]").val());
		$("#shipZip").val ( wrapper.find("[name=shipZip]").val() );
		$("#shipName").val( wrapper.find("[name=shipName]").val());
		$("#shipTel").val( wrapper.find("[name=shipTel]").val() );
		$("#shipMobile").val(wrapper.find("[name=shipMobile]").val());
		
	}
	,
	
	/**
	 * 设置修改界面的值 
	 * @param wrapper
	 */
	setAddressValue:function(wrapper){
	 
		wrapper.find("[name=shipAddr]").val  ( $("#shipAddr").val()  );
		wrapper.find("[name=shipZip]").val   ( $("#shipZip").val()  );
		wrapper.find("[name=shipName]").val  ( $("#shipName").val()  );
		wrapper.find("[name=shipTel]").val   ( $("#shipTel").val()   );
		wrapper.find("[name=shipMobile]").val( $("#shipMobile").val()   );
		
		
	},
	 
	/**
	 * 加载订单价格 
	 */
 	loadOrderTotal:function(){
 		var regionid = $("#region_id").val();
 		var dlytype = $("[name=typeId]:checked");
 		if( dlytype.size()== 0 ){
 			$.alert("请选择配送方式");
 			return ;
 		}
 		var typeId = dlytype.val();
 		$(".total_wrapper").load("checkout/checkout_total.html?regionId="+regionid+"&typeId="+typeId);
	},
	
	createOrder:function(){
		$("#createBtn").attr({"disabled":"disabled"}); //禁用按钮
 		$.Loading.show("正在提交您的订单，请稍候..."); 
		var options = {
				url : "api/shop/order!create.do?ajax=yes" ,
				type : "POST",
				dataType : 'json',
				success : function(result) {
	 				if(result.result==1){
		 				location.href="order_create_success.html?orderid="+result.order.order_id;
		 			}else{
		 				$.Loading.hide();
		 				$.alert(result.message);
		 				$("#createBtn").removeAttr("disabled"); //解除禁用
			 		} 
				},
				error : function(e) {
					$.alert("出现错误 ，请重试");
					$("#createBtn").removeAttr("disabled"); //解除禁用
				}
		};

		$('#checkoutform').ajaxSubmit(options);		
	}

};
$(function(){
	
	$("#mobile").setValidator(function(){
		var tel= $.trim( $("#tel").val() ) ;
		var mobile = $.trim( $("#mobile").val() ) ;
		
		if( tel=="" && mobile==""  ){
			return  "手机或电话必须填写一项";
		}
		
		if( mobile!="" && !$.isMobile(mobile) ){
			return  "手机格式不正确";
		}
		
		return true;
	});
	
	
	$("#region_id").setValidator(function(){
		var value = $("#region_id").val();
		if( value=="" || value=="0" ) return "地区信息不完整";
		else return true;
	});
	
	
	CheckOut.init();
});