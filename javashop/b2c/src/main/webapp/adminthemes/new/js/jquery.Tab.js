
/**
 * Javashop Tab页面插件
 * 该插件初始化元素之后，会按照指定的格式寻找html，然后自动生成tab页面（需要自定义样式）
 * 
 * @author Sylow
 * @date 2016-03-30
 * 
 * 用法： $(XX).Tab();
 */

(function($) {

	$.fn.Tab = function() {
		new _Tab($(this));
	}
	
	var _Tab=function(container){
		 //全局本对象变量
		var self = this;
		this.currentIndex=0,selectedIndex=0,
		
		self.init=function(){
			//this.id=id;
			//var self  = this;
			container.find(".tab>li").click(function(){
				var selected=$(this);
				self.toggle(selected);
			});
		};
		self.init();
		//切换
		self.toggle =function(selected){
			this.toggleTab(selected);
			this.toggleBody();
		};
		
		self.toggleTab=function(selected){
			//var self = this;
			var i = 0;
			container.find(".tab>li").each(function(){
				
				var tab= $(this);
				
				//找到当前的
				if(tab.attr("class")=='active'){
					self.currentIndex =i;
					tab.removeClass('active');
				}
				
				//当前中的
				if( this ==selected.get(0) ){
					self.selectedIndex =i;
					tab.addClass('active');
				}
				
				i++;			 
			});
			
		};
		
		
		//切换内容体
		self.toggleBody=function(){
			//var self =this;
			var i=0;
			container.find(".tab-page>div>div>.tab-panel").each(function(){
				var body = $(this);
				
				//当前的
				if(i == self.currentIndex){
					body.hide();
				}
				//选择中的
				if(i==self.selectedIndex) {
					body.show();
					$(".goods_sel_tb").datagrid("resize",{
					    width:1200,
						height:'auto' 
				   });
//					if(onTabShow && onTabShow!=undefined && typeof(onTabShow ) =="function"){
//						onTabShow(parseInt( body.attr("tabid")));
//					} 
				}
				
				
				
				i++;
			});
		};

	};

})(jQuery);




