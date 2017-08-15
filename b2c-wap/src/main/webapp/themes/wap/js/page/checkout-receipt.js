/**
 * Created by Andste on 2016/11/22.
 */
$(function () {
   var module = new Module();
    init();

    var backToCheckout = function () {location.replace('./checkout.html')};
    function init() {
        module.navigator.init({
            title: '发票信息',
            left : {click: function () {backToCheckout()}}
        });
        initIcheck();
        bindEvents();
    }

    function initIcheck() {
        $('.icheck').iCheck({
            checkboxClass: 'icheckbox_flat-red',
            radioClass: 'iradio_flat-red'
        });
    }

    function bindEvents() {
        $('.eui-checkbox-btn').on('click', function () {
            $(this).addClass('checked').siblings().removeClass('checked');
        })

        //  保存到session
        $('#save-btn').on('click', function () {
            var is_have = $('#dont-need-receipt').is('.checked') ? 0 : 1;
            var receiptTitle = $('#receiptTitle').val();
            $.ajax({
                url : ctx + '/api/shop/checkout/set-receipt.do',
                data: {
                    is_have       : is_have,
                    receiptType   : is_have == 1 ? (receiptTitle == '个人' ? 1 : 2) : '',
                    receiptTitle  : is_have == 1 ? (receiptTitle || '个人') : '',
                    receiptContent: is_have == 1 ? ($('.iradio_flat-red.checked').find('.icheck').val() || '明细') : ''
                },
                type: 'POST',
                success: function (res) {
                    if(res.result == 1){
                        backToCheckout();
                    }
                }
            })
        })
    }
})