/**
 * Created by Andste on 2016/11/23.
 */
$(function () {
    var module = new Module();
    var isAdd  = location.pathname.indexOf('address-add') > -1;
    var urlStr = module.getQueryString('back');
    var isCheckout = urlStr == 'checkout';
    var back = isCheckout ? 'checkout' : 'member';
    var backToAddressList = function () {location.replace('./address-list.html?back=' + back)};
    init();

    function init() {
        var title = isAdd ? '新建地址' : '编辑地址';
        isCheckout ? module.navigator.init({title: title, left : {click: backToAddressList}}) : module.navigator.init(title);

        bindEvents();
    }

    //  事件绑定
    function bindEvents() {
        var deData = $('#deData').val() ? $('#deData').val().split(',') : null;

        //  地区选择插件
        $('.select-address').addressSelect({
            apiPath: ctx,
            deData : deData,
            callBack: function (res) {
                $('.select-address').find('span').html(res.string);
            }
        });

        //  别名快速设置
        $('.alias-items').on('tap', '.alias-item', function () {
            $('.alias-input').val($(this).html());
            return false
        })

        //  设置默认控件
        module.switchControl.init({
            element: $('.operate'),
            isOpen : $('#set-def-addr').val() == 1, //  初始化默认状态
            open   : function () {
                $('#set-def-addr').val(1);
            },
            close  : function () {
                $('#set-def-addr').val(0)
            }
        });

        //  保存修改
        $('#save-btn').on('click', function () {
            var _this       = $(this),
                name        = $("input[name='name']").val(),
                mobile      = $("input[name='mobile']").val(),
                province_id = $("input[name='province_id']").val(),
                addr        = $("input[name='addr']").val(),
                shipAddressName = $("input[name='shipAddressName']").val();

            if(!name){
                module.message.error('收货人不能为空！');
                return false;
            }else if(!module.regExp.mobile.test(mobile)){
                module.message.error('手机格式有误！');
                return false;
            }else if(!province_id){
                module.message.error('地区不能为空！');
                return false;
            }else if(!addr){
                module.message.error('详细地址不能为空！');
                return false;
            }

            if(!shipAddressName){
                module.layerConfirm({
                    content: '起个别名更好记哦！确定不起吗？',
                    btn: ['起一个', '不起了'],
                    yes: function () {
                        $('.alias-input').focus();
                        return false
                    },
                    no : function () {
                        $('.alias-input').removeAttr('name');
                        saveForm(_this);
                    }
                })
            }else {
                saveForm(_this);
            }
        })
    }

    //  提交表单
    function saveForm(_this) {
        _this[0].disabled = true;
        module.layerLoading.open();
        var url = isAdd ? '/api/shop/member-address/add.do' : '/api/shop/member-address/edit.do';
        var options = {
            url: ctx + url,
            type: 'POST',
            success: function (res) {
                module.layerLoading.close(function () {
                    if(res.result == 1){
                        isCheckout ? backToAddressList() : history.back();
                    }else {
                        module.message.error(res.message);
                        _this[0].disabled = false;
                    }
                }, 300);
            },
            error: function () {
                module.layerLoading.close(function () {
                    _this[0].disabled = false;
                });
                module.message.error('出现错误，请重试！');
            }
        }

        $('#address-form').ajaxSubmit(options);
    }
})