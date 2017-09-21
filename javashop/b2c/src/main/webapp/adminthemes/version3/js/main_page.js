layui.config({
	base: ctx+'/adminthemes/version3/js/'
}).use(['element', 'layer', 'navbar', 'tab'], function() {
	var element = layui.element()
	$ = layui.jquery,
		layer = layui.layer,
		navbar = layui.navbar(),
		tab = layui.tab({
			elem: '.layout-nav-card' //设置选项卡容器
		});

	        //禁止网页文本被选中：
            if(document.all){
                document.onselectstart= function(){return false;}; //for ie
            }else{
                document.onmousedown= function(){return false;};
                document.onmouseup= function(){return true;};
            }
            document.onselectstart = new Function('event.returnValue=false;')   
	
           
            //只显示点击的一级菜单的子菜单
            $('#top-nav').on('click','li',function(){
            	 var _id = this.id;
            	 $("#side ul li").css("display","none");
            	 $("#side ul").find('li[id="'+_id+'"]').css("display","block");
            	 $("#_id").css("display","block");
            	 //默认点击第一个二级菜单
            	/* $("#side ul").find('li[id="'+_id+'"]')[0].children[0].click();*/
            })
            
            //默认点击点击一级菜单第一个
            $('.beg-layout-side-left').find('li[id=1]').click();
            //创建存储二级菜单的容器
            navbar.set({
                elem: '#side', //存在navbar数据的容器ID
                data: ''
            });
          
            //监听点击事件
            navbar.on('click(side)', function (data) {
                tab.tabAdd(data.field);
            });
		    //添加新tab
			element.on('nav(user)', function(data) {
				var $a = data.children('a');
				if($a.data('tab') !== undefined && $a.data('tab')) {
					tab.tabAdd({
						title: $a.children('cite').text(),
						href: $a.data('url')
					});
				}
			});
            
             

              
              /*  对全部tab选项卡的关闭等操作 */
                $("#contextmenu").on('click','ul li', function () {
                    var li = $(".layui-tab-title li")
                    var num = $(this).index();
                    //num为0 是刷新 1 是关闭其他 2 是关闭全部
                    if(num==0){
                    	var index= $('.layui-tab-title .layui-this').index();
                    	$(".layui-tab-content iframe")[index].contentWindow.location.reload(true);
                    }else if(num==1){
                    	 for (var i = 1;i<li.length;i++){
                             if(li[i].className==""){
                                 li[i].lastChild.click();
                             }
                         }
                    }else{
                    	 for (var i = 1;i<li.length;i++){
                             li[i].lastChild.click();
                         }
                         i=0;
                    }
                    $(".layout-tab-contextmenu").css("display","none");
                   
                });
                //tab选项卡操作的显示与隐藏
                $(".tabs-control").on('mouseover', function () {
                	 $(".layout-tab-contextmenu").css("display","block");
                });
                $(".tabs-control").on('mouseout', function () {
               	 $(".layout-tab-contextmenu").css("display","none");
               });
              
                //点击退出
                $("#logout_btn").click(function(){
				
				if( !confirm("确认退出吗？") ){
					return false;	
				}
				
				var options = {
					url : "../core/admin/admin-user/logout.do",
					type : "POST",
					dataType : 'json',
					success : function(result) {				
						if(result.result==1){
							var url = "${ctx}/admin/login.do";
							location.href=url;
						}else{
							$.Loading.error(result.message);
						}
					},
					error : function(e) {
						$.Loading.error("出现错误，请重试");
					}
				};
				$.ajax(options);		
			})
	           
});
