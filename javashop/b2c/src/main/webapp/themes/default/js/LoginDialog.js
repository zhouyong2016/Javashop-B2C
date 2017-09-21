(function($){
	var defaultOptions = 
	{
			title: "登录",modal:true,draggable:true,fixed:false,
			afterHide:function(){this.unload();},
			onLoadComplete:bindloginEvent,
			parent:null
			
	};
	var dialog;
	var loginSuccess;
	var loginError;
	$.LoginDialog = $.LoginDialog || {};
	
	
	$.LoginDialog.open=function(options){
		
		var loginurl= "detail/loginDialog.html?widgetid=login&ajax=yes&action=dialog";
		options = $.extend({}, defaultOptions, options || {});
		
		//登录成功和失败的事件
		loginSuccess =  options.loginSuccess;
		loginError = options.loginError;

		dialog = $.dialog({ title:"会员登录", width:475, height:312,lock:true,min:false,max:false});
		
		$.ajax({
		    url:loginurl,
		    success:function(html){
		    	dialog.content(html);
		    	bindloginEvent();
		    },
		    error:function(){
		    	$.alert("登录页面获取出错");
		    }
		    ,
		    cache:false
		});		

		return false;
	};
	
	$.LoginDialog.close=function(){
		dialog.cancel();
	};
	
	function reLoadLoginBar(){
	 	$("#widget_login_bar").html("加载中...");
	 	$("#widget_login_bar").load("login_bar.html?widgetid=/login_bar.html&ajax=yes");
	}
	
	function bindloginEvent(this_dialog){
		
			$("#login_dialog_wrapper .loginbtn").click(function(){
				$.Loading.show("正在登录，请稍候...");	
				var options = { 
						url : "widget.do?type=member_login&ajax=yes&action=ajaxlogin",
						type : "POST",
						dataType : 'json',
				
						success : function(result) {
							$.Loading.hide();
			 				if(result.result==1){
			 					 isLogin=true;
				 				 if(dialog){
				 					reLoadLoginBar();
				 					dialog.close();
				 					
				 					if(loginSuccess &&  typeof(loginSuccess) =='function' ){
				 						loginSuccess(result);
				 					}
					 			 }
				 			}else if(result.result==-1){
				 				$.alert("验证码验入错误");
			 					if(loginError &&  typeof(loginError) =='function' ){
			 						loginError(result);
			 					}
					 		}else if(result.result==0){
						 		$.alert("用户名或密码输入错误");
			 					if(loginError &&  typeof(loginError) =='function' ){
			 						loginError(result);
			 					}
						 	}
						},
						error : function(e) {
							$.Loading.hide();
							$.alert("出现错误 ，请重试");
						}
				};
		 
			$('#login_dialog_wrapper form').ajaxSubmit(options);
			
		});
		$("#login_dialog_wrapper .btn").jbtn();
		$("#verifyCodebox a").click(function(){
			$("#verifyCodebox img").attr("src","validcode.do?vtype=memberlogin&r="+new Date().getTime());
		});
	}
	
	
})(jQuery);
