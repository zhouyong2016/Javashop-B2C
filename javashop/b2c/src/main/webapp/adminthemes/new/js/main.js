//设置下拉菜单
function setdownArrow() {
    //$(".downArrow").offset().left = $(".tabs li:last").offset().left;
    var lilfet = $(".tabs li:last").offset().left + 1;
    var liwidth = $(".tabs li:last").width();
    $(".downArrow").css("left", (lilfet + liwidth) + "px");
}
function ShowMsg(msg) {
    $(".errorInfo").text(msg);
    //$(".errorInfo").fadeIn(100);
    //$(".errorInfo").fadeOut(6000);
    $(".errorInfo").show().delay(3000).fadeOut(200);
}
function addTab(title, url) {
    if ($('#tabs').tabs('exists', title)) {
        $('#tabs').tabs('select', title);//选中并刷新
        var currTab = $('#tabs').tabs('getSelected');
        var url = $(currTab.panel('options').content).attr('src');
        if (url != undefined && currTab.panel('options').title != '首页') {

            $("#divLoading").show();
            $('#tabs').tabs('update', {
                tab: currTab,
                options: {
                    content: createFrame(url)
                }
            })
        }
    } else {
        var content = createFrame(url);
        $('#tabs').tabs('add', {
            title: title,
            content: content,
            closable: true
        });
    }
    tabClose();
    if (title != '首页') {
        setdownArrow();
    }
}
//设置选中tab
function SelectTable(title, url, topTitle) {
    if ($('#tabs').tabs('exists', title)) {
        var currTab = $('#tabs').tabs('select', title);
    } else {
        addTab1(title, url, $("div[topname='" + topTitle + "']").attr("topvalue"));
    }
}



function addTab1(title, url, argId) {
    var varClosable = true;
    if (title == "首页" || title == "录入期初数据") {
        varClosable = false;
    }
    $("#divLoading").show();
    $(".parentMenu").removeClass("menuS");
    $("#parent" + argId).addClass("menuS");
    $("#hidCurTopMenuId").val(argId);

    if ($('#tabs').tabs('exists', title)) {
        $('#tabs').tabs('select', title);//选中并刷新
        var currTab = $('#tabs').tabs('getSelected');
        var urlOld = $(currTab.panel('options').content).attr('src');
        if (urlOld != undefined && currTab.panel('options').title != '首页') {

            $("#divLoading").show();
            $('#tabs').tabs('update', {
                tab: currTab,
                options: {
                    content: createFrame(url)
                }
            });
        }
    } else {
        var content = createFrame(url);
        $('#tabs').tabs('add', {
            title: title,
            content: content,
            closable: varClosable,
            id: argId
        });
    }
    tabClose();
    if (title != '首页') {
        setdownArrow();
    }
}
function childAddTab(title, url, topTitle) {
    addTab1(title, url, $("div[topname='" + topTitle + "']").attr("topvalue"));
}
function createFrame(url) {
    var s = '<iframe scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>';
    return s;
}

function CloseTab() {
    parent.$.messager.confirm('系统提示', '提示：确定要关闭当前页面吗？', function (r) {
        if (r) {
            var currTab = $('#tabs').tabs('getSelected');
            $('#tabs').tabs('close', currTab.panel('options').title);
            setdownArrow();
        }
    });
}

function CloseTabByTitle(title) {
    $('#tabs').tabs('close', title);
    setdownArrow();
}
function childAddTabAndCloseTab(title, url, topTitle, closeTitle) {
    $('#tabs').tabs('close', closeTitle);
    addTab1(title, url, $("div[topname='" + topTitle + "']").attr("topvalue"));
}
function tabCloseFun(obj) {
    var subtitle = $(obj).parent().children(".tabs-inner").children(".tabs-title").text();
    return;
}
function tabClose() {
    /*单击TAB选项卡*/
    $(".tabs-inner").click(function () {
        var subtitle = $(this).children(".tabs-closable").text();
        $(".parentMenu").removeClass("menuS");
        if (subtitle != null && subtitle != '') {
            var tab = $('#tabs').tabs('getTab', subtitle);
            $("#parent" + tab.panel('options').id).addClass("menuS");
            $("#hidCurTopMenuId").val(tab.panel('options').id);
        }
    });
    /*关闭TAB选项卡*/
    $(".tabs-close").click(function () {
        var subtitle = $(this).parent().find(".tabs-closable").text();
        $('#tabs').tabs('close', subtitle);
        setdownArrow();
        return false;
    });
    /*双击关闭TAB选项卡*/
    $(".tabs-inner").dblclick(function () {
        var subtitle = $(this).children(".tabs-closable").text();
        $('#tabs').tabs('close', subtitle);
        setdownArrow();
    });
}

