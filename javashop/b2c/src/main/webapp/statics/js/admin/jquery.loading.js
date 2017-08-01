(function($){

	$.Loading = $.Loading || {};
	
	if(top!=window){
		$.Loading=parent.$.Loading;
	}else{
		$.Loading.show=function(text){
			
			$.blockUI({ 
		         css: { top: '10px'} , 
				 message:text,
				 showOverlay:false
		    }); 
		
		};
		$.Loading.success=function(text){
			$(".blockUI").text(text);
			setTimeout($.unblockUI, 2000); 
		};
		
		//

		$.Loading.hide=function(){
			$.unblockUI();
		};
	}

	
})(jQuery);