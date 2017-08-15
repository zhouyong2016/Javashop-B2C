var Order={
	init:function(){
		var self = this;
		//确认收获
		$(".rogBtn").click(function(){
			var orderId = $(this).attr("orderid");
			if( confirm( "请您确认已经收到货物再执行此操作！" ) ){
				$.ajax({
					url:ctx+"/api/shop/order/rog-confirm.do?orderId="+orderId,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							location.reload();
						}else{
							
							alert(result.message);
						}
						
					},
					error:function(){
						alert("出错了:(");
					}
				});	
						
			}
		});
		//解冻
		$(".thawBtn").click(function(){
			var orderid = $(this).attr("orderid");
			if( confirm( "提前解冻积分后，被冻结积分相关的订单商品，将不能进行退换货操作。确认要解冻吗？" ) ){
				$.ajax({
					url:ctx+"/api/shop/returnorder/thaw.do?orderid="+orderid,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							location.reload();
						}else{
							alert(result.message);
						}
					},error:function(){
						alert("抱歉，解冻出错现意外错误");
					}
				});		
			}
		
		});
		
	}

}