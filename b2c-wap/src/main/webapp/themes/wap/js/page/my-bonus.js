/**
 * Created by Andste on 2016/12/14.
 */
$(function () {
    var module = new Module();
    var buonsNav = $('.buons-nav'), buonsContent  = $('.bouns-content');
    init();

    function init() {

        module.navigator.init('我的优惠券');

        bindEvents();
    }

    function bindEvents() {

        changeType();
    }


    //  tab切换
    function changeType() {
        buonsNav.on('tap', '.nav-item', function () {
            var $this = $(this),
                _type = $this.attr('data-type');
            $this.addClass('active').siblings().removeClass('active');

            buonsContent.find('.content-' + _type).addClass('show')
                          .siblings().removeClass('show');
        })
    }
})