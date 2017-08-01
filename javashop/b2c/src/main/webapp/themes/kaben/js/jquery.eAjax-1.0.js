/*!

 @Name jquery-eajax v1.0
 @Author Sylow
 @Site http://www.javamall.com.cn/
 @License Commercial
 @version v1.0 2016-02-23
 */

//是否开启跨域脚本
var jsonp = true;

(function($) {
	
	
	// 如果开启jsonp
	if(jsonp) {
		
		// 备份jquery的ajax方法
		var _ajax = $.ajax;
		var _ajaxSubmit = $.fn.ajaxSubmit;
		
		// 重写jquery的ajax方法
		$.ajax = function(opt) {
			// 备份opt中error和success方法
			var fn = {
				error : function(XMLHttpRequest, textStatus, errorThrown) {
				},
				success : function(data, textStatus) {
				}
			}
			if (opt.error) {
				fn.error = opt.error;
			}
			if (opt.success) {
				fn.success = opt.success;
			}
			
			// 扩展增强处理
			var _opt = $.extend(opt, {
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					// 错误方法增强处理
					fn.error(XMLHttpRequest, textStatus, errorThrown);
				},
				success : function(data, textStatus) {
					// 成功回调方法增强处理
					fn.success(data, textStatus);
				}
			});
			
			if(_opt.dataType != "html") {
				_opt = $.extend(_opt, {
					"dataType" : "JSONP",
					"jsonp" : "callback"
				});
			}
			_opt.url = getUrl(_opt.url);
			//alert(_opt.url);
			return _ajax(_opt);
		}
		
		$.fn.ajaxSubmit = function(opt) {
			var fn = {
				error : function(XMLHttpRequest, textStatus, errorThrown) {
				},
				success : function(data, textStatus) {
				}
			}
			if (opt.error) {
				fn.error = opt.error;
			}
			if (opt.success) {
				fn.success = opt.success;
			}

			// 扩展增强处理
			var _opt = $.extend(opt, {
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					// 错误方法增强处理
					fn.error(XMLHttpRequest, textStatus, errorThrown);
				},
				success : function(data, textStatus) {
					// 成功回调方法增强处理
					fn.success(data, textStatus);
				}
			});
			
			if(_opt.dataType != "html") {
				_opt = $.extend(_opt, {
					"dataType" : "JSONP",
					"jsonp" : "callback"
				});
			}

			_opt.url = getUrl(_opt.url);
			//alert(_opt.url);
			_ajaxSubmit.apply(this,[_opt] );
		}
	}
	
	//获取完整url（带api服务器地址）
	function getUrl(url) {
		//alert(URL_SETTING["/api/shop/commentApi/add.do"]);
		
		var arr = url.split('?');
		var noParamUrl = arr[0];
		var param = arr[1];
		var arr2 = noParamUrl.split(ctx);
		var noCtxUrl = arr2[1];
		var serverUrl;
		try {
			serverUrl = URL_SETTING[noCtxUrl];
		} catch(e) {
			//alert("错误： " + url);
		}
		if(serverUrl) {
			url = serverUrl + ctx + noCtxUrl + "?" + param;
		}
		return url;
	}
	

})(jQuery);
