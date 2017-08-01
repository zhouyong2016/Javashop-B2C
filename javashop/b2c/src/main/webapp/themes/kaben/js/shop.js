(function($){
 
	
	$.fn.jbtn = function(options){	
	
		
	    return this.each(function() {    
		
			$(this).unbind("hover").hover(

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
	
 

	$.Loading = $.Loading || {};
	$.Loading.show=function(text){
		$.blockUI({ 
	         css: { top: '10px'} , 
			 message:text,
			 showOverlay:false
	    }); 
	};

	$.Loading.hide=function(){
		$.unblockUI();
	};
	
	
	//$.alert=function(text){
	//	$.dialog.alert(text);
	//};
	
	//$.confirm=function(text,yes,no){
	//	$.dialog.confirm(text,yes,no);
	//};
}
)(jQuery);




$(function(){
	$(".btn").jbtn();	
	$("form.validate").validate();
});


