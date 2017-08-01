var OrderBase={
	init:function(){
		this.bindevent();
	},
	bindevent:function(){
		 
		var self = this;
		$("#editPriceBtn").click(function(){
			self.showpriceinput($(this));
		});
		$("#editShipMoneyBtn").click(function(){
			self.showshipmoneyinput($(this));
		});
		$("#editAddrBtn").click(function(){
			self.showaddressinput($(this));
		});
		
		$("#editship_dayBtn").click(function(){
			self.showship_dayinput($(this));
		});
		$("#editship_nameBtn").click(function(){
			self.showship_nameinput($(this));
		});
		$("#editship_telBtn").click(function(){
			self.showship_telinput($(this));
		});
		$("#editship_mobileBtn").click(function(){
			self.showship_mobileinput($(this));
		});
		$("#editship_zipBtn").click(function(){
			self.showship_zipinput($(this));
		});
		$("#editDepotBtn").click(function(){
			self.showDepotinput($(this));
		});
	},
	showpriceinput:function(btn){
		var self = this;
		var orderid = $("#orderid").val();
		var price_span =  $("#order_price_span");
		var price = price_span.attr("price") ;
		var price_input = $("<input type='text' class='input_text' style='width:60px' value='"+price+"' id='price_input' name='price' />");
		price_span.empty();
		price_span.append(price_input);
		btn.html("确定");
		$("#closePriceBtn").html("&nbsp关闭");
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			$("#closePriceBtn").unbind("click").bind("click",function(){
				self.closepriceinput(price_input.val());
			});
		}
		btn.unbind("click").bind("click",function(){
			self.savePrice(price_input.val(),orderid,btn);
		});			
	},
	closepriceinput:function(money){
		var self = this;
		
		$("#order_price_span").html("￥"+money);
		$("#editPriceBtn").html("<a href=\"javascript:;\" id=\"editPriceBtn\" class=\"easyui-linkbutton\" style=\"font-size:14px\">修改</a>");
		$("#closePriceBtn").html("&nbsp;");
		$("#editPriceBtn").bind("click",function(){
			self.showpriceinput($(this));
		});
	},
	showDepotinput:function(btn){
		var self = this;
		var orderid = $("#orderid").val();
		$("#depot_span").hide();
		$("#depotinfo_span").show();
		btn.html("确定");
		$("#closeDepotBtn").html("&nbsp关闭");
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			$("#closeDepotBtn").unbind("click").bind("click",function(){
				self.closedepotinput();
			});
		}
		btn.unbind("click").bind("click",function(){
			self.saveDepot($("#depot").val(),orderid,btn);
		});			
	},
	closedepotinput:function(){
		var self = this;
		$("#depot_span").show();
		$("#depotinfo_span").hide();
		$("#editDepotBtn").html("修改");
		$("#closeDepotBtn").html("&nbsp;");
		$("#editDepotBtn").unbind("click").bind("click",function(){
			self.showDepotinput($(this));
		});
	},
	showshipmoneyinput:function(btn){
		var self = this;
		var orderid = $("#orderid").val();
		var ship_span =  $("#order_shipmoney_span");
		var ship = ship_span.attr("price");
	 
		var ship_input = $("<input type='text' style='width:60px' class='input_text' value='"+ship+"' id='ship_input' name='shipmoney' />");
		ship_span.empty();
		ship_span.append(ship_input);
		btn.html("确定");
		$("#closeShipMoneyBtn").html("&nbsp关闭");
		$("#closeShipMoneyBtn").unbind("click").bind("click",function(){
			self.closeShipMoney(ship_input.val());
		});
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			btn.unbind("click").bind("click",function(){
				self.saveShipMoney(ship_input.val(),orderid,btn);
			});	
		}
	},
	closeShipMoney:function(shipmoney)
	{
		var self = this;
		$("#order_shipmoney_span").html("￥"+shipmoney);
		$("#closeShipMoneyBtn").html("&nbsp");
		$("#editShipMoneyBtn").html("修改");
		
		$("#editShipMoneyBtn").unbind("click").bind("click",function(){
			self.showshipmoneyinput($(this));
		});
	}
	,
	showaddressinput:function(btn){
		var self = this;
		var orderid = $("#orderid").val();
		var addr_span =  $("#order_addrdetail_span");
		 
		var addr = addr_span.html().replace(/(^\s*)|(\s*$)/g, "");//去除前后空格
		var addr_input = $("<input type='text' style='width:300px' class='input_text' value='"+addr+"' id='addr_input' name='addr' />");
		addr_span.empty().append(addr_input);
		btn.html("确定");
		$("#closeAddrBtn").html("&nbsp关闭");
		$("#closeAddrBtn").unbind("click").bind("click",function(){
			self.closeAddrBtninput(addr);
		});
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			btn.unbind("click").bind("click",function(){
				self.saveAddrDetail(addr,addr_input.val(),orderid,btn);
			});		
		}
	},
	closeAddrBtninput:function(addr){
		var self = this;
		$("#order_addrdetail_span").html(addr);
		$("#editAddrBtn").html("修改");
		$("#editAddrBtn").unbind("click").bind("click",function(){
			self.showaddressinput($(this));
		});	
		$("#closeAddrBtn").html("&nbsp;");
	}
	,
	showship_dayinput:function(btn){//收货日期
		var self = this;
		var orderid = $("#orderid").val();
		var ship_day_span =  $("#ship_day_span");
		var ship_day = ship_day_span.html().replace(/(^\s*)|(\s*$)/g, "");//去除前后空格
		var ship_day_input = $("<select id='ship_day_select'><option value='正常配送'>正常配送</option><option value='周一至周五'>周一至周五</option><option value='周六日及公众假期'>周六日及公众假期</option><option value='其他时间(请在订单备注中说明)'>其他时间(请在订单备注中说明)</option></select>");
		
		ship_day_span.empty().append(ship_day_input);
		$("#ship_day_select option[value='"+ship_day+"']").attr("selected",true);
		if($("#ship_day_select option:selected").text() == '其他时间(请在订单备注中说明)'){
			$("#ship_remark_div").remove();
			$("#ship_day_span").append("<div id='ship_remark_div'>备注：<input type='text' class='input_text' style='width:140px' value='' id='ship_remark' name='remark' /></div>");
		}
		ship_day_input.change( function() {
			if($("#ship_day_select option:selected").text() == '其他时间(请在订单备注中说明)'){
				$("#ship_remark_div").remove();
				$("#ship_day_span").append("<div id='ship_remark_div'>备注：<input type='text' class='input_text' style='width:140px' value='' id='ship_remark' name='remark' /></div>");
			}else{
				$("#ship_remark_div").remove();
			}
		}); 

		btn.html("确定");
		$("#closeship_dayBtn").html("&nbsp关闭");
		$("#closeship_dayBtn").bind("click",function(){
			self.closeship_day(ship_day);
		});
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			btn.unbind("click").bind("click",function(){
				var remark = $("#ship_remark").val();
				if(remark == undefined || remark ==null){
					remark = '';
				}
				self.saveship_day(ship_day,ship_day_input.val(),orderid,remark,btn);
			});	
		}
	},
	closeship_day:function(ship_day){
		$("#ship_day_span").empty().html(ship_day);
		$("#closeship_dayBtn").html("&nbsp;");
		$("#editship_dayBtn").html("修改");
		$("#editship_dayBtn").unbind("click").bind("click",function(){
			self.showship_dayinput($(this));
		});	
	},
	saveship_day:function(oldship_day,ship_day,orderid,remark,btn){
		btn.linkbutton("disable"); 
		var self = this;
		$.ajax({
			url:'order!saveShipInfo.do?ajax=yes',
			data:"ship_day="+ship_day+"&orderId="+orderid+"&remark="+remark,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#closeship_dayBtn").html("&nbsp;");
					$("#ship_day_span").empty().html(ship_day);
					$("#editship_dayBtn").html("修改");
					$("#editship_dayBtn").unbind("click").bind("click",function(){
						self.showship_dayinput($(this));
					});	
				}else{
					alert(result.message);
					$("#closeship_dayBtn").html("&nbsp;");
					$("#ship_day_span").empty().html(oldship_day);
					$("#editship_dayBtn").html("修改");
					$("#editship_dayBtn").unbind("click").bind("click",function(){
						self.showship_dayinput($(this));
					});	
				}
				btn.linkbutton("enable");
			},
			error:function(e){
				alert("保存地址出错"+e);
				btn.linkbutton("enable");
			}
			
		});
	},
	showship_nameinput:function(btn){//姓名
		var self = this;
		var orderid = $("#orderid").val();
		var ship_name_span =  $("#ship_name_span");
		var ship_name = ship_name_span.html().replace(/(^\s*)|(\s*$)/g, "");//去除前后空格
		var ship_name_input = $("<input type='text' class='input_text' style='width:100px' value='"+ship_name+"' id='ship_name_input' name='ship_name' />");
		ship_name_span.empty().append(ship_name_input);
		btn.html("确定");
		$("#closeship_nameBtn").html("&nbsp关闭");
		$("#closeship_nameBtn").bind("click",function(){
			self.closeship_nameinput(ship_name);
		});
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			btn.unbind("click").bind("click",function(){
				self.saveship_name(ship_name,ship_name_input.val(),orderid,btn);
			});		
		}
	},
	closeship_nameinput:function(oldship_name){
		var self = this;
		$("#closeship_nameBtn").html("&nbsp;");
		$("#ship_name_span").empty().html(oldship_name);
		$("#editship_nameBtn").html("修改");
		$("#editship_nameBtn").unbind("click").bind("click",function(){
			self.showship_nameinput($(this));
		});
	},
	saveship_name:function(oldship_name,ship_name,orderid,btn){
		btn.linkbutton("disable"); 
		var self = this;
		$.ajax({
			url: 'order!saveShipInfo.do?ajax=yes',
			data:"ship_name="+ship_name+"&orderId="+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#closeship_nameBtn").html("&nbsp;");
					$("#ship_name_span").empty().html(ship_name);
					$("#editship_nameBtn").html("修改");
					$("#editship_nameBtn").unbind("click").bind("click",function(){
						self.showship_nameinput($(this));
					});	
				}else{
					alert(result.message);
					$("#closeship_nameBtn").html("&nbsp;");
					$("#ship_name_span").empty().html(oldship_name);
					$("#editship_nameBtn").html("修改");
					$("#editship_nameBtn").unbind("click").bind("click",function(){
						self.showship_nameinput($(this));
					});
				}
				btn.linkbutton("enable");
			},
			error:function(e){
				alert("保存地址出错"+e);
				btn.linkbutton("enable");
			}
			
		});
	
	},
	showship_telinput:function(btn){//电话
		var self = this;
		var orderid = $("#orderid").val();
		var ship_tel_span =  $("#ship_tel_span");
		var ship_tel = ship_tel_span.html().replace(/(^\s*)|(\s*$)/g, "");//去除前后空格
		var ship_tel_input = $("<input type='text' class='input_text' style='width:100px' value='"+ship_tel+"' id='ship_tel_input' name='ship_tel' />");
		ship_tel_span.empty().append(ship_tel_input);
		btn.html("确定");
		$("#closeship_telBtn").html("&nbsp关闭");
		$("#closeship_telBtn").unbind("click").bind("click",function(){
			self.clocseship_tel(ship_tel);
		});	
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			btn.unbind("click").bind("click",function(){
				self.saveship_tel(ship_tel,ship_tel_input.val(),orderid,btn);
			});		
		}
	},
	clocseship_tel:function(oldship_tel){
		var self = this;
		$("#closeship_telBtn").html("&nbsp;");
		$("#ship_tel_span").empty().html(oldship_tel);
		$("#editship_telBtn").html("修改");
		$("#editship_telBtn").unbind("click").bind("click",function(){
			self.showship_telinput($(this));
		});	
	},
	saveship_tel:function(oldship_tel,ship_tel,orderid,btn){
		btn.linkbutton("disable"); 
		var self = this;
		$.ajax({
			url: 'order!saveShipInfo.do?ajax=yes',
			data:"ship_tel="+ship_tel+"&orderId="+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#closeship_telBtn").html("&nbsp;");
					$("#ship_tel_span").empty().html(ship_tel);
					$("#editship_telBtn").html("修改");
					$("#editship_telBtn").unbind("click").bind("click",function(){
						self.showship_telinput($(this));
					});	
				}else{
					alert(result.message);
					$("#closeship_telBtn").html("&nbsp;");
					$("#ship_tel_span").empty().html(oldship_tel);
					$("#editship_telBtn").html("修改");
					$("#editship_telBtn").unbind("click").bind("click",function(){
						self.showship_telinput($(this));
					});	
				}
				btn.linkbutton("enable");
			},
			error:function(e){
				alert("保存地址出错"+e);
				btn.linkbutton("enable");
			}
			
		});
	
	},
	showship_mobileinput:function(btn){//手机
		var self = this;
		var orderid = $("#orderid").val();
		var ship_mobile_span =  $("#ship_mobile_span");
		var ship_mobile = ship_mobile_span.html().replace(/(^\s*)|(\s*$)/g, "");//去除前后空格
		var ship_mobile_input = $("<input type='text' class='input_text' style='width:100px' value='"+ship_mobile+"' id='ship_mobile_input' name='ship_mobile' />");
		ship_mobile_span.empty().append(ship_mobile_input);
		btn.html("确定");
		$("#closeship_mobileBtn").html("&nbsp关闭");
		$("#closeship_mobileBtn").unbind("click").bind("click",function(){
			self.closeship_mobile(ship_mobile);
		});		
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			btn.unbind("click").bind("click",function(){
				self.saveship_mobile(ship_mobile,ship_mobile_input.val(),orderid,btn);
			});
		}
	},
	closeship_mobile:function(oldship_mobile){
		var self = this;
		$("#closeship_mobileBtn").html("&nbsp;");
		$("#ship_mobile_span").html(oldship_mobile);
		$("#editship_mobileBtn").html("修改");
		$("#editship_mobileBtn").unbind("click").bind("click",function(){
			self.showship_mobileinput($(this));
		});	
	},
	saveship_mobile:function(oldship_mobile,ship_mobile,orderid,btn){
		btn.linkbutton("disable"); 
		var self = this;
		$.ajax({
			url: 'order!saveShipInfo.do?ajax=yes',
			data:"ship_mobile="+ship_mobile+"&orderId="+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#ship_mobile_span").html(ship_mobile);
					$("#closeship_mobileBtn").html("&nbsp;");
					$("#editship_mobileBtn").html("修改");
					$("#editship_mobileBtn").unbind("click").bind("click",function(){
						self.showship_mobileinput($(this));
					});	
				}else{
					alert(result.message);
					$("#ship_mobile_span").html(oldship_mobile);
					$("#closeship_mobileBtn").html("&nbsp;");
					$("#editship_mobileBtn").html("修改");
					$("#editship_mobileBtn").unbind("click").bind("click",function(){
						self.showship_mobileinput($(this));
					});	
				}
				btn.linkbutton("enable");
			},
			error:function(e){
				alert("保存地址出错"+e);
				btn.linkbutton("enable");
			}
		});
	},
	showship_zipinput:function(btn){//邮编
		var self = this;
		var orderid = $("#orderid").val();
		var ship_zip_span =  $("#ship_zip_span");
		var ship_zip = ship_zip_span.html().replace(/(^\s*)|(\s*$)/g, "");//去除前后空格
		var ship_zip_input = $("<input type='text' class='input_text' style='width:100px' value='"+ship_zip+"' id='ship_zip_input' name='ship_zip' />");
		ship_zip_span.empty().append(ship_zip_input);
		btn.html("确定");
		//判断是否禁用,如果已经禁用则无法修改
		var disabled=  btn.hasClass("l-btn-disabled");
		if(! disabled){
			btn.unbind("click").bind("click",function(){
				self.saveship_zip(ship_zip,ship_zip_input.val(),orderid,btn);
			});
		}
		$("#closeship_zipBtn").html("&nbsp关闭");
		$("#closeship_zipBtn").unbind("click").bind("click",function(){
			self.closeship_zip(ship_zip);
		});
	},
	closeship_zip:function(oldship_zip){
		var self = this;
		$("#closeship_zipBtn").html("&nbsp;");
		$("#ship_zip_span").html(oldship_zip);
		$("#editship_zipBtn").html("修改");
		$("#editship_zipBtn").unbind("click").bind("click",function(){
			self.showship_zipinput($(this));
		});	
	},
	saveship_zip:function(oldship_zip,ship_zip,orderid,btn){
		btn.linkbutton("disable"); 
		var self = this;
		$.ajax({
			url: 'order!saveShipInfo.do?ajax=yes&ship_zip='+ship_zip+'&orderId='+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#ship_zip_span").html(ship_zip);
					$("#closeship_zipBtn").html("&nbsp;");
					$("#editship_zipBtn").html("修改");
					$("#editship_zipBtn").unbind("click").bind("click",function(){
						self.showship_zipinput($(this));
					});	
				}else{
					alert(result.message);
					$("#ship_zip_span").html(oldship_zip);
					$("#closeship_zipBtn").html("&nbsp;");
					$("#editship_zipBtn").html("修改");
					$("#editship_zipBtn").unbind("click").bind("click",function(){
						self.showship_zipinput($(this));
					});	
				}
				btn.linkbutton("enable");
			},
			error:function(e){
				alert("保存邮编出错"+e);
				btn.linkbutton("enable");
			}
		});
	},
	saveAddrDetail:function(oldaddr,addr,orderid,btn){
		var self = this;
		btn.linkbutton("disable"); 
		$.ajax({
			url: 'order!saveAddrDetail.do?ajax=yes',
			data:"addr="+addr+"&orderId="+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#closeAddrBtn").html("&nbsp;");
					$("#order_addrdetail_span").html(addr);
					$("#editAddrBtn").html("修改");
					$("#editAddrBtn").unbind("click").bind("click",function(){
						self.showaddressinput($(this));
					});	
				}else{
					alert(result.message);
					$("#closeAddrBtn").html("&nbsp;");
					$("#order_addrdetail_span").html(oldaddr);
					$("#editAddrBtn").html("修改");
					$("#editAddrBtn").unbind("click").bind("click",function(){
						self.showaddressinput($(this));
					});	
				}
				btn.linkbutton("enable");
			},
			error:function(e){
				alert("保存地址出错"+e);
				btn.linkbutton("enable");
			}
		});
	},
	saveShipMoney:function(shipmoney,orderid,btn){
		var self = this;
		btn.linkbutton("disable"); 
		$.ajax({
			url: '/shop/admin/order!saveShipMoney.do?ajax=yes',
			data:"shipmoney="+shipmoney+"&orderId="+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#order_shipmoney_span").html("￥"+shipmoney);
					$("#editShipMoneyBtn").html("修改");
					$("#closeShipMoneyBtn").html("&nbsp;");
					$("#editShipMoneyBtn").unbind("click").bind("click",function(){
						self.showshipmoneyinput($(this));
					});	
					$("#order_price_span").empty().html("￥"+result.price);
				}else{
					alert("保存订单运费失败!");
				}
				btn.linkbutton("enable");
			},
			error:function(e){
				alert("保存订单运费出错"+e);
				btn.linkbutton("enable");
			}
		});
	},
	savePrice:function(price,orderid,btn){
		var self = this;
		btn.linkbutton("disable"); 
		$.ajax({
			url: 'order!savePrice.do?ajax=yes',
			data:"price="+price+"&orderId="+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					$("#order_price_span").html("￥"+price);
					$("#editPriceBtn").html("修改");
					$("#closePriceBtn").html("&nbsp;");
					$("#editPriceBtn").unbind("click");
					$("#editPriceBtn").bind("click",function(){
						self.showpriceinput($(this));
					});	
				}else{
					alert("保存订单价格出错");
				}
				btn.linkbutton("enable"); 
			},
			error:function(e){
				alert("保存订单价格出错"+e);
				btn.linkbutton("enable"); 
			}
		});
	},
	saveDepot:function(depot,orderid,btn){
		var self = this;
		btn.linkbutton("disable"); 
		$.ajax({
			url: 'order!saveDepot.do?ajax=yes&depotid='+depot+'&orderId='+orderid,
			type:'POST',
			dataType:'json',
			success:function(result){
				if(result.result==1){
					location.reload();
					$("#editDepotBtn").unbind("click");
					$("#editDepotBtn").bind("click",function(){
						self.showDepotinput($(this));
					});	
				}else{
					alert("保存库房出错");
				}
				btn.linkbutton("enable"); 
			},
			error:function(e){
				alert("保存库房出错"+e);
				btn.linkbutton("enable"); 
			}
		});
	}
};
$(function(){
	OrderBase.init();	
	$("#depotinfo_span").hide(); 
});