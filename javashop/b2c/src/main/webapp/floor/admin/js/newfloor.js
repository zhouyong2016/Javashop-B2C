/**
 * Created by xulipeng on 2017/1/11.
 */
$(function () {
    $("#add_floor_button").click(function(){
    	 layer.open({
             title:"添加楼层",//标题
             maxmin :true,//右上角可否放大缩小
             offset: '100px',//弹框位置
             type:2,//弹框的类型
             shade: [0.3, '#000'],//黑色背景
             shadeClose:false,//黑色背景是否可以点击关闭
             content:ctx+"/core/admin/floor/add-floor.do?id="+pageid,//内容的URL
             area:['726px','363px'],//弹框大小
             scrollbar: false,//是否允许浏览器出现滚动条
         });
	});
    
    //回车触发事件
    $("#_search").keydown(function(event){
        if(event.keyCode==13){
            alert("搜索事件")
        }
    });
})


/**
 * 删除楼层
 * @param id 楼层id
 * @returns
 */
function del(id) {
	if (!confirm("删除楼层会删除楼层所有数据,确定删除吗？")) {
		return;
	}
	$.Loading.show("正在删除......");
	$.ajax({
		url : ctx+"/core/admin/floor/delete.do?floor_id=" + id,
		type : "POST",
		dataType : 'json',
		success : function(result) {
			if (result.result == 1) {
				$.Loading.success(result.message);
				parent.refreshCurrent();
			}
			if (result.result == 0) {
				$.Loading.error(result.message);
			}
		},
		error : function(e) {
			$.Loading.error("出现错误 ，请重试");
		}
	});
}


