var ShortMsg={
		init:function(){
			var self = this;
			self.checkNewMsg();
			$("#short_msg").hover(
				function(){
					$("#short_msg>.msglist").show();
					$(".sysmenu .num").show();
				},
				function(){
					$("#short_msg>.msglist").hide();
					$(".sysmenu .num").hide();
				}
			);
			function check(){
				self.checkNewMsg();
			}
			$('body').everyTime('30s',check);
		} 
		,
		loadNewMessage:function(msgList){
			var box =$(".sysmenu .msglist ul").empty();
			$.each(msgList,function(i,msg){
				box.append("<li><span class='name'><a class='mes' href='javascript:void(0);' onclick='addTab1(\""+msg.title+"\",\".."+msg.url+"\")' >"+msg.content+"</a></span></li>");
			});
			
		},
		/**
		 * 检测是否有新消息
		 */
		checkNewMsg:function(){
			var self= this;
			$.ajax({
				url:'../core/admin/short-msg/list-new.do',
				dataType:'json',
				cache:false,
				success:function(result){
					
					if(result.result==1){
						var data= result.data;
						var count = data.length;
						if(count>0){
							$(".sysmenu .num").text(count);
							$(".sysmenu .msglist ul").find("li").remove();
							$(".sysmenu .num").show();
						}else{
							$(".sysmenu .num").hide();
							$(".sysmenu .msglist").hide();
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