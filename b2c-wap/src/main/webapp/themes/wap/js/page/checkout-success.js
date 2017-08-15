/**
 * Created by Andste on 2016/12/15.
 */
$(function () {
    var module = new Module();
    init();

    function init() {
        module.navigator.init({
            title: '订单创建成功',
            left : {
                click: function () {
                    location.href = ctx + '/index.html'
                }
            }
        });
        toPay();
    }

    //  去支付
    function toPay() {
        $('.payment-online').on('tap', '.to-pay', function () {
            var $this      = $(this),
                order_id   = $this.attr('order-id'),
                payment_id = $this.attr('payment-id');
            location.href = ctx + '/api/shop/payment/execute.do' + '?orderid=' + order_id + '&paymentid=' + payment_id
        })
    }
})