/**
 * Created by Andste on 2016/11/23.
 */
$(function () {
    var module = new Module();
    var urlStr = module.getQueryString('back');
    var isCheckout = urlStr == 'checkout';
    var delDom = '<div class="address-delete-icon"></div>';
    init();

    var backToCheckout = function () {location.replace('../checkout/checkout.html')};

    function init() {
        isCheckout ? module.navigator.init({title: '收货地址', left : {click: function () {backToCheckout()}}}) : module.navigator.init({title: '收货地址', right: {element: delDom, click: function () {deleteEdit()}}});
        bindEvents();
    }

    function bindEvents() {

        //  添加地址
        $('#add-btn').on('tap', function () {
            isCheckout ? location.replace('./address-add.html?back=checkout') : location.href = './address-add.html';
            return false
        })

        //  编辑地址
        $('.address-list').on('tap', '.edit', function () {
            var $this  = $(this),
                add_id = $this.attr('address_id');
            isCheckout ? location.replace('./address-edit.html?address_id=' + add_id + '&back=' + urlStr) : location.href = './address-edit.html?address_id=' + add_id + '&back=' + urlStr;
            return false
        })

        //  删除地址
        $('.address-list').on('tap', '.delete', function () {
            var $this  = $(this),
                add_id = $this.attr('address_id');
            module.layerConfirm({
                content: '确定要删除这条地址吗？',
                yes: function () {
                    $.ajax({
                        url: ctx + '/api/shop/member-address/delete.do?addr_id=' + add_id,
                        type: 'POST',
                        success: function (res) {
                            if(res.result == 1){
                                location.reload();
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

            return false
        })

        //  结算页选择地址
        if(urlStr == 'checkout'){
            $('.address-list').on('click', '.content', function () {
                var $this  = $(this),
                    add_id = $this.attr('address_id');
                $.ajax({
                    url    : ctx + '/api/shop/member-address/isdefaddr.do',
                    data   : {
                        addr_id: add_id
                    },
                    type   : 'POST',
                    success: function (res) {
                        if (res.result == 1) {
                            backToCheckout();
                        }else {
                            module.message.error(res.message);
                        }
                    },
                    error: function () {
                        module.message.error('出现错误，请重试！');
                    }
                })
            })
        }
    }

    function deleteEdit() {
        var right = $('#nav-right');
        var isDel = right.html() != '取消';
        var _delDom = isDel ? '取消' : delDom;
        var editIcon = $('.edit-icon');
        module.navigator.right({element: _delDom});
        isDel ? editIcon.removeClass('edit').addClass('delete') : editIcon.removeClass('delete').addClass('edit');
    }
})