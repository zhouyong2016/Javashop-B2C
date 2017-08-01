/**
 * 异步编辑器
 * @author kingapex 
 * @date:2012-12-11
 * 
 * 在某页面直接编辑某值，通过异步方式保存。
 * 
 * @param saveurl
 * 必须的，提供保存值的url，会向此地址提供value={value}的参数
 * 
 * @param checker
 * 可选的，校验器，如果提供，必须是一个function。如果返回真则保存，如果返回假，则不保存。
 * 
 */
(function($){
	var tpl='<div class="edit-panel">';
	tpl+='<span class="num">';
	tpl+='<span class="txt"></span><a href="javascript:;">编辑</a>';
	tpl+='</span>';
	tpl+='<span class="ipt"><input type="text" name="num" /><a disabled="true" class="save disabled" href="javascript:;">保存</a><a  class="cancel" href="javascript:;">保存</a></span>';
	tpl+='</div>';
 
 	function disabled(el){
 		el.attr("disabled","true");
 		el.addClass("disabled");
 	}
 	
 	function enable(el){
 		el.attr("disabled","false");
 		el.removeClass("disabled");		
 	}
	
	$.fn.AjaxEditor = function(saveurl,checker){
		 return this.each(function(){
			 
			 var $this =   $(this);
			 var data= $this.attr("data"); //要提交的其它数据
			 if(""!=data){
				 data="&"+data;
			 }
			 
			 var value =$this.text();
			 var edit_panel=$(tpl);
			 $this.hide().before(edit_panel);//隐藏本身，生成编辑面板
			 
			 var num=edit_panel.find(".num");
			 var edit=edit_panel.find(".ipt");
			 var txt= num.find(".txt");
			 var input= edit.find("input");
			 var editlink = edit.find("a.save");
			 txt.html(value);
			 input.val(value);
			 

			 
			 num.find("a").click(function(){
				 num.hide();
				 edit.show();
				 input.select();
				 input.attr("oldvalue",input.val());
				 disabled(editlink);
			 });
			 
			 input.keyup(function(){
				 var oldvalue= input.attr("oldvalue");
				 if(oldvalue!=input.val()){
					 enable(editlink);					
				 }else{
					 disabled(editlink);
				 }
			 });
			 
			 edit.find("a.cancel").click(function(){
				 edit.hide();
				 num.show();
			 });
			 
			 
			 editlink.click(function(){
				 var editlink=$(this);
				 if(editlink.attr("disabled")=='true'){
					 return;
				 }
				 var newvalue= input.val();
				 
				 if(checker && typeof(checker) == 'function'){
					 if(!checker(value,newvalue)){
						 return;
					 } 
				 }
				 
				 input.attr("disabled",true);
				 disabled(editlink);
				 
				 $.ajax({
					 url:saveurl,
					 data:"value="+newvalue+"&ajax=yes"+data,
					 cache:false,
					 success:function(){
						 input.attr("disabled",false);
						 txt.html(newvalue );
						 edit.hide();
						 num.show();
						 enable(editlink);
						 location.reload();
					 },
					 error:function(){
						 input.attr("disabled",false);
						 enable(editlink);
						 alert("保存出错，请重试");
					 }
				 });
				 

			 });
			
		 });	
	}
	
})(jQuery);

