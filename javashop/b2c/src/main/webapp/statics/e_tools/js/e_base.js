/**
 * Created by Andste on 2016/5/11.
 * https://jquery.com/
 * http://getbootstrap.com/
 * https://icomoon.io/
 */
$(function () {
    lteIE8 ? initLow() : initHei();
    function initHei() {
        createHeiModal();  //  创建标准modal
    }

    function initLow() {
        createLowModal();  //  创建兼容modal
    }

    /* 标准modal创建
     ============================================================================ */
    function createHeiModal() {
        var alertModalHtml   = '<div class="modal fade" id="alertModal" role="dialog" style="z-index: 1060;">'
                + '<div class="modal-dialog" style="width: 265px;">'
                + '<div class="modal-content">'
                + '<div class="modal-header" style="padding: 10px;">'
                + '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<h4 class="modal-title" style="text-align: center; font-size: 14px;-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">提示信息</h4>'
                + '</div>'
                + '<div class="modal-body" style="text-align: center; line-height: 20px;"></div>'
                + '</div>'
                + '</div>'
                + '</div>'
            ,

            confirmModalHtml = '<div class="modal fade" id="confirmModal" role="dialog" style=" z-index: 1055; ">'
                + '<div class="modal-dialog">'
                + '<div class="modal-content" style="width: 300px; margin: 0 auto;">'
                + '<div class="modal-header">'
                + '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<h4 class="modal-title" style="text-align: center; font-size: 14px;-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">确认信息</h4>'
                + '</div>'
                + '<div class="modal-body" style="text-align: center; line-height: 20px;">'
                + '</div>'
                + '<div class="modal-footer" style="padding: 10px;">'
                + '<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>'
                + '<button type="button" class="btn btn-primary" id="confirm-true-btn">确定</button>'
                + '</div>'
                + '</div>'
                + '</div>'
                + '</div>'
            ,

            dialogModalHtml  = '<div class="modal fade" id="dialogModal" role="dialog" style="z-index: 1050; ">'
                + '<div class="modal-dialog" role="document">'
                + '<div class="modal-content">'
                + '<div class="modal-header" style="-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">'
                + '<button type="button" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<h4 class="modal-title" style="text-align: center; font-size: 14px;">标题</h4>'
                + '</div>'
                + '<div class="modal-body" style="overflow: hidden; "></div>'
                + '<div class="modal-footer">'
                + '<button type="button" class="btn btn-default" id="dialog-cancel-btn">取消</button>'
                + '<button type="button" class="btn btn-primary" id="dialog-true-btn">确定</button>'
                + '</div>'
                + '</div>'
                + '</div>'
                + '</div>'
            ,

            loadingModalHtml = '<div class="modal fade" id="loadingModal" role="dialog" style="z-index: 1065; background-color: #000000; opacity: 0.5; text-align: center">'
                + '<img src="' + ctx + '/statics/e_tools/images/app-loading.gif" style="position: absolute; left: 50%; top: 40%; margin-left: -25px; margin-top: -50px; -webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">'
                + '<p id="app_loading_message" style="position: absolute; top: 40%; left: 50%; margin-left-25px; margin-top: 55px;color: #ffffff; font-size: 12px; -webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">loading&nbsp;.&nbsp;.&nbsp;.</p>'
                + '</div>'
            ,
            messageModalHtml = '<div id="messageModal" style="z-index: 999999;">'
                + '<div class="message-body"></div>'
                + '</div>'
        ;
        var $body = $('body');
        !$body.find('#alertModalHtml')[0] && $body.append(alertModalHtml);
        !$body.find('#confirmModalHtml')[0] && $body.append(confirmModalHtml);
        !$body.find('#dialogModal')[0] && $body.append(dialogModalHtml);
        !$body.find('#loadingModal')[0] && $body.append(loadingModalHtml);
        !$body.find('#messageModal')[0] && $body.append(messageModalHtml);
    }

    /* 兼容modal创建
     ============================================================================ */
    function createLowModal() {
        var alertModalHtml   = '<div class="modal hide fade" id="alertModal"  role="dialog" aria-hidden="true" style="width: 265px; margin-left: -132.5px; z-index: 1060; ">'
                + '<div class="modal-header" style="padding: 10px; height: 20px;">'
                + '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'
                + '<h3 style="text-align: center; font-size: 14px;-ms-user-select: none;user-select: none;">提示信息</h3>'
                + '</div>'
                + '<div class="modal-body" style="text-align: center; line-height: 20px;"></div>'
                + '</div>'
            ,

            confirmModalHtml = '<div class="modal hide fade" id="confirmModal" role="dialog" style="width: 300px; margin-left: -150px; z-index: 1055; ">'
                + '<div class="modal-dialog" role="document">'
                + '<div class="modal-content">'
                + '<div class="modal-header" style="padding: 0;">'
                + '<button type="button" class="close" data-dismiss="modal" aria-label="Close" style="position: absolute; right: 10px;;"><span aria-hidden="true">&times;</span></button>'
                + '<h4 class="modal-title" style="text-align: center; font-size: 14px; padding: 10px 15px;">确认信息</h4>'
                + '</div>'
                + '<div class="modal-body" style="overflow: hidden; text-align: center; "></div>'
                + '<div class="modal-footer">'
                + '<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>'
                + '<button type="button" class="btn btn-primary" id="confirm-true-btn">确定</button>'
                + '</div>'
                + '</div><!-- /.modal-content -->'
                + '</div><!-- /.modal-dialog -->'
                + '</div><!-- /.modal -->'
            ,

            dialogModalHtml  = '<div class="modal hide fade" id="dialogModal" role="dialog" style="z-index: 1050; ">'
                + '<div class="modal-dialog" role="document">'
                + '<div class="modal-content">'
                + '<div class="modal-header" style="-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">'
                + '<button type="button" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<h4 class="modal-title" style="text-align: center; font-size: 14px;  padding: 10px 15px;">标题</h4>'
                + '</div>'
                + '<div class="modal-body" style="overflow: hidden; max-height: 800px;"></div>'
                + '<div class="modal-footer">'
                + '<button type="button" class="btn btn-default" id="dialog-cancel-btn">取消</button>'
                + '<button type="button" class="btn btn-primary" id="dialog-true-btn">确定</button>'
                + '</div>'
                + '</div>'
                + '</div>'
                + '</div>'
            ,

            loadingModalHtml = '<div class="modal hide fade" id="loadingModal" role="dialog" style="z-index: 1065; text-align: center; position: relative; ">'
                + '<img src="' + ctx + '/statics/e_tools/images/app-loading.gif" style="position: absolute; left: 50%; top: 40%; margin-left: -25px; margin-top: -50px; -webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">'
                + '<p id="app_loading_message" style="position: absolute; top: 40%; left: 50%; margin-left-25px; margin-top: 55px;color: #ffffff; font-size: 12px; -webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">loading&nbsp;.&nbsp;.&nbsp;.</p>'
                + '</div>'
            ,

            messageModalHtml = '<div id="messageModal"  style="z-index: 999999;">'
                + '<div class="message-body"></div>'
                + '</div>'
        ;
        var $body = $('body');
        !$body.find('#alertModalHtml')[0] && $body.append(alertModalHtml);
        !$body.find('#confirmModalHtml')[0] && $body.append(confirmModalHtml);
        !$body.find('#dialogModal')[0] && $body.append(dialogModalHtml);
        !$body.find('#loadingModal')[0] && $body.append(loadingModalHtml);
        !$body.find('#messageModal')[0] && $body.append(messageModalHtml);
    }

    /* 分页兼容IE78JS
     ============================================================================ */
    (function () {
        if ($('.lteIE8-pagination') && $('.gteIE9-pagination')) {
            if (lteIE8) {
                $('.lteIE8-pagination').addClass('pagination');
            } else {
                $('.gteIE9-pagination').addClass('pagination');
            }
        }
    }());

    /* ajax全局监控
     * 监听ajax发起与停止，自动禁用和启用按钮
     ============================================================================ */
    $(document).ajaxStart(function (event, jqXHR, options) {
        var activeEle = event.currentTarget.activeElement,
            type      = activeEle.type;
        if (type !== 'button') {
            return;
        }
        activeEle.disabled = true;
        activeEle.style.cursor = 'pointer';
        var cu = setTimeout(function () {
            activeEle.style.cursor = 'not-allowed';
        }, 500);
        $(document).ajaxStop(function () {
            clearTimeout(cu);
            activeEle.disabled = false;
            activeEle.style.cursor = 'pointer';
        });
    });
});

