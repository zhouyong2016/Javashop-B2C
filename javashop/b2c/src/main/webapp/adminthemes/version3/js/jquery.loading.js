(function($){

	$.Loading = $.Loading || {};
	
	if(top!=window){
		$.Loading=parent.$.Loading;
	}else{
		$.Loading.show=function(text){
			
			layer.msg(text, {
				skin: 'demo-class',
				icon: 7,
				offset: '10px',
				time: 5000 //5秒关闭（如果不配置，默认是3秒）
			}, function(){
			  //do something
			});   
		
		};
		$.Loading.success=function(text){
			layer.msg(text, {
			  skin: 'demo-class',
			  icon: 1,
			  offset: '10px',
			  time: 3000 //5秒关闭（如果不配置，默认是3秒）
			}, function(){
			  //do something
			}); 
		};
		
		$.Loading.error=function(text){
			layer.msg(text, {
			  skin: 'demo-class',
			  icon: 2,
			  offset: '10px',
			  time: 3000 //5秒关闭（如果不配置，默认是3秒）
			}, function(){
			  //do something
			}); 
		};
		
		//成功信息对话框，可以自定义显示多长时间 add by xulipeng 2017年05月03日
		$.Loading.successMessage=function(text,time){
			layer.msg(text, {
			  skin: 'demo-class',
			  icon: 1,
			  offset: '10px',
			  time: time*1000 //1000为1秒  time为多少秒
			}, function(){
			  //do something
			});
		};
		
		//错误信息对话框，可以自定义显示多长时间 add by xulipeng 2017年05月03日
		$.Loading.errorMessage=function(text,time){
			layer.msg(text, {
			  skin: 'demo-class',
			  icon: 2,
			  offset: '10px',
			  time: time*1000 //1000为1秒  time为多少秒
			}, function(){
			  //do something
			});
		};
		
		//

		$.Loading.hide=function(){
			layer.closeAll('msg');
		};
	}

	
})(jQuery);