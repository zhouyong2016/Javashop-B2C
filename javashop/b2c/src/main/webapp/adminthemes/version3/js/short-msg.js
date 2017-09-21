var ShortMsg={
		init:function(){
			var self = this;
			self.checkNewMsg();
			function check(){
				self.checkNewMsg();
			}
			
		},
		//对获取的数据进行操作
		loadNewMessage:function(msgList){
			var box =$(".main-logo .message-list ul").empty();
			console.log(msgList)
			$.each(msgList,function(i,msg){
				box.append("<li><span class='name'><a class='mes' href='javascript:void(0);' onclick='newTab(\""+msg.title+"\",\".."+msg.url+"\")' >"+msg.content+"</a></span></li>");
			});
		},
		/**
		 * 检测是否有新消息
		 */
		checkNewMsg:function(){
			var self= this;
			$.ajax({
				url:ctx+'/core/admin/short-msg/list-new.do',
				dataType:'json',
				cache:false,
				success:function(result){
					
					if(result.result==1){
						var data= result.data;
						var count = data.length;
						if(count>0){
							$(".msg_num").text(count);
							$(".main-logo .message-list ul").find("li").remove();
							$(".msg_num").show();
						}else{
							$(".msg_num").hide();
							$(".message-list").hide();
							//$('#msglist').remove("class","layui-nav-child");
						}
						self.loadNewMessage(data);
						
					}else{
						//api返回错误
					}
					
					
				},
				error:function(){
				}
			});
		}

};

$(function(){
	ShortMsg.init();
});