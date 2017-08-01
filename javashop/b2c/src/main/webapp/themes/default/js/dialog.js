

(function($){
 
	
	$.fn.jbtn = function(options){	
	
		
	    return this.each(function() {    
		
			$(this).hover(

				function ()
				{	
					var $this = $(this);   
					$this.addClass("hover");
				},
				function ()
				{	
					 
					var $this = $(this);   
					$this.removeClass("hover");		
	
				}
				
			);	
		});    
 		
	}; 

}
)(jQuery);

(function($){

	$.Dialog = $.Dialog || {};
	
	$.Dialog.openhtml=function(html,options){
		var el = html.clone();
		el.find(".btn").jbtn();
		var opts = $.extend({}, {unloadOnHide:true,draggable:false}, options);
		var myboxy = new Boxy(el , opts);		
		return myboxy;
	};
	
	$.Dialog.openurl=function(url){
		
	};
	$.Dialog.close=function(){
		
	};
	
})(jQuery);

