/**
 * Created by Andste on 2016/6/7.
 * version 1.0
 * 目前此插件错误提示依赖于bootstrap，将在后续版本中完善此插件，将它独立出来。
 */

;(function($, window, document, undefined) {
    //  获取浏览器版本信息
    var Sys = {}, ua = navigator.userAgent.toLowerCase(), s;

    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1]
        : (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1]
        : (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1]
        : (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1]
        : (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;

    if (Object.hasOwnProperty.call(window, "ActiveXObject") && !window.ActiveXObject) {
        Sys.ie = 11.0;
    };

    var e_valPlguin = function(ele, opts){
        if(ele){
            this.$element = ele;
            this.defaults = {
                validationNum: 0,  //  需要校验的总个数
                callBack: function(){
                    this.$element.ajaxSubmit() || function(){
                        console.error('没有指定提交事件！');
                        return false;
                    };  //  默认提交事件
                },
                submitBtn: this.$element.find("[e_val-name='submit_btn']") || $("input[type=submit]")
            };
            this.param = {
                valArray: [],
                needValEles: []
            };
            this.options = $.extend({}, this.defaults, opts);
        };
    };

    e_valPlguin.prototype = {
        _init: function(){
            var _this = this;
            _this._initvalidationNum();  // 初始化需要验证个数
            _this._initBtn();  // 初始化按钮禁用状态
            _this._blur();  // 失去焦点触发
        },

        _initvalidationNum: function(){
            var _this = this;
            $.each(_this.options.rules, function(n, v){
                if(_this.options.rules[n].select){
                    var sel = _this.$element.find('#'+n).find('select'),
                        selLen = sel.length;
                    for(var i = 0; i < selLen; i++){
                        _this.options.rules[sel.eq(i).attr('name')] = true;
                    }
                    _this.options.validationNum += selLen;

                }else {
                    _this.options.validationNum++;
                };

            });
        },

        //  获取需要校验节点
        _getRules: function(_node){
            var _this = this, nodes = _node, rules = _this.options.rules;
            if(rules){
                $.each(rules, function(n, v){
                    var node = $('#'+n);
                    if(nodes && nodes == n){
                        if(v.email){
                            _this._email({node: node});
                        }else if(v.mobile){
                            _this._mobile({node: node});
                        }else if(v.password){
                            _this._password({
                                node: node,
                                param: v.password
                            });
                        }else if(v.ispassword){
                            _this._ispassword({
                                node: node,
                                param: v.ispassword
                            });
                        }else if(v.rangelength){
                            _this._rangelength({
                                node: node,
                                param: v.rangelength
                            });
                        }else if(v.minlength){
                            _this._minLength({
                                node: node,
                                param: v.minlength
                            });
                        }else if(v.maxlength){
                            _this._maxLength({
                                node: node,
                                param: v.maxlength
                            });
                        }else if(v.max){
                            _this._maxVal({
                                node: node,
                                param: v.max
                            });
                        }else if(v.min){
                            _this._minVal({
                                node: node,
                                param: v.min
                            });
                        }else if(v.range){
                            _this._range({
                                node: node,
                                param: v.range
                            });
                        }else if (v.date) {
                            _this._date({
                                node: node
                            });
                        }else if(v.int){
                            _this._int({node: node});
                        }else if(v.posInt){_this._posInt({
                                node: node
                            });
                        }else if(v.enNum){_this._enNum({
                                node: node
                            });
                        }else if(v.number){_this._number({
                                node: node
                            });
                        }else if(v.url){_this._url({
                                node: node
                            });
                        }else if (v.reg){
                            _this._reg({
                                node: node,
                                param: v.reg
                            });
                        }else if(v.required){_this._required({
                                node: node
                            });
                        };
                        _this._val();
                    }else if(v.select){_this._select({
                            node: node
                        });
                    };
                });
            };
        },

        /* 检测功能
         ============================================================================ */
        //  非空校验
        _required: function(opts){
            var _this = this, node = opts.node;
            if(!node.val()){
                _this._hasError(node);
            }else {
                _this._hasSuccess(node);
            };
        },

        //  邮箱校验
        _email: function(opts){
            var _this = this, node = opts.node, reg = /^([a-zA-Z0-9_\.\-])+([-_.][A-Za-zd]+)*@([A-Za-zd]+[-.])+[A-Za-zd]{2,10}$/, result = reg.test(node.val());
            if(result){
                _this._hasSuccess(opts);
                return true;
            }else {
                _this._hasError(opts);
                return false;
            };
        },

        //  手机号校验
        _mobile: function(opts){
            var _this = this, node = opts.node, reg = /^1[3|4|5|7|8]\d{9}$/, result = reg.test(node.val());
            result ? _this._hasSuccess(node) : _this._hasError(node);
        },

        //  密码格式校验
        _password: function(opts){
            var _this = this, node = opts.node, val = node.val(), min = opts.param[0], max = opts.param[1], reg = new RegExp("^(?=.{"+min+","+max+"}$)(?![0-9]+$)(?!.*(.).*\1)[0-9a-zA-Z]+$");
            if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(val.length < min || val.length > max){
                opts.msg = '密码长度在'+min+'-'+max+'个字符之间！';
                _this._hasError(opts);
                return false;
            }else if(/(:?\d)\1{5,}/.test(val)){
                opts.msg = '密码不能为重复的数字！';
                _this._hasError(opts);
                return false;
            }/*else if(!reg.test(val)){
                opts.msg = '以字母开头,只能包含字符、数字和下划线！';
                _this._hasError(opts);
                return false;
            }*/else{
                _this._hasSuccess(opts);
            };
        },

        //  确认密码校验
        _ispassword: function(opts){
            var _this = this, node = opts.node, pwd =$('#'+opts.param);
            _this._checkPwd({
                node: node,
                param: pwd
            });
            pwd.keyup(function(){
                _this._checkPwd({
                    node: node,
                    param: pwd
                });
                _this._val();
            });
        },
        _checkPwd: function(opts){
            var _this = this, _isPwd = opts.node;
            if(!_isPwd.val()){
                _this.param.valArray[_isPwd.attr('id')] = 0;
            }else if(opts.param.val() !== _isPwd.val()){
                _this._hasError(_isPwd);
            }else {
                _this._hasSuccess(_isPwd);
            }
        },

        //  长度区间校验
        _rangelength: function(opts){
            var _this = this, node = opts.node, array = opts.param;
            if(array.length == 2){
                var valLen = node.val().length, temp1 = array[0], temp2 = array[1];
                if(isNaN(temp1) || isNaN(temp2)){
                    window.console.error('rangelength传参必须为数组，并且元素为正整数。');
                    return false;
                };
                if(valLen < temp1 || valLen > temp2){
                    _this._hasError(opts);
                    return false;
                }else {
                    _this._hasSuccess(opts);
                    return true;
                };
            };
        },

        //  最小长度校验
        _minLength: function(opts){
            var _this = this, valLen = opts.node.val().length, minLength = opts.param;
            if(!isNaN(minLength)){
                if(valLen < minLength){
                    _this._hasError(opts);
                    return false;
                }else {
                    _this._hasSuccess(opts);
                    return true;
                };
            };
        },

        //  最大长度校验
        _maxLength: function(opts){
            var _this = this, valLen = opts.node.val().length, maxLength = opts.param;
            if(!isNaN(maxLength)){
                if(valLen > maxLength){
                    _this._hasError(opts);
                    return false;
                }else {
                    _this._hasSuccess(opts);
                    return true;
                };
            };
        },

        //  最大值校验
        _maxVal: function(opts){
            var _this = this, max = parseFloat(opts.param), val = opts.node.val(), maxVal = parseFloat(val);
            if(!val){
                opts.msg = '输入字不能为空！';
                _this._hasError(opts);
                return false;
            }else if(isNaN(val)){
                opts.msg = '输入有误！';
                _this._hasError(opts);
                return false;
            }else if(maxVal > max) {
                _this._hasError(opts);
                return false;
            }else  if(maxVal != 0 && maxVal < max) {
                _this._hasSuccess(opts);
                return true;
            }else {
                opts.msg = '输入有误！';
                _this._hasError(opts);
                return false;
            };
        },

        //  最小值校验
        _minVal: function(opts){
            var _this = this, min = parseFloat(opts.param), val = opts.node.val(), minVal = parseFloat(val);
            if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(isNaN(val)){
                opts.msg = '输入有误！';
                _this._hasError(opts);
                return false;
            }else if(minVal < min) {
                _this._hasError(opts);
                return false;
            }else if(minVal > min){
                _this._hasSuccess(opts);
                return true;
            };
        },

        //  数值区间校验
        _range: function(opts){
            var _this = this, min = parseFloat(opts.param[0]), max = parseFloat(opts.param[1]), val = opts.node.val();
            if(isNaN(min) || isNaN(max)){
                window.console.error('range传参必须为数组，并且元素为数值。');
                return false;
            }else if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(isNaN(val)){
                opts.msg = '输入有误！';
                _this._hasError(opts);
                return false;
            }else if(val >= min && val <= max){
                _this._hasSuccess(opts);
                return true;
            }else {
                _this._hasError(opts);
                return false;
            };
        },

        //  日期格式校验
        _date: function(opts){
            var _this = this, node = opts.node;
            if(node.val().length != 10){
                _this._hasError(opts)
                return false;
            }else {
                _this._hasSuccess(opts);
                return true;
            };
        },

        //  整数校验
        _int: function(opts){
            var _this = this, val = opts.node.val();
            if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(/^-?\d+$/.test(val)){
                _this._hasSuccess(opts);
                return true;
            }else {
                _this._hasError(opts);
                return false;
            };
        },

        //  正整数校验（包括0）
        _posInt: function(opts){
            var _this = this, val = opts.node.val();
            if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(/^\d+$/.test(val)){
                _this._hasSuccess(opts);
                return true;
            }else {
                _this._hasError(opts);
                return false;
            };
        },

        //  只能为英文和数字
        _enNum: function(opts){
            var _this = this, val = opts.node.val();
            if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(!/^[u4E00-u9FA5]+$/.test(val)){
                _this._hasError(opts);
                return false;
            }else {
                _this._hasSuccess(opts);
                return true;
            };
        },

        //  数字校验
        _number: function(opts){
            var _this = this, val = opts.node.val();
            if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(!/^(-)?[1-9][0-9]*$/.test(val)){
                _this._hasError(opts);
                return false;
            }else {
                _this._hasSuccess(opts);
                return true;
            };
        },

        //  选择框空值校验
        _select: function(opts){
            var _this = this, node = opts.node, val = node.val(), sel = node.find('select'), selLen = sel.length;
            for(var i = 0; i < selLen; i++){
                var __thisSel = sel.eq(i);
                if(__thisSel.val() == 0){
                    opts.node = __thisSel;
                    _this._hasError(opts);
                    return false;
                }else {
                    opts.node = __thisSel;
                    _this._hasSuccess(opts);
                    return true;
                };
            };
        },

        //  url校验
        _url: function(opts){
            var _this = this, val = opts.node.val();
            if(!val){
                opts.msg = '输入值不能为空！';
                _this._hasError(opts);
                return false;
            }else if(!/(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/g.test(val)){
                _this._hasError(opts);
                return false;
            }else {
                _this._hasSuccess(opts);
                return true;
            };
        },

        //  自定义正则校验
        _reg: function(opts){
            var _this = this, val = opts.node.val(), reg = new RegExp(opts.param);
            if(reg.test(val)){
                _this._hasSuccess(opts);
                return true;
            }else {
                _this._hasError(opts);
                return false;
            };
        },

        /* 检测环境
         ============================================================================ */

        // 失去焦点检测
        _blur: function(){
            var _this = this, rules = _this.options.rules;
            if(rules){
                $.each(rules, function(n, v){
                    var node = $('#'+n);
                    node.blur(function(){
                        _this._getRules(n);
                    });
                });
                _this._keyUp()
            };
        },

        // 按键检测
        _keyUp: function(){
            var _this = this, rules = _this.options.rules;
            $.each(rules, function(n, v){
                _this.$element.find('#'+n).keyup(function(){
                    _this._getRules(n);
                });
            });
        },

        /* 校验效果
         ============================================================================ */

        //  错误时
        _hasError: function(opts){
            var _this = this, node = opts.node, tagName = document.getElementById(node.attr('id')).tagName.toLocaleLowerCase() || '';
            if(tagName == 'select'){
                node.css({border: '1px solid #a94442'});
                if(!opts.only){
                    _this.param.valArray[node.attr('name')] = 0;
                };
            }else {
                if(Sys.ie < 9){
                    node.closest('.control-group').addClass('error');
                    node.parent().css({position: 'relative'});
                    _this._printError(opts);
                    if(!opts.only){
                        _this.param.valArray[node.attr('id')] = 0;
                    };
                }else {
                    node.closest('.form-group').addClass('has-error');
                    node.parent().css({position: 'relative'});
                    _this._printError(opts);
                    if(!opts.only){
                        _this.param.valArray[node.attr('id')] = 0;
                    };
                };
            };
        },

        //  正确时
        _hasSuccess: function(opts){
            var _this = this, node = opts.node, tagName = document.getElementById(node.attr('id')).tagName.toLocaleLowerCase() || '';
            if(tagName == 'select'){
                node.css({border: ''});
                if(!opts.only){
                    _this.param.valArray[node.attr('name')] = 1;
                };
            }else {
                if(Sys.ie < 9){
                    node.closest('.control-group').removeClass('error');
                    node.parent().css({position: ''});
                    _this._removeError(node);
                    if(!opts.only){
                        _this.param.valArray[node.attr('id')] = 1;
                    };
                }else {
                    node.closest('.form-group').removeClass('has-error');
                    node.parent().css({position: ''});
                    _this._removeError(node);
                    if(!opts.only){
                        _this.param.valArray[node.attr('id')] = 1;
                    };
                };
            };
        },

        //  错误消息打印到页面
        _printError: function(opts){
            var _this = this,node = opts.node, nodeId = node.attr('id'), msg = '此处有误，请核对！', nodeSb = node.siblings(), html = '';
            if(opts.msg){
                msg = opts.msg;
            }else if(opts.only){
                switch (opts.rules){
                    case 'required': msg = '输入值不能为空！';
                        break;
                    case 'rangelength': msg = '长度应在'+ opts.param[0] + '到' + opts.param[1] + '个字符之间！';
                        break;
                    case 'minlength': msg = '长度最少为'+ opts.param + '个字符！';
                        break;
                    case 'maxlength': msg = '长度最多为'+ opts.param + '个字符！';
                        break;
                    case 'email': msg = '邮箱格式有误！';
                        break;
                    case 'mobile': msg = '手机格式错误！';
                        break;
                    case 'ispassword': msg = '两次输入不一致！';
                        break;
                    case 'max': msg = '输入的数值不能大于'+ opts.param +'！';
                        break;
                    case 'min': msg = '输入的数值不能小于'+ opts.param +'！';
                        break;
                    case 'range': msg = '输入数值应在'+ opts.param[0] + '到' + opts.param[1] + '之间！';
                        break;
                    case 'date': msg = '日期格式有误！';
                        break;
                    case 'int': msg = '只能输入整数！';
                        break;
                    case 'posInt': msg = '只能输入正整数！';
                        break;
                    case 'enNum': msg = '只能输入英文或数字！';
                        break;
                    case 'number': msg = '只能输入数字！';
                        break;
                    case 'url': msg = 'url格式错误！';
                        break;
                    default: '此处有误，请核对！';
                };
            } else {
                $.each(_this.options.rules, function(n, v){
                    if(n == nodeId){
                        var str = '';
                        $.each(v, function(k, j){
                            str = k
                        });
                        if(typeof _this.options.message[n]!= 'undefined' && typeof _this.options.message[n][str]!= 'undefined'){
                            msg = JSON.stringify(_this.options.message[n][str]);
                        }else {
                            if(str == 'required'){msg = '输入值不能为空！';};
                            if(str == 'rangelength'){msg = '长度应在'+ v.rangelength[0] + '到' + v.rangelength[1] + '个字符之间！';};
                            if(str == 'minlength'){msg = '长度最少为'+ v.minlength + '个字符！';};
                            if(str == 'maxlength'){msg = '长度最多为'+ v.maxlength + '个字符！';};
                            if(str == 'email'){msg = '邮箱格式有误！';};
                            if(str == 'mobile'){msg = '手机格式错误！';};
                            if(str == 'ispassword'){msg = '两次输入不一致！';};
                            if(str == 'max'){msg = '输入的数值不能大于'+ v.max +'！';};
                            if(str == 'min'){msg = '输入的数值不能小于'+ v.min +'！';};
                            if(str == 'date'){msg = '日期格式有误！'};
                            if(str == 'int'){msg = '只能输入整数！'};
                            if(str == 'posInt'){msg = '只能输入正整数！'};
                            if(str == 'enNum'){msg = '只能输入英文或数字！'};
                            if(str == 'number'){msg = '只能输入数字！'};
                            if(str == 'url'){msg = 'url格式错误！'};
                        };
                    };
                });
            };

            var m = parseInt(node.css('marginLeft')),
                p = parseInt(node.parent().css('paddingLeft')),
                h = parseInt(node.parent().css('height'))+'px',
                s = m + p +'px';
            if(Sys.ie < 9){s = 0;};
            html = '<span class="e_error-span" style="position: absolute; top: '+ h +'; left: '+ s +'; margin-left: 0; color: #a94442; font-size: 12px;">'+ msg +'</span>'
            if(nodeSb.is('.e_error-span')){
                node.parent().find('.e_error-span').html(msg);
            }else {
                $(html).appendTo(node.parent());
            };
        },

        _removeError: function(_node){
            var _this = this, node = _node, nodeSb = node.siblings();
            if(nodeSb.is('.e_error-span')){
                node.parent().find('.e_error-span').remove();
            };
        },

        /* 检测总结
         ============================================================================ */
        _val: function(){
            var _this = this, temp = 0, btn = _this.options.submitBtn;
            $.each(_this.options.rules, function(n, v){
                if(_this.param.valArray[n] != undefined){
                    temp += _this.param.valArray[n];
                };
                __val(temp);
            });
            function  __val(_temp){
                if(_temp == _this.options.validationNum){
                    btn.unbind('click').on('click', function(){
                        _this.options.callBack();
                        return false;
                    }).removeAttr('disabled').css({cursor: 'pointer'});
                }else {
                    btn.unbind('click').attr('disabled', 'disabled').css({cursor: 'not-allowed'});
                };
            };
        },

        //  初始化按钮禁用状态
        _initBtn: function(){
            this.options.submitBtn.unbind('click').attr('disabled', 'disabled').css({cursor: 'not-allowed'});
        }
    };

    $.fn.e_validation = function(opts){
        if(typeof opts == 'object'){  //  如果是object类型，说明是在给整个表单做校验
            var ePlugin = new e_valPlguin(this, opts);
            return this.each(function(){
                ePlugin._init();
            });
        }else {  //  否则就是在做单个校验[事实上这里otps为string类型]
            var ePlugin = new e_valPlguin(), _this = this, rules = arguments[0], param = arguments[1] || '', msg = arguments[2] || '';
            //  如果长度为2，并且第二个参数为字符串类型，说明是错误消息。[自定义正则表达式校特殊处理]
            if(arguments.length == 2 && typeof arguments[1] == 'string'){
                if(arguments[0] == 'reg'){
                    param = arguments[1];
                }else {
                    param = '';
                    msg = arguments[1];
                };
            };

            return  ePlugin['_'+rules]({
                rules: rules,  //  校验规则
                node : _this,  //  需要校验的元素[this]
                param: param,  //  某些校验需要传参
                msg  : msg,    //  可能需要自己传错误消息
                only : true    //  标识为单个校验
            });
        };
    };
})(jQuery, window,document);