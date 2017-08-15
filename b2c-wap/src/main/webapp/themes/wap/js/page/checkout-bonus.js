/**
 * Created by Andste on 2016/11/22.
 */
$(function () {
    var module = new Module();
    var type_id = module.getQueryString('type_id'), region_id = module.getQueryString('region_id');
    init();

    var backToCheckout = function () {location.replace('./checkout.html')};
    function init() {
        module.navigator.init({
            title: '优惠券',
            left : {click: function () {backToCheckout()}},
            right: false
        });
        bindEvents();
    }

    function bindEvents() {
        //  tab切换
        var items = $('.content-bonus').find('.items');
        $('.nav-checkout-bonus').on('click', '.item', function () {
            var $this = $(this),
                index = $this.index();
            $this.addClass('selected')
                 .siblings().removeClass('selected');
            items.removeClass('show')
                 .eq(index).addClass('show');
        })

        //  优惠券使用
        $('.available').on('click', '.eui-bonus', function () {
            var $this      = $(this),
                bonus_id   = $this.attr('rel');
            useBonus(bonus_id);
        })

        //  不可用优惠券点击提醒
        /*$('.unavailable').on('click', '.eui-bonus', function () {
            var $this = $(this),
                _html = $this.find('.min-goods-amount').html();
            module.message.error(_html + '才可以使用哦！');
        })*/
    }

    //  优惠券使用
    function useBonus(bonus_id) {
        $.ajax({
            url : ctx + '/api/shop/bonus/use-one.do',
            data: {
                bonusid : bonus_id,
                typeid  : type_id,
                regionid: region_id
            },
            type: 'POST',
            success: function (res) {
                if(res.result == 1){
                    try {
                        $.cookie('checkout_bonus_id', bonus_id);
                        backToCheckout();
                    } catch (error){
                        module.message.error('请查看您是否禁用了浏览器cookie！');
                    }
                }else {
                    module.message.error(res.message);
                }
            },
            error: function () {
                module.message.error('出现错误，请重试！');
            }
        })
    }
})