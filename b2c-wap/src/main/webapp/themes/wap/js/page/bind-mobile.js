/**
 * Created by Andste on 2016/12/27.
 */
$(function () {
    var module = new Module();
    var isChecked;
    var mobile;
    init();

    function init() {
        module.navigator.init('绑定手机');
        bindEvents();
    }

    function bindEvents() {
        //  获取图片验证码
        getValidateImg();
        $('.img-validate-img').on('tap', function () {
            getValidateImg();
            return false
        })

        //  获取短信验证码
        $('.send-mobile-validate').on('tap', function () {
            sendSms();
            return false
        })

        //  提交表单
        $('.validate-submit').on('tap', function () {
            submitForm();
            return false
        })
    }

    //  获取图片验证码
    function getValidateImg() {
        $('.img-validate-img').attr('src', ctx + '/validcode.do?vtype=membervalid&r=' +new Date().getTime());
    }

    //  获取短信验证码
    function sendSms() {
        mobile = $('#mobile').val();
        if(!module.regExp.mobile.test(mobile)){module.message.error('手机格式有误！'); return false}

        $.ajax({
            url : ctx + '/api/shop/sms/send-sms-code.do',
            data: {
                mobile: mobile,
                key : 'binding'
            },
            type: 'POST',
            success: function (res) {
                if(res.result == 1){
                    module.countDown($('.send-mobile-validate'));
                    module.message.success('短信发送成功，请注意查收！');
                    isChecked = true;
                }else {
                    module.message.error(res.message);
                }
            },
            error: function () {
                module.message.error('出现错误，请重试！');
            }
        })
    }

    //  提交表单
    function submitForm() {
        if(!isChecked){module.message.error('请先完成手机校验！'); return false}
        if(!module.regExp.mobile.test(mobile)){module.message.error('手机格式有误！'); return false};
        var mobileCode = $('#sms-validate').val(), validcode = $('#img-validate').val();
        if(!mobileCode){module.message.error('请填写手机验证码！'); return false}
        if(!validcode){module.message.error('请填写图片验证码！'); return false}

        module.layerLoading.open();
        $.ajax({
            url : ctx + '/api/shop/member/member-mobile-validate.do',
            data: {
                mobile    : mobile,
                key       : 'binding',
                mobileCode: mobileCode,
                validcode : validcode
            },
            type: 'POST',
            success: function (res) {
                module.layerLoading.close(function () {
                    if(res.result == 1){
                        module.message.success('绑定成功！', function () {
                            location.replace('./security.html');
                        })
                    }else {
                        module.message.error(res.message);
                        getValidateImg();
                    }
                }, 300);
            },
            error: function () {
                module.layerLoading.close();
                module.message.error('出现错误，请重试！');
                getValidateImg();
            }
        })
    }
})