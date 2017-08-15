/**
 * validate- jQuery Plug-in
 * @version 1.0
 * @author kingapex
 * Copyright 2009 enation  
 */
(function($){
	$.Validator={};
	var DefLang ={
			
			validate_fail:'您提交的表单中有无效内容，请检查高亮部分内容。',
			required:'此项为必填',
			string:'不能含有特殊字符',
			is_not_int:'此选项必须为整型数字',
			is_not_float:'此选项必须为浮点型数字',
			is_not_date:'日期格式不正确',
			is_not_email:'email格式不正确',
			is_not_mobile:'手机号码格式不正确',
			is_not_id_card:'cart not valid...',
			is_not_post_code:'邮政编码格式不正确',
			is_not_url:'不是有效的地址格式',
			is_not_tel_num:'电话号码格式不正确',
			is_not_chinese:'中文格式错误'
			
			
		};
		$.isDate=function(val){var reg = /^\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\d|3[0-1])$/; return reg.test(val);};
		$.isTime=function(val) { var reg = /^([0-1]\d|2[0-3]):[0-5]\d:[0-5]\d$/; return reg.test(val); };
		$.isEmail=function(val){var reg = /^([a-z0-9+_]|\-|\.|\-)+@([\w|\-]+\.)+[a-z]{2,4}$/i; return reg.test(val);};	
		$.isNumber=function(val){if(val=='') return true; return parseInt(val) == val;}	
		$.isTel=function(val){var reg = /^([0]\d{2,3}-)?(\d{7,11})(-(\d{2,4}))?$/; return reg.test(val);};	
		$.isMobile=function(val){var reg = /^[\d|-|\+]{11}$/; return reg.test(val);};	
		$.isChinese=function(val){var reg = /[^\u4e00-\u9fa5]/; return reg.test(val);};
		$.isSpecialChar=function(val){    var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]") ; return pattern.test(val); }
		
		function validator_lang_exist(key){
			  if (typeof(DefLang) != "object") return false;
			  if (typeof(DefLang[key]) == 'string') return true;
			  return false;
		}
		
		dt_chinese = function ()
		{
		  this.check = function (val) {$.isChinese(val);};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_chinese')) {return DefLang.is_not_chinese;} else {return "this value is not Chinese!";}};
		};
		
		dt_string = function ()
		{
			
		  this.check = function (val) {
			  
			  var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]") 
			  return !pattern.test(val);
			  
		  };
		  this.test = function (val, testStr) {return new RegExp(testStr).test(val)};
		  this.errorMsg = function () { return DefLang.string ;};
		};
		dt_int = function ()
		{
		  this.check = function (val){ if(val=='') return true; return parseInt(val) == val;};
		  this.test = function (val, testStr) {
		    var arr = testStr.split(',');
		    val = parseInt(val);
		    var test = arr[0].trim();
		    if (test != '*' && val < parseInt(test)) return false;
		    if (arr.length > 1){
		      test = arr[1].trim();
		      if (test != '*' && val > parseInt(test)) return false;
		    }
		    return true;
		  };
		  this.errorMsg = function () {if (validator_lang_exist('is_not_int')) {return DefLang.is_not_int} else {return "this value is not int!"}};
		};


		dt_float = function ()
		{
		  this.check = function (val){if(val=='') return true;   return parseFloat(val) == val;};
		  this.test = function (val, testStr) {
			if(val=='') return true;  
		    var arr = testStr.split(',');
		    val = parseFloat(val);
		    var test = arr[0].trim();
		    if (test != '*' && val < parseFloat(test)) return false;
		    if (arr.length > 1){
		      test = arr[1].trim();
		      if (test != '*' && val > parseFloat(test)) return false;
		    }
		    return true;
		  };
		  this.errorMsg = function () {if (validator_lang_exist('is_not_float')) {return DefLang.is_not_float} else {return "this value is not float!"}};
		};
		dt_date = function()
		{
		  var self = this;
		  this.check = function (val){return $.isDate(val);};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_date')) {return DefLang.is_not_date} else {return "this value is not date!"}};
		};

		dt_email = function()
		{
		  this.check = function (val) {if(val=='') return true;  return $.isEmail(val);};
		  this.test = function() {return true};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_email')) {return DefLang.is_not_email} else {return "this value is not email!"}};
		};
		dt_tel_num = function()
		{
		  this.check = function (val) {if(val=='') return true; return /^([0]\d{2,3}-)?(\d{7,11})(-(\d{2,4}))?$/.test(val);};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_tel_num')) {return DefLang.is_not_tel_num} else {return "this value is not telephone Number!"}};
		};
		dt_mobile = function()
		{
		  this.check = function (val) {if(val=='') return true; return /^[\d|-|\+]{11}$/.test(val);};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_mobile')) {return DefLang.is_not_mobile} else {return "this value is not mobile Number!"}};
		};
		dt_id_card = function()
		{
		  this.check = function (val) {return true};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_id_card')) {return DefLang.is_not_id_card} else {return "this value is not IDCard Number!"}};
		};
		dt_post_code = function()
		{
		  this.check = function (val) {return /^[0-9]\d{5}(?!\d)$/.test(val);};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_post_code')) {return DefLang.is_not_post_code} else {return "this value is not postCode!"}};
		};
		dt_url = function()
		{
		  this.check = function (val) {return val.match(/^(?:^(https?):\/\/[\-A-Z0-9+&@#\/%?=~_|!:,.;]*[\-A-Z0-9+&@#\/%=~_|]$)$/i);};
		  this.errorMsg = function () {if (validator_lang_exist('is_not_url')) {return DefLang.is_not_url} else {return "this value is not url!"}};
		};
		dt_file = function ()
		{
		  this.check = function (val) {return true};
		  this.test = function (val, testStr) {return true};
		};
			
		var opts;
	var Validator={
			types :{"chinese":new dt_chinese(),"string":new dt_string(),"int":new dt_int(),"date":new dt_date(), "email":new dt_email,"tel_num":new dt_tel_num(),
			    "mobile":new dt_mobile(), "id_card":new dt_id_card(), "post_code":new dt_post_code(), "url":new dt_url(), "region":new dt_string(), "file":new dt_file(),"float":new dt_float()
			  },
			/*
			 * 显示提示信息
			 */
			note:function(frm_ele){
				var required = frm_ele.attr("isrequired");
				if( required  ){
					this.showNote(frm_ele, DefLang.required);			
				} 			
			},
			
			
			
			/*
			 * 校验一个元素
			 */
			check:function(frm_ele){
				var self = this;
				var haschecker =false;
				if(!frm_ele) return true;
				if(frm_ele.attr("disabled")) return true;
				try{
					
					var required = frm_ele.attr("isrequired");
	
					if( required  ){
						haschecker= true;
						 if( $.trim(frm_ele.val() ) == '' ){
							 this.showError(frm_ele, DefLang.required);
							 return false;
						 }
					}
					
					var dataType = frm_ele.attr("dataType");
					if( dataType  && this.types[dataType] ){
						haschecker= true;
						var checker  = this.types[dataType];
						if( checker.check(  frm_ele.val() ) ){
							this.showOk(frm_ele ,"");
						}else{
							this.showError(frm_ele, checker.errorMsg());
							return false;
						}
					}
			 	
					function callBack(result,text){
						if(result){
							self.showOk(frm_ele, text);
						}else{
							self.showError(frm_ele, text);
						}
					}
					
					var fun = frm_ele.attr("fun");
					eval('result= typeof(' + fun + ') == "function"');
					if(result== true){
						haschecker= true;
						var r = eval(fun+"(frm_ele.val( ),callBack)");
						if( typeof(r)=='string' ){
							this.showError(frm_ele, r);
							return false;
						}
						
						if(!r) return false;					
						
					}
					
					var myvalidataor = frm_ele.data("validator");
					if(myvalidataor){
						haschecker= true;
						var r = myvalidataor( frm_ele.val( ) );
						if( typeof(r)=='string' ){
							this.showError(frm_ele, r);
							return false;
						}
						
						if(!r) return false;						
					}
					
					if(haschecker)
						this.showOk(frm_ele ,"");
					
					return true;
				}catch(e){
					//alert(e);
					//alert(frm_ele.attr("name"));
				}
			},
			
			
			/*
			 * 显示提示信息
			 */
			showNote:function(frm_ele,msg){
				var note_span = this.getNoteSpan(frm_ele);
				note_span.removeClass("error");
				note_span.removeClass("ok");
				note_span.addClass("note");
				note_span.text(msg);
			},	
			
			/*
			 * 显示验证正确
			 */
			showOk:function(frm_ele,msg){
		
				
				if(frm_ele.attr("dataType")!="" ||frm_ele.attr("isrequired")!=""  ||frm_ele.attr("fun")!=""){
					var note_span = this.getNoteSpan(frm_ele);
					note_span.removeClass("error");
					note_span.removeClass("note");
					if("no"==frm_ele.attr("showok")){
						note_span.empty();
					}else{
						note_span.addClass("ok");
						note_span.text(msg);
					}
				}
			},
			
			/*
			 * 显示错误
			 */
			showError:function(frm_ele,msg){
				
				var result = true;
				if(opts.onShowError && typeof(opts.onShowError  ) == 'function'){
					result = opts.onShowError(frm_ele,msg);
				}
					
				if(result ) {
					var note_span = this.getNoteSpan(frm_ele);
					note_span.removeClass("ok");
					note_span.removeClass("note");
					note_span.addClass("error");
					note_span.text(msg);
				}
				
			},
			
			
			/*
			 * 获取提示的span
			 */
			getNoteSpan:function(frm_ele){
				var note_span=undefined;
				
				var mytiper = frm_ele.data("tiper");
				if(mytiper){
					return  mytiper.addClass("tip");
				}
				
				var tiper  = frm_ele.attr("tiper");
				if(tiper && tiper!=''){
					note_span = $(tiper);
				}
				
				if(note_span && note_span.size()>0  ){
					note_span.addClass("tip");
					return note_span;
				}else{
					note_span=frm_ele.next("span.tip");
					if(note_span && note_span.size()==0  ){
						note_span = $("<span class=\"tip\"></span>").insertAfter(frm_ele);
					}
				}
				
				return note_span;
			}
	};
	
	

	/*
	 * 检查所有的表单项
	 */
	var checkAll=function(inputs){
		var result =true;
		inputs.each(function(){
			if(this){
				var el =$(this);
				var myvalidataor = el.data("validator");
				if( el.attr("isrequired") || el.attr("dataType")|| el.attr("fun") || myvalidataor ){
				
					if ( !Validator.check(  el ) ) {  el.focus(); result = false;   }
				}
			}
		});
		return result;
	};
	
	
	/**
	 * 为控件绑定事件
	 */
	var bindIptEvent = function(inputs){
		
		inputs.blur(function(){
			
			var el=  $(this);
			if("false"==el.attr("blurcheck") ) return true;
			
			if( !Validator.check( el )  ){
				el.addClass("fail");
			}else{
				el.removeClass("fail");
			}
		})
		.focus(function(){
			var el=  $(this);
			if("false"==el.attr("blurcheck") ) return true;
			Validator.note( $(this) );
		});
		
	}
	
	
	/**
	 * 检查所有控件，并返回校验结果
	 */
	$.fn.checkall=function(){
		var   inputs  = $(this).find(opts.types);
		if(checkAll(inputs)){
			$(this).attr("validate","true");
			return true;
		}else{
			$(this).attr("validate","false");
			alert(DefLang.validate_fail);
			return false;
		}
		
	};
	
	
	/**
	 * 给表单添加一个需要校验的表单
	 */
    $.fn.addinput=function(newinput){
    	 bindIptEvent(newinput);
    };
    
    
    
    /**
     * 设置某个控件的校验器
     */
    $.fn.setValidator=function(v){
    	 return this.each(function(){
    		 $(this).data("validator",v);
    	 });
    };
    
    
    
    /**
     * 设置某个控件的提示器
     */
    $.fn.setTiper=function(t){
   	 return this.each(function(){
   		 $(this).data("tiper",t);
   	 });
   };
   
    
   
   
	$.fn.validate=function(options,customFun){
		var defaults = {   
						types: 'input[type=text],input[type=password],select,textarea',
						lang:DefLang
	    				};   
	  

	 return this.each(function(){
			 
			  opts = $.extend({},defaults, options||{});   
				 
			  DefLang = opts.lang;
			  var self = this;
			  var $this =   $(this);
			  var inputs  = $this.find(opts.types);
				
			  $this.submit(function(){
				     inputs  = $(this).find(opts.types);
					if(customFun) {
						if(!customFun()) {
							alert(DefLang.validate_fail);
							return false;
						}
					}
					if(checkAll(inputs)){
						$(this).attr("validate","true");
						return true;
					}else{
						$(this).attr("validate","false");
						alert(DefLang.validate_fail);
						return false;
					}
					

				});
				

			  var inputs =  $this.find(opts.types);
			  bindIptEvent(inputs);
		});		
		
	};

	
})(jQuery);
