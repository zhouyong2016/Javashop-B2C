var Loginer={
		init:function(){
			var self = this;
			self.initValidCode();
			if($("#username").val()){$("#psdinput").focus();}
			$("#username").parent().parent().addClass("curr");
			$("#login_btn").click(function(){
				self.login();
			});
			$("#login_btn").attr("disabled",false).val("确定"); 
			
			$(document).keydown(function(event){
				if(event.keyCode==13){
					self.login();
				}
				
			});
			self.infoText("#username", ".username_img");
			self.infoText("#password", ".password_img");
			self.infoText("#valid_code", ".validcode_img");
			$(".inputstyle").focus(function(){
				$(this).parent().parent().find(".text").hide();
				$(this).parent().parent().addClass("curr");
			});
			$(".inputstyle").blur(function(){
				$(this).parent().parent().removeClass("curr");
				if ( $(this).val()==''){
					$(this).parent().parent().find(".text").show();
				}
			});
			$(".inputstyle").keydown(function(){
				$(this).parent().parent().find(".text").hide();
			});

		},
		login:function(){
			if($("#username").val().length==0){
				alert("用户名不能为空");
				return; 
			}if($("#password").val().length==0){
				alert("密码不能为空");
				return; 
			} if($("#valid_code").val().length==0){
				alert("验证码不能为空");
				return; 
			} 
			$("#login_btn").attr("disabled",true).val("正在登录..."); 
		 
			var options = {
					url : "../core/admin/admin-user/login.do",
					type : "POST",
					dataType : 'json',
					success : function(result) {				
						if(result.result==1){
							var referer=$("#referer").val();
							var url = "main.do";
							if(referer!=""){
								url=referer;
								//url+="?referer="+referer;
							}
							location.href=url;
						}else{
							$("#login_btn").attr("disabled",false).val("确定"); 
							 alert(result.message);
						}
					},
					error : function(e) {
						$("#login_btn").attr("disabled",false).val("确定"); 
						alert("出现错误 ，请重试");
					}
				};

				$('form').ajaxSubmit(options);		
		},
		infoText:function(n,m){
			
			if($(n).val()==''){
				$(m).next(".text").show();
			}else{
				$(m).next(".text").hide();
			};
			
		},
		initValidCode:function(){
		 
			$("#username").focus();
		    var that =this;
			$("#code_img").attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime())
			.click(function(){
				$(this).attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime() );
				
			});		
		}
}