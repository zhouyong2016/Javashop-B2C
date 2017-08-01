(function($){
		function disabled(el){
	 		//el.attr("disabled","true");
	 		el.addClass("no-enable");
	 	}
	 	
	 	function enable(el){
	 		//el.attr("disabled","false");
	 		el.removeClass("no-enable");		
	 	}
	 	$.fn.SelectEditor = function(tpl,saveurl,paramName,checker){
			 return this.each(function(){
				 var data= $(this).attr("data"); //要提交的其它数据
				 if(""!=data){
					 data="&"+data;
				 }
				 var value =$(this).text();
				 var edit_panel=$(tpl);
				 $(this).hide().before(edit_panel);//隐藏本身，生成编辑面板
				 var num=edit_panel.find(".num");
				 var set=edit_panel.find(".set");
				 
				 var txt= num.find(".txt");
				 var setvalue= set.find();
				 var editlink = set.find("a.save");
				 txt.html(value);
				 var setval="";
				 

				 
				 num.find("a").click(function(){
					 num.hide();
					 set.show();
					 setvalue.select();
					 setvalue.attr("oldvalue",setvalue.val());
					 disabled(editlink);
				 });
				 $("#tplselect"+paramName).click(function(){
					 setval= $(this).val();
				 });
				 $("#tplselect"+paramName).change(function(){
					 if($(this).val()==setval){
						 disabled(editlink);
					 }else{
						 enable(editlink);	
					 }
				 });
				 set.find("a.cancel").click(function(){
					 set.hide();
					 num.show();
				 });
				 
				 
				 editlink.click(function(){
					 var editlink=$(this);
					 if(editlink.attr("disabled")=='true'){
						 return;
					 }
					 var newvalue= $("#tplselect"+paramName).val();
					 if(checker && typeof(checker) == 'function'){
						 if(!checker(value,newvalue)){
							 return;
						 } 
					 }
					 setvalue.attr("disabled",true);
					 disabled(editlink);
					 $.ajax({
						 type: 'POST',
						 url:saveurl,
						 data:paramName+"="+newvalue+"&ajax=yes"+data,
						 cache:false,
						 dataType:'json',
						 contentType:'application/x-www-form-urlencoded; charset=UTF-8',
						 success:function(result){
							 if(result.result==0){
								 alert(result.message);
							 }else{
								 location.reload();
								 $.Loading.success(result.message);
							 }
						 },
						 error:function(){
							 setvalue.attr("disabled",false);
							 enable(editlink);
							 alert("保存出错，请重试");
						 }
					 });
					 

				 });
				
			 });	
		}
})(jQuery);