/* 添加到jquery方法中
 ============================================================================ */
jQuery.extend({

    /**
     * 消息提示框调用
     * @param _content
     */
    alert: function alertModal(_content) {
        if (lteIE8) {
            $('#confirmModal').modal('hide');
            $('#dialogModal').modal('hide');
        }
        var id   = $('#alertModal'),
            body = _content;
        if (_content) {
            body = _content;
        } else if (_content == undefined) {
            body = 'undefined';
        } else if (_content == null) {
            body = 'null';
        }
        id.find('.modal-body').text(body);
        $('body.modal-open').css({padding: 0});
        id.modal();
        id.on('hidden.bs.modal', function () {
            id.find('.modal-body').empty();
        });
    },

    /**
     * 带确认取消按钮的提示框调用
     * @param _content  //  需要提示的内容
     * @param _fn       //  点击确定后的回调
     */
    confirm: function confirmModal(_content, _fn) {
        if (lteIE8) {
            $('#alertModal').modal('hide');
            $('#dialogModal').modal('hide');
        }
        var id   = $('#confirmModal'),
            body = _content || '',
            fn   = _fn;
        id.find('.modal-body').html(body);
        $('body.modal-open').css({padding: 0});
        id.modal();
        id.find('#confirm-true-btn').off('click').on('click', function () {
            id.modal('toggle');
            typeof (fn) === 'function' && fn();
        });
        id.on('hidden.bs.modal', function () {
            id.find('.modal-body').empty();
        });
    },

    /**
     * 兼容IE78的dialogModal
     * @param opts
     */
    dialogModal: function dialogModal(opts) {
        if (lteIE8) {
            $('#alertModal').modal('hide');
            $('#confirmModal').modal('hide');
        }
        opts = opts ? opts : {};
        var id       = $('#dialogModal'),
            title    = opts.title ? opts.title.toString() : '提示信息',       //  提示信息
            html     = opts.html ? opts.html : '',                           //  需要在dialog里显示的页面
            width    = opts.width ? opts.width : '',                         //  dialog宽度
            height   = opts.height ? opts.height : '',                       //  dialog高度                       【一般自适应】
            top      = opts.top ? opts.top : '',                        	 //  dialog距离顶部位置
            btn      = opts.btn,                                             //  是否显示dialog的“取消”、“确定”按钮
            backdrop = opts.backdrop ? opts.backdrop : 'static',             //  点击背景时，dialog是否消失
            keyboard = opts.keyboard,                                        //  当按键盘上的ESC键时，dialog是否消失
            showCall = opts.showCall,                                        //  当dialog显示时，需要执行的方法
            callBack = opts.callBack;                                        //  当点击“确定”按钮时执行的方法
        id.find('.modal-title').html(title);
        id.find('.modal-body').html(html);
        if (lteIE8) {
            var ml = -(280 - (560 - parseFloat(width)) / 2);
            id.css({
                width     : width,
                height    : height,
                marginTop : top,
                marginLeft: ml
            }).find('.modal-header').css({
                padding: 0
            }).find('.close').css({
                marginLeft: -20
            });
            if (Sys.ie === 8) {
                id.find('.modal-header .close').css({
                    marginRight: 10
                });
            }
        } else {
            id.find('.modal-dialog').css({
                width    : width,
                height   : height,
                marginTop: top
            });
        }
        if (btn === false) {
            id.find('.modal-footer').css('display', 'none');
            if (opts.backdrop !== false) {
                backdrop = true;
            }
        } else {
            id.find('.modal-footer').css('display', '');
        }
        if (opts.keyboard === undefined) {
            keyboard = false;
        }
        $('body.modal-open').css({padding: 0});
        id.modal({
            backdrop: backdrop,
            keyboard: keyboard
        });
        typeof (showCall) === 'function' && showCall();
        id.find('#dialog-true-btn').off('click').on('click', function () {
            if (typeof (callBack) === 'function') {
                var str = callBack();
                if (str !== false) {
                    id.modal('hide');
                }
            }
        });
        id.find('#dialog-cancel-btn').off('click').on('click', function () {
            id.modal('hide');
        });
        id.find('.close').off('click').on('click', function () {
            id.modal('hide');
        });
        if (lteIE8) {
            return;
        }
        id.on('hidden.bs.modal', function () {
            id.find('.modal-body').empty();
        });
    },
    loading    : function loadingModal() {
        if (lteIE8) {
            return;
        }
        var id = $('#loadingModal'), i = 0,
            messageArry = ['loading&nbsp;', 'loading&nbsp;.', 'loading&nbsp;.&nbsp;.', 'loading&nbsp;.&nbsp;.&nbsp;.'];
        $('body.modal-open').css({padding: 0});
        id.modal({
            backdrop: 'static',
            keyboard: 'false'
        });

        (function loadingMessage() {
            setTimeout(function () {
                $('#app_loading_message').html(messageArry[i]);
                i++;
                if (i == 4) {
                    i = 0;
                }
                if ($('#loadingModal').css('display') == 'none') {
                    clearTimeout(loadingMessage);
                } else {
                    loadingMessage();
                }
            }, 500);
        })();
    },

    closeLoading: function closeLoadingMoadl(_fun) {
        var id = $('#loadingModal');
        setTimeout(function () {
            id.modal('hide');
            if (_fun && typeof _fun == 'function') {
                setTimeout(_fun, 300);
            }
        }, 300);
    },

    /**
     * 错误、成功提示信息
     */
    message: {
        message  : undefined,
        error    : function messageError(message) {
            var id    = $('#messageModal'),
                _this = this;
            id.find('.message-body').css({backgroundColor: '#ff4343'});
            this.message = message || '错误提示！';
            setTimeout(function () {
                _this.showModal();
            }, 500);
        },
        success  : function messageSuccess(message, param) {
            var id = $('#messageModal'), _this = this;
            id.find('.message-body').css({backgroundColor: '#449d44'});
            this.message = message || '成功提示！';
            setTimeout(function () {
                _this.showModal();
                if (param && param != undefined) {
                    setTimeout(function () {
                        if (param == 'reload') {
                            location.reload();
                        } else if (typeof param == 'function') {
                            param();
                        }
                    }, 500);
                }
            }, 500);
        },
        showModal: function showMOdal() {
            var id = $('#messageModal'), body = id.find('.message-body');
            body.html(this.message);
            id.css({display: 'block'});
            id.removeClass('show');
            body.removeClass('show');
            setTimeout(function () {
                id.addClass('show');
                body.addClass('show');
            }, 100);

            setTimeout(function () {
                id.removeClass('show');
                body.removeClass('show');
                setTimeout(function () {
                    id.css({display: 'none'});
                }, 155);
            }, 2000);
        }
    },

    /**
     * 商城通知
     * @param opts
     * opts.content   :  '通知内容'
     * opts.callBack  :  '通知显示时执行的function'
     * opts.closeBack :  '通知关闭时执行的function'
     */
    notification: function notification(opts) {
        opts = opts ? opts : {};
        var content = opts.content ? opts.content : '';
        var html = '<div class="alert alert-warning alert-dismissible fade in" id="appNotification" role="alert" style="width: 100%; position: fixed; top: 0; margin-bottom: 0; z-index: 99999; text-align: center; ">'
            + '<button type="button" class="close" data-dismiss="alert" aria-label="Close" style="top: 0; right: 60px;"><span aria-hidden="true">×</span></button>'
            + '<span class="app-notification-content">' + content + '</span>'
            + '</div>';
        if ($('body').find('#appNotification').length == 0) {
            $(html).appendTo($('body'));
        } else {
            $('#appNotification').find('.app-notification-content').html(content);
        }
        $('body').css({marginTop: 50});
        if (opts.callBack && typeof opts.callBack == 'function') {
            opts.callBack();
        }
        var id = $('#appNotification'), close = id.find('.close');
        close.on('click', function () {
            $('body').animate({marginTop: 0}, 200);
            if (opts.closeBack && typeof opts.closeBack == 'function') {
                opts.closeBack();
            }
        });
    },

    /**
     * 用于发送验证码的倒计时
     * @param opts
     * opts.elem   : 发送验证码按钮
     * opts.count  : 倒计时秒数【默认60秒】
     *
     */
    countDown: function countDown(opts) {
        var isObj = opts.elem ? true : false;
        var elem,
            count    = 60,
            beforeStr,
            afterStr,
            complete = '重新发送';
        if (isObj) {
            var opts = opts || {};
            elem = opts.elem;
            count = opts.count || 60;
            beforeStr = opts.beforeStr || null;
            afterStr = opts.afterStr || null;
            complete = opts.complete || '重新发送';
        } else {
            elem = arguments[0];
            if (arguments.length == 2 && arguments[1] > 0) {
                count = arguments[1];
            }
        }
        if (!elem) {
            throw error('没有传入任何element');
        }
        var ele      = elem[0],
            tagName  = ele.tagName,
            width    = ele.clientWidth,
            paddingL = parseInt(elem.css('padding-left')),
            paddingR = parseInt(elem.css('padding-right'));
        var type = tagName == 'INPUT';
        var button = (tagName == 'INPUT') || (tagName == 'BUTTON');
        if (button) {
            elem.addClass('disabled')
                .css({cursor: 'not-allowed', pointerEvents: 'auto', width: width - paddingL - paddingR})
                .blur();
            if (Sys.ie) {
                elem.css({width: 'auto'});
            }
        }

        var timer = setInterval(function () {
            if (count == 1) {
                _countTo0();
                return;
            }
            count--;
            if (isObj && beforeStr && afterStr) {
                type ? ele.value = beforeStr + count + afterStr : ele.innerHTML = beforeStr + count + afterStr;
            } else {
                type ? ele.value = "已发送 " + count + "秒" : ele.innerHTML = "已发送 " + count + "秒";
            }
            if (button) {
                ele.disabled = true;
                ele.style.cursor = 'not-allowed';
            }
        }, 1000);

        function _countTo0() {
            clearTimeout(timer);
            type ? ele.value = complete : ele.innerHTML = complete;
            if (button) {
                elem.removeClass('disabled')
                    .css({cursor: 'pointer'});
                ele.disabled = false;
            }
        }
    }
});

