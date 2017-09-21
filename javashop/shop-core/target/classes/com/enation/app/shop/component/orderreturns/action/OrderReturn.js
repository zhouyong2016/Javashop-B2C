//by dable 11-10-11
var ReturnOrderStatus={};
//
//退换货表状态：
//0.申请已提交 
//1.已拒绝 
//2.已通过审核    
//3.已收货（并发货）
//4.完成


ReturnOrderStatus.APPLY_SUB = 0;
ReturnOrderStatus.APPLY_REFUSE = 1;
ReturnOrderStatus.APPLY_PASSED = 2;
ReturnOrderStatus.GOODS_REC = 3;
ReturnOrderStatus.APPLY_END =4;


var OrderReturn ={
		returnOrderId : undefined,
		returnOrderStatus:undefined,
		returnOrderType:undefined,
		init:function(returnOrderId,returnOrderStatus,returnOrderType){
			Eop.Dialog.init({id:"returnorder_dlg",modal:false,title:"订单退换操作",width:"450px"});
			Eop.Dialog.init({id:"changeSn_dlg",modal:false,title:"填写换货货号",width:"450px"});
			var self = this; 
			this.returnOrderStatus = returnOrderStatus;
			this.returnOrderId = returnOrderId;
			this.returnOrderType=returnOrderType;
			this.bindReturnEvent(this.returnOrderId,this.returnOrderStatus);	
		},
		//绑定退换货流程事件
		bindReturnEvent:function(rid,rstate){
			var self =this;
			if(self.returnOrderType==1){
				//退货拒绝事件绑定
				$("#returnrefuse").unbind("click");
				$("#returnrefuse").bind("click",function(){
					Eop.Dialog.open("returnorder_dlg");
						$("#returnorder_dlg .submitBtn").unbind("click");
						$("#returnorder_dlg .submitBtn").bind("click",function(){
							self.returnRefuse();
						});
				});
			}
			
			if(self.returnOrderType==2){
				//换货拒绝事件绑定
				$("#returnrefuse").unbind("click");
				$("#returnrefuse").bind("click",function(){
					Eop.Dialog.open("returnorder_dlg");
						$("#returnorder_dlg .submitBtn").unbind("click");
						$("#returnorder_dlg .submitBtn").bind("click",function(){
							self.changeRefuse();
						});
				});
			}
			
			if(self.returnOrderType==1){
				//同意退货事件绑定
				$("#returnagree").unbind("click");
				$("#returnagree").bind("click",function(){
					if($("input[name='itemid_array']:checked").length==0){
						alert("您还没有选择同意物品！");
						return;
					}else{
						if(confirm("确认同意此退货申请吗？")){
							self.agreeReturn();
						}
					}
				});
			}
			
			if(self.returnOrderType==2){
				//同意换货事件绑定
				$("#returnagree").unbind("click");
				$("#returnagree").bind("click",function(){
					if($("input[name='itemid_array']:checked").length==0){
						alert("您还没有选择同意物品！");
						return;
					}else{
						if(confirm("确认同意此换货申请吗？")){
							self.agreeChange();
						}
					}
				});
			}
			
			if(self.returnOrderType==1){
				//退货确认收货事件绑定
				$("#confirm_rec").unbind("click");
				$("#confirm_rec").bind("click",function(){
					if(confirm("确认收到退货吗？")){
						self.comfirmReturn();
					}
				});
			}
			
			if(self.returnOrderType==2){
				//换货确认收货事件绑定
				$("#confirm_rec").unbind("click");
				$("#confirm_rec").bind("click",function(){
					Eop.Dialog.open("changeSn_dlg");
					$("#changeSn_dlg .con").load(basePath+"returnOrder!returnD.do?ajax=yes&returnOrderId="+rid+"&returnOrderStatus="+rstate,function(){
						$("#change_btn").unbind("click");
						$("#change_btn").bind("click",function(){
							self.comfirmChange();
						});
					}); 
				});
			}
			if(self.returnOrderType==1){
				//退货完成事件绑定
				$("#return_complete").unbind("click");
				$("#return_complete").bind("click",function(){
						if(confirm("确认完成退货流程吗？")){
							self.returnComplete();
						}
				});
			}
			if(self.returnOrderType==2){
				//换货完成事件绑定
				$("#return_complete").unbind("click");
				$("#return_complete").bind("click",function(){
					if(confirm("确认完成换货流程吗？")){
						self.changeComplete();
					}
				});
			}
			//按钮状态
			this.initBtnStatus();
		},
		//退货拒绝
		returnRefuse:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!refuseReturn.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							Eop.Dialog.close("returnorder_dlg");
							$("#confirm_rec").attr("disabled",true); 
							$("#returnrefuse").attr("disabled",true); 
							$("#returnagree").attr("disabled",true); 
							alert("操作成功");
							location.reload();
							$("#r_reason").html($("#refuse_reason1").val());
						}
						if(responseText.result==0){
							alert(responseText.message);
						}						
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#returnorderform').ajaxSubmit(options); 
		},
		//换货拒绝
		changeRefuse:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!refuseChange.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							Eop.Dialog.close("returnorder_dlg");
							$("#confirm_rec").attr("disabled",true); 
							$("#returnrefuse").attr("disabled",true); 
							$("#returnagree").attr("disabled",true); 
							alert("操作成功");
							location.reload();
							$("#r_reason").html($("#refuse_reason1").val());
						}
						if(responseText.result==0){
							alert(responseText.message);
						}						
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#returnorderform').ajaxSubmit(options); 
		},
		//同意退货
		agreeReturn:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!agreeReturn.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							$("#returnrefuse").attr("disabled",true); 
							$("#returnagree").attr("disabled",true); 
							$("#return_complete").attr("disabled",true); 
							alert("操作成功");
							location.reload();
						}
						if(responseText.result==0){
							alert(responseText.message);
						}						
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#returnorderitems').ajaxSubmit(options); 
		},
		//同意换货
		agreeChange:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!agreeChange.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							$("#returnrefuse").attr("disabled",true); 
							$("#returnagree").attr("disabled",true); 
							$("#return_complete").attr("disabled",true); 
							alert("操作成功");
							location.reload();
						}
						if(responseText.result==0){
							alert(responseText.message);
						}						
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#returnorderitems').ajaxSubmit(options); 
		},
		//收到退货
		comfirmReturn:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!confirmReturnReceive.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							$("#return_complete").attr("disabled",false);
							$("#confirm_rec").attr("disabled",true);  
							alert("操作成功");
							location.reload();
						}
						if(responseText.result==0){
							alert(responseText.message);
						}						
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#returnorderitems').ajaxSubmit(options); 
		},
		//收到换货，目标货物发出
		comfirmChange:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!confirmChangeReceive.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							Eop.Dialog.close("changeSn_dlg");
							$("#confirm_rec").attr("disabled",true); 
							$("#return_complete").attr("disabled",false);
							alert("操作成功");
							location.reload();
						}
						if(responseText.result==0){
							alert(responseText.message);
						}								
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#changeSn_form').ajaxSubmit(options); 
		},
		//换货完成
		changeComplete:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!changed.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							$("#return_complete").attr("disabled",true); 
							alert("操作成功");
							location.reload();
						}
						if(responseText.result==0){
							alert(responseText.message);
						}						
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#returnorderitems').ajaxSubmit(options); 
		},
		//退款完成，退货流程结束
		returnComplete:function(){
			var self= this;
			var options = {
					url:'../admin/returnOrder!returned.do?ajax=yes',
					type:"post",
					dataType:"json",
					success: function(responseText) { 
						if(responseText.result==1){
							$("#return_complete").attr("disabled",true); 
							alert("操作成功");
							location.reload();
						}
						if(responseText.result==0){
							alert(responseText.message);
						}						
					},
					error:function(){
						alert("出错了:(");
					}
			};
			$('#returnorderitems').ajaxSubmit(options); 
		},
		//初始化按钮状态
		initBtnStatus:function(){
			var self= this;
			//未处理
			if( self.returnOrderStatus ==  ReturnOrderStatus.APPLY_SUB ){
				$("#confirm_rec").attr("disabled",true); 
				$("#return_complete").attr("disabled",true);
			}
			//同意退换
			if( self.returnOrderStatus ==  ReturnOrderStatus.APPLY_PASSED ){
				$("#confirm_rec").attr("disabled",false); 
				$("#return_complete").attr("disabled",true);
				$("#returnrefuse").attr("disabled",true); 
				$("#returnagree").attr("disabled",true); 
			}else{
				$("#confirm_rec").attr("disabled",true); 
			}
			//已收货
			if( self.returnOrderStatus ==  ReturnOrderStatus.GOODS_REC){
//				$("input[name='itemid_array']").attr("checked",true).attr("disabled",true);
				$("#returnrefuse").attr("disabled",true); 
				$("#returnagree").attr("disabled",true); 
				$("#confirm_rec").attr("disabled",true); 				
			}
			
			//拒绝
			if( self.returnOrderStatus ==  ReturnOrderStatus.APPLY_REFUSE
					|| self.returnOrderStatus ==  ReturnOrderStatus.APPLY_END){
				$("#return_complete").attr("disabled",true);
				$("#returnrefuse").attr("disabled",true); 
				$("#returnagree").attr("disabled",true); 
				$("#confirm_rec").attr("disabled",true); 				
			}
		}
}