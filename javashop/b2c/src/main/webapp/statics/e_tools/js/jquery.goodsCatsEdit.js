/**
 * Created by Andste on 2017/3/27.
 * version 1.0.0
 */
;(function ($, window, document, undefined) {
    var goodsCatsEdit = function (ele, opts) {
        this.$element = ele;
        this.node = {
            'app': null
        };
        this.layer = null;
        this.params = {
            activeColumn: 0,
            activeItem  : 0,
            parentid    : 0,
            catDatas    : [],
            __catDatas  : []
        };

        this.default = {
            'host'    : ctx,
            'api'     : '/shop/admin/cat/get-list-by-parentid-json.do',
            'canEdit' : true,
            'maxLeave': 3
        };

        this.options = $.extend({}, this.default, opts);
        this.options.url = this.options.host + this.options.api;
    };
    /* ============================================================================ */
    goodsCatsEdit.prototype = {
        init: function () {
            this.createLinkEle();
            this.createAppEle();
            this.Arrayfilter();
            this.bindEvents();
            this.getCatList();
            return this;
        },

        refreshData: function () {
            this.params.parentid = 0;
            this.params.activeColumn = 0;
            this.params.activeItem = 0;
            this.node.controller.empty();
            this.getCatList();
        },

        bindEvents: function () {
            var _this = this;

            this.node.app.on('keyup', '.__GCD__input_input', function (e) {
                e.keyCode === 13 && _this.searchCat($(this))
            });

            this.node.app.on('click', '.__search', function () {
                _this.searchCat($(this))
            });

            this.node.app.on('click', '.__GCD__add_cat', function () {
                _this.addCat($(this))
            });

            this.node.app.on('click', '.__cat_item', function () {
                _this.clickItem($(this))
            });

            this.node.app.on('click', '.__edit', function () {
                _this.editCat($(this))
            });

            this.node.app.on('click', '.__delete', function () {
                _this.deleteCat($(this))
            })
        },

        /* 逻辑处理
         ============================================================================ */
        clickItem: function ($this) {
            var _this = this, cat_id = parseInt($this.attr('data-id')), _maxLeave = this.options.maxLeave, _index;
            this.params.activeColumn = ( _index = $this.closest('.__GCD__column').index() + 1);
            this.params.activeItem = $this.index() - 1;
            if (_index !== _maxLeave && $this.is('.active')) return;
            $this.addClass('active').siblings().removeClass('active');
            if (this.params.catDatas.length + _index === _maxLeave * 2) {
                var _callback = _this.options.callback;
                !_this.options.canEdit && typeof (_callback) === 'function' && (function () {
                    var _datas = _this.params.__catDatas[_maxLeave - 1], _data;
                    for (var i = 0; i < _datas.length; i++) {
                        if (_datas[i]['id'] === cat_id) {
                            _data = _datas[i];
                            break;
                        }
                    }
                    _data && _callback(_data)
                })();
                return;
            }
            //  移除本列表后面所有列表
            $this.closest('.__GCD__column').nextAll().remove();
            this.params.parentid = cat_id;
            this.getCatList();
        },

        searchCat: function ($this) {
            var _this = this;
            var _val   = $this[0].tagName === 'INPUT' ? $this.val() : $this.siblings('input').val(),
                _leave = parseInt($this.attr('data-leave'));
            if(!_this.params.__catDatas[_leave - 1]) return;
            _this.params.catDatas[_leave - 1] = _this.params.__catDatas[_leave - 1].filter(function (element) {
                var _str = element.text;
                return _str.indexOf(_val) > -1
            });
            if(!_this.params.catDatas[_leave - 1][0]){ alert('无匹配项！'); return }
            this.params.activeColumn = $this.closest('.__GCD__column').index() + 1;
            _this.appendContent(_leave - 1, _val)
        },

        addCat: function ($this) {
            var _this = this, _leave = parseInt($this.attr('data-leave')), _addCat = _this.options.addCat, _cat = {};
            _cat.leave = _leave;
            _leave > 1 && (function () {
                var _parent_id = parseInt($this.attr('data-parentid')),
                    _catDatas_ = _this.params.__catDatas[_leave - 2];
                _cat.parent_data = {};
                _cat.parent_data['datas'] = _catDatas_;
                for (var i = 0; i < _catDatas_.length; i++) {
                    if (_catDatas_[i]['id'] === _parent_id) {
                        _cat.parent_data['data'] = _catDatas_[i];
                        break;
                    }
                }
            })();
            _addCat && typeof (_addCat) === 'function' && (function () {
                _addCat(_cat)
            })();
        },

        editCat: function ($this) {
            var _this = this;
            var _editCat = _this.options.editCat;
            _editCat && typeof (_editCat) === 'function' && (function () {
                var _leave        = parseInt($this.attr('data-leave')),
                    _cat_id       = parseInt($this.attr('data-id')),
                    _cat_parentid = parseInt($this.attr('data-parentid')),
                    _cat_text     = $this.attr('data-text');
                var _cat = {}, __data = {};
                _cat.leave = _leave;
                _leave > 1 && (function () {
                    var _data = _this.params.__catDatas[_leave - 2];
                    __data.datas = _data;
                    for (var i = 0; i < _data.length; i++) {
                        if (_data[i]['id'] === _cat_parentid) {
                            __data.data = _data[i];
                            break;
                        }
                    }
                })();
                _cat.id = _cat_id;
                _cat.text = _cat_text;
                _cat.parent_id = _cat_parentid;
                _cat.parent_data = __data;
                _editCat(_cat)
            })();
        },

        deleteCat : function ($this) {
            var _this = this, _cat = {};
            _cat.id = $this.attr('data-id');
            this.params.parentid = parseInt($this.attr('data-parentid'));
            var _deleteCat = _this.options.deleteCat;
            _deleteCat && typeof (_deleteCat) === 'function' && (function () {
                _deleteCat(_cat)
            })();
        },
        conutLevel: function (str) {
            var path_splited = str.split('');
            var count = 0;
            for (var i = 0; i < path_splited.length; i++) {
                if (path_splited[i] === '|') {
                    count += 1
                }
            }
            return count;
        },
        //  计算宽度
        countWidth: function () {
            var columns = this.node.app.find('.__GCD__column'), columnsLen = columns.length;
            columnsLen = columnsLen < 4 ? 3 : columnsLen;
            columns.css('width', 100 / columnsLen + '%')
        },
        returnData: function (_index) {
            var _callback = this.options.callback,
                __datas   = this.params.__catDatas;
            this.countWidth();
            !this.options.canEdit && typeof (_callback) === 'function' && _callback(__datas[__datas.length - 1][_index || 0]);
        },

        /* 接口、数据处理相关
         ============================================================================ */
        getCatList       : function () {
            var _this = this;
            var _options = {
                url     : _this.options.url,
                type    : 'GET',
                data    : {
                    parentid: _this.params.parentid
                },
                dataType: 'json',
                success : function (res) { _this.processingCatList(res) }
            };
            $.ajax(_options)
        },
        processingCatList: function (res) {
            var _params = this.params, _maxLeave = this.options.maxLeave, __ = (_params.activeColumn += 1) - 1;
            if (res.length === 0) {
                _params.catDatas.splice(__, 1000);
                _params.__catDatas.splice(__, 1000);
                var __catDatas = this.params.catDatas;
                var __activeItem = __catDatas[__catDatas.length - 1];
                var __id = __activeItem === undefined ? 0 : __activeItem[_params.activeItem]['id'];
                this.options.canEdit
                    ? (__catDatas.length < _maxLeave && (__catDatas.push([]) && this.appendContent(__, __id)))
                    : this.returnData(_params.activeItem);
                return
            }
            _params.activeItem = 0;
            _params.catDatas.splice(__, 1000, res);
            _params.__catDatas.splice(__, 1000, res);
            var _dataLen = _params.catDatas.length;
            this.appendContent(__);
            this.countWidth();
            _params.parentid = res[0]['id'];
            _dataLen === _maxLeave ? this.returnData(0) : _dataLen < _maxLeave && this.getCatList();
        },

        /* DOM操作相关
         ============================================================================ */
        appendContent: function (count, _val) {
            var _this = this, __l = count + 1;
            var res = this.params.catDatas[count];
            var _item = '';
            for (var i = 0; i < res.length; i++) {
                var _btns = this.options.canEdit ? '<i class="__icon __edit" data-id="' + res[i]['id'] + '" data-text="' + res[i]['text'] + '" data-parentid="' + res[i]['parent_id'] + '" data-leave="' + __l + '"></i><i class="__icon __delete" data-id="' + res[i]['id'] + '" data-parentid="' + res[i]['parent_id'] + '" data-leave="' + __l + '"></i>' : '';
                _item += '<div class="__cat_item' + (i === 0 ? ' active' : '') + '" data-id="' + res[i]['id'] + '" data-path="' + res[i]['cat_path'] + '">\
                        <h4>' + res[i]['text'] + '</h4>\
                        <div class="__cat_item_btns">\
                            ' + _btns + '\
                        </div>\
                    </div>';
            }
            var _strs = ['一', '二', '三', '四', '五', '六'];
            var _leave = _strs[count];
            var _add = this.options.canEdit ? '<a href="javascript:;" class="__GCD__add_cat" data-leave="' + __l + '" data-parentid="' + (typeof(_val) === 'number' ? _val : res[0]['parent_id']) + '"><i class="__GCD__add_cat_i"></i>添加' + _leave + '级分类</a>' : '';
            var content = '<div class="__GCD__column">\
                    <div class="__GCD__item">\
                        <div class="__GCD__content">\
                            <div class="__GCD__input"><input type="text" value="' + (typeof(_val) === 'string' ? _val : '') + '" class="__GCD__input_input" placeholder="输入名称查找" data-leave="' + __l + '"><i class="__search __icon" data-leave="' + __l + '"></i></div>\
                            <div class="__GCD__list">\
                                ' + _add + '\
                                ' + _item + '\
                            </div>\
                        </div>\
                    </div>\
                </div>';

            //  插入或替换节点
            if (this.node.app.find('.__GCD__column').eq(count).length === 0) {
                $(content).appendTo(this.node.controller);
            } else {
                this.node.app.find('.__GCD__column').eq(count).replaceWith(content);
            }
            this.countWidth();
            //  如果是搜索，获取后面的
            _val !== undefined && typeof (_val === 'string') && res.length > 0 && (function () {
                _this.node.app.find('.__GCD__column').eq(count).nextAll().remove();
                _this.params.parentid = res[0]['id'];
                _this.getCatList()
            })();
        },
        createAppEle : function () {
            this.$element[0].innerHTML = '\
                <div id="__GCD__">\
                    <div class="__GCD__controller"></div>\
                </div>\
                ';
            this.node.app = $('#__GCD__');
            this.node.controller = this.node.app.find('.__GCD__controller');
        },
        createLinkEle: function () {
            $('#goodsCatsEditCss').length === 0 && (function () {
                var styleEle = document.createElement('link');
                styleEle.id = 'goodsCatsEditCss';
                styleEle.type = 'text/css';
                styleEle.rel = 'stylesheet';
                styleEle.href = ctx + '/statics/e_tools/js/jquery.goodsCatsEdit.css';
                document.head.appendChild(styleEle);
            })();

            $('#select2Css').length === 0 && (function () {
                var styleEle = document.createElement('link');
                styleEle.id = 'select2Css';
                styleEle.type = 'text/css';
                styleEle.rel = 'stylesheet';
                styleEle.href = ctx + '/statics/e_tools/css/library/select2.min.css';
                document.head.appendChild(styleEle);
            })();

            $('#select2JS').length === 0 && (function () {
                var jsEle = document.createElement('script');
                jsEle.id = 'select2JS';
                jsEle.type = 'text/javascript';
                jsEle.src = ctx + '/statics/e_tools/js/library/select2.min.js';
                document.head.appendChild(jsEle);
            })();
        },
        Arrayfilter  : function () {
            if (!Array.prototype.filter) {
                console.log('ArrayFilter...');
                Array.prototype.filter = function (fun /*, thisArg */) {
                    "use strict";
                    if (this === void 0 || this === null) throw new TypeError();
                    var t = Object(this);
                    var len = t.length >>> 0;
                    if (typeof fun !== "function") throw new TypeError();
                    var res = [];
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
    $.fn.goodsCatsEdit = function (options) {
        return new goodsCatsEdit(this, options);
    }
})(jQuery, window, document);