/**
 * 一个可以将任何元素以dialoModal形式弹出的接口
 * 使用方法： $('元素节点').dialogModal();
 */
$.fn.dialogModal = function (opts) {
    opts = opts ? opts : {};
    var _this          = this,
        id             = $('#dialogModal'),
        title          = opts.title ? opts.title.toString() : '提示信息',       //  提示信息
        html           = _this.clone(),                                        //  需要在dialog里显示的页面
        width          = opts.width ? opts.width : '',                         //  dialog宽度
        height         = opts.height ? opts.height : '',                       //  dialog高度                       【一般自适应】
        top            = opts.top ? opts.top : '',                        	 //  dialog距离顶部位置
        btn            = opts.btn,                                             //  是否显示dialog的“取消”、“确定”按钮
        backdrop       = opts.backdrop ? opts.backdrop : 'static',             //  点击背景时，dialog是否消失
        keyboard       = opts.keyboard,                                        //  当按键盘上的ESC键时，dialog是否消失
        showCall       = opts.showCall,                                        //  当dialog显示时，需要执行的方法
        callBack       = opts.callBack,                                        //  当点击“确定”按钮时执行的方法
        alwaysBackfill = opts.alwaysBackfill === true;                   //  总是将所做更改回填回去
    id.find('.modal-title').html(title);
    id.find('.modal-body').empty().html(html);
    if (lteIE8) {
        var ml = -(280 - (560 - parseFloat(width)) / 2);
        id.css({
            width     : width,
            height    : height,
            marginTop : top,
            marginLeft: ml
        }).find('.modal-header').css({
            padding: 0
        }).find('.close').css({
            marginLeft: -20
        });
        if (Sys.ie === 8) {
            id.find('.modal-header .close').css({
                marginRight: 10
            });
        }
    } else {
        id.find('.modal-dialog').css({
            width    : width,
            height   : height,
            marginTop: top
        });
    }
    if (btn === false) {
        id.find('.modal-footer').css('display', 'none');
        if (opts.backdrop !== false) {
            backdrop = true;
        }
    } else {
        id.find('.modal-footer').css('display', '');
    }
    if (opts.keyboard === undefined) {
        keyboard = true;
    }
    $('body.modal-open').css({padding: 0});

    id.modal({
        backdrop: backdrop,
        keyboard: keyboard
    });

    if (showCall && typeof showCall === 'function') {
        showCall();
    }

    id.find('#dialog-true-btn').unbind('click').on('click', function () {
        var str = true;
        if (callBack && typeof callBack === 'function') {
            str = callBack();
        }
        if (str === true) {
            _this.parent().html(id.find('.modal-body').find('>div').clone());
            id.modal('hide');
            setTimeout(function () {
                id.find('.modal-body').empty();
            }, 500);
        }
    });
    id.find('#dialog-cancel-btn').on('click', function () {
        alwaysBackfill && _this.parent().html(id.find('.modal-body').find('>div').clone());
        id.modal('hide');
        setTimeout(function () {
            id.find('.modal-body').empty();
        }, 500);
    });
    id.find('.close').on('click', function () {
        alwaysBackfill && _this.parent().html(id.find('.modal-body').find('>div').clone());
        id.modal('hide');
        setTimeout(function () {
            id.find('.modal-body').empty();
        }, 500);
    });
};

