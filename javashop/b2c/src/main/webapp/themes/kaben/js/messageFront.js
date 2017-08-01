var MessageFront= {
    init     : function (staticserver) {
        var self = this;
        this.bindEvent();
    },
    bindEvent: function () {
        var self = this;
        //火狐操作后会将下个复选框选中，所以默认设置false
        $("input[name=msgcheckbox]").each(function(){
        	$(this).prop('checked',false);
        });
        $("#all-check").prop('checked',false);
        //放入回收站
        $(".recycle-one").click(function () {
        	if(!confirm("确认将该消息放入回收站吗？")){	
    			return ;
    		}
            var $this = $(this);
            var messageid = $this.attr("messageid");
            $.Loading.show("正在放入回收站...");
            $.ajax({
                url     : ctx + "/api/shop/messageFront/in-recycle.do",
                data    : "messageids=" + messageid,
                dataType: "json",
                success : function (result) {
                    if (result.result == 1) {
                    	window.location.reload();
                    } else {
                        $.alert(result.message);
                    }
                    $.Loading.hide();
                },
                error   : function () {
                    $.Loading.hide();
                    $.message.error('出现错误，请重试！');
                }
            });
        });
        
        //全部放入回收站
        $("#in-recycle-all").click(function () {
            var $this = $(this);
            var messageids="";
            $("input[name=msgcheckbox]:checked").each(function(){
            	messageids+=$(this).val()+",";
            });
            if(messageids.length<=1){
            	alert("请选择要回收的消息...");
            }else{
            	if(!confirm("确认将这些消息全部放入回收站吗？")){	
        			return ;
        		}
            	$.Loading.show("正在放入回收站...");
            	self.btnHiddden();
            	$.ajax({
            		url     : ctx + "/api/shop/messageFront/in-recycle.do",
            		data    : "messageids=" + messageids.substring(0,messageids.length-1),
            		dataType: "json",
            		success : function (result) {
            			if (result.result == 1) {
            				window.location.reload();
            			} else {
            				$.alert(result.message);
            				self.btnShow();
            			}
            			$.Loading.hide();
            		},
            		error   : function () {
            			$.Loading.hide();
            			$.message.error('出现错误，请重试！');
            			self.btnShow();
            		}
            	});
            }
        });
        
        //标记为已读
        $(".have-read-one").click(function () {
            var $this = $(this);
            var messageid = $this.attr("messageid");
            $.Loading.show("消息已读...");
            $.ajax({
                url     : ctx + "/api/shop/messageFront/have-read.do",
                data    : "messageids=" + messageid,
                dataType: "json",
                success : function (result) {
                    if (result.result == 1) {
                    	//$this.remove();
                    	$.alert(result.message);
                    	location.reload();  //刷新页面
                    	MessageFrontTool.loadNum();
                    } else {
                        $.alert(result.message);
                    }
                    $.Loading.hide();
                },
                error   : function () {
                    $.Loading.hide();
                    $.message.error('出现错误，请重试！');
                }
            });
        });
        
        //全部标记为已读
        $("#have-read-all").click(function () {
            var $this = $(this);
            var messageids="";
            $("input[name=msgcheckbox]:checked").each(function(){
            	messageids+=$(this).val()+",";
            });
            if(messageids.length<=1){
            	alert("请选择要已读状态的消息...");
            }else{
            	if(!confirm("确认将这些消息全部标记为已读吗？")){	
        			return ;
        		}
            	self.btnHiddden();
            	$.ajax({
            		url     : ctx + "/api/shop/messageFront/have-read.do",
            		data    : "messageids=" + messageids.substring(0,messageids.length-1),
            		dataType: "json",
            		success : function (result) {
            			if (result.result == 1) {
            				window.location.reload();
            			} else {
            				$.alert(result.message);
            				self.btnShow();
            			}
            			$.Loading.hide();
            		},
            		error   : function () {
            			$.Loading.hide();
            			$.message.error('出现错误，请重试！');
            			self.btnShow();
            		}
            	});
            }
        });
        
        //回收站消息删除
        $(".delete-one").click(function () {
        	if(!confirm("确认将该消息删除吗？")){	
    			return ;
    		}
            var $this = $(this);
            var messageid = $this.attr("messageid");
            $.Loading.show("正在删除...");
            $.ajax({
                url     : ctx + "/api/shop/messageFront/msg-delete.do",
                data    : "messageids=" + messageid,
                dataType: "json",
                success : function (result) {
                    if (result.result == 1) {
                    	window.location.reload();
                    } else {
                        $.alert(result.message);
                    }
                    $.Loading.hide();
                },
                error   : function () {
                    $.Loading.hide();
                    $.message.error('出现错误，请重试！');
                }
            });
        });
        
        //回收站消息全部删除
        $("#delete-all").click(function () {
        	if(!confirm("确认将这些消息删除吗？")){	
    			return ;
    		}
            var $this = $(this);
            var messageids="";
            $("input[name=msgcheckbox]:checked").each(function(){
            	messageids+=$(this).val()+",";
            });
            if(messageids.length<=1){
            	alert("请选择要删除的消息...");
            }else{
            	$.ajax({
            		url     : ctx + "/api/shop/messageFront/msg-delete.do",
            		data    : "messageids=" + messageids.substring(0,messageids.length-1),
            		dataType: "json",
            		success : function (result) {
            			if (result.result == 1) {
            				window.location.reload();
            			} else {
            				$.alert(result.message);
            			}
            			$.Loading.hide();
            		},
            		error   : function () {
            			$.Loading.hide();
            			$.message.error('出现错误，请重试！');
            		}
            	});
            }
        });
        
        //全选
        $("#all-check").click(function () {
        	var check = $( this ).is( ":checked" );
        	if (check) {
        		$("input[name=msgcheckbox]").prop('checked',true);
            } else {
            	$("input[name=msgcheckbox]").removeAttr("checked");
            }
        });
        
        $("input[name=msgcheckbox]").click(function(){
        	var flag = true;
        	$("input[name=msgcheckbox]").each(function(){
        		var check = $(this).is( ":checked" );
        		if(!check){
        			flag = false;
        		}
        	});
        	$("#all-check").prop('checked',flag);
        });
        
        
        
    },
    
    btnHiddden : function(){
//    	$("#have-read-all").linkbutton("disable");
//    	$("#in-recycle-all").linkbutton("disable");
    },
    btnShow : function(){
//    	$("#have-read-all").linkbutton("enable");
//    	$("#in-recycle-all").linkbutton("enable");
    }
};
