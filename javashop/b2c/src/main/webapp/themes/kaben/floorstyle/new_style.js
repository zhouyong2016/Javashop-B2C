/**
 * Created by Andste on 2017/6/13.
 */
$(function () {
    //  将懒加载图片恢复
    $('img.lazy').each(function () {
        var $this = $(this);
        $this.attr('src', $this.attr('data-original')).removeAttr('data-original');
    });

    var floors = $('#editTemplate'), floorsSwipers = [];
    //  设置鼠标悬浮切换tabs
    floors.find('.hd-tags').on('mouseenter', 'li', function () {
        var $this = $(this), _index = $this.index();
        $this.addClass('current').siblings().removeClass('current');
        $this.closest('.floor-line-con').find('.f-r-main').eq(_index).css('display', 'block')
             .siblings().css('display', 'none');
    });

    /*//  初始化广告轮播
    floors.find('.swiper-container-index-floor').each(function () {
        var _this = this;
        var floorSwiper = new Swiper(_this, {
            autoplay: 3000,
            loop: true,
            pagination: $(this).find('.swiper-pagination-index-floor'),
            autoplayDisableOnInteraction: false
        });
        floorsSwipers.push(floorSwiper);
    });*/

    var floorId = 0;
    //  编辑楼层商品
    floors.on('click', '.b_tags', function () {
        var _deData = [];
        var $this = $(this),
            _isExchange = $this.is('.is_exchange'),
            _index = $this.closest('li').index(),
            _closestItem = $this.closest('.same-box-right'),
            _frmain = _closestItem.find('.child_floor_show').eq(_index);
        _frmain.find('.box-li').each(function () {
            _deData.push($(this).find('.goods_sn').val());
        });
        floorId = $(this).attr('data-floorId');
        var options = {
            defaultData: _deData,
            confirm: function (data) {
                saveGoodsItem(data);
            }
        };
        _isExchange && (options.api = '/shop/admin/exchange/search-goods.do');
        $.GoodsSelector(options)
    });

    //  楼层商品修改，或新增。
    function saveGoodsItem(res) {
        var goodsIds = [], resLen = res.length;
        for(var i = 0; i < resLen; i++){ goodsIds.push(res[i]['goods_id']) }
        $.ajax({
            url: ctx + '/core/admin/template/save-batch-goods.do',
            data: {
                floor_id: floorId,
                goods_ids: goodsIds
            },
            type: 'post',
            dataType: 'json',
            success: function (res) {
                res.result === 1 && layer.alert(res.message, { icon: 1 }, function () {
                    refreshCurrent();
                });
            }
        })
    }

    //  编辑楼层滚动广告
    floors.on('click', '.b_advs', function () {
        var $this = $(this), floorId = $this.attr('data-floorId'), index = $this.attr('data-goods-index');
        newTab('修改广告列表', ctx + '/core/admin/template/edit-adv.do?floor_id=' + floorId + '&index=' + index)
    });

    //  编辑品牌列表
    floors.on('click', '.b_brand', function () {
        var $this = $(this), floorId = $this.attr('data-floorId');
        newTab('修改品牌列表', ctx + '/core/admin/template/edit-brand.do?floor_id=' + floorId)
    });

    //  编辑分类列表
    floors.on('click', '.b_cats', function () {
        var $this = $(this), floorId = $this.attr('data-floorId');
        newTab('修改分类列表', ctx + '/core/admin/template/edit-cat.do?floor_id=' + floorId)
    })
});