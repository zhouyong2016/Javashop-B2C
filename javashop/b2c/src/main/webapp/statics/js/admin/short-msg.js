var ShortMsg={
		
		init:function(){
			var self = this;
			self.checkNewMsg();
			$("#short_msg").hover(
				function(){
					$("#short_msg>.msglist").show();
				},
				function(){
					$("#short_msg>.msglist").hide();
				}
			);
			
			
			function check(){
				self.checkNewMsg();
			}
			$('body').everyTime('30s',check);
			
			
		} 
		,
		loadNewMessage:function(msgList){
		 																																																																																													
			var box =$("#short_msg>.msglist>ul").empty();
			$.each(msgList,function(i,msg){
				box.append("<li><span class='name'><a href='../"+msg.url+"' target='"+msg.target+"'>"+msg.title+"</a></span></li>");
			});
			
			$("#short_msg>.msglist>ul a").click(function(){
				Eop.AdminUI.load($(this));
				return false;
			});
			
		}
		,
		checkNewMsg:function(){
			var self= this;
			$.ajax({
				url:'../core/admin/shortMsg!listNew.do?ajax=yes',
				dataType:'json',
				success:function(result){
					var count = result.length;
					if(count>0){
						$("#short_msg>span").html("您有" + count +"条新消息" ).addClass("newmessage");
						$("#msg_view_btn").show();
					}else{
						$("#short_msg>span").html("您没有新消息" ).removeClass("newmessage");
					}
					self.loadNewMessage(result);
				},
				error:function(){
					 
				}
			});
		}

}
$(function(){
	ShortMsg.init();
});