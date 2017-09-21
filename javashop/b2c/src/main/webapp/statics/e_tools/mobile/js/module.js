/**
 * Created by Andste on 2016/11/15.
 */

var Module = function(){};
function isEmptyObject(e) {var t;for (t in e){return !1;}return !0;}

/**
 * 倒计时模块
 * @param opts
 */
Module.prototype.countDown = function (opts) {
    var isObj = opts.elem ? true : false;
    var elem, count = 60, beforeStr, afterStr, complete = '重新发送';
    if(isObj){
        var opts  = opts || {};
        elem      = opts.elem;
        count     = opts.count || 60;
        beforeStr = opts.beforeStr || null;
        afterStr  = opts.afterStr  || null;
        complete  = opts.complete || '重新发送';
    }else {
        elem = arguments[0];
        if(arguments.length == 2 && arguments[1] > 0){count = arguments[1]}
    }
    if(!elem){throw error('没有传入任何element')}
    var ele      = elem[0],
        tagName  = ele.tagName;
    var type     = tagName == 'INPUT';
    var button = (tagName == 'INPUT') || (tagName == 'BUTTON');
    var timer = setInterval(function () {
        if (count == 1) {_countTo0();return}
        count--;
        if(isObj && beforeStr && afterStr){
            type ? ele.value = beforeStr + count + afterStr : ele.innerHTML = beforeStr + count + afterStr;
        }else {
            type ? ele.value = "已发送 " + count + "秒" : ele.innerHTML = "已发送 " + count + "秒";
        }
        ele.disabled = true;
    }, 1000)

    button ? elem.addClass('disabled').blur() : null;

    function _countTo0() {
        clearTimeout(timer);
        type ? ele.value = complete : ele.innerHTML = complete;
        if(button){elem.removeClass('disabled');ele.disabled = false;}
        typeof opts.callBack == 'function' ? opts.callBack() : null;
    }
}

/**
 * 加载遮罩层
 * @type {{open, close}}
 */
Module.prototype.layerLoading = (function () {
    var index;
    function _open(callback) {
        index = layer.open({
            type: 2,
            shadeClose: false
        });
        if(callback && typeof callback == 'function'){
            callback();
        }
    }

    function _close(callback, time) {
        if (callback && typeof callback == 'function') {
            if (time && !isNaN(time)) {
                setTimeout(function () {
                    layer.close(index);
                    callback()
                }, time)
            } else {
                layer.close(index);
                callback();
            }
        }else {
            layer.close(index);
        }
    }

    return {
        open: function (_callback) {
            return _open(_callback)
        },
        close: function (_callback, time) {
            return _close(_callback, time)
        }
    }
})()

/**
 * loading【不晃眼版】
 * @type {{open, close}}
 */
Module.prototype.loading = (function () {
    function _open() {

        return
    }

    function _close() {

        return
    }

    return {
        open: function () {
            return _open()
        },
        close: function () {
            return _close()
        }
    }
})()

/**
 * 确认选项
 * @param ops
 */
Module.prototype.layerConfirm = function (ops) {
    var options = ops || {};
    layer.open({
        content: options.content || '请确认您的操作',
        btn: options.btn || ['确定', '取消'],
        success: options.success || null,
        yes: function(index){
            typeof options.yes == 'function' ? options.yes() : null;
            layer.close(index);
        },
        no : function () {
            typeof options.no == 'function' ? options.no() : null;
        }
    });
}

/**
 * 错误、成功信息提示
 * @type {{error, success}}
 */
Module.prototype.message = (function () {
    var _background = '#42aa43';
    function _error(msg, callback) {_background = '#ff4351';showMessage(msg, callback);}
    function _success(msg, callback) {_background = '#42aa43';showMessage(msg, callback);}
    function showMessage(msg, callback) {
        layer.open({
            title: [
                '提示信息',
                'background-color: '+ _background +'; color:#fff;'
            ],
            shadeClose: false,
            btn: '确定',
            content: msg || '',
            yes: function (index) {
                if(callback && typeof callback === 'function'){callback()}
                layer.close(index);
            }
        });
    }

    return {
        error: function (_msg, _callback) {
            return _error(_msg, _callback);
        },
        success: function (_msg, _callback) {
            return _success(_msg, _callback);
        }
    }
})()

/**
 * 用户协议
 * @type {{open, close}}
 */
Module.prototype.userAgreement = (function () {
    var _html;
    var uaLayer;
    function _open() {
        if(!_html){
            $.ajax({
                url : ctx + '/statics/e_tools/mobile/user-agreement.txt',
                type: 'GET',
                success: function (__html) {
                    _html = __html;
                    __open();
                }
            })
        }else {
            __open();
        }

        function __open() {
            uaLayer = layer.open({
                type   : 1,
                content: _html,
                anim   : 'up',
                style  : 'position:fixed; left:0; top:0; width:100%; height:100%; border: none; -webkit-animation-duration: .5s; animation-duration: .5s;overflow-y: scroll;',
                success: function () {
                    setTimeout(function () {
                        $('<i class="close-user-agreement"></i>').appendTo($('body'));
                        $('.close-user-agreement').on('tap', function () {
                            _close();
                        })
                    }, 600)
                }
            });
        }
    }

    function _close() {
        $('.close-user-agreement').remove();
        layer.close(uaLayer)
    }

    return {
        open: function () {
            return _open()
        },
        close: function () {
            return _close()
        }
    }
})()