/**
 * 订单状态表
 * @param status
 */
$.fn.flowDiagram = function (status, toPay) {
    var _this = this, status = status, fill = 0,
        control                             = '<div class="app-flow-control">'
            + '<div class="inner-app-flow">'
            + '<div class="box-app-flow"></div>'
            + '</div>'
            + '</div>'
        ,
        items                               = '';

    switch (status) {
        case 0:
        case 1:
        case 6:
            fill = 1;
            break;
        default:
            fill = status;
            break;
    }
    if (_this.find('.app-flow-control').length == 0) {
        _this.html(control);
        render();
    }

    function render() {
        var innerControl = _this.find('.box-app-flow'),
            titleArray   = ['提交订单', '已取消', '已付款', '已发货', '已收货', '已完成', '申请售后'];
        switch (status) {
            case 6:
                titleArray = [titleArray[0], titleArray[1]];
                break;
            case 7:
            case 8:
                titleArray.splice(1, 1);
                titleArray.splice(6, 1);
                break;
            default:
                titleArray.splice(1, 1);
                titleArray.splice(5, 2);
                break;
        }

        if (toPay && status != 6) {
            if (status == 3) {
                fill = 2;
            }
            titleArray.splice(1, 1);
        }

        var titleLen = titleArray.length;

        for (var i = 0; i < titleLen; i++) {
            if (i < fill) {
                if (i == fill - 1 && status != 6) {
                    items += '<div class="items-app-flow">'
                        + '<span class="app-flow-hr show end"></span>'
                        + '<div class="app-flow-bg">'
                        + '<div class="app-flow-color show">'
                        + '<i class="icomoon app-flow-c show"></i>'
                        + '<i class="icomoon app-flow-e"></i>'
                        + '</div>'
                        + '</div>'
                        + '<div class="app-flow-title">' + titleArray[i] + '</div>'
                        + '</div>'
                    ;
                } else {
                    items += '<div class="items-app-flow">'
                        + '<span class="app-flow-hr show"></span>'
                        + '<div class="app-flow-bg">'
                        + '<div class="app-flow-color show">'
                        + '<i class="icomoon app-flow-c show"></i>'
                        + '<i class="icomoon app-flow-e"></i>'
                        + '</div>'
                        + '</div>'
                        + '<div class="app-flow-title">' + titleArray[i] + '</div>'
                        + '</div>'
                    ;
                }
            } else {
                if (status == 6) {
                    items += '<div class="items-app-flow">'
                        + '<span class="app-flow-hr show"></span>'
                        + '<div class="app-flow-bg">'
                        + '<div class="app-flow-color show">'
                        + '<i class="icomoon app-flow-c"></i>'
                        + '<i class="icomoon app-flow-e show"></i>'
                        + '</div>'
                        + '</div>'
                        + '<div class="app-flow-title">' + titleArray[i] + '</div>'
                        + '</div>'
                    ;
                } else {
                    items += '<div class="items-app-flow">'
                        + '<span class="app-flow-hr"></span>'
                        + '<div class="app-flow-bg">'
                        + '<div class="app-flow-color">'
                        + '<i class="icomoon app-flow-c show"></i>'
                        + '<i class="icomoon app-flow-e"></i>'
                        + '</div>'
                        + '</div>'
                        + '<div class="app-flow-title">' + titleArray[i] + '</div>'
                        + '</div>'
                    ;
                }
            }
        }
        innerControl.html(items);
        var itemsApp = innerControl.find('.items-app-flow'), itemsAppF = itemsApp.eq(0),
            itemsAppL = itemsApp.eq(titleLen - 1), itemsAppLTitle = itemsAppL.find('.app-flow-title');
        itemsAppF.find('.app-flow-title').addClass('first');
        itemsAppF.addClass('first').find('.app-flow-bg').addClass('first');
        itemsAppLTitle.addClass('last');
        itemsAppL.addClass('last').find('.app-flow-bg').addClass('last');
        if (itemsAppLTitle.html().length == 4) {
            itemsAppLTitle.css({marginLeft: 52});
        }
        if (status == 7) {
            itemsAppL.find('.app-flow-c').html('');
        }
        if (titleArray.length == 2) {
            innerControl.css({width: 220});
        } else {
            innerControl.css({width: (titleLen - 1) * 160 + 60});
        }
    };
};