function  updateCurrentTab(title,url){
	  var currTab = $('#tabs').tabs('getSelected');
	  var content = '<iframe scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>';
	  $('#tabs').tabs('update', {
          tab: currTab,
          options: {
        	  title:title,
              content: content
          }
      });
}

function UpdateTabFirst() {
    var firstTab = $('#tabs').tabs('getTab', '首页');
    var url = $(firstTab.panel('options').content).attr('src');
    var content = '<iframe scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>';
    if (url != undefined) {
        $('#tabs').tabs('update', {
            tab: firstTab,
            options: {
                content: content
            }
        });

        var ieset = navigator.userAgent;
        if (ieset.indexOf("MSIE 6.0") > -1 || ieset.indexOf("Chrome") > -1) {
            var currTab1 = $('#tabs').tabs('getTab', '首页');
            setTimeout(function () { refreshTab(currTab1) }, 0);
        }
    }
}
function refreshTab(refresh_tab) {
    if (refresh_tab && refresh_tab.find('iframe').length > 0) {
        var _refresh_ifram = refresh_tab.find('iframe')[0];
        var refresh_url = _refresh_ifram.src;
        _refresh_ifram.contentWindow.location.href = refresh_url;
    }
}

//刷新当前Tab
function refreshCurrentTab(){
	refreshTab($('#tabs').tabs('getSelected'));
}

//绑定菜单事件
function tabCloseEven() { 
    //刷新
    $('#mm-tabupdate').click(function () {
        var currTab = $('#tabs').tabs('getSelected');
        var url = $(currTab.panel('options').content).attr('src');
        if (url != undefined) {

            $("#divLoading").show();
            $('#tabs').tabs('update', {
                tab: currTab,
                options: {
                    content: createFrame(url)
                }
            });
        }
    });
    //关闭当前
    $('#mm-tabclose').click(function () {
        var currtab_title = $('#mm').data("currtab");
        $('#tabs').tabs('close', currtab_title);
        setdownArrow();
    });
    //全部关闭
    $('#mm-tabcloseall').click(function () {
        $('.tabs-inner span').each(function (i, n) {
            var t = $(n).text();
            if (t != '首页') {
                $('#tabs').tabs('close', t);
            }
        });
        setdownArrow();
    });
    //关闭除当前之外的TAB
    $('#mm-tabcloseother').click(function () {
        var prevall = $('.tabs-selected').prevAll();
        var nextall = $('.tabs-selected').nextAll();
        if (prevall.length > 0) {
            prevall.each(function (i, n) {
                var t = $('a:eq(0) span', $(n)).text();
                if (t != '首页') {
                    $('#tabs').tabs('close', t);
                }
            });
        }
        if (nextall.length > 0) {
            nextall.each(function (i, n) {
                var t = $('a:eq(0) span', $(n)).text();
                if (t != '首页') {
                    $('#tabs').tabs('close', t);
                }
            });
        }
        setdownArrow();
        return false;
    });
    //关闭当前右侧的TAB
    $('#mm-tabcloseright').click(function () {
        var nextall = $('.tabs-selected').nextAll();
        if (nextall.length == 0) {
            //msgShow('系统提示','后边没有啦~~','error');
            alert('后边没有啦~~');
            return false;
        }
        nextall.each(function (i, n) {
            var t = $('a:eq(0) span', $(n)).text();
            $('#tabs').tabs('close', t);
        });
        return false;
    });
    //关闭当前左侧的TAB
    $('#mm-tabcloseleft').click(function () {
        var prevall = $('.tabs-selected').prevAll();
        if (prevall.length == 0) {
            alert('到头了，前边没有啦~~');
            return false;
        }
        prevall.each(function (i, n) {
            var t = $('a:eq(0) span', $(n)).text();
            $('#tabs').tabs('close', t);
        });
        return false;
    });

    //退出
    $("#mm-exit").click(function () {
        $('#mm').menu('hide');
    });
}
//加载超时信息
function LoadByPwd() {
    $('#win').dialog({
        title: '登录已超时，请重新输入密码：',
        width: 300,
        height: 150,
        closed: false,
        cache: false,
        closable: false,
        href: '/admin/login.html',
        modal: true
    });
}
$(function () {
	eval("");
    document.onkeydown = function (e) {
        var ev = document.all ? window.event : e;
        if (ev.keyCode == 13) {
            if ($(document.activeElement).parent().parent().parent().find('.button').first()) {
                $(document.activeElement).parent().parent().parent().find('.button').first().click();
            } else if (typeof ($("#tabs .panel:visible iframe")[0]) != 'undefined' && $("#tabs .panel:visible iframe")[0].contentWindow.$('.blueButton').first().html() != null) {
                $("#tabs .panel:visible iframe")[0].contentWindow.$('.blueButton').first().click();
            }
        }
    }
    tabCloseEven();
    $(".parentMenu").mouseenter(function () {
        OpenSecondMenu($(this).attr("id").replace("parent", ""));
    });
    $(".parentMenu").mouseleave(function () {
        TopMenuMouseOut();
    });
    $(".memberInfo").mouseenter(function () {
        $(".MImore").fadeIn(250);
    });
    $(".memberInfo").mouseleave(function () {
        $(".MImore").fadeOut(250);
    });

    //ConstraintActive();
    //打开首页
    childAddTab('首页', '../core/admin/index/show.do', '');
    //关闭默认tab
    $('.tabs-inner span').each(function (i, n) {
        var t = $(n).text();
        if (t != '首页') {
            $('#tabs').tabs('close', t);
        }
    });
});
////打开二级菜单
//function OpenSecondMenu(arg) {
//    $(".parentMenu").removeClass("menuS");
//    $("#parent" + arg).addClass("menuS");
//    $('#' + arg).show();
// $('#' + arg).css({"top":"auto","top":"80%"});
//}
//点击菜单打开窗口



