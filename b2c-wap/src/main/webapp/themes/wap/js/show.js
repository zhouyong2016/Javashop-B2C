var show = {
		//显示遮罩，阻止客户进行其他操作
		show_cover:function(){
			$(".show").css("display","block");
			$("#BgDiv1").css({ display: "block", height: $(document).height() });    //背景宽度和高度
			var yscroll_wap = document.body.scrollTop;                       //测试在手机上有效，可以德高滚动高度
			//var yscroll_pc = document.documentElement.scrollTop;                //获得滚动的高度(此方便在PC上有效，在火狐测试过。其他浏览器没有测试)
			var screenx=$(window).width();                                           //活页页面宽度
			var screeny=$(window).height();  
			//alert(screenx+"+"+screeny+"+"+yscroll_wap)                             //测试高度使用。
			//获得当前屏幕的高度
			$(".DialogDiv").css("display", "block");              
			$(".DialogDiv").css("top",yscroll_wap+"px");                                 //这样一来，等待的这个div就会在当前屏幕的上面。
			 var DialogDiv_width=$(".DialogDiv").width();                            //获得class=DialogDiv的DIV的宽度
			 var DialogDiv_height=$(".DialogDiv").height();			                 //获得class=DialogDiv的DIV的高度
			$(".DialogDiv").css("left",(screenx/2-DialogDiv_width/2)+"px") ;          
			$(".DialogDiv").css("top",(yscroll_wap+screeny/2)+"px");                      //滚动的高度+当前页面高度的一半，这样，就能使DialogDiv在当前屏幕居中的地方了。
			$("body").css("overflow","hidden"); 
		},

		
		close_cover:function(){
			$(".show").css("display","none");
			$("body").css("overflow","visible");
		},

};