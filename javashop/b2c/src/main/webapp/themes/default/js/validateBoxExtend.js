(function($) {
	/** 
	 * jQuery EasyUI 1.4 --- 刘帅
	 *  
	 * Copyright (c) 2009-2015 RCM 
	 * 
	 * 新增 validatebox 校验规则 
	 * 
	 */
	$.extend($.fn.validatebox.defaults.rules, {
		//整数
		checkNum : {
			validator : function(value, param) {
				return /^([0-9]+)$/.test(value);
			},
			message : '请输入整数'
		},
		//浮点型小数
		checkFloat : {
			validator : function(value, param) {
				return /^[+|-]?([0-9]+\.[0-9]+)|[0-9]+$/.test(value);
			},
			message : '请输入合法数字'
		},
		//通用字符
		generalText : {
			validator : function(value, param) {
				return /^.{0,58}$/.test(value);
			},
			message : '不能超过58个字符'
		},
		//邮编
		zip : {
			validator : function(value, param) {
				return /^([0-9]{6})$/.test(value);
			},
			message : '请输入正确的邮编'
		},
		
	});
})(jQuery);