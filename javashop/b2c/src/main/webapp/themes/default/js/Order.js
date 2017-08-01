var Order={
	init:function(){
		var self = this;
		//取消订单
		$(".cancelBtn").click(function(){
			var sn = $(this).attr("sn");
 			var html = $("#cancelForm").html();
			var dialog = $.dialog({
				title:"请输入取消原因",content:html,width:400,lock:true,
			});
			$(".ui_content .yellow_btn").jbtn().click(function(){
				var demo = $(".ui_content").find("textarea ").val();
				$('.yellow_btn').attr('disabled',"true");
				$('#cancelForm').ajaxSubmit({
					url:"../api/shop/order!cancel.do?sn="+sn+"&reason="+demo,
					type : "POST",
					dataType : 'json',
					success : function(data) {	
						if(data.result==1){
							alert("取消订单成功");
							location.href = "../member/member.html";
						}
						else{
							alert(data.message);
							$('.yellow_btn').removeAttr("disabled");
						}
					},
					 cache:false
				});	
	    	});
		});
		
		//确认收货
		$(".rogBtn").click(function(){
			var orderId = $(this).attr("orderid");
			if( confirm( "请您确认已经收到货物再执行此操作！" )){
				$.Loading.show("请稍候..."); 
				$.ajax({
					url:"../api/shop/order!rogConfirm.do?orderId="+orderId,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							location.reload();
						}else{
							 
							$.alert(result.message);
							$.Loading.hide();
						}
						
					},
					error:function(){
						$.alert("出错了:(");
					}
				});	
						
			}
		});
		//解冻积分
		$(".thawBtn").click(function(){
			var orderid = $(this).attr("orderid");
			$.confirm("提前解冻积分后，被冻结积分相关的订单商品，将不能进行退换货操作。确认要解冻吗？",
				function(){
					$.Loading.show("请稍候..."); 
					$.ajax({
						url:"../api/shop/returnorder!thaw.do?orderid="+orderid,
						dataType:"json",
						cache:false,
						success:function(result){
							if(result.result==1){
								location.reload();
							}else{
								$.Loading.hide(); 
								$.alert(result.message);
							}
						},error:function(){
							$.Loading.hide(); 
							$.alert("抱歉，解冻出错现意外错误");
						}
					});
				}	
			);
		
		});
		
	}

}