/**
 * 移除一些基本的emoji表情
 * @param str
 * @returns {string|*|void|XML}
 */
Module.prototype.removeEmojiCode = function (str) {
    var ranges = [
        '\ud83c[\udf00-\udfff]',
        '\ud83d[\udc00-\ude4f]',
        '\ud83d[\ude80-\udeff]'
    ];
    return str .replace(new RegExp(ranges.join('|'), 'g'), '');
}

/**
 * 正则表达式
 * @type {{mobile: RegExp, email: RegExp, password: RegExp, integer: RegExp, price: RegExp}}
 */
Module.prototype.regExp = {
    //  手机号
    mobile  : /^0?(13[0-9]|15[0-9]|18[0-9]|14[0-9]|17[0-9])[0-9]{8}$/,
    //  电子邮箱
    email   : /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
    //  密码【6-20位】
    password: /^[\@A-Za-z0-9\!\#\$\%\^\&\*\.\~\,]{6,20}$/,
    //  正整数【不包含0】
    integer : /^[0-9]+$/,
    //  price
    price   : /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/
}

/**
 * 获取url中的传参
 * @param str
 * @returns {string}
 */
Module.prototype.getQueryString = function (str) {
    var reg = new RegExp("(^|&)" + str + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    var context = '';
    if(r != null){context = r[2]}
    reg = null;
    r = null;
    return context == null || context == "" || context == "undefined" ? "" : decodeURI(context);
}


/* 控件
 ============================================================================ */
/**
 * 导航栏控件
 * @type {{init}}
 */
Module.prototype.navigator = (function () {
    var _navigator;
    var _title;
    var _left;
    var _right;
    function _init(opts) {
        var options = opts || {}
        if(typeof arguments[0] == 'string'){options = {}; options.title = arguments[0]}
        var left  = options.left || {}, right = options.right || {};
        var leftDom = '', rightDom = '';
        var leftStyle = left.style || 'dark';
        leftDom = left.element || ('<div class="item '+ leftStyle +'"></div>');
        rightDom = right.element || ('<div class="item">'+ right.text +'</div>')
        var titleText   = options.title || '未定义标题';
        if(options.left == false){leftDom = ''}
        if(isEmptyObject(right) || options.right == false){rightDom = ''}
        var navigatorDom = '<div id="navigator"><div id="nav-left">'+ leftDom +'</div><div id="nav-middle">'+ titleText +'</div><div id="nav-right">'+ rightDom +'</div></div>';
        $(navigatorDom).insertBefore($('body').find('*').eq(0));
        _navigator = $('#navigator');_title = $('#nav-middle');_left = $('#nav-left') ;_right = $('#nav-right');
        if(options.backgroundColor){_navigator.css({backgroundColor: options.backgroundColor})}
        if(options.titleColor){_title.css({color: options.titleColor})}
        _left.on('tap', function () {if(left.click){left.click();return false}else {window.history.back();return false}});
        if(right.click){_right.on('tap', function () {right.click();return false})}
        if(right.textColor){_right.find('.item').css({color: right.textColor})}
    }

    function __right(opts) {
        var options = opts || {};
        options.element ? _right.html(options.element) : null;
        options.click ? _right.on('tap', function () {options.click();return false}) : null;
    }

    return {
        init: function (_opts) {
            return _init(_opts)
        },
        right: function (_opts) {
            return __right(_opts)
        }
    }
})()

/**
 * 搜索页控件
 * @type {{init, show, hide}}
 */
Module.prototype.searchControl = (function () {
    var searchPage, searchLayer, closeBtn, searchTypeBox, arrowDown;
    function _init() {
        if(!layer.v || layer.v < 2){throw new Error('没有加载layer-mobile,或不是最新版本！')};
        searchPage ? _show() : $.ajax({
            url: ctx + '/common/search.html',
            type: 'GET',
            success: function (html) {
                searchPage = html;
                _show();
            }
        })
    }

    function _show() {
        searchLayer = layer.open({
            type   : 1,
            shadeClose: false,
            content: searchPage,
            anim   : 'up',
            style  : 'position:fixed; left:0; top:0; width:100%; height:100%; border: none; -webkit-animation-duration: .3s; animation-duration: .3s;',
            success: function () {
                $('.layui-m-layercont').css({width: '100%', height: '100%'});
                closeBtn = $('#search-close');
                searchTypeBox = $('.control-search').find('.search-type-box');
                arrowDown = $('.control-search').find('.arrow-down');
                bindEvents()
            }
        });
    }

    function _hide() {
        layer.close(searchLayer)
    }

    function bindEvents() {
        closeBtn.on('tap', function () {
            _hide()
            return false
        })

        $('.show-search-type-box').on('tap', function () {
            var _open = searchTypeBox.is('.open');
            _open ? show_hide('hide') : show_hide('show');
           return false
        })

        searchTypeBox.on('tap', '.item', function () {
            var dataTitle = $(this).attr('data-title');
            $('.search-type').html(dataTitle);
            dataTitle == '商品' ? (function () {
                    $('#search-form').attr('action', ctx + '/goods-list.html');
                    $('#search-type').val('goods');
                })() : (function () {
                    $('#search-form').attr('action', ctx + '/store/store-list.html');
                    $('#search-type').val('store');
                })()
        })

        $('.key-words').on('tap', '.item', function () {
            location.href = ctx + '/goods-list.html?keyword=' + $(this).html();
            return false
        })

        $(document).on('tap', function () {
            show_hide('hide');
        })
    }

    function show_hide(str) {
        str == 'show' ? (function () {
            searchTypeBox.addClass('open');
            setTimeout(function () {
                searchTypeBox.addClass('show');
            }, 20)
        })() : (function () {
            searchTypeBox.removeClass('show');
            setTimeout(function () {
                searchTypeBox.removeClass('open');
            }, 300)
        })()
    }

    return {
        init: function () {
            return _init()
        },
        show: function () {
            return _show()
        },
        hide: function () {
            return _hide()
        }
    }
})()

/**
 * 开关控件
 * @type {{init}}
 */
Module.prototype.switchControl = (function () {
    var switchControl;
    var _id = 0;
    function _init(opts) {
        var options = opts || {};
        var isopen = (options.isOpen == undefined || options.isOpen) ? ' open' : '';
        if(!options.element){throw  new Error('element is undefined!')}
        var switchControlDom = '<div class="control-switch control-switch-'+ _id + isopen +'"><span class="control-switch-span"></span></div>'
        options.element.html(switchControlDom);
        switchControl = $('.control-switch-'+ _id);
        if(options.backgroundColor){switchControl.css({backgroundColor: options.backgroundColor})}
        switchControl.on('click', function () {
            var $this  = $(this),
                isOpen = $this.is('.open');
            isOpen ? $this.removeClass('open') : $this.addClass('open');
            isOpen ? options.close() : options.open();
        })
        _id += 1;
    }

    return {
        init: function (_opts) {
            return _init(_opts)
        }
    }
})()

/**
 * 返回顶部控件
 * @type {{init, show, hide}}
 */
Module.prototype.scrollToTopControl = (function () {
    var toTop;
    function _init(opts) {
        var options = opts || {};
        var scrollTop = options.scrollTop || 150;
        if($('#control-to-top')[0]){return}
        var toTopHtml = '<div id="control-to-top"><div class="inner-to-top"><i></i></div></div>';
        $(toTopHtml).appendTo($('body'));
        toTop = $('#control-to-top');
        if(typeof options.clickToTop !== 'function'){
            toTop.on('tap', function (e) {
                $('body,html').animate({scrollTop: 0}, 300, function () {_hide()})
                e.preventDefault();
            })
        }else {
            toTop.on('tap', function (e) {
                options.clickToTop();
                e.preventDefault();
            })
        }
        if(options.bottom !== undefined){toTop.css({bottom: options.bottom})}
        if(options.rollListen !== false){rollingListening()}
        $(window).scrollTop() > scrollTop ? _show() : _hide();
    }

    function _show() {
        toTop.addClass('open');
        setTimeout(function () {
            toTop.addClass('show')
        }, 20)
    }

    function _hide() {
        toTop.removeClass('show');
        setTimeout(function () {
            toTop.removeClass('open')
        }, 210)
    }

    function rollingListening() {
        var flag = 0;
        $(window).scroll(function () {
            var scrollTop = $(window).scrollTop();
            scrollTop > 150 ? (function () {
                if(flag == 1){return}
                _show();
                flag = 1
            })() : (function () {
                if(flag == 0){return}
                _hide();
                flag = 0
            })();
        })
    }

    return {
        init: function (_opts) {
            return _init(_opts)
        },
        show: function () {
            return _show()
        },
        hide: function () {
            return _hide()
        }
    }
})();

var moduleJs   = document.scripts, moduleScript = moduleJs[moduleJs.length - 1], jsPath = moduleScript.src;
var modulePath = jsPath.substring(0, jsPath.lastIndexOf("/") - 2);
var moduleCss  = document.createElement('link');
moduleCss.href = modulePath + 'css/module.css';
moduleCss.type = 'text/css';
moduleCss.rel  = 'styleSheet';
moduleCss.id   = 'module';
document.head.appendChild(moduleCss);

$(document).on('tap', '.tap', function () {
    var _href = $(this).attr('data-href');
    if(_href){location.href = _href;}
    return false
});
