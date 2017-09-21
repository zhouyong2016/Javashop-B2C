/**
 * Created by Andste on 2017/7/5.
 */
var Receipt = {};

$(function () {
    invoiceEvent();
    /* 发票信息事件绑定
     ============================================================================ */

    function invoiceEvent() {
        var invoice = $('.content-ckt.invoice'), dialogModal = $('#dialogModal');
        //  发票鼠标悬浮显示按钮
        dialogModal.on('mouseenter mouseleave', '.head-invoice', function (event) {
            Receipt.invoice.h_invoiceTitle(event, $(this));
        });

        //  发票抬头编辑
        dialogModal.on('click', '.__edit__', function () {
            Receipt.invoice.e_invoiceTitle($(this));
        });

        //  增加发票
        dialogModal.on('click', '.add-invoice', function () {
            Receipt.invoice.a_invoice($(this));
        });

        //  保存发票抬头
        dialogModal.on('click', '.__save__', function (event) {
            Receipt.invoice.s_invoiceTitle($(this));
            event.stopPropagation();
        });

        //  编辑发票
        dialogModal.on('click', '.__update__', function (event) {
            Receipt.invoice.u_inboiceTitle($(this));
            event.stopPropagation();
        });

        //  删除发票
        dialogModal.on('click', '.__delete__', function (event) {
            Receipt.invoice.d_invoiceTitle($(this));
            event.stopPropagation();
        });

        //  发票信息编辑
        $('.edit-invoice').on('click', function () {
            Receipt.invoice.e_invoice();
        });

        //  发票抬头选择
        dialogModal.on('click', '.ckt-checkbox.head-invoice', function () {
            Receipt.invoice.s_invoiceHead($(this));
        });

        //  发票内容选择
        dialogModal.on('click', '.ckt-checkbox.content-invoice', function () {
            var _this = $(this);
            Receipt.invoice.s_invoiceContent(_this);
        });

        //  取消发票
        invoice.on('click', '.cancel-invoice', function () {
            var $this = $(this);
            $this.siblings('.receipt-title').html('');
            $this.siblings('.receipt-content').html('无需发票');
            $("input[name='receipt']").val(2);
            $this.remove();

            invoice.find("input[name='receiptType']").val('');
            invoice.find("input[name='receiptContent']").val('');
            invoice.find("input[name='receiptTitle']").val('');
            invoice.find("input[name='receiptDuty']").val('');
        });
    }

    /* 发票信息逻辑处理
     ============================================================================ */
    Receipt.invoice = (function () {

        //  修改发票信息
        function E_invoice() {
            var invoiceDialog = $('#invoice_dialog');
            invoiceDialog.dialogModal({
                title         : '发票信息',
                top           : 100,
                alwaysBackfill: true,
                showCall      : function () {
                    invoiceDialog.on('click', '.company-invoice-input', function (e) {
                        e.stopPropagation();
                    });
                },
                callBack      : function () {
                    var dialogModal = $('#dialogModal');
                    if ($('.company-invoice-input:visible').length > 0) {
                        $.message.error('请先保存发票抬头信息！');
                        return false;
                    } else {
                        if($('.head-invoice.selected').length === 0) {
                            $.message.error('请选择一个发票抬头！');
                            return false
                        }
                        setTnvoice();
                        return true;
                    }

                    function setTnvoice() {
                        $('.invoice-inputs').find("input[name='receipt']").val(1);
                        $('.receipt-title').html(dialogModal.find('.head-invoice.selected').find(".company-invoice-input").val());
                        $('.receipt-content').html(dialogModal.find('.content-invoice.selected').find('span').html());
                        $('.cancel-invoice').length < 1 && $('.edit-invoice').after('<a href="javascript: void(0);" class="cancel-invoice" style="margin-left: 10px; color: #ff3500;">取消发票</a>');
                    }
                }
            });
        }

        //  显示编辑删除按钮
        function H_invoiceTitle(event, $this) {
            if ($this.is('.__add_ele__')) return;
            var mouseenter = event.type === 'mouseenter', btns = $this.find('.__btns__');
            mouseenter ? btns.addClass('show') : btns.removeClass('show');
        }

        //  编辑发票抬头
        function E_invoiceTitle($this) {
            var _item = $this.closest('.head-invoice');
            _item.addClass('__editing__');
            _item.find('.__title__').hide();
            _item.find('.__edit__').hide();
            _item.find('.__update__').show();
            _item.find('.company-invoice-input').show().focus();
            $('#dialogModal').find('.duty-invoice-input').prop('readonly', false);
        }

        //  添加发票
        function A_invoiceTitle($this) {
            var addEle = $this.siblings('.__add_ele__');
            $this.css('display', 'none');
            addEle.css('display', 'block').addClass('selected').find('.company-invoice-input').css('display', 'block').focus();
            addEle.siblings().removeClass('selected');
            $('#dialogModal').find('.invoice-head.duty').show().find('.duty-invoice-input').val('').prop('readonly', false);
            cancelUpdate(addEle);
        }

        //  保存添加
        function S_invoicetTitle($this) {
            var dialogModal = $('#dialogModal'),
                _input      = $this.closest('.head-invoice').find('input'),
                _val        = _input.val(),
                _duty = dialogModal.find('.duty-invoice-input').val();
            if (!_val) { $.message.error('发票抬头不能为空！'); _input.focus(); return }
            if(!_duty){ $.message.error('纳税人识别号不能为空！'); dialogModal.find('.duty-invoice-input').focus(); return }
            if(!/^[0-9a-zA-Z]+$/.test(_duty)){ $.message.error('纳税人识别号不能包含特殊字符！'); return }
            $.ajax({
                url    : ctx + '/api/shop/member-receipt/add.do',
                data   : {
                    title  : _val,
                    content: $('.content-invoice.selected').find('span').html(),
                    duty   : _duty,
                    type   : 2
                },
                success: function (res) {
                    res.result === 1 ? addSuccess(res.data) : $.message.error(res.message);
                },
                error  : function () {
                    $.message.error('出现错误，请稍后重试！');
                }
            });
        }

        function addSuccess(data) {
            var dialogModal = $('#dialogModal'), add_ele = dialogModal.find('.__add_ele__');
            var __ele__ = '\
		        <div class="ckt-checkbox head-invoice company selected">\
                    <div class="__title__">' + data['title'] + '</div>\
                        <input type="text" class="company-invoice-input" maxlength="30" value="' + data['title'] + '" data-old_value="' + data['title'] + '" style="border: none; outline: none;">\
                    <div class="__btns__">\
                        <a href="javascript:;" class="__edit__">编辑</a>\
                        <a href="javascript:;" class="__update__">保存</a>\
                        <a href="javascript:;" class="__delete__">删除</a>\
                    </div>\
                    <input type="hidden" name="receipt_id" value="' + data['id'] + '">\
                    <input type="hidden" name="receipt_content" value="' + data['content'] + '">\
                    <input type="hidden" name="receipt_duty" value="' + data['duty'] + '">\
                </div>\
		    ';
            add_ele.siblings().removeClass('selected');
            add_ele.before(__ele__);
            dialogModal.find('.head-invoice.company').length < 10 && dialogModal.find('.add-invoice').show();
            dialogModal.find('.duty-invoice-input').prop('readonly', true);
            add_ele.hide().find('input').val('');
            S_invoiceHead($(__ele__));
        }

        //  保存编辑
        function U_updateEdit($this) {
            var dialogModal = $('#dialogModal'),
                _item       = $this.closest('.head-invoice'),
                _id         = _item.find("input[name='receipt_id']").val(),
                _input      = _item.find('.company-invoice-input'),
                _title      = _input.val(),
                _duty = dialogModal.find('.duty-invoice-input').val(),
                _content    = dialogModal.find('.content-invoice.selected').find('span').html();
            if (!_title){ $.message.error('发票抬头不能为空！'); _item.find("input[name='receipt_id']").focus(); return }
            if(!_duty){ $.message.error('纳税人识别号不能为空！'); dialogModal.find('.duty-invoice-input').focus(); return }
            if(!/^[0-9a-zA-Z]+$/.test(_duty)){ $.message.error('纳税人识别号不能包含特殊字符！'); return }
            $.ajax({
                url    : ctx + '/api/shop/member-receipt/edit.do',
                data   : {
                    id     : _id,
                    title  : _title,
                    content: _content,
                    duty   : _duty,
                    type   : 2
                },
                success: function (res) {
                    res.result === 1 ? (function () {
                        $.message.success('修改成功！');
                        updateSuccess();
                    })() : $.message.error(res.message);
                },
                error  : function () {
                    $.message.error('出现错误，请稍候重试！');
                }
            });

            function updateSuccess() {
                _item.find('.__title__').html(_title).show();
                _input.data('old_value', _title);
                _item.find('.company-invoice-input').hide();
                _item.find('.__edit__').show();
                _item.find('.__update__').hide();
                _item.removeClass('__editing__');
                dialogModal.find('.invoice-head.duty').show();
                dialogModal.find('.duty-invoice-input').prop('readonly', true);
                _item.find("input[name='receipt_content']").val(dialogModal.find('.content-invoice.selected').find('span').html());
                _item.find("input[name='receipt_duty']").val(dialogModal.find('.duty-invoice-input').val());
            }
        }

        //  删除发票
        function D_invoiceTitle($this) {
            //  虽然是删除抬头，但是是删除整个发票
            var _invoice = $this.closest('.head-invoice'), _id = _invoice.find("input[name='receipt_id']").val(), dialogModal = $('#dialogModal');
            $.confirm('确认删除这条数据吗？', function () {
                $.ajax({
                    url    : ctx + '/api/shop/member-receipt/delete.do',
                    data   : {id: _id},
                    success: function (res) {
                        res.result === 1 ? (function () {
                            $.message.success('删除成功！');
                            _invoice.siblings('.__add_ele__').removeClass('selected').hide();
                            _invoice.siblings('.add-invoice').show();
                            _invoice.remove();
                            dialogModal.find('.company.selected').length === 0 && dialogModal.find('.personal').addClass('selected');
                            dialogModal.find('.invoice-head.duty').val('').hide();
                        })() : $.message.error(res.message);
                    },
                    error  : function () {
                        $.message.error('出现错误，请稍后重试！');
                    }
                });
            });
        }

        //  发票抬头选择
        function S_invoiceHead(_this) {
            var is_add                                    = _this.is('.__add_ele__'),
                is_personal                               = _this.is('.personal'),
                dialogModal = $('#dialogModal'), _content = _this.find("input[name='receipt_content']").val();
            cancelUpdate(_this);
            _this.addClass('selected').siblings().removeClass('selected');
            !is_add && ($('.head-invoice.__add_ele__').hide()) && (dialogModal.find('.head-invoice.company').length < 10 && $('.add-invoice').show());

            dialogModal.find('.content-invoice').each(function () {
                var $this = $(this), __content = $this.find('span').html();
                _content === __content && ($this.addClass('selected').siblings().removeClass('selected'));
            });
            !is_personal && dialogModal.find('.duty-invoice-input').val(_this.find("input[name='receipt_duty']").val());
            !is_personal ? dialogModal.find('.invoice-head.duty').show() : dialogModal.find('.invoice-head.duty').hide();

            dialogModal.find("input[name='receipt']").val(2);
            dialogModal.find("input[name='receiptType']").val(_this.is('.personal') ? 1 : 2);
            dialogModal.find("input[name='receiptContent']").val(dialogModal.find('.content-invoice.selected').find('span').html());
            dialogModal.find("input[name='receiptTitle']").val(_this.find('.company-invoice-input').val());
            dialogModal.find("input[name='receiptDuty']").val(is_personal ? '' : _this.find("input[name='receipt_duty']").val());
        }

        //  取消编辑
        function cancelUpdate($this) {
            var __item = $this.siblings('.__editing__'), __input = __item.find('.company-invoice-input');
            __item.find('.__title__').show();
            __item.find('.__edit__').show();
            __item.find('.__update__').hide();
            __input.val(__input.data('old_value')).hide();
        }

        //  发票内容选择
        function S_invoiceContent(_this) {
            var receiptContent = $('#dialogModal').find('.invoice-inputs').find("input[name='receiptContent']");
            _this.addClass('selected')
                .siblings().removeClass('selected');
            receiptContent.val(_this.find('span').html());
        }

        return {
            e_invoice       : function () {
                return E_invoice();
            },
            e_invoiceTitle  : function ($this) {
                return E_invoiceTitle($this);
            },
            a_invoice       : function ($this) {
                return A_invoiceTitle($this);
            },
            s_invoiceTitle  : function ($this) {
                return S_invoicetTitle($this);
            },
            u_inboiceTitle  : function ($this) {
                return U_updateEdit($this);
            },
            d_invoiceTitle  : function ($this) {
                return D_invoiceTitle($this);
            },
            h_invoiceTitle  : function (event, $this) {
                return H_invoiceTitle(event, $this);
            },
            s_invoiceHead   : function (_this) {
                return S_invoiceHead(_this);
            },
            s_invoiceContent: function (_this) {
                return S_invoiceContent(_this);
            }
        };
    })();
});