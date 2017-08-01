var GoodsStore={
	currgoodsid:undefined,
	init:function(){
		var self = this;
		
		//库存维护
		$(".sorebtn").click(function(){
			self.onStock=undefined;
			var goodsid  = $(this).attr("goodsid");
			self.openStoreDlialog(goodsid);
		});
		
		//进货
		$(".stock").click(function(){
			self.onStock=undefined;
			var goodsid  = $(this).attr("goodsid");
			self.openStockDlialog(goodsid);
		});
		
		//出货
		$(".ship").click(function(){
			self.onStock=undefined;
			var goodsid  = $(this).attr("goodsid");
			self.openShipDlialog(goodsid);
		});
		
		//报警设置
		$("input[name='warn_num']").click(function(){
			var goodsid = $(this).attr("goodsid");
			self.openWarnDlialog(goodsid);
		});
	},
	
	/**
	 * 打开库存维护对话框
	 * @param goodsid
	 */
	openStoreDlialog:function(goodsid){
		var self = this;
		self.createDialogHtml();
		Eop.Dialog.init({id:"store_box",modal:true,title:"库存维护",width:"500px",height:"650px",remove:true});
		Eop.Dialog.open("store_box");
		$("#store_content").load("goodsStore!getStoreDialogHtml.do?ajax=yes&goodsid=" +goodsid,function(){
			$("#storeform").validate();
			$("#save_store_btn").unbind("click").bind("click",function(){
				self.saveStore();
			});
		});
		
	},
	
	/**
	 * 打开进货对话框
	 * @param goodsid
	 */
	openStockDlialog:function(goodsid){
		var self = this;
		currgoodsid = goodsid;
		self.createDialogHtml();
		Eop.Dialog.init({id:"store_box",modal:true,title:"进货",width:"500px",height:"650px",remove:true});
		Eop.Dialog.open("store_box");
		
		$("#store_content").load("goodsStore!getStockDialogHtml.do?ajax=yes&goodsid=" +goodsid,function(){
			$("#storeform").validate();
			$("#save_store_btn").unbind("click").bind("click",function(){
				self.saveStock();
			});
		});
		
	},
	/**
	 * 打开报警对话框
	 * @param goodsid
	 */
	openWarnDlialog:function(goodsid){
		var self = this;
		currgoodsid = goodsid;
		self.createDialogHtml();
		Eop.Dialog.init({id:"store_box",modal:true,title:"报警设置",width:"500px",height:"300px",remove:true});
		Eop.Dialog.open("store_box");
		$("#store_content").load("goodsStore!getWarnDialogHtml.do?ajax=yes&goodsid=" +goodsid,function(){
			$("#storeform").validate();
			$("#save_store_btn").unbind("click").bind("click",function(){
				self.saveWarn();
			});
		});
		
	},
	/**
	 * 打开出货对话框
	 * @param goodsid
	 */
	openShipDlialog:function(goodsid){
		var self = this;
		self.createDialogHtml();
		Eop.Dialog.init({id:"store_box",modal:true,title:"出货",width:"500px",height:"650px",remove:true});
		Eop.Dialog.open("store_box");
		$("#store_content").load("goodsStore!getShipDialogHtml.do?ajax=yes&goodsid=" +goodsid,function(){
			$("#storeform").validate();
			$("#save_store_btn").unbind("click").bind("click",function(){
				self.saveShip();
			});
		});
		
	},
	
	/**
	 * 创建对话框的html
	 */
	createDialogHtml:function(){
			var content = "<div id='store_box'>";
			content+="<div style='height:500px;overflow:auto;margin-top:5px;margin-left:10px' id='store_content'></div>";
			content+="<div style='text-align:center'><input style='margin-left:10px;height:30px' type='button' id='save_store_btn' value='保存' /></div>"
				   +"</div>";
			$("body").append(content);
		
	}
	,
	
	/**
	 * 保存进货
	 * @returns {Boolean}
	 */
	saveStock:function(){

		var self = this;
		if ( !$("#storeform").checkall() ){
			return false;
		}
		$.Loading.show('正在保存，请稍候...');
		var options = {
				url : "goodsStore!saveStock.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {		
					
					if(self.onStock){
						self.onStock(result);
						$.Loading.hide();
						return ;
					}
					
					
					if(result.result==1){
						alert("进货成功");
						Eop.Dialog.close("store_box");
						$("#stock_task_img"+currgoodsid).parents("tr").remove();
					}else{
						alert("进货失败:"+result.message);
					}
					
					$.Loading.hide();
					
				},
				error : function(e) {
					alert("出现错误 ，请重试");
				}
			};

		$('#storeform').ajaxSubmit(options);		
	}
	
	,
	/**
	 * 保存报警
	 * @returns {Boolean}
	 */
	saveWarn:function(){
		var self = this;
		if ( !$("#storeform").checkall() ){
			$("#save_store_btn").removeAttr("disabled"); 
			return false;
		}
		$("#save_store_btn").attr("disabled",true);
		$(".closeBtn").attr("class","tempCloseBtn");
		$.Loading.show('正在保存，请稍候...');
		var options = {
				url : "goodsStore!saveWarn.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {
					$(".tempCloseBtn").attr("class","closeBtn");
					if(result.result==1){
						alert("报警设置成功");
						Eop.Dialog.close("store_box");
						$("#stock_task_img"+currgoodsid).parents("tr").remove();
					}else{
						alert("报警设置失败:"+result.message);
					}
					$.Loading.hide();
				},
				error : function(e) {
					alert("出现错误 ，请重试");
				}
			};

		$('#storeform').ajaxSubmit(options);		
	}
	
	,
	
	/**
	 * 保存出货
	 * @returns {Boolean}
	 */
	saveShip:function(){
		var self = this;
		if ( !$("#storeform").checkall() ){
			return false;
		}
		$.Loading.show('正在保存，请稍候...');
		var options = {
				url : "goodsStore!saveShip.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {	
					
					if(result.result==1){
						alert("出货成功");
						Eop.Dialog.close("store_box");
					}else{
						alert("出货失败:"+result.message);
					}
					
					$.Loading.hide();
					
				},
				error : function(e) {
					alert("出现错误 ，请重试");
				}
			};

		$('#storeform').ajaxSubmit(options);		
	}
	
	
	,
	
	/**
	 * 保存库存
	 * @returns {Boolean}
	 */
	saveStore:function(){
		if ( !$("#storeform").checkall() ){
			return false;
		}
		$.Loading.show('正在保存，请稍候...');
		var options = {
				url : "goodsStore!saveStore.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {				
					if(result.result==1){
						alert("库存保存成功");
						Eop.Dialog.close("store_box");
					}else{
						alert("库存保存失败:"+result.message);
					}
				
					$.Loading.hide();
					
				},
				error : function(e) {
					alert("出现错误 ，请重试");
				}
			};

			$('#storeform').ajaxSubmit(options);
	}
	,
	/**
	 * 更新某商品已库存维护已完成
	 */
	updateGoodsCmpl:function(){
		$.Loading.show('正在保存，请稍候...');
		var options = {
				url : "goodsStore!saveCmpl.do?ajax=yes",
				type : "POST",
				dataType : 'json',
				success : function(result) {		
					if(result.result==1){
						alert("已标记此商品为维护完成状态")
						Eop.Dialog.close("store_box");
						$("#stock_task_img"+currgoodsid).parents("tr").remove();
					}else{
						alert("标记状态 失败:"+result.message);
					}
					
					$.Loading.hide();
					
				},
				error : function(e) {
					$.Loading.hide();
					alert("出现错误 ，请重试");
				}
			};

		$('#storeform').ajaxSubmit(options);	
	}
}
$(function(){
	GoodsStore.init();
});