/**
 * Created by Andste on 2016/11/15.
 */
$(function () {
    var module      = new Module();
    var canRegister = {username: false, email: false, password: false, validcode: false}
    var registerBtn = $('#register-btn');

    init();

    function init() {
        getImgVal();
        bindEvent();
        checkRegister()
    }

    //  初始化导航栏
    module.navigator.init({
        title          : '邮箱注册',
        titleColor     : '#fff',
        backgroundColor: '#ff6700',
        left : {
            style: 'light'
        },
        right: false
    });

    //  事件绑定
    function bindEvent() {
        //  密码输入框显示隐藏切换
        module.switchControl.init({
            element: $('.operate.pwd'),
            open   : function () {
                $('#input-password')[0].type = 'text';
                $('#input-password-again')[0].type = 'text';
            },
            close  : function () {
                $('#input-password')[0].type = 'password';
                $('#input-password-again')[0].type = 'password';
            }
        })

        //  输入框输入监听
        $('.register-form').on('input propertychange', checkRegister)

        var username  = $('#input-username'),
            email     = $('#input-email'),
            password  = $('#input-password'),
            _password = $('#input-password-again'),
            validcode = $('#input-validcode');
        username.on('input propertychange', function () {
            var $this            = $(this),
                _val             = $this.val();
            canRegister.username = _val.length > 3 && _val.length < 20;
        })

        email.on('input propertychange', function () {
            var $this         = $(this),
                _val          = $this.val();
            canRegister.email = module.regExp.email.test(_val);
        })

        password.on('input propertychange', function () {
            var $this            = $(this),
                _val             = $this.val();
            var pwdAgain         = _password.val();
            canRegister.password = _val == pwdAgain && module.regExp.password.test(_val);
        })

        _password.on('input propertychange', function () {
            var $this            = $(this),
                _val             = $this.val();
            var pwd              = password.val();
            canRegister.password = _val == pwd && module.regExp.password.test(_val);
        })

        validcode.on('input propertychange', function () {
            var $this = $(this),
                _val  = $this.val();
            canRegister.validcode = _val.length == 4;
        })

        //  注册
        registerBtn.on('click', function () {
            module.layerLoading.open();
            $.ajax({
                url : ctx + '/api/shop/member/register.do',
                data: {
                    username : username.val(),
                    email    : email.val(),
                    password : password.val(),
                    validcode: validcode.val(),
                    license  : 'agree'
                },
                type   : 'POST',
                success: function (res) {
                    module.layerLoading.close();
                    if(res.result == 1){
                        location.href = forward || 'member.html';
                    }else {
                        alert(res.message);
                    }
                },
                error  : function () {
                    module.layerLoading.close();
                    alert('出现错误，请重试！');
                }
            })
        })

        //  打开用户协议
        $('.show-user-agreement').on('tap', function () {
            module.userAgreement.open();
            return false
        })
    }

    //  获取图片验证码
    function getImgVal() {
        $('.validcode-img').attr('src', ctx + '/validcode.do?vtype=memberreg&r=' + new Date().getTime());
        $('.validcode-img').on('click', function () {
            getImgVal()
        })
    }

    //  是否禁用注册按钮
    function checkRegister() {
        (canRegister.username && canRegister.email && canRegister.password && canRegister.validcode) ? registerBtn[0].disabled = false : registerBtn[0].disabled = true;
    }
})