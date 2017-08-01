
/**
 * 后台界面构建js
 * @author kingapex
 */
var BackendUi={
	menu:undefined,
	init:function(menu){
	//	Eop.AdminUI.init({wrapper:$("#right_content")});
		
		$(".desktop a").click(function(){
			
			self.loadUrlInFrm($(this).attr("href"));
			return false;
		});
		this.opts ={};
		this.opts.wrapper = $( "#right_content");
		this.opts.frm   =$("<iframe id='auto_created_frame' width='100%' height='100%' frameborder='0' ></iframe>");
		this.opts.wrapper.append(this.opts.frm);
		this.opts.frm.load(function(){
			$.Loading.hide();
		});	
		
		this.menu =menu;
		this.autoHeight();
		var self =this;
		$(window).resize(function(){self.autoHeight();});
		
		$("#logout_btn").click(function(){
			$.Loading.show('正在注销...');
			$.ajax({
				url:"../core/admin/admin-user/logout.do",
				dataType:"json",
				success:function(){
					$.Loading.success("成功注销");
					location.href="login.do";
				},
				error:function(){
					alert("出现意外错误");
				}
			});
		});
		
	},
	loadUrlInFrm:function(url){
		$.Loading.show('正在加载，请稍侯...');
		url= url+(url.indexOf("?") == -1 ? "?" : "&") + "_="+(new Date()).getTime();
		this.opts.frm.attr("src",url);
	}
	,
	disMenu:function(){
		this.disSysMenu();
		this.disAppMenu();
	},
	
	/**
	 * 显示系统菜单
	 */
	disSysMenu:function(){
		var self =this;
		var menu = this.menu;
		$.each(menu.sys,function(k,v){
			var link = self.createLink(v);
			 $("<li/>").appendTo( $(".sysmenu>ul") ).append(link);
			 if(v.target!='_blank'  ){
				link.click(function(){
					self.loadUrlInFrm($(this).attr("href"));
					return false;
				});
			 }
		});		
	},
	/**
	 * 显示应用菜单
	 */
	disAppMenu:function(){
		var self=this;
		var menu = this.menu;
		var i=0;
		$.each(menu.app,function(k,v){
			 
				var link = $("<a  target='"+v.target+"' href='"+ v.url +"' >" + v.text + "</a>");
				$("<li><span></span></li>").appendTo($(".appmenu>ul")).children("span").append(link);
				var children = v.children;
				 
					link.click(function(){
						if(children){
							self.disAppChildren(children);
						}
					 
						$(".appmenu li").removeClass("current");
						$(this).parent().parent().addClass("current");
						return false;
					});
	
					
					if(i==0 ){ 
						link.click();
						if(referer){
							self.loadUrlInFrm(referer);
						}  
					}
					i++;
		});			
	},
	/**
	 * 显示应用的子菜单
	 */
	disAppChildren:function(children){
		var self= this;
		var leftMenu = $("#leftMenus");
		leftMenu.empty();
		$.each(children,function(k,v){
			leftMenu.append($("<dl class='item' id=i" + k +"><dt> <a href='#' class=\"folder\">"+this.text+"</a></dt></dl>"));
			var item  = $("#i"+k);
			if(this.children){
				$.each(this.children,function(k,v){
					var link = self.createLink(v);
					item.append($("<dd></dd>").append(link));
					link.click(function(){
						
						self.loadUrlInFrm($(this).attr("href"));
						return false;
					});
				});
			}
		});
		$("#leftMenus dl:first").addClass("curr");
		$("#leftMenus dt").click(function(){
			$("#leftMenus dl").removeClass("curr");
			$(this).parent().addClass("curr");
		});
	},
	createLink:function(v){
		var link = $("<a  target='"+v.target+"' href='"+ v.url +"' >" + v.text + "</a>");
		return link;
	},
	autoHeight:function(){
		var height= $(window).height()-100;
		$("#leftMenus").height(height);
		$("#right_content").height(height);
	}


};

$(function(){
	BackendUi.init(menu);
	BackendUi.disMenu();
});

