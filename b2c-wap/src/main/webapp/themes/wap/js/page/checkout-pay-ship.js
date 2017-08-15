/**
 * Created by Andste on 2016/11/21.
 */
$(function () {
    var module = new Module();
    init();

    var backToCheckout = function () {location.replace('../checkout/checkout.html')};
    function init() {
        module.navigator.init({
            title: '支付配送',
            left : {click: function () {backToCheckout()}}
        });
        bindEvents();
    }

    function bindEvents() {
        $('.eui-checkbox-btn').on('click', function () {
            $(this).addClass('checked').siblings().removeClass('checked');
        })

        //  保存到session
        $('#save-btn').on('click', function () {
            savePayShip();
        })
    }

    //  保存到session
    function savePayShip() {
        $.ajax({
            url : ctx + '/api/shop/checkout/set-pay-dlytype.do',
            data: {
                paymentId: $('.pay-way-checkbox .checked').attr('rel'),
                dlyTypeId: $('.ship-way-box .checked').attr('rel'),
                shipDay  : $('.pay-way-time .checked').attr('rel')
            },
            type: 'POST',
            success: function () {
                backToCheckout();
            }
        })
    }
})