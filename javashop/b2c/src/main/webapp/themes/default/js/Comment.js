
var CommentList={
		init:function(){
			var self  = this;		
		//	self.bindAllPageEvent();
		 	self.bindTabEvent();
		},
		
		/**
		 * 评论分页点击事件绑定
		 */
		bindDiscussPageEvent:function(){
			var self = this;
			$("#discuss_list_wrapper .page a.unselected").click(function(){
				var page = $(this).attr("page");
				self.loadDiscussList(page);
				return false;
			});
		},
		
		/**
		 * 评论分页点击事件绑定
		 */
		bindAskPageEvent:function(){
			var self = this;
			$("#ask_list_wrapper .page a.unselected").click(function(){
				var page = $(this).attr("page");
				self.loadAskList(page);
				return false;
			});
		},
		
		bindTabEvent:function(){
			//选 项卡切换
			$("#comment_tab li.discuss").click(function(){
				$("#discuss_wrapper").show();
				$("#ask_wrapper").hide();
				$("#record_wrapper").hide();
				$("#comment_tab li").removeClass("selected");
				$(this).addClass("selected");
			});

			$("#comment_tab li.ask").click(function(){
				$("#discuss_wrapper").hide();
				$("#ask_wrapper").show();
				$("#record_wrapper").hide();
				$("#comment_tab li").removeClass("selected");
				$(this).addClass("selected");
			});
			
			//成交记录
			$("#comment_tab li.record").click(function(){
				$("#discuss_wrapper").hide();
				$("#ask_wrapper").hide();
				$("#record_wrapper").show();
				$("#comment_tab li").removeClass("selected");
				$(this).addClass("selected");
			});
			
		}
	};
	

/**
 * 评论表单提交
 */
var dialog;
var CommentForm={
	init:function(){		
		var self  = this;
		Grade.init();
	},
	
	formSubmit:function(btn,form){
		var options = { 
				url : "api/shop/commentApi!add.do?ajax=yes",
				type : "POST",
				dataType : "json",
				success : function(result) {
	 				if(result.result==1){
						 alert("提交成功，请等待管理员审核!");
						 form.get(0).reset();
						 window.location.reload();
			 		}else{
			 			alert(result.message);
				 	}
				},
				error : function(e) {
					alert("出现错误 ，请重试");
					btn.attr("disabled",false); 
				}
		};
		form.ajaxSubmit(options);
	},
}


/**
 * 评价
 */
var Grade={
		clicked:false,	
		init:function(){
			var self = this;
			$("#discuss_form_wrapper .grade a").hover(
					function(){
						self.hover($(this));
					},
					function(){
						if(!self.clicked){
							 var index = parseInt($("#grade_input").val());							 
							 self.selectced(index);
						}
					}
			);

			$("#discuss_form_wrapper .grade a").click(function(){
				 self.clicked=true;
				 var index = parseInt($(this).text());
				 self.selectced(index);
			});
			
		},
		hover:function(curritem){
			$("#discuss_form_wrapper .grade a").slice(index-1,5).removeClass("selected");
			this.clicked= false;
			var index = parseInt(curritem.text());
			this.selectced(index);
			 
		},
		selectced:function(index){
			$("#discuss_form_wrapper .grade a").slice(0,index).addClass("selected");
			$("#gradevalue").html(index);
			$("#grade_input").val(index);
		}
	}