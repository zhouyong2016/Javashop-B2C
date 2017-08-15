/**
 * Created by Andste on 2016/11/17.
 */
$(function () {
    var docHeight = document.body.scrollHeight - 44;
    var module = new Module();
    var catScroll;
    var conScroll;

    init();

    //  初始化导航栏
    module.navigator.init({
        title: '商品分类',
        right: false
    });

    //  初始化方法
    function init() {
        $('#classify').css({height: docHeight})
        $('#con-wrapper').css({width: document.body.offsetWidth-80})
        $('#con-wrapper').find('.inner-content').html($('#conts').find('.item-0').clone())
        initIscroll();
        bindEvent();
    }

    //  初始化iscroll
    function initIscroll() {
        //  分类导航iscroll【初始化一次】
        catScroll = new IScroll('#cat-wrapper', {
            probeType: 1,
            mouseWheel: false,
            disableTouch: false,
            tap: true,
        });

        //  分类内容iscroll【后续还会重新初始化】
        conScroll = new IScroll('#con-wrapper', {
            probeType: 1,
            mouseWheel: false,
            disableTouch: false,
            tap: true
        });
    }

    function bindEvent() {
        var innerContent = $('#con-wrapper').find('.inner-content');
        //  分类导航tap事件绑定
        $('#cat-wrapper .item').on('tap', function () {
            var $this = $(this),
                _this = $this[0],
                _rel  = $this.attr('rel');
            if($this.is('.focus')){return}
            $this.addClass('focus').siblings().removeClass('focus');
            //  导航对应分类滚动到中间
            catScroll.scrollToElement(_this, 500, null, -175, IScroll.utils.ease.quadratic);
            //  分类内容过渡
            innerContent.removeClass('show');
            var cont  = $('#conts').find('.item-' + _rel);
            //  懒加载
            if(cont.find('img').eq(0).attr('data-src')){
                var imgs = cont.find('img');
                for(var i = 0; i < imgs.length; i++){
                    var _img = imgs.eq(i),
                        _src = _img.attr('data-src');
                    _img.attr('src', _src).removeAttr('data-src');
                }
            }
            var _cont = cont.clone();
            //  分类内容过渡后
            setTimeout(function () {
                //  分类内容替换
                innerContent.html(_cont);
                innerContent.addClass('show');
                //  重新初始化分类内容iscroll
                conScroll = new IScroll('#con-wrapper', {
                    probeType   : 1,
                    mouseWheel  : false,
                    disableTouch: false,
                    tap         : true
                });
            }, 300)
        });

        //  分类内容tap事件
        innerContent.on('tap', '.img-cat-item', function () {
            var $this  = $(this),
                cat_id = $this.attr('cat_id');
            console.log($this)
            location.href = ctx + '/goods_list.html?cat=' + cat_id;
        })
    }
})

//  阻止document的touchmove默认事件【iscroll需要】
document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);