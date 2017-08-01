/**
 * Created by Andste on 2017/1/9.
 */
var Address = {};
$(function () {

    infoEvent();

    /* 收货人信息事件绑定
     ============================================================================ */
    function infoEvent(){
        var info = $('.center-ckt-info');

        //  新增收货地址
        $('.c-new-address').on('click', function(){
            Address.info.a_InfoAdd();
        });

        //  收货人信息设置默认
        info.on('click', '.set', function(){
            var _this = $(this);
            Address.info.s_InfoSetDefAdd(_this);
        });

        //  收货人信息编辑
        info.on('click', '.edit', function(){
            var _this = $(this);
            Address.info.e_InfoEdit(_this);
        });

        //  收货人信息删除
        info.on('click', '.delete', function(){
            var _this = $(this);
            Address.info.d_InfoItem(_this);
        });

        //  收货人信息选中
        info.on('click', '.ckt-checkbox.info', function(){
            var _this = $(this);
            var _state = _this.is('.selected');
            if(!_state){
                Address.info.s_InfoItem(_this);
            }
        });

        //  地区列表项鼠标移入移出
        info.on('mouseover mouseout', '.li-ckt-info', function(event){
            var _this = $(this);
            Address.info.c_InfoItem(_this, event);
        });

        //  更多地址、收起地址
        $('.collapse-ckt-info').on('click', function(){
            var _this = $(this);
            Address.info.c_InfoCollapse(_this);
        });

    }

    /* 收货人信息逻辑处理
     ============================================================================ */
    Address.info = (function () {
        var info = $('.center-ckt-info');

        //  收货人信息删除
        function deleteInfo(_this) {
            var _this      = _this,
                address_id = _this.attr('address_id');
            if (typeof $.confirm == 'function') {
                $.confirm('确认要删除吗？', function () {
                    // ...
                    delAddress();
                });
            } else {
                if (confirm('确认要删除吗？')) {
                    // ...
                    delAddress();
                }
            }

            function delAddress() {
                $.loading();
                $.ajax({
                    url    : ctx + '/api/shop/member-address/delete.do?addr_id=' + address_id,
                    type   : 'POST',
                    success: function (result) {

                        if (result && typeof result == 'string') {
                            result = JSON.parse(result);
                        }
                        if (result.result == 1) {
                            $.closeLoading(function () {
                                initAddressList();
                                $.message.success('删除成功！');
                            });
                        } else {
                            $.message.error(result.message);
                        }
                        $.closeLoading();
                    },
                    error  : function () {
                        $.message.error('出现错误，请重试！');
                    }
                });
            }
        }

        //  收货人信息选择
        function selectInfo(_this, _global) {
            var _this      = _this,
                _global    = _global === false ? false : true;
            var address_id = _this.closest('.li-ckt-info').attr('address_id');
            if (_global != false) {$.loading()}

            _changeAddress();

            function _changeAddress() {
                var _thisCkt  = _this.closest('.li-ckt-info');
                var _thisData = _thisCkt.find('.data-input');

                $("input[name='shipAddr']").val(_thisData.attr('shipAddr'));
                $("input[name='shipName']").val(_thisData.attr('shipName'));
                $("input[name='shipMobile']").val(_thisData.attr('shipMobile'));

                $("input[name='province_id']").val(_thisData.attr('province_id'));
                $("input[name='city_id']").val(_thisData.attr('city_id'));
                $("input[name='region_id']").val(_thisData.attr('region_id'));
                $("input[name='town_id']").val(_thisData.attr('town_id'));

                $("input[name='province']").val(_thisData.attr('province'));
                $("input[name='city']").val(_thisData.attr('city'));
                $("input[name='region']").val(_thisData.attr('region'));
                $("input[name='town']").val(_thisData.attr('town'));

                $("input[name='shipAddr']").val(_thisData.attr('shipAddr'));
                $("input[name='shipName']").val(_thisData.attr('shipName'));

                $("input[name='shipTel']").val(_thisData.attr('shipTel'));
                $("input[name='shipZip']").val(_thisData.attr('shipZip'));
                $("input[name='addressId']").val(_thisData.attr('addressId'));
                $("input[name='shipAddressName']").val(_thisData.attr('shipAddressName'));

                var typeId = $('.hidden_dly').val();
                var activity_id = $('#isActivity').val();
                var regionid = _thisData.attr('region_id');
                //选择地址时，要重新加载配送方式信息
                $(".dly_list").load(ctx + "/checkout/dlytype_list.html?regionid="+regionid);

                //重新加载赠送的赠品和优惠券
                $(".gift_bonus").load(ctx + "/checkout/gift_bonus.html?regionId="+regionid+"&typeId="+typeId+"&activityId="+activity_id);

                //重新加载优惠券
                var bonusShow = $(".checkout-bonus-use").attr("rel");
                if(bonusShow == "show"){
                    $(".checkout-bonus-list").load(ctx + "/checkout/bonus.html?regionid="+regionid+"&typeid="+typeId+"&activityId="+activity_id);
                }

                //重新加载购物车价格
                $(".total").load(ctx + "/checkout/checkout_total.html?regionId="+regionid+"&typeId="+typeId+"&activityId="+activity_id);

                $.closeLoading(function () {
                    $('.ckt-checkbox.info').removeClass('selected');
                    _this.addClass('selected');
                    _this.closest('.li-ckt-info').addClass('selected')
                         .siblings().removeClass('selected');
                });
            }
        }

        //  地区鼠标移入
        function curInfo(_this, _event) {
            var _this    = _this,
                _type    = _event.type;
            var InfoInfo = _this.find('.address-li-ckt-info'),
                title    = InfoInfo.attr('data-title'),
                html     = InfoInfo.html(),
                oper     = _this.find('.operate-li-ckt-info');
            if (_type == 'mouseover') {
                //  InfoInfo.addClass('min');
                oper.addClass('show');
                if (html && html.length > 29) {
                    InfoInfo.html(html.substring(0, 29) + '...');
                }
            } else if (_type == 'mouseout') {
                //  InfoInfo.removeClass('min');
                oper.removeClass('show');
                InfoInfo.html(title);
            }
        }

        //  更多地址、收起地址
        function collInfo(_this) {
            var _this = _this;
            var state = _this.is('.more'),
                liLen = info.find('.li-ckt-info').length;
            if (state) {
                //  收起
                _this.removeClass('more')
                     .find('span').html('更多地址');
                _this.find('i').removeClass('more');
                info.css({height: 42});
                Address.public.insertEletTo($('.li-ckt-info.selected'))
            } else {
                //  更多
                _this.addClass('more')
                     .find('span').html('收起地址');
                _this.find('i').addClass('more');
                liLen > 3 ? info.css({height: 36 * 4}) : info.css({height: 36 * liLen});
            }
        }

        //  新增收货人信息【没有地址时】
        function newInfo() {
            $.ajax({
                url    : ctx + '/checkout/address-add.html',
                type   : 'GET',
                success: function (html) {
                    var _api = '/api/shop/member-address/add-new-address.do';
                    $.dialogModal({
                        title   : '新增收货人信息',
                        html    : html,
                        top     : 100,
                        btn     : false,
                        backdrop: false,
                        showCall: function () {
                            $('#dialogModal').find('.close').css('display', 'none');
                            $('#dialogModal').find('.modal-body').css({overflow: 'scroll'});
                            return showDialog(_api);
                        }
                    })
                }
            })
        }

        //  新增收货人信息【有地址时】
        function addInfo() {
            $.ajax({
                url    : ctx + '/checkout/address-add.html',
                type   : 'GET',
                success: function (html) {
                    var _api = '/api/shop/member-address/add-new-address.do';
                    $.dialogModal({
                        title   : '新增收货人信息',
                        html    : html,
                        top     : 100,
                        btn     : false,
                        showCall: function () {
                            $('#dialogModal').find('.modal-body').css({overflow: 'scroll'});
                            return showDialog(_api);
                        }
                    })
                }
            })
        }

        //  编辑收货人信息
        function editInfo(_this) {
            var address_id = _this.attr('address_id');
            $.ajax({
                url    : ctx + '/checkout/address-edit.html?addressid=' + address_id,
                type   : 'GET',
                success: function (html) {
                    var _api = '/api/shop/member-address/edit.do?addr_id=' + address_id;
                    $.dialogModal({
                        title   : '修改收货人信息',
                        html    : html,
                        top     : 100,
                        btn     : false,
                        showCall: function () {
                            $('#dialogModal').find('.modal-body').css({overflow: 'scroll'});
                            return showDialog(_api);
                        }
                    })
                }

            })
        }

        //  新增、修改地址dialog逻辑
        function showDialog(_api) {
            var app             = $('#dialogModal').find('.address_div'),
                _api            = _api;
            var name            = app.find("input[name='name']"),
                addr            = app.find("input[name='addr']"),
                mobile          = app.find("input[name='mobile']"),
                shipAddressName = app.find("input[name='shipAddressName']");

            var inputs = app.find('.form-control');

            //  别名
            app.find('.example-aliases').on('click', function () {
                var _val = $(this).html();
                shipAddressName.val(_val);
            });

            inputs.on('input propertychange blur', function () {
                var _this = $(this),
                    _name = _this.attr('name'),
                    _val  = _this.val();
                if (_name == 'mobile' || _name == 'shipMobile') {
                    testMobile(_val) ? _this.parent().removeClass('has-error error') : _this.parent().addClass('has-error error');
                } else if (_name != 'shipAddressName') {
                    _val ? _this.parent().removeClass('has-error error') : _this.parent().addClass('has-error error');
                }
            });

            //  保存地址
            $('.add-btn').on('click', function () {
                sendAjax($(this));
            });

            function sendAjax(_this) {
                var _this = _this;
                for (var i = 0, len = inputs.length; i < len; i++) {
                    var __this = inputs.eq(i),
                        _val   = __this.val();
                    if (!_val && __this.attr('name') != 'shipAddressName') {
                        __this.parent().addClass('has-error error');
                        if (i == len) {
                            return
                        }
                    }
                }

                if (app.find('.error').length > 0) {
                    $.message.error('请核对表单填写是否有误！');
                    return false;
                }

                /* 对地址信息进行校验
                 ============================================================================ */
                var aLen      = $('.app-address-tab').find('a').length,
                    _inputLen = 0,
                    _input    = $('.app-address').find("input[type='hidden']");
                for (var i = 0, len = _input.length; i < len; i++) {
                    if (_input.eq(i).val() != '') {
                        _inputLen += 1;
                    }
                }
                if (aLen != _inputLen / 2) {
                    $.message.error('请核对收货地址是否完整！');
                    return false;
                }
                /* 对地址信息进行校验
                 ============================================================================ */
                _this.off('click');

                var options = {
                    url    : ctx + _api,
                    type   : 'POST',
                    success: function (result) {
                        if (result && typeof result == 'string') {
                            result = JSON.parse(result);
                        }
                        if (result.result == 1) {
                            $.closeLoading(function () {
                                $.message.success('保存成功！');
                                initAddressList()
                                $('#dialogModal').modal('hide').find('.close').css({display: 'block'});
                            });
                        } else {
                            shipAddressName.attr('name', 'shipAddressName');
                            $.message.error(result.message);
                            _this.on('click', function () {
                                sendAjax(_this);
                            })
                        }
                    },
                    error  : function () {
                        shipAddressName.attr('name', 'shipAddressName');
                        $.message.error('出现错误，请重试！');
                        _this.on('click', function () {
                            sendAjax(_this);
                        })
                    }

                };

                $.loading();
                app.find('#address_form').ajaxSubmit(options);
                $.closeLoading();
            }

            function testMobile(mobile) {
                return /^0?(13[0-9]|15[0-9]|18[0-9]|14[0-9]|17[0-9])[0-9]{8}$/.test(mobile);
            }
        }

        //  显示、隐藏【更多地址】
        function initAddressList(str) {
            $.ajaxSetup({cache: false});
            if (str && str == 'setDef') {
                $('#address_list').empty().load(ctx + '/checkout/address-list.html', function () {
                    if($('.li-ckt-info').length < 1){return false};
                    var _defAddress = $('.default-li-ckt-info').closest('.li-ckt-info').find('.ckt-checkbox.info');
                    Address.info.s_InfoItem(_defAddress, false);
                });
                return
            }
            $('#address_list').empty().load(ctx + '/checkout/address-list.html', function () {
                var collapse = $('.collapse-ckt-info'),
                    liCkt    = $('.li-ckt-info'),
                    liCktLen = liCkt.length;
                if (liCktLen > 1) {
                    collapse.addClass('show more');
                    collapse.find('.more-collapse-ckt').html('收起地址');
                    collapse.find('.icon-collapse-ckt-info').addClass('more');
                    var liLen = $('.li-ckt-info').length;
                    liLen > 3 ? info.css({height: 36 * 4}) : info.css({height: 36 * liLen});
                } else if (liCktLen == 0) {
                    collapse.removeClass('show');
                    newInfo();
                } else {
                    collapse.removeClass('show');
                }
                if(liCktLen > 0){
                    var _defAddress = $('.default-li-ckt-info').length > 0 ? $('.default-li-ckt-info').closest('.li-ckt-info').find('.ckt-checkbox.info') : liCkt.eq(0).find('.ckt-checkbox.info');
                    Address.info.s_InfoItem(_defAddress, false);
                }
            });
        }

        //  设置默认收货地址
        function setDefaultAddress(_this) {
            var add_id = _this.attr('address_id');
            $.loading();
            $.ajax({
                url    : ctx + '/api/shop/member-address/set-def-address.do?addr_id=' + add_id,
                type   : 'POST',
                success: function (result) {
                    if (result && typeof result == 'string') {
                        result = JSON.parse(result);
                    }
                    if (result.result == 1) {
                        $.closeLoading(function () {
                            initAddressList('setDef')
                            $.message.success('设置成功！');
                        });
                    } else {
                        $.message.error(result.message);
                    }
                },
                error  : function () {
                    $.message.error('出现错误，穷重试！');
                }
            })
            $.closeLoading();
        }

        return {
            i_InfoInitAddress: function () {
                return initAddressList();
            },
            d_InfoItem       : function (_this) {
                return deleteInfo(_this);
            },
            s_InfoItem       : function (_this, g) {
                return selectInfo(_this, g);
            },
            c_InfoItem       : function (_this, _event) {
                return curInfo(_this, _event);
            },
            c_InfoCollapse   : function (_this) {
                return collInfo(_this);
            },
            a_InfoAdd        : function () {
                return addInfo();
            },
            e_InfoEdit       : function (_this) {
                return editInfo(_this);
            },
            s_InfoSetDefAdd  : function (_this) {
                return setDefaultAddress(_this);
            }
        }
    })();


    /* 公共方法
     ============================================================================ */
    Address.public = (function(){
        //  将此元素放到第一位
        function insertEletTo(element, toEle, eq){
            var _this = element,
                toEle = toEle ? toEle : _this.siblings(),
                eq    = eq ? eq : 0;
            //  克隆
            var _clone = _this.clone(true);
            $(_clone).insertBefore(toEle.eq(eq));
            _this.remove();
            $('.center-ckt-info')[0].scrollTop = 0;
        }

        return {
            insertEletTo : function(ele, toEle, eq){
                return insertEletTo(ele, toEle, eq);
            }
        }
    })();
})