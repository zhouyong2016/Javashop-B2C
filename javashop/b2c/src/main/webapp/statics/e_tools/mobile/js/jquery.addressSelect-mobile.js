/**
 * Created by Andste on 2016/10/9.
 * version 1.1.0
 *
 * 引用hammer手势插件  官网：
 * http://hammerjs.github.io/
 */

;(function($, window, document, undefined){
    var addressSelect = function (ele, opts) {
        this.$element = ele;

        this.debug = false;

        //  定义APP节点
        this.node = {};

        //  传入默认地区长度
        this.deDataLen = null;

        //  定义默认参数
        this.params = {
            apiUrl     : '/api/base/region/get-children.do',
            defaultStr : this.$element.text(), //  被调用元素默认text
            inputRegion: ['province', 'city', 'region', 'town'],    //  隐藏域input名称
            index      : 0,                 //  地区主窗体目前所处index
            region_id  : 0,		           //  当前被选中地区id
            name       : '请选择',           //  当前被选中地区名称
            navTabLen  : 0     	           //  目前navTab个数
        };

        this.defaults = {
            'apiPath' : 'http://localhost:8080/b2b2c/',
            'apiUrl'  : null,
            'jsonp'   : false,
            'debug'   : false,
            'quick'   : false,
            'deData'  : null,
            'input'   : true,
            'callBack': null
        };
        this.options  = $.extend({}, this.defaults, opts)
    };

    addressSelect.prototype = {
        //  初始化方法
        init: function () {
            this.debug = this.options.debug || false;
            //  创建css样式、加载touch插件
            this.createStyles();

            //  初始化element
            this.initElement();

            //  将默认地址数组长度赋给this.deDataLen
            if (this.options.deData) {
                this.deDataLen = this.options.deData.length;
            }

            if (this.options.apiUrl) {
                this.params.apiUrl = this.options.apiUrl;
            }
        },

        //  初始化事件绑定
        initEvent: function () {
            var _this = this;

            //  初始化APP
            _this.$element.on('click', function () {
                _this.openApp();
                if (_this.deDataLen > 1) {
                    _this.changeNavTabBottomHr();
                }
            })

            //  navTab点击切换事件
            _this.clickAppNavTab();

            //  地区主窗体手势事件
            _this.touchAppMain();

            //  地区选择事件
            _this.clickRegion();

            //  APP关闭事件
            _this.closeAppEvent();

            //  访问第一个[全国省会]地区数据
            _this.accessApi(0);
        },

        /* 事件处理区
         ============================================================================ */

        //  APP关闭事件
        closeAppEvent: function () {
            var _this = this,
                _node = _this.node;

            //  点击遮罩关闭
            _node.app_bg.on('click', function () {
                _this.closeApp();
            })

            //  点击关闭按钮关闭
            _node.app_close.on('click', function () {
                _this.closeApp();
            })
        },

        //  计算navTab个数
        computeNavTabLen: function () {
            var _this              = this,
                _node              = _this.node;
            _this.params.navTabLen = _node.app_nav.find('li').length;
        },

        //  navTab点击切换事件
        clickAppNavTab: function () {
            var _this = this,
                _node = _this.node;
            _node.app_nav.on('click', 'li', function () {
                var __this = $(this),
                    _index = __this.index();
                if (_index != _this.params.index) {
                    _this.mainSwitchTo(_index)
                }
            })
        },

        //  地区主窗体手势事件
        touchAppMain: function () {
            var _this  = this,
                _node  = _this.node;
            var hammer = new Hammer(_node.app_main[0], {});
            //  向左滑动
            hammer.on('swipeleft', function () {
                if (_this.params.index == _this.params.navTabLen - 1) {
                    return
                }
                _this.params.index++;
                _this.mainSwitchTo(_this.params.index);

                if (_this.debug) {
                    console.log('hammer --> [swipeleft]')
                }
            });
            //  向右滑动
            hammer.on('swiperight', function () {
                if (_this.params.index == 0) {
                    return
                }
                _this.params.index--;
                _this.mainSwitchTo(_this.params.index);

                if (_this.debug) {
                    console.log('hammer --> [swiperight]')
                }
            })
        },

        //  地区选择事件
        clickRegion: function () {
            var _this = this,
                _node = _this.node;
            _node.app_main.on('click', '.regions', function () {
                var __this = $(this),
                    reg_id = __this.attr('region_id');
                if (__this.is('.selected')) {
                    return
                }
                _this.params.index     = __this.closest('ul').index() + 1;
                _this.params.region_id = __this.attr('region_id');
                _this.params.name      = __this.text();
                _this.selectedRegion(__this);
                _this.accessApi(reg_id);
            })
        },

        /* 逻辑实现区
         ============================================================================ */

        //  根据nav的tab个数计算数据展示地区窗体总宽度
        computeMainWidth: function () {
            var _this     = this,
                _node     = _this.node;
            var navTabLen = _this.params.navTabLen;
            _node.app_main.css({width: navTabLen * 100 + '%'});
            _node.app_main.find('ul').css({width: 100 / navTabLen + '%'});

            if (_this.debug) {
                console.log('computeMainWidth[app_main, app_main ul]: ' + navTabLen * 100 + '%' + ', ' + 100 / navTabLen + '%')
            }
        },

        //  地区窗体展示切换
        mainSwitchTo: function (index) {
            var _this = this,
                _node = _this.node;
            _node.app_main.css({left: ( -index * 100) + '%'});
            _this.params.index = index;
            _this.changeNavTabBottomHr();
            if (_this.debug) {
                console.log('mainSwitchTo[index]: ' + index)
            }
        },

        //  处理导航底部横线宽度、位置
        changeNavTabBottomHr: function () {
            var _this    = this,
                _node    = _this.node;
            var _index   = _this.params.index,
                _navTabA = _node.app_nav.find('li').eq(_index).find('a')[0],
                _left    = _navTabA.offsetLeft,
                _width   = _navTabA.offsetWidth;
            _node.app_nav_hr.css({
                left : _left,
                width: _width
            });

            if (_this.debug) {
                console.log('changeNavTabBottomHr[left, width]: ' + _left)
            }
        },

        //  处理导航宽度【下一版本做】
        navTabWidth: function () {
            //  coding...
        },

        //  地区选中效果
        selectedRegion: function (__this) {
            __this.addClass('selected')
                  .siblings().removeClass('selected');
        },

        //  打开APP
        openApp: function () {
            var _this = this,
                _node = _this.node;
            _node.app.addClass('show');
            setTimeout(function () {
                _node.app_body.addClass('show');
                _node.app.addClass('open');
            }, 100)
        },

        //  关闭app
        closeApp: function () {
            var _this = this,
                _node = _this.node;
            _node.app.removeClass('open');
            setTimeout(function () {
                _node.app.removeClass('show');
            }, 300)
            _node.app_body.removeClass('show');
        },

        //  完成赋值
        complete: function () {
            var _this = this,
                _node = _this.node;
            var as    = _node.app_nav.find('a');
            var str   = '';
            var ret   = {};
            _this.$element.find("input[type='hidden']").remove();
            for (var i = 0, len = as.length; i < len; i++) {
                var title  = as.eq(i).attr('title');
                var id     = as.eq(i).attr('region_id');
                var region = _this.params.inputRegion[i];
                if (_this.options.input) {
                    var inputRegionId = '<input type="hidden" name="' + region + '_id' + '" value="' + id + '" >',
                        inputRegion   = '<input type="hidden" name="' + region + '" value="' + title + '" >'
                    $(inputRegionId).appendTo(_this.$element);
                    $(inputRegion).appendTo(_this.$element);
                }
                str += title;
                ret[region] = {}

                ret[region].id   = id;
                ret[region].name = title;
            }
            if (_this.options.callBack) {
                ret.string = str;
                _this.options.callBack(ret);
            }

            if (this.debug) {
                console.log(ret)
            }
        },

        /* 接口访问区
         ============================================================================ */
        //  接口访问[id: 地区id] 获取该id下的所有子地区
        accessApi: function (region_id) {
            var _this   = this;
            var options = {
                url    : _this.options.apiPath + _this.params.apiUrl + '?regionid=' + region_id,
                type   : 'GET',
                success: function (res) {
                    if (res && res instanceof Array) {
                        _this.useResone(region_id, res)
                    } else if (_this.debug) {
                        console.error('接口返回数据错误！')
                    }
                }
            };
            //  判断是否开启跨域模式
            if (_this.options.jsonp) {
                options.dataType = 'JSONP';
                options.jsonp    = 'callback'
            }
            $.ajax(options)
        },

        //  处理返回数据
        useResone: function (region_id, res) {
            var _this = this,
                _node = _this.node;
            if (res.length == 0) {
                _this.params.index--
                _this.closeApp();
                setTimeout(function () {
                    var _name = _this.params.name;
                    if (_name.length > 5) {
                        _name = _name.substr(0, 4) + '...'
                    }
                    _node.app_nav.find('li').eq(_this.params.index).find('a')
                         .html(_name)
                         .attr({
                             region_id: _this.params.region_id,
                             title    : _this.params.name
                         });
                    _this.changeNavTabBottomHr();
                    _this.complete();
                }, 100);
            } else {
                var names   = [],
                    regions = [];
                for (var i in res) {
                    names.push(res[i].local_name);
                    regions.push(res[i].region_id);
                }
                _this.removeNextAllElement();
                _this.addAElement(names, regions);
                if (_this.options.quick && res.length == 1) {
                    _node.app_main.find('ul').last().find('.regions').click();
                }
            }

            if (_this.debug) {
                console.log(res)
            }
        },

        /* 初始化、创建element
         ============================================================================ */
        //  添加一个导航和一个地区窗体[name: 选中地区名称， names: 所有地区名称, regions: 所有地区id]
        addAElement: function (names, regions) {
            var _this   = this,
                _node   = _this.node,
                _index  = _this.params.index;
            var mainReg = '';
            for (var i in names) {
                mainReg += '<li region_id="' + regions[i] + '" class="regions">' + names[i] + '<i></i></li>'
            }

            var _li = _node.app_nav.find('li');
            if (_li.eq(_index).length > 0) {
                _li.eq(_index).find('a').html('请选择');
                _node.app_main.find('ul').eq(_index).html(mainReg);
            } else {
                var mainUl = '<ul>' + mainReg + '</ul>';
                var navTab = '<li><a href="javascript: void(0);">请选择</a></li>';

                $(navTab).appendTo(_node.app_nav.find('ul'));
                $(mainUl).appendTo(_node.app_main);
            }
            var _name = _this.params.name;
            if (_name.length > 5) {
                _name = _name.substr(0, 4) + '...'
            }
            _li.eq(_index - 1).find('a')
               .html(_name)
               .attr({
                   region_id: _this.params.region_id,
                   title    : _this.params.name
               });

            //  计算navTab个数
            _this.computeNavTabLen();

            //  计算地区窗体宽度
            _this.computeMainWidth();

            //  切换到下一个地区窗体
            _this.mainSwitchTo(_index);

            //  如果有默认地址，模拟点击。
            if (_this.options.deData) {
                if (_index < _this.deDataLen) {
                    _node.app_main.find('ul').eq(_index).find("li[region_id='" + _this.options.deData[_index] + "']").click();
                    if (_index == _this.deDataLen - 1) {
                        _this.options.deData = null
                    }
                }
            }
        },

        //  移除index之后的导航和地区窗体
        removeNextAllElement: function () {
            var _this  = this,
                _node  = _this.node,
                _index = _this.params.index;
            _node.app_nav.find('li').eq(_index).nextAll().remove();
            _node.app_main.find('ul').eq(_index).nextAll().remove();
        },

        initElement: function () {
            var _this = this;
            if ($('body').find('#selectAddressApp').length > 0) {
                $('#selectAddressApp').addClass('show');
                return
            }
            var app = '<div id="selectAddressApp" class="s-a-app">'
                + ' <div class="bg-s-a-app"></div>'
                + '<div class="body-s-a-app">'
                + '<div class="title-s-a-app">'
                + '<span>地区选择<i class="close-s-a-app"></i></span>'
                + '</div>'
                + '<div class="nav-s-a-app">'
                + '<ul>'
                + '</ul>'
                + '<span class="navhr-s-a-app"></span>'
                + '</div>'
                + '<div class="main-s-a-app">'
                + '</div></div></div>';
            $(app).appendTo($('body'));

            //  缓存节点
            this.node = {
                app       : $('#selectAddressApp'),
                app_bg    : $('.bg-s-a-app'),
                app_close : $('.close-s-a-app'),
                app_nav   : $('.nav-s-a-app'),
                app_nav_hr: $('.navhr-s-a-app'),
                app_body  : $('.body-s-a-app'),
                app_main  : $('.main-s-a-app')
            };

            //  计算navTab个数
            _this.computeNavTabLen();

            //  绑定事件
            _this.initEvent();
        },

        /* 初始化创建css样式
         ============================================================================ */
        createStyles: function () {
            var style    = document.createElement('style'),
                head     = document.head || document.getElementsByTagName('head')[0];
            style.type   = 'text/css';
            var textNode = document.createTextNode(styles);
            style.appendChild(textNode);
            head.appendChild(style);
        }
    };

    $.fn.addressSelect = function (options) {
        var select = new addressSelect(this, options);
        return this.each(function () {
            select.init();
        });
    };

    var styles = ' * {margin: 0;padding: 0;-webkit-tap-highlight-color:rgba(255,0,0,0);}a {color: #1f1f1f;text-decoration: none;outline: none;}li {list-style: none;}.s-a-app, .body-s-a-app, .navhr-s-a-app, .nav-s-a-app li a, .main-s-a-app {-webkit-transition: all .3s ease-out;-moz-transition: all .3s ease-out;-ms-transition: all .3s ease-out;-o-transition: all .3s ease-out;transition: all .3s ease-out;}.s-a-app {display: none;position: fixed;left: 0;top: 0;width: 100%;height: 100%;overflow: hidden;z-index: 100000;font-size: .8rem;opacity: 0;}.s-a-app.show {display: block;}.s-a-app.open {opacity: 1;}.bg-s-a-app {position: absolute;top: 0;left: 0;width: 100%;height: 100%;background-color: rgba(0, 0, 0, 0.43);z-index: 1;}.body-s-a-app {position: absolute;left: 0;bottom: -65%;width: 100%;height: 63%;background-color: #ffffff;z-index: 2;box-shadow: 0px -4px 23px 0px rgba(75, 76, 76, 0.76);}.body-s-a-app.show {bottom: 0 }.title-s-a-app {position: relative;text-align: center;}.title-s-a-app span {position: relative;display: block;margin-top: .8rem;margin-bottom: .8rem;color: #a9a9a9;font-size: .8rem;}.close-s-a-app {position: absolute;top: 0;right: .8rem;display: block;width: .8rem;height: .8rem;background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAACS0lEQVR4Xu2a303DMBCHfZ2gGyA2gAmACeApad9gAzZAnaS8NclL2ABGYIRuALxWqowSNVKJ+sdn3/0cFPfZsf19/l1Un0Jm5D8aOb9JAlICRm4glcDIA5BegqkEUgmM3EAqAW4A6rqebjabpbV2aq1dzOfzD+4ckuOrqrqy1r40cxLRIsuyT8787AQURfFGRPfdIkT0lGXZK2dRqbE7+HdjzLSZ01q7ns1ml5z52QLKsmxO/GZ/kRgS+vC7/fzked7KcP35CHgwxtT9BZASjsA3JcBOI1tAA15V1aO1dhlDgiR8+95wjUp/XAwJ0vBBAtBJ0IAPFoCSoAUvIkBbgia8mAAtCdrwogKkJSDgxQVISUDBqwgIlYCEVxPgKwENryqAKyEGvLoAVwmx4CECzknYbrfryWTSXK7+3OJ8LjY+f+u97wLcxY7dHQ7Ng4KHJaCDdJGAhIcLOFUO7WY87vPcJPbHw0rAJQVEdM3t6f0rAQ4l8E1Ed0gJsAQ4wHeHCZUAEXCqe9RQH2ivwSSoC3BpnR0ZA5GgKsAF/szLUV2CmgAOfEwJKgJ84GNJEBcQAh9DgqgACXi0BDEBkvBICSICNOBREoIFaMIjJAQJQMBrS/AWgITXlOAlIAa8lgS2gJjwGhLYAsqy/IrVwNxvfhw6CMg3QkVRrInoottMjDbWiSTofyO0Wq1uiaj9KoyInvM8fwttS4U8v58En8Ngl0DIZof4bBIwxFNB7iklAGl7iGulBAzxVJB7SglA2h7iWikBQzwV5J5Gn4BfxZZTX6h8sgwAAAAASUVORK5CYII=") 0 0 no-repeat;background-size: .8rem .8rem;}.nav-s-a-app {position: relative;width: 100%;height: 1.8rem;}.nav-s-a-app ul {position: relative;height: 1.5rem;border-bottom: 1px solid #dddddd;margin-top: -1px;padding-left: 10px;}.navhr-s-a-app {position: absolute;margin-bottom: -1px;left: 10px;bottom: .3rem;width: 30px;height: 1px;background-color: #eb4f38;}.nav-s-a-app ul {width: 100%;overflow-y: hidden;overflow-x: scroll;}.nav-s-a-app li {float: left;padding-top: .4rem;padding-right: 1.5rem;list-style: none;font-size: .7rem;}.main-s-a-app {position: relative;left: 0;width: 100%;height: 84%;}.main-s-a-app ul {float: left;width: 100%;height: 95%;margin-bottom: 10px;overflow-x: hidden;overflow-y: scroll;-webkit-overflow-scrolling: touch;}.main-s-a-app ul li {padding-top: 10px;padding-bottom: 10px;margin-left: 10px;}.main-s-a-app ul li.selected {color: #eb4f38;}.main-s-a-app ul li.selected i {background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAACf0lEQVR4Xu2YTU4CMRiGv2+icSkm4Joj4EKHJR5AB2+gJ5AbGE6gnkA9gYgHMO5AF+IJ3IOJuDTqfGZEAgNC/4cZWtedpu/T5y22CJb/oeX5wQFwBlhOwFXAcgHcIegq4CpgOQFXAcsFcL8CrgKuApYTcBVYNgG61e0SkndCAEUvpHr+9qExL+NSGfAX/g4Ac4PQ1C/ctDesAPBWLeW+aO1lFH4Qu3DTmrvJS2FAFP6T1u4QsDS+20h0nm+2a0ttwKzwRHC12Wwdss64TBugGj6Ck2kA3cB/mtSed+eHZmQWQDcoXyBATHEiel71PiobjU6fpX6mAegKn8kK6AyfOQCv+/4ZIR6P6y2jfeynkrcrix73ulc+JA8u4uugd8Kwstl47MiuLxOHoKnwmaiAyfBcAIa3q9+rBYZ1Fd1ENTUdng9AUH5BgOLwdkUY7iYBYfpmN1gBhuEB64orApp5BvQCvw+A66NJqW8awuzwcJS/bV2KBGSNZQKYoaExCEmG56pANCgpCEmH5waQBASVOz1Lc23vAaZM0HGtlYXAPAMmJ/4fQnQ6yx1QiwwvVIFxELogLDq8NIDZZ4KYCb3AvwbAaswygvtCs1WRVVr0O+EK6DJB97VWNPhwvBIAWRPSEl6pArImpCm8NgC8JvT2/Bp4eBrXld5X8KMo8o4nq/t/3ylXgNeEaJyJBw1VGFoBzDNheqHqrzmq4bVWgMeE0Zh0hDcGgGUC4fdWEm8KPIZorwDLBNl/mXnCyIwxCmBgwk6VPO8MgHIYYk33g4ZM6PFvjANQXaDp7x0A04TTPr8zIO07ZHp9zgDThNM+vzMg7Ttken3OANOE0z6/9Qb8AEZhoVBuMVehAAAAAElFTkSuQmCC") 0 0 no-repeat;background-size: .8rem .8rem;display: inline-flex;width: .8rem;height: .8rem;margin-left: 10px;}';
})(jQuery, window, document);


