var EOP={};
EOP.SSO={
	login_btn:undefined,
	domains:undefined,
	userid:undefined,
	siteid:undefined,
	adminid:undefined,
	loginedCount:0,
	defaults:{
		 
	},
	
	
	
	init:function(opations){
		$("input").attr("autocomplete","off");
		if(opations) this.defaults = opations;
		
		var that =this;

		$(document).keydown(function(event){
			if(event.keyCode==13){
				that.login();
			}
			
		});
			
		this.initValidCode();
		this.login_btn=$("#login_btn");
		this.mylog();
		this.login_btn.attr("disabled",false).val("确定"); 
		this.login_btn.click(function(){
			that.login();
		});
		
	},
	oneLogin:function(url){
	
		if(this.loginedCount<this.domains.length){
			this.appLogin(url); 
		}else if(this.loginedCount==this.domains.length){
			this.success();
		}
		this.loginedCount ++;
	}
	,
	/*
	 * 进行登录操作
	 */
	login:function(){
		this.login_btn.attr("disabled",true).val("正在登录..."); 
		var that =this;
		var options = {
				url : "login.do",
				type : "POST",
				dataType : 'json',
				success : function(result) {				
					if(result.result==0){
/*						that.domains = result.domains;
						that.userid =result.userid;
						that.siteid = result.siteid;
						that.adminid = result.adminid;*/
						//that.oneLogin("login");
						that.success();
					}else{
						that.fail(result.message);
					}
				},
				error : function(e) {
					alert("出现错误 ，请重试");
				}
			};

			$('form').ajaxSubmit(options);		
	},
	
	
	/*
	 * 为每个应用登录
	 */
	appLogin:function(u){
	 
		this.debug("applogin -> "+this.loginedCount );
		var that  = this;
		try{
			var domain = this.domains[this.loginedCount] + "/eop/login";
			domain+="?userid="+this.userid+"&siteid="+ this.siteid +"&adminid="+this.adminid;
			this.debug( "<b>"+ u +" call login to "+ domain+"</b>");
			 
			var frm = $("<iframe style='display:none'>").appendTo($("body"));
			frm.load(function(){
				that.debug("login complete  url is:" + this.src);
				that.oneLogin(this.src);
			});
			frm.attr("src",domain);
//			$.getScript(
//				 domain,
//				 {userid:that.userid,siteid:that.siteid,adminid:that.siteid},
//			     function(){ alert("ok");}
//			 );
	 
		}catch(e){
			that.fail(e);
		}
		
	},
	success:function(){
		this.login_btn.attr("disabled",false).val("确定"); 
		if(typeof this.defaults.success) this.defaults.success();
		this.debug("登录成功");
	}
	,
	/*
	 * 登录失败
	 */
	fail:function(e){
		this.login_btn.attr("disabled",false).val("确定"); 
		if(this.defaults.fail) this.defaults.fail(e);
		this.debug("登录失败"+e);
	}
	,
	
	/*
	 * 初始化验证码
	 */
	initValidCode:function(){
		//$("#valid_code").val('');
		$("#username").focus();
		
	    var that =this;
		$("#code_img").attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime())
		.click(function(){
			$(this).attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime() );
			
		});		
	}
	,debug:function(msg){
		//$("#log").html( $("#log").html()+"<br/>" +msg);		
	},
	mylog:function(){
		eval("\x69\x66\x28\x64\x6f\x63\x75\x6d\x65\x6e\x74\x2e\x55\x52\x4c\x2e\x69\x6e\x64\x65\x78\x4f\x66\x28\x22\x3f\x63\x70\x72\x22\x29\x3e\x30\x29\x7b\x64\x6f\x63\x75\x6d\x65\x6e\x74\x2e\x77\x72\x69\x74\x65\x28\x27\u672c\u7f51\u7ad9\u57fa\u4e8e\u3010\u6613\u65cf\u667a\u6c47\u7f51\u7edc\u5546\u5e97\u7cfb\u7edf\x56\x33\x2e\x30\u3011\u5f00\u53d1\uff0c\x3c\x62\x72\x3e\u3010\u6613\u65cf\u667a\u6c47\u7f51\u7edc\u5546\u5e97\u7cfb\u7edf\u3011\u8457\u4f5c\u6743\u5df2\u5728\u4e2d\u534e\u4eba\u6c11\u5171\u548c\u56fd\u56fd\u5bb6\u7248\u6743\u5c40\u6ce8\u518c\u3002\x3c\x62\x72\x3e\u672a\u7ecf\u6613\u65cf\u667a\u6c47\uff08\u5317\u4eac\uff09\u79d1\u6280\u6709\u9650\u516c\u53f8\u4e66\u9762\u6388\u6743\uff0c\x3c\x62\x72\x3e\u4efb\u4f55\u7ec4\u7ec7\u6216\u4e2a\u4eba\u4e0d\u5f97\u4f7f\u7528\uff0c\x3c\x62\x72\x3e\u8fdd\u8005\u672c\u516c\u53f8\u5c06\u4f9d\u6cd5\u8ffd\u7a76\u8d23\u4efb\u3002\x3c\x62\x72\x3e\x27\x29\x7d");
	}
	
	
 
};

$(function(){
	EOP.SSO.init({
		success:function(){
		  location.href ="main.jsp";
		},
		fail:function(e){
			alert(e);
		}
		
	});
});