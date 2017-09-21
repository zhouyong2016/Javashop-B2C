var OrderStatus={};

// 订单状态

OrderStatus.ORDER_NOT_PAY = 0;	// 新建订单，未确认（用于货到付款订单）
OrderStatus.ORDER_CONFIRM = 1;	// 已确认
OrderStatus.ORDER_PAY=2;		// 已支付
OrderStatus.ORDER_SHIP = 3; 	// 已发货
OrderStatus.ORDER_ROG = 4; 		// 已收货
OrderStatus.ORDER_COMPLETE =5;	// 已完成
OrderStatus.ORDER_CANCELLATION =6; // 订单取消（货到付款审核未通过、新建订单取消、订单发货前取消）
OrderStatus.ORDER_MAINTENANCE =7;	// 交易成功已申请退货申请


// 付款状态
OrderStatus.PAY_NO= 0;   // 未付款
OrderStatus.PAY_PARTIAL_PAYED =1 ;// 部分付款
OrderStatus.PAY_YES= 2; // 已全部支付

// 货运状态

OrderStatus.SHIP_NO= 0;  // 0未发货
OrderStatus.SHIP_YES= 1;// 1已发货
OrderStatus.SHIP_ROG= 2;// 2已收货

var OrderDetail = {
	orderid : undefined,
	orderStatus : undefined,
	payStatus : undefined,
	shipStatus : undefined,
	isCod : false,
	isCancel : undefined,
	init : function(orderid, orderStatus, payStatus, shipStatus, isCod, paymentid, isCancel) {
		// 初始化订单的状态
		this.orderStatus = orderStatus;
		this.payStatus = payStatus;
		this.shipStatus = shipStatus;
		this.isCod = isCod;
		this.isCancel = isCancel;
		this.paymentid = paymentid;
		var self = this;
		this.orderid = orderid;
		this.bindFlowEvent();

	},

	/**
	 * 绑定订单流程按钮事件
	 */
	bindFlowEvent : function() {
		var self = this;
		// 付款事件绑定 确认付款
		$("#pay").unbind("click");
		$("#pay").bind("click",function() {
			var disabled=  $("#pay").hasClass("layui-btn-disabled");
			if( !disabled ){
			 	layer.open({
		    		title : "确认付款",//标题
		            maxmin :false,//右上角可否放大缩小
		            offset: '50px',
		            type:2,//弹框的类型
		            shade: [0.3, '#000'],//黑色背景
		            shadeClose:false,//黑色背景是否可以点击关闭
		            content:ctx+"/shop/admin/payment/show-pay-dialog.do?orderId="+ self.orderid,//内容的URL
		            area:['700px','430px'],//弹框大小
		            scrollbar: true//是否允许浏览器出现滚动条
		        });
			}
		});
		

		// 退货事件绑定
		$("#returned").unbind("click");
		$("#returned").bind("click",function() {
			var disabled=  $("#returned").hasClass("layui-btn-disabled");
			if( !disabled ){
				layer.open({
					  content: '售后申请'
					  ,btn: ['退货', '退款']
					  ,yes: function(index, layero){
						  //按钮【按钮一】的回调
						  		 layer.close(index);
				　　				 newTab("申请售后","../shop/admin/sell-back/add-sellback.do?orderId="+ self.orderid);
				　　				 
				　　		}
					  ,btn2: function(index, layero){
						  //按钮【按钮二】的回调
						  		parent.layer.close(index);
				　　				newTab("申请售后","../shop/admin/sell-back/add-refund.do?orderId="+ self.orderid);
					  }
					})
			}
		});
		

		// 收货事件绑定
		$("#rog").unbind("click");
		$("#rog").bind("click",function() {
			var disabled = $("#rog").hasClass("layui-btn-disabled");
			if( !disabled ){
				layer.confirm('确认顾客已收到货了吗?', {icon: 3, title:'提示'}, function(index){
					$.ajax({url : ctx+"/shop/admin/order/rog-confirm.do?orderId="+self.orderid,
						dataType : "json",
						success : function(res) {
							if (res.result == 1) {
								layer.closeAll();
								location.reload();
							} else {
								//$.Loading.error(res.message);
							}
						}
					});
				});
			}
		});

		// 完成事件绑定
		$("#complete").unbind("click");
		$("#complete").bind("click", function() {
			var disabled=  $("#complete").hasClass("layui-btn-disabled");
			if(!disabled){
				if (confirm("完成 操作会使该订单归档且不允许再做任何操作，确定要执行吗？")) {
					$.Loading.show("正在保存请稍候");
					self.complete(self.orderid);
				}
			}
		});

		// 作废事件绑定
		$("#cancel").unbind("click");
		$("#cancel").bind("click",function() {
			var disabled=  $("#cancel").hasClass("layui-btn-disabled");
			if(!disabled){
				if (confirm("作废操作会使该订单归档且不允许再做任何操作，确定要执行吗？")) {
//					$("#cancelorder").show();
//				　　	$('#cancelorder').dialog({
//				　　		title: '作废',	
//				　　		width:630,
//				　　		closed: false,
//				　　		modal: true,
//				　　		buttons: [{		
//				　　			 text:'保存',
//				　　			 handler:function(){
//				　　				 var savebtn = $(this);
//				　　				 var disabled=savebtn.hasClass("l-btn-disabled");
//				　　				 if(!disabled){
//					　　				var reason=$("select[name='reason'] option:selected").text();
//					　　				self.cancel(self.orderid,reason,savebtn);
//				　　				 }
//				　　			 }
//				　　		}]
//			　　		});
					layer.open({
						 title:'取消订单'
						,type:1
						,content: $('#cancelorder') 
						  ,btn: ['确定', '取消']
						  ,yes: function(index, layero){
							  var savebtn = $(this);
							  var disabled=savebtn.hasClass("layui-btn-disabled");
							  if(!disabled){
								  var reason=$("#cancelorder").find("option:selected").text();
								  self.cancel(self.orderid,reason);
								  layer.close(index); //如果设定了yes回调，需进行手工关闭
							  }
						  },btn2: function(index, layero){
						   layer.closeAll();//取消
						  }
						});
				}
			}
		});
		// 确认订单绑定
		$("#confirmorder").unbind("click");
		$("#confirmorder").bind("click", function() {
			var disabled=  $("#confirmorder").hasClass("layui-btn-disabled");
			if(!disabled){
				if (confirm("确认要确认此订单吗？")) {
					self.confirmOrder(self.orderid);
				}
			}
		});
		 // 保存， 发货
		$("#ship").unbind("click");
		$("#ship").bind("click",function() {
			var disabled=  $("#ship").hasClass("layui-btn-disabled");
			if( !disabled ){
				var ecpressNo = $("input[name='expressNo']").val()
				if(ecpressNo != ""){
					if(confirm("确认发货？快递公司："+$("#logi").find("option:selected").text()+"  快递单号："+ecpressNo)){
						self.saveShipNoInto(self.orderid,ecpressNo);
					}
			}else{
			
				layer.prompt({
					  offset: '50px',
					  title: '请输入【'+$("#logi").find("option:selected").text()+'】快递单号',
					  btn: ['发货', '取消'], // 可以无限个按钮
				  	  btn2: function(value, index, elem){
				  		layer.closeAll();//取消
				  	  }}, function(value, index, elem){//发货
				  		if(value==""){
							alert("请填写快递单号！");
							return false;
						}else if(value!=""){ 
							$('#expressVal').val(value)
							layer.confirm('确定发货?', {icon: 3, title:'提示'}, function(index){
								self.saveShipNoInto(self.orderid,value);
							});
						}
			  	  });
			 	
			}}
			
		});
			
		this.initBtnStatus();
	},
	
	/**
	 * 初始化按钮状态
	 */
	initBtnStatus : function() {
		
		// 货到付款审核订单
		if(this.isCod && (this.orderStatus == OrderStatus.ORDER_NOT_PAY)){
			$("#confirmorder").removeClass("layui-btn-disabled"); 
			$("#cancel").removeClass("layui-btn-disabled");
		}
		
		// 支付按钮
		if ( 
			(!this.isCod && this.payStatus == OrderStatus.PAY_NO)// 非货到付款的话，是未支付时使按钮可用
			|| (!this.isCod && this.payStatus == OrderStatus.PAY_PARTIAL_PAYED)// 非货到付款，部分付款时，按钮可用
			|| (this.isCod && (this.orderStatus == OrderStatus.ORDER_ROG) && (this.payStatus== OrderStatus.PAY_NO)) // 货到付款的，完成后付款按钮可用
		){
			$("#pay").removeClass("layui-btn-disabled"); 
			if(!this.isCod){
				$("#cancel").removeClass("layui-btn-disabled"); 
			}
		}
		
		// 发货按钮
		if ( 
			(!this.isCod && (this.shipStatus == OrderStatus.SHIP_NO && this.orderStatus==OrderStatus.ORDER_PAY) )
			|| (this.isCod && (this.shipStatus == OrderStatus.SHIP_NO && this.orderStatus==OrderStatus.ORDER_CONFIRM) )
			){
			$("#ship").removeClass("layui-btn-disabled");
			// $("#cancel").removeClass("l-btn-disabled");
		}
		
		// 收货按钮
		if (this.shipStatus == OrderStatus.SHIP_YES && this.orderStatus==OrderStatus.ORDER_SHIP) {
			$("#rog").removeClass("layui-btn-disabled"); 
		}
		
		// 订单退货
		if(this.orderStatus==OrderStatus.ORDER_COMPLETE){
			$("#returned").removeClass("layui-btn-disabled"); 
		}
		// 订单状态为作废，则禁用所有钮
		if (this.orderStatus == OrderStatus.ORDER_CANCELLATION||this.orderStatus == OrderStatus.ORDER_MAINTENANCE) {
			//$("#nextForm .easyui-linkbutton").addClass("layui-btn layui-btn-radius layui-btn-disabled");
			$("#cancel").addClass("layui-btn layui-btn-radius layui-btn-disabled");
			$("#pay").addClass("layui-btn layui-btn-radius layui-btn-disabled");
			
			
			
		}
		
		// 如果当前订单已经申请取消订单了，在还未审核通过期间，禁用所有按钮 add_by DMRain 2016-8-31
		if (this.isCancel == 1) {
			$("#nextForm .easyui-linkbutton").addClass("layui-btn-disabled");
		}
	},

	/**
	 * 直接发货
	 */
    saveShipNoInto : function(orderId,value) {
		var logi_id=$("#logi").val();
		var express = value;
		var logi_name=$("#logi").find("option:selected").text();
		var self = this;
		$.ajax({
			url :  ctx+'/shop/admin/order-print/save-ship-no.do',
			data:{'order_id':orderId,'expressno':express,'logi_id':logi_id,'logi_name':logi_name},
			type : "post",
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$('#expressVal').val($('#express').val())
					self.ship(self.orderid);
					layer.closeAll();
				}
			},
			error : function() {
				savebtn.linkbutton("enable");
			}
		});
	},
	
	
	/**
	 * 保存
	 */
	saveShipNo : function(orderId,savebtn) {
		var logi_id=$("#logi").val();
		var express=$("#express").val();
		var logi_name=$("#logi").find("option:selected").text();
		var self = this;
	 	var formflag= $("#intoForm").form().form('validate');
	 	if(formflag){
	 		$.Loading.show("正在处保存，请稍候...");
	 		savebtn.linkbutton("disable");
			var options = {
				url :  ctx+'/shop/admin/order-print/save-ship-no.do',
				data:{'order_id':orderId,'expressno':express,'logi_id':logi_id,'logi_name':logi_name},
				type : "post",
				dataType : "json",
				success : function(responseText) {
					if (responseText.result == 1) {
						$.Loading.success(responseText.message);
						self.refresh(responseText);
						$('#expressVal').val($('#express').val())
					}
					if (responseText.result == 0) {
						$.Loading.error(responseText.message);
						savebtn.linkbutton("enable");
					}
				},
				error : function() {
					$.Loading.error("出错了:(");
					savebtn.linkbutton("enable");
				}
			};
			$('#intoForm').ajaxSubmit(options);
	 	}
	},
	
	/**
	 * 支付
	 */
	pay : function(_form,savebtn) {
		var self = this;
	 	if(true){
			 var options = {
				 	url :  ctx+'/shop/admin/payment/pay.do',
				 	type : "POST",
				 	dataType : 'json',
				 	success : function(result) {
					if (responseText.result == 1) {
						layer.close(index);
						location.reload();
					}
					if (responseText.result == 0) {
						alert(responseText.message);
					}
				},error : function() {
					
				}
			};
			_form.ajaxSubmit(options);
	 	}
	},

	refresh : function(responseText) {
		location.reload();
	},
	
	/**
	 * 退货
	 */
	returned : function() {
		var flag = true;
		$("input[name=numArray]").each(function(i, v) {
			if ($.trim(v.value) == '') {
				flag = false;
			} else {
				if (!isdigit(v.value)) {
					flag = false;
				} else if (parseInt(v.value) < 0) {
					flag = false;
				}
			}

		});

		if (!flag) {
			$.Loading.error("请输入正确的退货数量");	
			return;
		}
		$("#order_dialog .submitBtn").attr("disabled", true);
		var self = this;
		var options = {
			url : basePath + "ship/returned.do",
			type : "post",
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					Eop.Dialog.close("order_dialog");
					self.shipStatus = responseText.shipStatus;
					self.bindFlowEvent();
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
			}
		};
		$('#order_form').ajaxSubmit(options);
	},
	
	/**
	 * 完成
	 */
	complete : function(orderId) {
		var self = this;
		$.ajax({
			url :  ctx+'/shop/admin/order/complete.do?orderId=' + orderId,
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					self.refresh(responseText);
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
			}
		});
	},
	/**
	 * 作废
	 */
	cancel : function(orderId, canel_reason) {
		var self = this;
		$.Loading.show("正在保存请稍候..");
		//savebtn.linkbutton("disable");
		$.ajax({
			url : ctx+'/shop/admin/order/cancel.do?orderId=' + orderId,
			data:{'cancel_reason':canel_reason},
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					self.refresh(responseText);
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
					//savebtn.linkbutton("enable");
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
				//savabtn.linkbutton("enable");
			}
		});
	},
	
	/**
	 * 发货
	 */
	
	ship:function(orderId){
		var self=this;
		//$.Loading.show("正在处理发货...");
		$.ajax({
			url :  ctx+'/shop/admin/order-print/ship.do?order_id='+orderId,
			dataType : "json",
			cache:false,
			success : function(responseText) {
				if (responseText.result == 1) {
					layer.msg(responseText.message, {icon: 6}); 
					self.refresh(responseText);
				}
				if (responseText.result == 0) {
					layer.msg(responseText.message, {icon: 5}); 
				}
			},
			error : function() {
				layer.msg("出错了", {icon: 5}); 
			}
		});
	},
	
	
	
	/**
	 * 订单确认
	 * 
	 * @param orderId
	 */
	confirmOrder : function(orderId) {
		var self = this;
		$.Loading.show("正在保存请稍候");
		$.ajax({
			url :  ctx+'/shop/admin/order/confirm-order.do?orderId='+ orderId,
			dataType : "json",
			success : function(responseText) {
				if (responseText.result == 1) {
					$.Loading.success(responseText.message);
					self.refresh(responseText);
				}
				if (responseText.result == 0) {
					$.Loading.error(responseText.message);
				}
			},
			error : function() {
				$.Loading.error("出错了:(");
			}
		});
	} 
 
};


/**
 * 绑定按钮
 */
layui.use('form', function() {
	var form = layui.form();

	//取消按钮
	$(".layui-layer-btn1").click(function(){
    	parent.layer.close(index);
    });
	//确认付款按钮提交
	form.on('submit(payBtn)', function(data) {
		var options = {
			 	url :  ctx+'/shop/admin/payment/pay.do',
			 	type : "POST",
			 	dataType : 'json',
			 	success : function(result) {
				if (result.result == 1) {
					layer.msg('保存成功', {icon: 6});
					parent.layer.close(index);
					parent.location.reload();
				}
				if (result.result == 0) {
					layer.msg(result.message, {icon: 5});
				}
			},error : function() {
				layer.msg('出现错误，请稍后重试', {icon: 5});
			}
		};
		$("#payform").ajaxSubmit(options);
		return false;
	});
});

function isdigit(s) {
	var r, re;
	re = /\d*/i; // \d表示数字,*表示匹配多个数字
	r = s.match(re);
	return (r == s);
}