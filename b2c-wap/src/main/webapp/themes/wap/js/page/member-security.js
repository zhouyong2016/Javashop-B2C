/**
 * Created by Andste on 2016/12/1.
 */
$(function () {
    var module = new Module();
    var mobile = $('#num-mobile').val();
    var formPage;
    var sendBtn = $('.send-mobile-validate');
    var layerElement;
    init();

    function init() {
        module.navigator.init('修改密码');

        bindEvents();
    }

    //  事件绑定
    function bindEvents() {

        //  发送手机验证码
        sendMobileValidate();

        //  提交手机验证码校验
        submitValidate();

        _ajaxImgVlidate();
    }

    //  发送手机验证码事件绑定
    function sendMobileValidate() {
        sendBtn.on('tap', function () {
            _ajaxValidateMobile();
        })
    }

    //  发送验证码ajax
    function _ajaxValidateMobile() {
        if(!module.regExp.mobile.test(mobile)){return}
        $.ajax({
            url : ctx + '/api/shop/sms/send-sms-code.do',
            data: { key: 'check', isCheckRegister: 1, mobile: mobile },
            type: 'POST',
            success: function (res) {
                if(res.result == 1){
                    module.message.success('验证码发送成功，请注意查收。', function () {
                        sendBtn.off('tap');
                        module.countDown({
                            elem: sendBtn,
                            callBack: sendMobileValidate
                        });
                    })
                }else {
                    module.message.error(res.message);
                }
            },
            error: function () {
                module.message.error('出现错误，请重试！');
            }
        })
    }

    //  获取图片验证码
    function _ajaxImgVlidate() {
        $(".img-validate-img").attr('src', ctx + '/validcode.do?vtype=membervalid&r=' + new Date().getTime());
    }

    $('.img-validate-img').on('tap', function () {
        _ajaxImgVlidate();
        return false
    })

    //  退出登录
    function loginOut() {
        $.ajax({
            url: ctx + '/api/shop/member/logout.do',
            success: function (res) {
                if(res.result == 1){
                    location.href = ctx + '/member.html'
                }
            }
        })
    }

    function submitValidate() {
        //  提交手机验证码校验
        $('.validate-submit').on('tap', function () {
            if(!module.regExp.mobile.test(mobile)){return}
            var smsValidate = $("input[name='sms-validate']").val();
            var imgValidate = $("input[name='img-validate']").val();
            if(!module.regExp.integer.test(smsValidate)){module.message.error('手机验证码填写有误！'); return false}
            if(imgValidate.length < 4){module.message.error('图片验证码填写有误！'); return false}
            $.ajax({
                url : ctx + '/api/shop/member/member-mobile-validate.do',
                data: { key: 'check', mobile: mobile, mobileCode: smsValidate, validcode: imgValidate },
                type: 'POST',
                success: function (res) {
                    if(res.result == 1){
                        openFormPage();
                    }else {
                        module.message.error(res.message);
                        _ajaxImgVlidate();
                    }
                },
                error: function () {
                    module.messager.error('出现错误，请重试！');
                    _ajaxImgVlidate();
                }
            })
            return false
        })
    }

    //  手机校验弹出页
    function openFormPage() {
        formPage = layer.open({
            type: 1,
            content: $('.form-hide').html(),
            anim: 'up',
            style: 'position:fixed; left:0; top:0; width:100%; height:100%; border: none; -webkit-animation-duration: .5s; animation-duration: .5s;',
            success: function () {
                layerElement = $('.layui-m-layermain');
                formPageEvents();
            }
        });
    }

    //  form事件绑定
    function formPageEvents() {

        //  关闭formPage
        $('.security-navigator-left').on('tap', function () {
            layer.close(formPage);
            return false
        })

        //  获取图片验证码
        _ajaxFormImgValidate();
        function _ajaxFormImgValidate() {
            $('.img-validate-form').attr('src', ctx + '/validcode.do?vtype=membervalid&r=' + new Date().getTime())
        }

        $('.img-validate-form').on('tap', function () {
            _ajaxFormImgValidate();
            return false
        })

        layerElement.on('tap', '.form-save-btn', function () {
            var data = {
                newpassword: layerElement.find("input[name='newpassword']").val(),
                re_passwd  : layerElement.find("input[name='re_passwd']").val(),
                authCode: layerElement.find("input[name='form-validate-img']").val()
            }

            if(!module.regExp.password.test(data.newpassword)){module.message.error('密码格式错误！'); return false}
            if(data.newpassword != data.re_passwd){module.message.error('两次密码输入不同！'); return false}
            if(data.authCode.length < 4){module.message.error('图片验证码填写有误！'); return false}

            $.ajax({
                url: ctx + '/api/shop/member/update-password.do',
                data: data,
                type: 'POST',
                success: function (res) {
                    if(res.result == 1){
                        module.message.success('修改成功，请牢记您的新密码！', loginOut);
                    }else {
                        module.message.error(res.message);
                        _ajaxFormImgValidate();
                    }
                }
            })

            return false
        })
    }
})