/**
 * Created by Andste on 2016/12/20.
 */
$(function () {
    var module = new Module();
    var product_id = module.getQueryString('product_id');
    var orderid = module.getQueryString('orderid');
    var goods_id = module.getQueryString('goods_id');
    init();

    function init() {
        module.navigator.init('评论商品');

        bindEvents();
    }

    function bindEvents() {

        //  评分星星
        $('.content-item').on('tap', '.comment-star', function () {
            var $this  = $(this),
                _input = $this.siblings('input');

            $this.removeClass('off').addClass('on');
            $this.nextAll('i').removeClass('on').addClass('off');
            $this.prevAll('i').removeClass('off').addClass('on');
            _input.val(parseInt($this.index() + 1));
            return false
        })

        //  评级
        $('.grade-comment-content').on('tap', '.comment-grade', function () {
            var $this = $(this),
                _rel  = $this.attr('data-rel');
            $this.siblings('input').val(_rel);
            $this.addClass('selected').siblings('.comment-grade').removeClass('selected');
            return false
        })

        //  提交评论
        $('#save-btn').on('tap', function () {
        	$("#save-btn").hide();
            var inputs = $("input[type='hidden']");
            var data = {};

            for(var i = 0; i < inputs.length; i++){
                var _input = inputs.eq(i),
                    _name  = _input.attr('name'),
                    _val   = _input.val();
                data[_name] = _val;
            }
            data['grade_'+product_id] =$("input[name=grade]").val();
            data['goods_id'] = goods_id;
            data['product_id'] = product_id;
            data['orderid'] = orderid;
            data['content'] = $('.comemnt-text').val();
            data['commenttype'] = 1;

            if(data['content'] == ''){
                module.message.error('评论内容不能为空！');
                $("#save-btn").show();
                return false
            }else {
                _ajax();
            }

            function _ajax() {
                $.ajax({
                    url : ctx + '/api/shop/commentApi/addComment.do',
                    data: data,
                    type: 'POST',
                    success: function (res) {
                        if(res.result == 1){
                            module.message.success('发表成功，请等待审核！', function () {
                                history.back();
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
            return false
        })
    }

})