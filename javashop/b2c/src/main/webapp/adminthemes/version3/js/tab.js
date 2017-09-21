/**
 * Created by lishida on 2016/12/20.
 */
layui.define(['element', 'common'], function(exports) {
	"use strict";

	var mod_name = 'tab',
		$ = layui.jquery,
		element = layui.element(),
		commo = layui.common,
		globalTabIdIndex = 0,
		Tab = function() {
			this.config = {
				elem: undefined,
				closed: true, //是否包含删除按钮
				autoRefresh: false,
				contextMenu:false
			};
		};
	var ELEM = {};
	//版本号
	Tab.prototype.v = '0.1.5';
	/**
	 * 参数设置
	 * @param {Object} options
	 */
	Tab.prototype.set = function(options) {
		var that = this;
		$.extend(true, that.config, options);
		return that;
	};
	/**
	 * 初始化
	 */
	Tab.prototype.init = function() {
		var that = this;
		var _config = that.config;
		if(typeof(_config.elem) !== 'string' && typeof(_config.elem) !== 'object') {
			common.throwError('Tab error: elem参数未定义或设置出错，具体设置格式请参考文档API.');
		}
		var $container;
		if(typeof(_config.elem) === 'string') {
			$container = $('' + _config.elem + '');
		}
		if(typeof(_config.elem) === 'object') {
			$container = _config.elem;
		}
		if($container.length === 0) {
			common.throwError('Tab error:找不到elem参数配置的容器，请检查.');
		}
		var filter = $container.attr('lay-filter');
		if(filter === undefined || filter === '') {
			common.throwError('Tab error:请为elem容器设置一个lay-filter过滤器');
		}
		_config.elem = $container;
		ELEM.titleBox = $container.children('ul.layui-tab-title');
		ELEM.contentBox = $container.children('div.layui-tab-content');
		ELEM.tabFilter = filter;
		return that;
	};
	/**
	 * 查询tab是否存在，如果存在则返回索引值，不存在返回-1
	 * @param {String} 标题
	 */
	Tab.prototype.exists = function(title) {
		var that = ELEM.titleBox === undefined ? this.init() : this,
			tabIndex = -1;
		ELEM.titleBox.find('li').each(function(i, e) {
			var $cite = $(this).children('cite');
			if($cite.text() === title) {
				tabIndex = i;
			};
		});
		return tabIndex;
	};
	/**
	 * 获取tabid
	 * @param {String} 标题
	 */
	Tab.prototype.getTabId=function(title){
		var that = ELEM.titleBox === undefined ? this.init() : this,
			tabId = -1;
		ELEM.titleBox.find('li').each(function(i, e) {
			var $cite = $(this).children('cite');
			if($cite.text() === title) {
				tabId = $(this).attr('lay-id');
			};
		});
		return tabId;
	};
	/**
	 * 添加选择卡，如果选择卡存在则获取焦点
	 * @param {Object} data
	 */
	Tab.prototype.tabAdd = function(data) {
		var that = this;
		var _config = that.config;
		var tabIndex = that.exists(data.title);
		if(tabIndex === -1) {
			//设置只能同时打开多少个tab选项卡
			if(_config.maxSetting !== 'undefined') {
				var currentTabCount = _config.elem.children('ul.layui-tab-title').children('li').length;
				if(typeof _config.maxSetting === 'number') {
					if(currentTabCount === _config.maxSetting) {
						layer.msg('为了系统的流畅度，只能同时打开' + _config.maxSetting + '个选项卡。');
						return;
					}
				}
				if(typeof _config.maxSetting === 'object') {
					var max = _config.maxSetting.max || 8;
					var msg = _config.maxSetting.tipMsg || '为了系统的流畅度，只能同时打开' + max + '个选项卡。';
					if(currentTabCount === max) {
						layer.msg(msg);
						return;
					}
				}
			}
			globalTabIdIndex++;
			var content = '<iframe src="' + data.href + '" data-id="' + globalTabIdIndex + '"  ></iframe>';
			var title = '';
			if(data.icon !== undefined) {
				if(data.icon.indexOf('fa-') !== -1) {
					title += '<i class="fa ' + data.icon + '" aria-hidden="true"></i>';
				} else {
					title += '<i class="layui-icon">' + data.icon + '</i>';
				}
			}
			title += '<cite class="color-'+Math.ceil(Math.random()*8)+'" >' + data.title + '</cite>';
			if(_config.closed) {
				title += '<span></span><i class="iconfont layui-unselect layui-tab-close" data-id="' + globalTabIdIndex + '">&#xe646;</i>';
			}
			//添加tab
			element.tabAdd(ELEM.tabFilter, {
				title: title,
				content: content,
				id:new Date().getTime()
			});
			//iframe 自适应
			ELEM.contentBox.find('iframe[data-id=' + globalTabIdIndex + ']').each(function() {
				$(this).height($(window).height()-parseInt(75));
			});
			if(_config.closed) {
				//监听关闭事件
				ELEM.titleBox.find('li').children('i.layui-tab-close[data-id=' + globalTabIdIndex + ']').on('click', function() {
					element.tabDelete(ELEM.tabFilter, $(this).parent('li').attr('lay-id')).init();
					if(_config.contextMenu) {
						$(document).find('div.uiba-contextmenu').remove(); //移除右键菜单dom
					}
				});
			};
			//切换到当前打开的选项卡
			element.tabChange(ELEM.tabFilter, that.getTabId(data.title));
		} else {
			//加载到当前并刷新选项卡
			element.tabChange(ELEM.tabFilter, that.getTabId(data.title))
			var index= $('.layui-tab-title .layui-this').index();
			$(".layui-tab-content iframe")[index].contentWindow.location.reload(true);
		}
	};
	Tab.prototype.on = function(events, callback) {
		
	}
	
	/**
	 * 刷新当前页面
	 */
	Tab.prototype.refresh = function() {
		$(window.parent.document).find(".layui-tab-title li").each(function(){
        	if($(this).hasClass("layui-this")){
        		var dataId = $(this).find(".layui-tab-close").attr("data-id");
        		var src =ELEM.contentBox.find('iframe[data-id=' + dataId + ']')[0].src
        		ELEM.contentBox.find('iframe[data-id=' + dataId + ']')[0].src = src;
        		return;
        	}
        });
	}
	
	/**
	 * 关闭当前Tab
	 */
	Tab.prototype.closeCurrent = function(){
		$(window.parent.document).find(".layui-tab-title li").each(function(){
        	if($(this).hasClass("layui-this")){
        		element.tabDelete("layout-tab",$(this).attr("lay-id"));
        		return;
        	}
        });
	}
	
	/**
	 * 关闭其它Tab
	 */
	Tab.prototype.closeOther = function(){
		$(window.parent.document).find(".nav-tabs li").each(function(){
        	if(!$(this).hasClass("layui-this")){
        		element.tabDelete(ELEM.tabFilter,$(this).attr("lay-id"));
        	}
        });
	}

	var tab = new Tab();
	exports(mod_name, function(options) {
		return tab.set(options);
	});
});