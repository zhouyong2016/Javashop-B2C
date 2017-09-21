/**
 * Created by lishida on 2017/1/16.
 */
layui.use(['layer','element'], function() {
    var element = layui.element(),
        layer = layui.layer,
        $ = layui.jquery;
});
    var dialog = {
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
                area:['800px','500px'],//弹框大小
                scrollbar: true,//是否允许浏览器出现滚动条
                //anim:1,弹出动画
                //cancel: function(index){
                //    if(confirm('确定要关闭么')){
                //        layer.close(index)
                //    }
                //    return false;
                //}//系统弹框提示要退出?
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
        layer.msg('保存成功', {
            time: 2000, //2s后自动关闭
        });
    },

    // 确认弹出层
    show : function(message, url) {
        layer.msg('正在保存..',{
            time:0,//0即为不消失
            //success: function(layero, index){
            //    alert(layero, index);
            //}//可以设置回调函数
        });
    }
}