function OpenSecondMenu(arg) {
	$(".parentMenu").removeClass("menuS");
	$("#parent" + arg).addClass("menuS");

	//子菜单展示
	var left_height = $(".leftMenu").height();  //获得左侧菜单总高度
	$(".parentMenu").mouseover(function(){
		var secondHeight = $(this).find(".secondFloat").height();  //或则当前菜单子菜单高度
		var leftmenu_index = $(this).index();                    //获得当前li的索引，即是第几个
		var top = leftmenu_index*70+22;                               //获得距离顶部距离
		var upHeight = $(".upHeight").val();                         //向上滚动的高度
		$(this).find(".secondFloat").show();
		$(this).find(".secondFloat").css({"top":top+"px"});
		
		//或者滚动高度，先获得属性，并通过截取字符串的形式得到相关参数
		var rollHeight= parseInt( $('.roll_ul').css('marginTop') );  //获获得UL向上滚动高度
		var middel_height =  (left_height-top)/2;              //获得左侧菜单一半高度
		//判断距离顶部高度大于左侧菜单一半的高度,并且是否滚动
		if(top > middel_height && upHeight==0){
			var bottom_height = parseInt(left_height-top-70);
			$(this).find(".secondFloat").css({"bottom":bottom_height+"px","top":"auto"});
		}
		if(top > middel_height && upHeight!=0){
			var up_bottom_height = parseInt(left_height-top-70);
			$(this).find(".secondFloat").css({"bottom":up_bottom_height+"px","top":"auto"});
		}
		
		//判断是否滚动,并计算滚动值 如果滚动，rollHeight不为0
		if(rollHeight!=0){
			var roll_bottom_height = parseInt(left_height-top-70)-rollHeight;
			if(top < middel_height && upHeight!=0){
				var roll_top = parseInt(top)+rollHeight;
				
				$(this).find(".secondFloat").css({"top":roll_top+"px"});
			}
			if(top > middel_height && upHeight!=0){
				
				$(this).find(".secondFloat").css({"bottom":roll_bottom_height+"px","top":"auto"});
			}
		}
		
		//判断子菜单是否需要加宽
		
		if(parseInt(secondHeight+top) > parseInt(left_height)){
			$(this).find("ul").find("li").find("ul").css("width","480px");
		}
		
		
		
		
	})
}



function OpenWindow(obj) {
	
    var sobState = "1";
    var $this = $(obj);
    var href = $this.attr('src');
    var title = $this.text();
    addTab1(title, href, $this.attr('index'));
    $('.secondFloat').hide();
}
//鼠标移出一级菜单时关闭二级菜单
function TopMenuMouseOut() {
    $(".parentMenu").removeClass("menuS");
    $("#parent" + $("#hidCurTopMenuId").val()).addClass("menuS");
    $('.secondFloat').hide();
}

//修改密码
function userPwd() {
    $('#win').dialog({
        title: '修改密码',
        width: 300,
        height: 220,
        closed: false,
        cache: false,
        closable: true,
        href: '/User/PwdEdit',
        modal: true
    });
}
//退出
function signOut() {
    $.messager.confirm('系统提示', "确定要退出系统吗？", function (r) {
        if (r) {
            $("#hidout").val("1");
            window.location.href = '/admin/login.html';
        }
    });
}
