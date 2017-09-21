/**
 * Created by lishida on 2017/2/4.
 */
layui.config({
	base: '../adminthemes/version3/js/'
}).use(['element', 'layer','form', 'layedit', 'laydate','code'], function() {
    var element = layui.element(),
        layer = layui.layer,
        form = layui.form(),
        layer = layui.layer,
        layedit = layui.layedit,
        laydate = layui.laydate,
        $ = layui.jquery;
    layui.code({
        about:false
    });

    /*下拉按钮开始*/
    $(".drop-down-show").on("click",function(event){
        event.stopImmediatePropagation();
        //$(".dropdown").toggle();
        if (this.parentNode.children[1].style.display==""||this.parentNode.children[1].style.display=="none"){
            $(".dropdown-menu").hide();
            this.parentNode.children[1].style.display="block";
        }else {
            this.parentNode.children[1].style.display="";
        }
    });
    /*$(document).bind("click",function(){
        $(".dropdown-menu").hide();
    });*/
    /*下拉按钮结束*/

    //创建新tab开始
    newTab =function (title,icon,href){
        window.parent.tab.tabAdd({
            title: title,
            /*icon: icon,*/
            href: href
        });
    }
    //创建新tab结束


})

/*var dialog = {
    // 错误弹出层
    error: function(message) {
        layer.open({
            content: '测试回调',
            success: function(layero, index){
                console.log(layero, index);
            }
        });
    },

    //成功弹出层
    success : function(message,url) {
        layer.open({
            title:message,//标题
            maxmin :true,//右上角可否放大缩小
            type:2,//弹框的类型
            shade: [0.3, '#000'],//黑色背景
            shadeClose:false,//黑色背景是否可以点击关闭
            content:url,//内容的URL
            area:['750px','300px'],//弹框大小
            scrollbar: true,//是否允许浏览器出现滚动条
            btn : ['是','否'],
            yes : function(){
                location.href=url;
            },
        });
    },

    // 确认弹出层
    confirm : function(message, url) {
        layer.open({
            content : message,
            btn : ['是','否'],
            yes : function(){
                location.href=url;
            },
            cancel: function(index){
                if(confirm('确定要关闭么')){
                    layer.close(index)
                }
                return false;
            }
        });
    },

    //无需跳转到指定页面的确认弹出层
    toconfirm : function(message) {
        layer.open({
            content : message,
            btn : ['确定'],
        });
    },
}

var Loading = {
    // 错误弹出层
    error: function(message) {
        layer.msg('保存失败', {
            time: 2000, //2s后自动关闭
        });
    },

    //成功弹出层
    success : function(message,url) {
        layer.msg(message, {
            time: 2000, //2s后自动关闭
        });
    },

    // 确认弹出层
    show : function(message, url) {
        layer.msg('正在保存..',{
            time:0,//0即为不消失
            success: function(layero, index){
                alert(layero, index);
            }//可以设置回调函数
        });
    }
}*/
