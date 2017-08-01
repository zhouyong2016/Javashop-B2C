var MemberDetail ={
		memberid : undefined,
		init:function(memberid){
			
			var self = this; 
			this.memberid = memberid;
			new Tab(".member_detail");
		//	this.bindTabEvent();			
		//	self.showBase();
			
			
		},
		/**
		 * 绑定tab事件
		 */
		bindTabEvent:function(){
			var self = this;
			$("#base").click(function(){ self.showBase(); });
			$("#edit").click(function(){ self.showEdit(); });
			$("#orderLog").click(function(){ self.showOrderLog(); });
			$("#editPoint").click(function(){ self.showEditPoint();   });
			$("#levelPointLog").click(function(){ self.showPointLog(0);   });
			$("#mkPointLog").click(function(){ self.showPointLog(1);   });
			$("#advance").click(function(){ self.showAdvance();  });
			$("#ask").click(function(){ self.showAsk();  });
			$("#discuss").click(function(){ self.showDiscuss();  });
			$("#remark").click(function(){ self.showRemark();  });
		},
		
		/**
		 * 显示会员基本信息
		 */
		showBase:function(){
			$("#baseTab").load("member!base.do?ajax=yes&member_id="+this.memberid+"&rmd="+new Date().getTime());
		},
		
		/**
		 * 显示订单货物信息
		 */
		showEdit:function(){
			$("#editTab").load("member!edit.do?ajax=yes&member_id="+this.memberid+"&rmd="+new Date().getTime(),function(){
				$('input.dateinput').datepicker();
			});
		},
		
		/**
		 * 显示订单日志
		 */
		showOrderLog:function(){
			$("#orderLogTab").load("member!orderLog.do?ajax=yes&member_id="+this.memberid+"&rmd="+new Date().getTime(),function(){
				$(".order_link").click(function(){
					var orderid  = $(this).attr("orderid");
					parent.Eop.AdminUI.loadUrl('../shop/admin/order!detail.do?orderId='+orderid);
				});
			});
		},
		
		/**
		 * 显示货运日志
		 */
		showEditPoint:function(){
			$("#editPointTab").load("member!editPoint.do?ajax=yes&member_id="+this.memberid+"&rmd="+new Date().getTime());
		},
		
		/**
		 * 显示积分日志
		 */
		showPointLog:function(pointtype){
			if(pointtype==0)
			$("#levelPointLogTab").load("member!pointLog.do?ajax=yes&pointtype="+pointtype+"&member_id="+this.memberid+"&rmd="+new Date().getTime());
			if(pointtype==1)
			$("#mkPointLogTab").load("member!pointLog.do?ajax=yes&pointtype="+pointtype+"&member_id="+this.memberid+"&rmd="+new Date().getTime());
		},
		
		/**
		 * 显示订单日志
		 */
		showAdvance:function(){
			$("#advanceTab").load("member!advance.do?ajax=yes&member_id="+this.memberid+"&rmd="+new Date().getTime());
		},
		
		showAsk:function(){
			$("#askTab").load("member!comments.do?ajax=yes&object_type=2&member_id="+this.memberid+"&rmd="+new Date().getTime());
		},
		
		showDiscuss:function(){
			$("#discussTab").load("member!comments.do?ajax=yes&object_type=1&member_id="+this.memberid+"&rmd="+new Date().getTime());
		}, 
		
		showRemark:function(){
			$("#remarkTab").load("member!remark.do?ajax=yes&member_id="+this.memberid+"&rmd="+new Date().getTime());
		}
};