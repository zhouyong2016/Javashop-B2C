/**
 * Created by lishida on 2017/1/24.
 */


var Loginer={
    init:function(){
        //背景图的轮播
     /*   $(".screenbg ul li").each(function(){
            $(this).css("opacity","0");
        });
        $(".screenbg ul li:first").css("opacity","1");
        $(".screenbg ul img").css("width","100%");
        var index = 0;
        var t;
        var li = $(".screenbg ul li");
        var number = li.size();
        function change(index){
            li.css("visibility","visible");
            li.eq(index).animate({opacity:1},4000);
            li.eq(index).siblings().animate({opacity:0},4000);
        }
        function show(){
            index++;
            if(index<=number-1){
                change(index);
            }else{
                index = 0;
                change(index);
            }
        }
        t = setInterval(show,8000);
        //根据窗口宽度生成图片宽度
*/        

        //加载登录的js
        var self = this;
        self.initValidCode();
        if($("#username").val()){$("#psdinput").focus();}
        $("#username").parent().parent().addClass("curr");
        $("#login_btn").click(function(){
            self.login();
        });
        $("#login_btn").attr("disabled",false).val("确定");
        
        //回车触发登录
        $(document).keydown(function(event){
            if(event.keyCode==13){
                self.login();
            }

        });
        /*self.infoText("#username", ".username_img");
        self.infoText("#password", ".password_img");
        self.infoText("#valid_code", ".validcode_img");*/
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
    //登录js
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
        
        var username = $.trim($("#username").val());
        var password = $.trim($("#password").val());
        var valid_code = $.trim($("#valid_code").val());
        var options = {
            url : "../core/admin/admin-user/login.do",
            data:{username:username,password:password,valid_code:valid_code},
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
   /* infoText:function(n,m){

        if($(n).val()==''){
            $(m).next(".text").show();
        }else{
            $(m).next(".text").hide();
        };

    },*/
    //加载验证码
    initValidCode:function(){

        $("#username").focus();
        var that =this;
        $("#code_img").attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime())
		.click(function(){
			$(this).attr("src","../validcode.do?vtype=admin&rmd="+new Date().getTime() );
			
		});		
    }
}