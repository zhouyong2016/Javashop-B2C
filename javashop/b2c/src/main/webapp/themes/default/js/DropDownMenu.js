/**
 * 下拉菜单jquer插件
 * 不支持多级菜单
 * @author:kingapex
 * html结构：
 *
 <li>
   菜单名
   <ul>
		<li>子菜单</li>
		<li>子菜单</li>	
   </ul>
 </li> 
 * 调用:
	 $('yourli').dropDownMenu();
 *
 *v1.1:
 *新增addClzTo选项，可以指定给某个子加样式
 */
(function($){
 
	
	$.fn.dropDownMenu = function(options){
		
		var opts = $.extend({}, $.fn.dropDownMenu.defaults, options);
		
	    return this.each(function() {    
		
			$(this).hover(

				function open()
				{	
					var $this = $(this);
					if(opts.addClzTo){
						if($this.find(opts.addClzTo).attr("selected") != "1"){
							$this.find(opts.addClzTo).addClass(opts.selectedclz);
						}
					}else{
						if($this.attr("selected") != "1"){
							$this.addClass(opts.selectedclz);
						}
					}
										
					var children = $this.children(opts.child);
					if(children.size()==0) return;
					children.show();
					//children.fadeIn();

			        if(opts.onShow)
						opts.onShow($this);
				},
				function close()
				{	
					 
					var $this = $(this);   
					if(opts.addClzTo){
						if($this.find(opts.addClzTo).attr("selected") != "1"){
							$this.find(opts.addClzTo).removeClass(opts.selectedclz);
						}
					}else{	
						if($this.attr("selected") != "1"){
							$this.removeClass(opts.selectedclz);
						}
					}					
					var children = $this.children(opts.child);
					
					if(children.size()==0) return;
					
					children.hide();			
					//children.fadeOut();
				}
				
			);	
		});    
 		
	}; 
 		
 	$.fn.dropDownMenu.defaults={child:'.son',selectedclz:'selected'};	
}
)(jQuery);