/*! Hammer.JS - v2.0.8 - 2016-04-23
 * http://hammerjs.github.io/
 *
 * Copyright (c) 2016 Jorik Tangelder;
 * Licensed under the MIT license */
!function(a,b,c,d){"use strict";function e(a,b,c){return setTimeout(j(a,c),b)}function f(a,b,c){return Array.isArray(a)?(g(a,c[b],c),!0):!1}function g(a,b,c){var e;if(a)if(a.forEach)a.forEach(b,c);else if(a.length!==d)for(e=0;e<a.length;)b.call(c,a[e],e,a),e++;else for(e in a)a.hasOwnProperty(e)&&b.call(c,a[e],e,a)}function h(b,c,d){var e="DEPRECATED METHOD: "+c+"\n"+d+" AT \n";return function(){var c=new Error("get-stack-trace"),d=c&&c.stack?c.stack.replace(/^[^\(]+?[\n$]/gm,"").replace(/^\s+at\s+/gm,"").replace(/^Object.<anonymous>\s*\(/gm,"{anonymous}()@"):"Unknown Stack Trace",f=a.console&&(a.console.warn||a.console.log);return f&&f.call(a.console,e,d),b.apply(this,arguments)}}function i(a,b,c){var d,e=b.prototype;d=a.prototype=Object.create(e),d.constructor=a,d._super=e,c&&la(d,c)}function j(a,b){return function(){return a.apply(b,arguments)}}function k(a,b){return typeof a==oa?a.apply(b?b[0]||d:d,b):a}function l(a,b){return a===d?b:a}function m(a,b,c){g(q(b),function(b){a.addEventListener(b,c,!1)})}function n(a,b,c){g(q(b),function(b){a.removeEventListener(b,c,!1)})}function o(a,b){for(;a;){if(a==b)return!0;a=a.parentNode}return!1}function p(a,b){return a.indexOf(b)>-1}function q(a){return a.trim().split(/\s+/g)}function r(a,b,c){if(a.indexOf&&!c)return a.indexOf(b);for(var d=0;d<a.length;){if(c&&a[d][c]==b||!c&&a[d]===b)return d;d++}return-1}function s(a){return Array.prototype.slice.call(a,0)}function t(a,b,c){for(var d=[],e=[],f=0;f<a.length;){var g=b?a[f][b]:a[f];r(e,g)<0&&d.push(a[f]),e[f]=g,f++}return c&&(d=b?d.sort(function(a,c){return a[b]>c[b]}):d.sort()),d}function u(a,b){for(var c,e,f=b[0].toUpperCase()+b.slice(1),g=0;g<ma.length;){if(c=ma[g],e=c?c+f:b,e in a)return e;g++}return d}function v(){return ua++}function w(b){var c=b.ownerDocument||b;return c.defaultView||c.parentWindow||a}function x(a,b){var c=this;this.manager=a,this.callback=b,this.element=a.element,this.target=a.options.inputTarget,this.domHandler=function(b){k(a.options.enable,[a])&&c.handler(b)},this.init()}function y(a){var b,c=a.options.inputClass;return new(b=c?c:xa?M:ya?P:wa?R:L)(a,z)}function z(a,b,c){var d=c.pointers.length,e=c.changedPointers.length,f=b&Ea&&d-e===0,g=b&(Ga|Ha)&&d-e===0;c.isFirst=!!f,c.isFinal=!!g,f&&(a.session={}),c.eventType=b,A(a,c),a.emit("hammer.input",c),a.recognize(c),a.session.prevInput=c}function A(a,b){var c=a.session,d=b.pointers,e=d.length;c.firstInput||(c.firstInput=D(b)),e>1&&!c.firstMultiple?c.firstMultiple=D(b):1===e&&(c.firstMultiple=!1);var f=c.firstInput,g=c.firstMultiple,h=g?g.center:f.center,i=b.center=E(d);b.timeStamp=ra(),b.deltaTime=b.timeStamp-f.timeStamp,b.angle=I(h,i),b.distance=H(h,i),B(c,b),b.offsetDirection=G(b.deltaX,b.deltaY);var j=F(b.deltaTime,b.deltaX,b.deltaY);b.overallVelocityX=j.x,b.overallVelocityY=j.y,b.overallVelocity=qa(j.x)>qa(j.y)?j.x:j.y,b.scale=g?K(g.pointers,d):1,b.rotation=g?J(g.pointers,d):0,b.maxPointers=c.prevInput?b.pointers.length>c.prevInput.maxPointers?b.pointers.length:c.prevInput.maxPointers:b.pointers.length,C(c,b);var k=a.element;o(b.srcEvent.target,k)&&(k=b.srcEvent.target),b.target=k}function B(a,b){var c=b.center,d=a.offsetDelta||{},e=a.prevDelta||{},f=a.prevInput||{};b.eventType!==Ea&&f.eventType!==Ga||(e=a.prevDelta={x:f.deltaX||0,y:f.deltaY||0},d=a.offsetDelta={x:c.x,y:c.y}),b.deltaX=e.x+(c.x-d.x),b.deltaY=e.y+(c.y-d.y)}function C(a,b){var c,e,f,g,h=a.lastInterval||b,i=b.timeStamp-h.timeStamp;if(b.eventType!=Ha&&(i>Da||h.velocity===d)){var j=b.deltaX-h.deltaX,k=b.deltaY-h.deltaY,l=F(i,j,k);e=l.x,f=l.y,c=qa(l.x)>qa(l.y)?l.x:l.y,g=G(j,k),a.lastInterval=b}else c=h.velocity,e=h.velocityX,f=h.velocityY,g=h.direction;b.velocity=c,b.velocityX=e,b.velocityY=f,b.direction=g}function D(a){for(var b=[],c=0;c<a.pointers.length;)b[c]={clientX:pa(a.pointers[c].clientX),clientY:pa(a.pointers[c].clientY)},c++;return{timeStamp:ra(),pointers:b,center:E(b),deltaX:a.deltaX,deltaY:a.deltaY}}function E(a){var b=a.length;if(1===b)return{x:pa(a[0].clientX),y:pa(a[0].clientY)};for(var c=0,d=0,e=0;b>e;)c+=a[e].clientX,d+=a[e].clientY,e++;return{x:pa(c/b),y:pa(d/b)}}function F(a,b,c){return{x:b/a||0,y:c/a||0}}function G(a,b){return a===b?Ia:qa(a)>=qa(b)?0>a?Ja:Ka:0>b?La:Ma}function H(a,b,c){c||(c=Qa);var d=b[c[0]]-a[c[0]],e=b[c[1]]-a[c[1]];return Math.sqrt(d*d+e*e)}function I(a,b,c){c||(c=Qa);var d=b[c[0]]-a[c[0]],e=b[c[1]]-a[c[1]];return 180*Math.atan2(e,d)/Math.PI}function J(a,b){return I(b[1],b[0],Ra)+I(a[1],a[0],Ra)}function K(a,b){return H(b[0],b[1],Ra)/H(a[0],a[1],Ra)}function L(){this.evEl=Ta,this.evWin=Ua,this.pressed=!1,x.apply(this,arguments)}function M(){this.evEl=Xa,this.evWin=Ya,x.apply(this,arguments),this.store=this.manager.session.pointerEvents=[]}function N(){this.evTarget=$a,this.evWin=_a,this.started=!1,x.apply(this,arguments)}function O(a,b){var c=s(a.touches),d=s(a.changedTouches);return b&(Ga|Ha)&&(c=t(c.concat(d),"identifier",!0)),[c,d]}function P(){this.evTarget=bb,this.targetIds={},x.apply(this,arguments)}function Q(a,b){var c=s(a.touches),d=this.targetIds;if(b&(Ea|Fa)&&1===c.length)return d[c[0].identifier]=!0,[c,c];var e,f,g=s(a.changedTouches),h=[],i=this.target;if(f=c.filter(function(a){return o(a.target,i)}),b===Ea)for(e=0;e<f.length;)d[f[e].identifier]=!0,e++;for(e=0;e<g.length;)d[g[e].identifier]&&h.push(g[e]),b&(Ga|Ha)&&delete d[g[e].identifier],e++;return h.length?[t(f.concat(h),"identifier",!0),h]:void 0}function R(){x.apply(this,arguments);var a=j(this.handler,this);this.touch=new P(this.manager,a),this.mouse=new L(this.manager,a),this.primaryTouch=null,this.lastTouches=[]}function S(a,b){a&Ea?(this.primaryTouch=b.changedPointers[0].identifier,T.call(this,b)):a&(Ga|Ha)&&T.call(this,b)}function T(a){var b=a.changedPointers[0];if(b.identifier===this.primaryTouch){var c={x:b.clientX,y:b.clientY};this.lastTouches.push(c);var d=this.lastTouches,e=function(){var a=d.indexOf(c);a>-1&&d.splice(a,1)};setTimeout(e,cb)}}function U(a){for(var b=a.srcEvent.clientX,c=a.srcEvent.clientY,d=0;d<this.lastTouches.length;d++){var e=this.lastTouches[d],f=Math.abs(b-e.x),g=Math.abs(c-e.y);if(db>=f&&db>=g)return!0}return!1}function V(a,b){this.manager=a,this.set(b)}function W(a){if(p(a,jb))return jb;var b=p(a,kb),c=p(a,lb);return b&&c?jb:b||c?b?kb:lb:p(a,ib)?ib:hb}function X(){if(!fb)return!1;var b={},c=a.CSS&&a.CSS.supports;return["auto","manipulation","pan-y","pan-x","pan-x pan-y","none"].forEach(function(d){b[d]=c?a.CSS.supports("touch-action",d):!0}),b}function Y(a){this.options=la({},this.defaults,a||{}),this.id=v(),this.manager=null,this.options.enable=l(this.options.enable,!0),this.state=nb,this.simultaneous={},this.requireFail=[]}function Z(a){return a&sb?"cancel":a&qb?"end":a&pb?"move":a&ob?"start":""}function $(a){return a==Ma?"down":a==La?"up":a==Ja?"left":a==Ka?"right":""}function _(a,b){var c=b.manager;return c?c.get(a):a}function aa(){Y.apply(this,arguments)}function ba(){aa.apply(this,arguments),this.pX=null,this.pY=null}function ca(){aa.apply(this,arguments)}function da(){Y.apply(this,arguments),this._timer=null,this._input=null}function ea(){aa.apply(this,arguments)}function fa(){aa.apply(this,arguments)}function ga(){Y.apply(this,arguments),this.pTime=!1,this.pCenter=!1,this._timer=null,this._input=null,this.count=0}function ha(a,b){return b=b||{},b.recognizers=l(b.recognizers,ha.defaults.preset),new ia(a,b)}function ia(a,b){this.options=la({},ha.defaults,b||{}),this.options.inputTarget=this.options.inputTarget||a,this.handlers={},this.session={},this.recognizers=[],this.oldCssProps={},this.element=a,this.input=y(this),this.touchAction=new V(this,this.options.touchAction),ja(this,!0),g(this.options.recognizers,function(a){var b=this.add(new a[0](a[1]));a[2]&&b.recognizeWith(a[2]),a[3]&&b.requireFailure(a[3])},this)}function ja(a,b){var c=a.element;if(c.style){var d;g(a.options.cssProps,function(e,f){d=u(c.style,f),b?(a.oldCssProps[d]=c.style[d],c.style[d]=e):c.style[d]=a.oldCssProps[d]||""}),b||(a.oldCssProps={})}}function ka(a,c){var d=b.createEvent("Event");d.initEvent(a,!0,!0),d.gesture=c,c.target.dispatchEvent(d)}var la,ma=["","webkit","Moz","MS","ms","o"],na=b.createElement("div"),oa="function",pa=Math.round,qa=Math.abs,ra=Date.now;la="function"!=typeof Object.assign?function(a){if(a===d||null===a)throw new TypeError("Cannot convert undefined or null to object");for(var b=Object(a),c=1;c<arguments.length;c++){var e=arguments[c];if(e!==d&&null!==e)for(var f in e)e.hasOwnProperty(f)&&(b[f]=e[f])}return b}:Object.assign;var sa=h(function(a,b,c){for(var e=Object.keys(b),f=0;f<e.length;)(!c||c&&a[e[f]]===d)&&(a[e[f]]=b[e[f]]),f++;return a},"extend","Use `assign`."),ta=h(function(a,b){return sa(a,b,!0)},"merge","Use `assign`."),ua=1,va=/mobile|tablet|ip(ad|hone|od)|android/i,wa="ontouchstart"in a,xa=u(a,"PointerEvent")!==d,ya=wa&&va.test(navigator.userAgent),za="touch",Aa="pen",Ba="mouse",Ca="kinect",Da=25,Ea=1,Fa=2,Ga=4,Ha=8,Ia=1,Ja=2,Ka=4,La=8,Ma=16,Na=Ja|Ka,Oa=La|Ma,Pa=Na|Oa,Qa=["x","y"],Ra=["clientX","clientY"];x.prototype={handler:function(){},init:function(){this.evEl&&m(this.element,this.evEl,this.domHandler),this.evTarget&&m(this.target,this.evTarget,this.domHandler),this.evWin&&m(w(this.element),this.evWin,this.domHandler)},destroy:function(){this.evEl&&n(this.element,this.evEl,this.domHandler),this.evTarget&&n(this.target,this.evTarget,this.domHandler),this.evWin&&n(w(this.element),this.evWin,this.domHandler)}};var Sa={mousedown:Ea,mousemove:Fa,mouseup:Ga},Ta="mousedown",Ua="mousemove mouseup";i(L,x,{handler:function(a){var b=Sa[a.type];b&Ea&&0===a.button&&(this.pressed=!0),b&Fa&&1!==a.which&&(b=Ga),this.pressed&&(b&Ga&&(this.pressed=!1),this.callback(this.manager,b,{pointers:[a],changedPointers:[a],pointerType:Ba,srcEvent:a}))}});var Va={pointerdown:Ea,pointermove:Fa,pointerup:Ga,pointercancel:Ha,pointerout:Ha},Wa={2:za,3:Aa,4:Ba,5:Ca},Xa="pointerdown",Ya="pointermove pointerup pointercancel";a.MSPointerEvent&&!a.PointerEvent&&(Xa="MSPointerDown",Ya="MSPointerMove MSPointerUp MSPointerCancel"),i(M,x,{handler:function(a){var b=this.store,c=!1,d=a.type.toLowerCase().replace("ms",""),e=Va[d],f=Wa[a.pointerType]||a.pointerType,g=f==za,h=r(b,a.pointerId,"pointerId");e&Ea&&(0===a.button||g)?0>h&&(b.push(a),h=b.length-1):e&(Ga|Ha)&&(c=!0),0>h||(b[h]=a,this.callback(this.manager,e,{pointers:b,changedPointers:[a],pointerType:f,srcEvent:a}),c&&b.splice(h,1))}});var Za={touchstart:Ea,touchmove:Fa,touchend:Ga,touchcancel:Ha},$a="touchstart",_a="touchstart touchmove touchend touchcancel";i(N,x,{handler:function(a){var b=Za[a.type];if(b===Ea&&(this.started=!0),this.started){var c=O.call(this,a,b);b&(Ga|Ha)&&c[0].length-c[1].length===0&&(this.started=!1),this.callback(this.manager,b,{pointers:c[0],changedPointers:c[1],pointerType:za,srcEvent:a})}}});var ab={touchstart:Ea,touchmove:Fa,touchend:Ga,touchcancel:Ha},bb="touchstart touchmove touchend touchcancel";i(P,x,{handler:function(a){var b=ab[a.type],c=Q.call(this,a,b);c&&this.callback(this.manager,b,{pointers:c[0],changedPointers:c[1],pointerType:za,srcEvent:a})}});var cb=2500,db=25;i(R,x,{handler:function(a,b,c){var d=c.pointerType==za,e=c.pointerType==Ba;if(!(e&&c.sourceCapabilities&&c.sourceCapabilities.firesTouchEvents)){if(d)S.call(this,b,c);else if(e&&U.call(this,c))return;this.callback(a,b,c)}},destroy:function(){this.touch.destroy(),this.mouse.destroy()}});var eb=u(na.style,"touchAction"),fb=eb!==d,gb="compute",hb="auto",ib="manipulation",jb="none",kb="pan-x",lb="pan-y",mb=X();V.prototype={set:function(a){a==gb&&(a=this.compute()),fb&&this.manager.element.style&&mb[a]&&(this.manager.element.style[eb]=a),this.actions=a.toLowerCase().trim()},update:function(){this.set(this.manager.options.touchAction)},compute:function(){var a=[];return g(this.manager.recognizers,function(b){k(b.options.enable,[b])&&(a=a.concat(b.getTouchAction()))}),W(a.join(" "))},preventDefaults:function(a){var b=a.srcEvent,c=a.offsetDirection;if(this.manager.session.prevented)return void b.preventDefault();var d=this.actions,e=p(d,jb)&&!mb[jb],f=p(d,lb)&&!mb[lb],g=p(d,kb)&&!mb[kb];if(e){var h=1===a.pointers.length,i=a.distance<2,j=a.deltaTime<250;if(h&&i&&j)return}return g&&f?void 0:e||f&&c&Na||g&&c&Oa?this.preventSrc(b):void 0},preventSrc:function(a){this.manager.session.prevented=!0,a.preventDefault()}};var nb=1,ob=2,pb=4,qb=8,rb=qb,sb=16,tb=32;Y.prototype={defaults:{},set:function(a){return la(this.options,a),this.manager&&this.manager.touchAction.update(),this},recognizeWith:function(a){if(f(a,"recognizeWith",this))return this;var b=this.simultaneous;return a=_(a,this),b[a.id]||(b[a.id]=a,a.recognizeWith(this)),this},dropRecognizeWith:function(a){return f(a,"dropRecognizeWith",this)?this:(a=_(a,this),delete this.simultaneous[a.id],this)},requireFailure:function(a){if(f(a,"requireFailure",this))return this;var b=this.requireFail;return a=_(a,this),-1===r(b,a)&&(b.push(a),a.requireFailure(this)),this},dropRequireFailure:function(a){if(f(a,"dropRequireFailure",this))return this;a=_(a,this);var b=r(this.requireFail,a);return b>-1&&this.requireFail.splice(b,1),this},hasRequireFailures:function(){return this.requireFail.length>0},canRecognizeWith:function(a){return!!this.simultaneous[a.id]},emit:function(a){function b(b){c.manager.emit(b,a)}var c=this,d=this.state;qb>d&&b(c.options.event+Z(d)),b(c.options.event),a.additionalEvent&&b(a.additionalEvent),d>=qb&&b(c.options.event+Z(d))},tryEmit:function(a){return this.canEmit()?this.emit(a):void(this.state=tb)},canEmit:function(){for(var a=0;a<this.requireFail.length;){if(!(this.requireFail[a].state&(tb|nb)))return!1;a++}return!0},recognize:function(a){var b=la({},a);return k(this.options.enable,[this,b])?(this.state&(rb|sb|tb)&&(this.state=nb),this.state=this.process(b),void(this.state&(ob|pb|qb|sb)&&this.tryEmit(b))):(this.reset(),void(this.state=tb))},process:function(a){},getTouchAction:function(){},reset:function(){}},i(aa,Y,{defaults:{pointers:1},attrTest:function(a){var b=this.options.pointers;return 0===b||a.pointers.length===b},process:function(a){var b=this.state,c=a.eventType,d=b&(ob|pb),e=this.attrTest(a);return d&&(c&Ha||!e)?b|sb:d||e?c&Ga?b|qb:b&ob?b|pb:ob:tb}}),i(ba,aa,{defaults:{event:"pan",threshold:10,pointers:1,direction:Pa},getTouchAction:function(){var a=this.options.direction,b=[];return a&Na&&b.push(lb),a&Oa&&b.push(kb),b},directionTest:function(a){var b=this.options,c=!0,d=a.distance,e=a.direction,f=a.deltaX,g=a.deltaY;return e&b.direction||(b.direction&Na?(e=0===f?Ia:0>f?Ja:Ka,c=f!=this.pX,d=Math.abs(a.deltaX)):(e=0===g?Ia:0>g?La:Ma,c=g!=this.pY,d=Math.abs(a.deltaY))),a.direction=e,c&&d>b.threshold&&e&b.direction},attrTest:function(a){return aa.prototype.attrTest.call(this,a)&&(this.state&ob||!(this.state&ob)&&this.directionTest(a))},emit:function(a){this.pX=a.deltaX,this.pY=a.deltaY;var b=$(a.direction);b&&(a.additionalEvent=this.options.event+b),this._super.emit.call(this,a)}}),i(ca,aa,{defaults:{event:"pinch",threshold:0,pointers:2},getTouchAction:function(){return[jb]},attrTest:function(a){return this._super.attrTest.call(this,a)&&(Math.abs(a.scale-1)>this.options.threshold||this.state&ob)},emit:function(a){if(1!==a.scale){var b=a.scale<1?"in":"out";a.additionalEvent=this.options.event+b}this._super.emit.call(this,a)}}),i(da,Y,{defaults:{event:"press",pointers:1,time:251,threshold:9},getTouchAction:function(){return[hb]},process:function(a){var b=this.options,c=a.pointers.length===b.pointers,d=a.distance<b.threshold,f=a.deltaTime>b.time;if(this._input=a,!d||!c||a.eventType&(Ga|Ha)&&!f)this.reset();else if(a.eventType&Ea)this.reset(),this._timer=e(function(){this.state=rb,this.tryEmit()},b.time,this);else if(a.eventType&Ga)return rb;return tb},reset:function(){clearTimeout(this._timer)},emit:function(a){this.state===rb&&(a&&a.eventType&Ga?this.manager.emit(this.options.event+"up",a):(this._input.timeStamp=ra(),this.manager.emit(this.options.event,this._input)))}}),i(ea,aa,{defaults:{event:"rotate",threshold:0,pointers:2},getTouchAction:function(){return[jb]},attrTest:function(a){return this._super.attrTest.call(this,a)&&(Math.abs(a.rotation)>this.options.threshold||this.state&ob)}}),i(fa,aa,{defaults:{event:"swipe",threshold:10,velocity:.3,direction:Na|Oa,pointers:1},getTouchAction:function(){return ba.prototype.getTouchAction.call(this)},attrTest:function(a){var b,c=this.options.direction;return c&(Na|Oa)?b=a.overallVelocity:c&Na?b=a.overallVelocityX:c&Oa&&(b=a.overallVelocityY),this._super.attrTest.call(this,a)&&c&a.offsetDirection&&a.distance>this.options.threshold&&a.maxPointers==this.options.pointers&&qa(b)>this.options.velocity&&a.eventType&Ga},emit:function(a){var b=$(a.offsetDirection);b&&this.manager.emit(this.options.event+b,a),this.manager.emit(this.options.event,a)}}),i(ga,Y,{defaults:{event:"tap",pointers:1,taps:1,interval:300,time:250,threshold:9,posThreshold:10},getTouchAction:function(){return[ib]},process:function(a){var b=this.options,c=a.pointers.length===b.pointers,d=a.distance<b.threshold,f=a.deltaTime<b.time;if(this.reset(),a.eventType&Ea&&0===this.count)return this.failTimeout();if(d&&f&&c){if(a.eventType!=Ga)return this.failTimeout();var g=this.pTime?a.timeStamp-this.pTime<b.interval:!0,h=!this.pCenter||H(this.pCenter,a.center)<b.posThreshold;this.pTime=a.timeStamp,this.pCenter=a.center,h&&g?this.count+=1:this.count=1,this._input=a;var i=this.count%b.taps;if(0===i)return this.hasRequireFailures()?(this._timer=e(function(){this.state=rb,this.tryEmit()},b.interval,this),ob):rb}return tb},failTimeout:function(){return this._timer=e(function(){this.state=tb},this.options.interval,this),tb},reset:function(){clearTimeout(this._timer)},emit:function(){this.state==rb&&(this._input.tapCount=this.count,this.manager.emit(this.options.event,this._input))}}),ha.VERSION="2.0.8",ha.defaults={domEvents:!1,touchAction:gb,enable:!0,inputTarget:null,inputClass:null,preset:[[ea,{enable:!1}],[ca,{enable:!1},["rotate"]],[fa,{direction:Na}],[ba,{direction:Na},["swipe"]],[ga],[ga,{event:"doubletap",taps:2},["tap"]],[da]],cssProps:{userSelect:"none",touchSelect:"none",touchCallout:"none",contentZooming:"none",userDrag:"none",tapHighlightColor:"rgba(0,0,0,0)"}};var ub=1,vb=2;ia.prototype={set:function(a){return la(this.options,a),a.touchAction&&this.touchAction.update(),a.inputTarget&&(this.input.destroy(),this.input.target=a.inputTarget,this.input.init()),this},stop:function(a){this.session.stopped=a?vb:ub},recognize:function(a){var b=this.session;if(!b.stopped){this.touchAction.preventDefaults(a);var c,d=this.recognizers,e=b.curRecognizer;(!e||e&&e.state&rb)&&(e=b.curRecognizer=null);for(var f=0;f<d.length;)c=d[f],b.stopped===vb||e&&c!=e&&!c.canRecognizeWith(e)?c.reset():c.recognize(a),!e&&c.state&(ob|pb|qb)&&(e=b.curRecognizer=c),f++}},get:function(a){if(a instanceof Y)return a;for(var b=this.recognizers,c=0;c<b.length;c++)if(b[c].options.event==a)return b[c];return null},add:function(a){if(f(a,"add",this))return this;var b=this.get(a.options.event);return b&&this.remove(b),this.recognizers.push(a),a.manager=this,this.touchAction.update(),a},remove:function(a){if(f(a,"remove",this))return this;if(a=this.get(a)){var b=this.recognizers,c=r(b,a);-1!==c&&(b.splice(c,1),this.touchAction.update())}return this},on:function(a,b){if(a!==d&&b!==d){var c=this.handlers;return g(q(a),function(a){c[a]=c[a]||[],c[a].push(b)}),this}},off:function(a,b){if(a!==d){var c=this.handlers;return g(q(a),function(a){b?c[a]&&c[a].splice(r(c[a],b),1):delete c[a]}),this}},emit:function(a,b){this.options.domEvents&&ka(a,b);var c=this.handlers[a]&&this.handlers[a].slice();if(c&&c.length){b.type=a,b.preventDefault=function(){b.srcEvent.preventDefault()};for(var d=0;d<c.length;)c[d](b),d++}},destroy:function(){this.element&&ja(this,!1),this.handlers={},this.session={},this.input.destroy(),this.element=null}},la(ha,{INPUT_START:Ea,INPUT_MOVE:Fa,INPUT_END:Ga,INPUT_CANCEL:Ha,STATE_POSSIBLE:nb,STATE_BEGAN:ob,STATE_CHANGED:pb,STATE_ENDED:qb,STATE_RECOGNIZED:rb,STATE_CANCELLED:sb,STATE_FAILED:tb,DIRECTION_NONE:Ia,DIRECTION_LEFT:Ja,DIRECTION_RIGHT:Ka,DIRECTION_UP:La,DIRECTION_DOWN:Ma,DIRECTION_HORIZONTAL:Na,DIRECTION_VERTICAL:Oa,DIRECTION_ALL:Pa,Manager:ia,Input:x,TouchAction:V,TouchInput:P,MouseInput:L,PointerEventInput:M,TouchMouseInput:R,SingleTouchInput:N,Recognizer:Y,AttrRecognizer:aa,Tap:ga,Pan:ba,Swipe:fa,Pinch:ca,Rotate:ea,Press:da,on:m,off:n,each:g,merge:ta,extend:sa,assign:la,inherit:i,bindFn:j,prefixed:u});var wb="undefined"!=typeof a?a:"undefined"!=typeof self?self:{};wb.Hammer=ha,"function"==typeof define&&define.amd?define(function(){return ha}):"undefined"!=typeof module&&module.exports?module.exports=ha:a[c]=ha}(window,document,"Hammer");