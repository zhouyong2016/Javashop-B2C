/**
 * Created by Andste on 2017/3/27.
 * version 1.0.0
 */
;(function ($, window, document, undefined) {
    var goodsCatsSelect = function (ele, opts) {
        this.$element = ele;
        this.node     = {
            'app': null
        };
        this.params = {
            parentid: 0,
            headerItems: [],
            headerActiveItem: '请选择',
            catDatas: [],
            __catDatas: []
        };

        this.default = {
            'host'    : ctx,
            'api'     : '/shop/admin/cat/get-list-by-parentid-json.do'
        };

        this.options = $.extend({}, this.default, opts);
        this.options.url = this.options.host + this.options.api;
        window.__goodsCatSelect__ = this;
    };
    /* ============================================================================ */
    goodsCatsSelect.prototype = {
        init: function () {
            this.createLinkEle();
            this.createAppEle();
            this.Arrayfilter();
            this.bindEvents();
            this.getCatList();
        },

        bindEvents: function () {
            var _this = this;

            _this.$element.on('focus', function () {
                _this.open()
            });

            _this.$element.on('click', function (e) {
                e.stopPropagation()
            });

            _this.node.app.on('click', function (e) {
                e.stopPropagation()
            });

            $(document).on('click', function () {
                _this.close()
            });

            _this.node.app.on('click', '.__GCS__header_close', function () {
                _this.close()
            });

            this.node.app.on('click', '.__cat_item', function () {
                _this.clickItem($(this))
            });

            this.node.headerItems.on('click', '.__GCS__header_item', function () {
                _this.clickHeader($(this))
            });

            this.node.app.on('keyup', '.__GCS__input_input', function (e) {
                e.keyCode === 13 && _this.searchCat($(this))
            });

            this.node.app.on('click', '.__search', function () {
                _this.searchCat($(this))
            });
        },

        /* 逻辑处理
        ============================================================================ */
        open: function () {
            this.node.app.css(this.countCss());
            this.node.app.removeClass('hide')
        },
        close: function () {
            this.node.app.addClass('hide')
        },
        clickItem: function ($this) {
            var cat_id = $this.attr('data-id'), _leave = this.conutLevel($this.attr('data-path')), cat_text = $this.find('h4').html();
            $this.addClass('active').siblings().removeClass('active');
            this.params.headerActiveItem = cat_text;
            this.appendHeader(_leave);
            this.callback(cat_id, cat_text);
            if(_leave === 4) return;
            this.params.parentid = cat_id;
            this.getCatList();
        },
        clickHeader: function ($this) {
            $this.addClass('active').siblings().removeClass('active');
            this.node.content.css({left: - $this.index() * 100 + '%'});
        },

        searchCat: function ($this) {
            var _this = this;
            var _val = $this[0].tagName === 'INPUT' ? $this.val() : $this.siblings('input').val(),
                _leave = parseInt($this.attr('data-leave'));
            _this.params.catDatas[_leave - 1] = _this.params.__catDatas[_leave - 1].filter(function (element) {
                var _str = element.text;
                return _str.indexOf(_val) > -1
            });

            _this.appendContent(_leave + 1, _val);
            _this.countContWidth(_leave + 1)
        },

        conutLevel: function (str) {
            var path_splited = str.split('');
            var count = 0;
            for(var i = 0; i < path_splited.length; i++){
                if(path_splited[i] === '|'){ count += 1 }
            }
            return count;
        },
        countContWidth: function (leave) {
            var _this = this;
            var _catData = _this.params.__catDatas,
                _catDataLen = _catData.length,
                _leftLeave = (leave-1) || _catDataLen;
            _this.node.content.css({
                width: _catDataLen * 100 + '%',
                left : -(_leftLeave - 1) * 100 + '%'
            });
            _this.node.content.find('.__GCS__column').css('width', 100 / _catDataLen + '%');
        },
    
        countCss: function () {
            var offset = this.$element.offset();
            var _offsetLeft   = this.options.left || offset.left,
                _offsetTop    = this.options.top || offset.top,
                _offsetHeight = this.$element[0].offsetHeight,
                _zIndex       = this.options.zIndex || 1;
            var _position = this.options.position;
            _position && _position === 'top' && (_offsetTop -= (402 + _offsetHeight));
            return {
                left   : _offsetLeft,
                top    : _offsetTop,
                _height: _offsetHeight,
                zIndex : _zIndex
            };
        },

        callback: function (id, text) {
            var callback = this.options.callback;
            if(!callback || typeof (callback) !== 'function') return;
            var items = this.params.headerItems, __str = '';
            for(var i = 0; i < items.length; i++){ i === 0 ? __str += items[i] : __str += ' - ' + items[i] }
            var ret = {}; ret.cat_id = id; ret.cat_text = text; ret.string = __str;
            callback(ret)
        },

        /* 接口、数据处理相关
        ============================================================================ */
        getCatList: function () {
            var _this = this;
            var _options = {
                url: _this.options.url,
                type: 'GET',
                data: {
                    parentid: _this.params.parentid
                },
                dataType: 'json',
                success: function (res) { _this.processingCatList(res) }
            };
            $.ajax(_options)
        },
        processingCatList: function (_res) {
            var res = _res, _this = this;
            if(!res || res.length === 0) return;
            var count = this.conutLevel(res[0]['cat_path']);
            if(count === 2){
                _this.params.catDatas.splice(0, 10, res);
                _this.params.__catDatas.splice(0, 10, res);
            }else if(count === 3){
                _this.params.catDatas.splice(1, 10, res);
                _this.params.__catDatas.splice(1, 10, res);
            }else if(count === 4){
                _this.params.catDatas.splice(2, 10, res);
                _this.params.__catDatas.splice(2, 10, res);
            }
            _this.appendContent(count);
            _this.countContWidth();
        },

        /* DOM操作相关
        ============================================================================ */
        appendContent: function (count, _val) {
            var res = this.params.catDatas[count-2];
            var _item = '';
            for(var i = 0; i < res.length; i++){
                _item += '<div class="__cat_item" data-id="'+ res[i]['id'] +'" data-path="'+ res[i]['cat_path'] +'">\
                        <h4>'+ res[i]['text'] +'</h4>\
                        <div class="__cat_item_btns">\
                            <i class="__icon __edit" data-id="'+ res[i]['id'] +'" data-text="'+ res[i]['text'] +'" data-parentid="'+ res[i]['parent_id'] +'" data-leave="'+ (count - 1) +'"></i>\
                            <i class="__icon __delete" data-id="'+ res[i]['id'] +'"></i>\
                        </div>\
                    </div>';
            }
            var content = '<div class="__GCS__column">\
                    <div class="__GCS__item">\
                         <div class="__GCS__input"><input type="text" value="' + (_val || '') + '" class="__GCS__input_input" placeholder="输入名称查找" data-leave="' + (count - 1) + '"><i class="__search __icon" data-leave="' + (count - 1) + '"></i></div>\
                         <div class="__GCS__list">\
                                ' + _item + '\
                            </div>\
                    </div>\
                </div>';

            //  插入或替换节点
            if(this.node.app.find('.__GCS__column').eq(count-2).length === 0){
                $(content).appendTo(this.node.content);
            }else {
                this.node.app.find('.__GCS__column').eq(count-2).replaceWith(content);
                if(_val) return;
                this.node.app.find('.__GCS__column').eq(count-2).nextAll().remove();
            }
        },
        appendHeader: function (count) {
            var _headerActiveItem = this.params.headerActiveItem;
            var _headerItem = '<a href="javascript:;" class="__GCS__header_item">'+ _headerActiveItem +'</a>';
            if(this.node.headerItems.find('.__GCS__header_item').eq(count-2).length === 0){
                $(_headerItem).appendTo(this.node.headerItems)
            }else {
                this.node.headerItems.find('.__GCS__header_item').eq(count-2).replaceWith(_headerItem);
                this.node.headerItems.find('.__GCS__header_item').eq(count-2).nextAll().remove()
            }
            this.params.headerItems.splice(count-2, 10, _headerActiveItem);
            this.node.headerItems.find('.__GCS__header_item').eq(count-2).addClass('active').siblings().removeClass('active')
        },
        createAppEle: function () {
            var offset = this.countCss();
            var _app = '\
                <div id="__GCS__" class="hide" style="border: 1px solid #d0d0d0;left: '+ offset.left + 'px' + '; top: '+ (offset.top + offset._height) + 'px' + '; z-index: '+ offset.zIndex +';">\
                    <div class="__GCS__header">\
                        <div class="__GCS__header_items">\
                            <a href="javascript:;" class="__GCS__header_item">'+ this.params.headerActiveItem +'</a>\
                        </div>\
                        <i class="__GCS__header_close"></i>\
                    </div>\
                    <div class="__GCS__controller"><div class="__GCS__content"></div></div>\
                </div>';
            $('body').find('#__GCS__').length === 0 && (function () {
                $(_app).appendTo($('body'))
            })();
            this.node.app = $('#__GCS__');
            this.node.headerItems = this.node.app.find('.__GCS__header_items');
            this.node.controller = this.node.app.find('.__GCS__controller');
            this.node.content = this.node.controller.find('.__GCS__content');
        },
        createLinkEle: function () {
            $('#goodsCatsEditCss').length === 0 && (function () {
                var styleEle  = document.createElement('link');
                styleEle.id   = 'goodsCatsEditCss';
                styleEle.type = 'text/css';
                styleEle.rel  = 'stylesheet';
                styleEle.href = ctx + '/statics/e_tools/js/jquery.goodsCatsSelect.css';
                document.head.appendChild(styleEle);
            })();
        },
        Arrayfilter: function () {
            if (!Array.prototype.filter) {
                console.log('ArrayFilter...');
                Array.prototype.filter = function (fun /*, thisArg */) {
                    "use strict";
                    if (this === void 0 || this === null) throw new TypeError();
                    var t   = Object(this);
                    var len = t.length >>> 0;
                    if (typeof fun !== "function") throw new TypeError();
                    var res     = [];
                    var thisArg = arguments.length >= 2 ? arguments[1] : void 0;
                    for (var i = 0; i < len; i++) {
                        if (i in t) {
                            var val = t[i];
                            if (fun.call(thisArg, val, i, t))
                                res.push(val);
                        }
                    }
                    return res;
                };
            }
        }
    };
    /* ============================================================================ */
    $.fn.goodsCatsSelect = function (options) {
        var edit = new goodsCatsSelect(this, options);
        return this.each(function () {
            edit.init();
        })
    }
})(jQuery, window, document);