/**
 * Created by Andste on 2016/11/22.
 */
$(function () {
   var module = new Module();
    init();

    var backToCheckout = function () {location.replace('./checkout.html')};
    function init() {
        module.navigator.init({
            title: '发票信息',
            left : {click: function () {backToCheckout()}}
        });
        initIcheck();
        bindEvents();
        isShowCompany();
        companyChange();
    }

    function initIcheck() {
        $('.icheck').iCheck({
            checkboxClass: 'icheckbox_flat-red',
            radioClass: 'iradio_flat-red'
        });
    }
    
    //判断第一次是否是单位发票抬头
    function isShowCompany() {
    	var isShow = $(".invoice-type").val();
    	
        if(isShow == '个人'){
        	$(".receipt-word").hide();
        }
    }
    
    //单位切换显示隐藏
    function companyChange(){
    	$('input[name="receiptType"]').click(function(){
    		var invoiceType = $(this).val();
    		
    		if(invoiceType == "2"){
        		$(".receipt-word").show();
        	}else{
        		$(".receipt-word").hide();
        	}
    		
    	})
    }
    
    
    function bindEvents() {
        $('.eui-checkbox-btn').on('click', function () {
            $(this).addClass('checked').siblings().removeClass('checked');
        })
        
        
        //input获得焦点下拉列表显示，失去焦点，下拉列表隐藏。
        $("#receiptTitle").click(function(){
        	$(".receipt-word-list").show();
        })
        $("#receiptTitle").blur(function(){
        	$(".receipt-word-list").hide();
        })
        
        //选中发票列表，并且将税号显示在输入框中
	    $(".receipt-word-list ul li").mousedown(function(){
	    	var _invoiceSn = $(this).attr("rel");      //税号
	    	var _invoiceTitle = $(this).text();      //发票抬头
	    	$("#receiptTitle").val(_invoiceTitle);
	    	$("#receipt-sn").val(_invoiceSn);
	    })

        //  保存到session
        $('#save-btn').on('click', function () {
        
            var is_have = $('#dont-need-receipt').is('.checked') ? 0 : 1;   //是否开具发票
            var receiptTitle = $('#receiptTitle').val();    //发票抬头
            var receiptSn = $('#receipt-sn').val();         //发票税号
            var receiptCenter = $('.content-receipt .iradio_flat-red.checked').find('.icheck').val();   //发票内容
            var receiptType = $('input[name="receiptType"]:checked ').val();    //类型，1为个人，2位单位
            
            
            if(receiptType == 2 && receiptSn == ""){
            	alert("发票税号不能为空");
            	return false;
            }
            
            //然后再保存到session中,然后保存到数据库中
            $.ajax({
            	url : ctx + '/api/shop/checkout/set-receipt.do',
                data: {
                    is_have       : is_have,
                    receiptType   : is_have == 1 ? (receiptType == '1' ? 1 : 2) : '',
                    receiptTitle  : is_have == 1 ? (receiptType == '1' ? '个人' : receiptTitle) : '',
                    receiptContent: is_have == 1 ? ($('.content-receipt .iradio_flat-red.checked').find('.icheck').val() || '明细') : '',
                    receiptDuty   : is_have == 1 ? (receiptSn || '') : '',
                },
                dataType: "json", 
                success: function (res) {
                	//如果成功，但是不开具发票。
                    if(res.result == 1 && is_have == 0 || receiptType == 1){
                    	 backToCheckout();
                    }
                   //如果成功，但是开具发票，并且是开具单位发票，执行保存发票操作。
                    else if(res.result == 1 && is_have == 1 && receiptType == 2){
                    	$.ajax({
                          url : ctx + '/api/shop/member-receipt/add.do',
                          data: {
                        	  title   : receiptTitle,        //抬头
                        	  content : receiptCenter,       //内容
                        	  duty    : receiptSn,           //税号
                        	  type    : receiptType         //发票类型 ，1为个人，2位单位
                          },
                          type: 'POST',
                          success: function (res) {
                        	  //如果成功，返回结算，如果失败，显示结果。
                              if(res.result == 1){
                            	  backToCheckout();
                              }else{
                            	  alert(res.message);
                              }
                          }
                      })
                    }
                }
            })
        })
    }
})