/* 浏览器升级提示
 ============================================================================ */
if ((Sys.ie < 11) && (($.cookie('app_up_message') == undefined) || ($.cookie('app_up_message') != 0))) {
    $(document).ready(function () {
        setTimeout(show, 1000);
        function show() {
            var html = '<div id="upBrowserMessage" style="position: fixed; height: 30px; width: 100%; color: #333; background-color: #ff6700; z-index: 1000; top: -30px; font-size: 12px; line-height: 30px; text-align: center">'
                + '您的浏览器版本过低，为了更好的体验，避免出现兼容问题，建议您升级浏览器！'
                + '<a href="//cdn.dmeng.net/upgrade-your-browser.html" style="color: #0a89c7;">升级浏览器</a>'
                + '<a id="closeBrowserMessage" href="javascript: void(0);" class="icomoon" style="color: #dddddd; position: absolute; float: right; right: 20px; top: 0;"></a>'
                + '</div>';
            $(html).appendTo($('body'));
            var b = $('#upBrowserMessage'),
                c = $('#closeBrowserMessage');
            b.animate({top: 0}, 500);
            $('body').animate({marginTop: '30px'}, 500).css({position: 'relative'});
            if (Sys.ie == 10) {
                c.css({right: 30});
            }
            c.hover(function () {
                $(this).css({color: '#ffffff'});
            }, function () {
                $(this).css({color: '#dddddd'});
            });
            c.unbind('click').on('click', function () {
                $.cookie('app_up_message', '0', {expires: 7, path: '/'});
                b.animate({top: -30}, 500);
                $('body').animate({marginTop: 0}, 500).css({position: ''});
            });
        };
    });
}

