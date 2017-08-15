/**
 * Created by Andste on 2016/11/18.
 */
$(function () {
	
    var module  = new Module();
    var _remark, bonus_id;
    try {
        _remark = $.cookie('checkout_remark');
        bonus_id = $.cookie('checkout_bonus_id');
    } catch (error) {
        module.message.error('请查看您是否禁用了浏览器cookie！');
    }
    init();

    //  初始化
    function init() {
        module.navigator.init('填写订单');
        bindEvents();
    }

    function bindEvents() {
        //  跳转到新建地址
        $('.no-address').on('tap', function () {
            location.replace('../member/address-add.html?back=checkout');
            return false
        })

        //  备注信息
        $('#open-remark').on('tap', function () {
            openRemark();
            return false
        })

        //  读取备注信息
        $('#data-remark')[0].value     = _remark || '';
        $('#view-remark')[0].innerHTML = _remark || '未填写';

        //  读取bonusId
        if($('#bonusId').length > 0){$('#bonusId')[0].value = bonus_id || 0;}

        //  提交订单
        $('#cerate-order').on('click', function () {
            cerateOrder();
        })
    }

    //  打开备注输入框
    function openRemark() {
        var _input;
        $.ajax({
            url    : './checkout-remark.html',
            type   : 'GET',
            success: function (html) {
                layer.open({
                    content: html,
                    btn    : ['确定', '取消'],
                    yes    : function (index) {
                        try {
                            $.cookie('checkout_remark', _input.val());
                            location.reload();
                        } catch (error) {
                            layer.close(index);
                            module.message.error('请查看您是否禁用了浏览器cookie！');
                        }
                    },
                    success: function () {
                        _input = $('#input-remark');
                        _remark ? _input.val(_remark) : _input.val('');
                        $('#clean-remark').on('click', function () {_input.val('').focus();})
                    }
                });
            }
        })
    }

    //  提交订单
    function cerateOrder() {
        var inputs = $('input[name]'), length = inputs.length, data = {};
        for (var i = 0; i < length; i++) {
            var _input  = inputs.eq(i)[0],
                _name   = _input.name,
                _val    = _input.value;
            data[_name] = _val;
        }

        if(!data.addressId){module.message.error('请添加一个地址！');return false}

        $.ajax({
            url: ctx + '/api/shop/order/create.do',
            data: data,
            type: 'POST',
            success: function (res) {
                if(res.result == 1){
                    try {
                        $.cookie('checkout_remark', '');
                        $.cookie('checkout_bonus_id', 0);
                        if(res.data.order.payment_name == '在线支付'){
                            module.message.success('订单创建成功啦，快去支付吧！', function () {
                                location.href = './checkout-success.html?ordersn=' + res.data.order.sn;
                            });
                        }else {
                            location.href = './checkout-success.html?ordersn=' + res.data.order.sn;
                        }
                    } catch (error) {
                        module.message.error('请查看您是否禁用了浏览器cookie！');
                    }
                }else {
                    module.message.error(res.message, function () {
                        if(res.message.indexOf('购物车不能为空') > -1){
                            location.href = ctx + '/index.html'
                        }
                    });
                }
            },
            error: function () {
                module.message.error('出现错误，请重试！');
            }
        })
    }
})