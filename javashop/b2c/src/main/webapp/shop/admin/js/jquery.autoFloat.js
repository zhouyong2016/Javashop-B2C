/**
 * Javashop 自动悬浮插件
 * 该插件初始化元素之后，会自动判断元素是否在可视范围内，若不在，悬浮到浏览器窗口底部，每次页面滚动都会刷新，若显示了，则还原原位
 * 
 * @author Sylow
 * @date 2016-03-24
 * 
 * 用法： $(XX).autoFloat();
 */

(function($) {

	$.fn.autoFloat = function() {
		new Float($(this));
		
	}
	var Float = function(container){
		   
		   //全局本对象变量
		   var self = this;
		   
		   /**
		    * 自动判断是否要浮动到最底部
		    * @isClone 是否克隆元素（第一次需要克隆）
		    */
		   this.float = function(isClone){
				var $autoFloat = container;
				var id = $autoFloat.attr("id");
				if($autoFloat.isOnScreen()) {
					$("#" + id + "-clone").hide();
				} else {
					$("#" + id + "-clone").show();
					if(isClone) {
						var $autoFloatClone = $autoFloat.clone(true);
						
						$autoFloatClone.attr("id",id + "-clone")
						
						$autoFloat.parent().append($autoFloatClone)
					}
					
					$("#" + id + "-clone").css({
						//left:$autoFloat.offset().left,
						bottom: 0,
						position: 'fixed'
					});
				}
			}
		   
		   this.float(true);
			
			
			$(window).scroll(function() {
				self.float(false);
				
			});
		   
		  
		   
	};
	/**
	 * 判断元素是否在可视范围内
	 */
	$.fn.isOnScreen = function(){
	     
	    var win = $(window);
	     
	    var viewport = {
	        top : win.scrollTop(),
	        left : win.scrollLeft()
	    };
	    viewport.right = viewport.left + win.width();
	    viewport.bottom = viewport.top + win.height() - this.outerHeight();
	     
	    var bounds = this.offset();
	    bounds.right = bounds.left + this.outerWidth();
	    bounds.bottom = bounds.top + this.outerHeight();
	     
	    return (!(viewport.right < bounds.left || viewport.left > bounds.right || viewport.bottom < bounds.top || viewport.top > bounds.bottom));
	     
	};

})(jQuery);