/* flat选择框主题加载
 ============================================================================ */
/*if(!Sys.ie || Sys.ie > 7){
 document.write('<script type="text/javascript" src="' + ctxPath + '/e_tools/js/icheck.min.js"></script>');
 document.write('<link rel="stylesheet" href="' + ctxPath + '/e_tools/flat/green.css">');

 //  初始化icheck
 $(document).ready(function(){
 $('input').iCheck({
 checkboxClass : 'icheckbox_flat-green',
 radioClass    : 'iradio_flat-green'
 });
 });
 }*/


/* 全局方法
 ============================================================================ */
/**
 * 获取URL中的传参
 * @param str
 * @returns {string}
 * @constructor
 */
function GetQueryString(str) {
    var reg = new RegExp("(^|&)" + str + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    var context = "";
    if (r != null)
        context = r[2];
    reg = null;
    r = null;
    return context == null || context == "" || context == "undefined" ? "" : decodeURI(context);
}

/**
 * 获取今天日期 yyyy-mm-dd
 * @returns {string}
 * @constructor
 */
function GetTodayString() {
    var date = new Date(),
        y    = date.getYear() + 1900,
        m    = date.getMonth() + 1,
        d    = date.getDate();
    if (m < 10) {
        m = '0' + m;
    }
    if (d < 10) {
        d = '0' + d;
    }
    return y + '-' + m + '-' + d;
}

/**
 * 弹窗方法，如果浏览器阻止了弹窗，则执行回调，或者在本窗口打开
 * @param opts
 * opts.href    :    需要打开新窗口的url
 * opts.name    :    需要打开新窗口的name
 * opts.param   :    需要打开新窗口的参数
 * opts.callBack:    打开新窗口被阻止时的回调
 * @constructor
 */
function PopPpWindow(opts) {
    var href     = opts.href ? opts.href : '',
        name     = opts.name ? opts.name : '新窗口',
        param    = opts.param ? opts.param : 'height=500, width=800, top=200, left=200, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, status=no',
        callBack = (opts.callBack && typeof opts.callBack == 'function') ? opts.callBack : undefined;
    var e   = false,
        pw1 = null;
    try {
        pw1 = window.open(href, name, param, true);
        if (null == pw1 || true == pw1.closed) {
            e = true;
        }
    } catch (ex) {
        e = true;
    }
    if (e == true && callBack != undefined) {
        callBack();
    } else if (e == true) {
        $.confirm('您的浏览器已阻止新窗口弹出，本次将在本窗口打开。', function () {
            location.href = href;
        });
    } else {
        window.open(href, name, param, true);
    }
}