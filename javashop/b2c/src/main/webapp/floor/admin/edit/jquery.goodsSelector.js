/**
 * Javashop 商品选择器插件
 * @author:kingapex
 * @date:2016-03-21
 * 
 * 用法：
 * 		var options ={
 *			selected_data_url:ctx+'/shop/admin/floorGoods!listGoodsJson.do?floorid=1'
 *		}
 *		$(".floor_goods1").goodsSelector(options);
 *  	则会在$(".floor_goods1")中生成一个商品选择器
  
 *   
 *  
 * 可选参数：  
 *  selected_data_url: 指定已经选中的grid的商品数据json URL
 * 	selector_data_url:选器商品Grid的商品数据 json url，默认为：/shop/admin/goods-select!listGoodsById.do
 *  select_btn_text: 选择按钮的文字
 *  un_select_btn_text:取消选择按钮的文字
 *  selected_text:已选中的文字
 *  goodsid_name:向后台post的商品id名字，默认为：goods_id_array
 */

(function($){
	
	$.fn.goodsSelector = function( options){
		
	    var defaults = {
	            'selector_data_url': ctx+'/core/admin/floor-goods-select/list-goods-by-id.do?pageid='+pageid,
	            'select_btn_text':'选择此商品',
	            'un_select_btn_text':'取消选择',
	            'selected_text':'已选择',
	            'goodsid_name':'goods_id_array'
	    };
	    var settings = $.extend(defaults, options);
	    
		 return this.each(function(){
			 
			//这个this是定义要在哪里生成选择器
			 var $this =   $(this);
			 
		 
				//生成选择器的基本结构
				 $this.append(tpl);
				 
				 //生成样式表
				 $this.append(css_html); 
				 
				 new Selector($this,settings);
			  
			 
		 });	
	}
 
   var   Selector=function(container,settings){
	   
	   //全局本对象变量
	   var self = this;
	   
	   //初初化父容器
	   this.container=container; 
	   
	   //记录选择器的gird的dom对象
	   this.selector_grid = container.find('.goods_sel_tb')
	   
	   
	   /**
	    * 绑定tab的切换事件
	    */
	   this.bindTabEvent=function(){
		
		   container.find(".contentTab>ul>li").click(function() {
				var tabid = $(this).attr("tabid");
				container.find(".contentTab>ul>li").removeClass("contentTabS");
				$(this).addClass("contentTabS");
				container.find(".tab-page .tab-panel").hide();
				container.find(".tab-panel[tabid=" + tabid + "]").show();
				//如果是第一次切换显示到已选择癌，则resize
				// $(".selected_goods .division span.none_msg").size()==0 && 
				if("2"==tabid ){
					self.toggleNoneMsg();
				}
			});
	   };
	   
	   
	   /**
	    * 创建已经选择的商品列表
	    */
	   this.createSelectedGrid=function(){
		   
			//添加已经选择商品的table
		   container.find(".selected_goods .division").append("<div class='selected_wrapper'><table class='selected_goods_tb' ></table></div>");
		   
		   //记录已经选择grid的dom对象
		   this.selected_grid =container.find(".selected_goods_tb");
		   var grid_columns=[[
								{field:'g_temp_id',checkbox:true },	   
					            {field:'sn',title:'编号',width:100 },
					            {field:'name',title:'名称',width:400},
					            {field:'price',title:'价格' },
					            {field:'store',title:'库存量'},
			   		            {field:'op_btn',width:150,title:'操作',formatter:self.selected_btn_formater},
			   		            {field:'goods_id_array',title:'goodsid',width:0,hidden:'true',formatter:self.goodsid_hiiden_formater}
					          
				    ]];
		   
			//创建已经选择的商品列表
		   if(settings.selected_data_url){
			   container.find(".selected_goods_tb").datagrid({
					url:settings.selected_data_url,
					fitColumns:true,
					data:[],
					columns:grid_columns,
				    
				    onLoadSuccess:self.selectedGridLoadSuccess
				});
		   }else{
			   container.find(".selected_goods_tb").datagrid({
					fitColumns:true,
					data:[],
					columns:grid_columns,
				    
				    onLoadSuccess:self.selectedGridLoadSuccess
				});
		   }
		    
			
	   };
	  
	   
	   /**
		* 批量选择事件
		*/
	   this.batchChooseEvent=function(){
		   container.find(".batch_sel_btn").click(function(){
			   
			   //获取选择器grid里选中的行
			   var allrows =  self.selector_grid.datagrid("getSelections");
				 for(i =0;i<allrows.length;i++){
					 var row  =  allrows[i];
					 if(row.selected=="selected"){
						 continue;
					 }
						 row.selected="selected";
						 self.selected_grid.datagrid('appendRow',row);  //添加到已选择
						 var goodsid  = row.goods_id;
						  container.find(".cancel_select_btn[goodsid="+goodsid+"]").bind("click",self.cancelFromSelected)
						  var btn= container.find(".select_btn[goodsid="+goodsid+"]")
						  
						 btn.text(settings.selected_text)
						 btn.unbind("click").bind("click",self.cancelChooseOne)
						.removeClass("select_btn").addClass("un_select_btn"); //重新绑定事件，并切换样式
				 }
				 
				 
		   });		   
	   }
	   
	   
	   
	   /**
		* 批量取消事件
		*/
	   this.batchCancelEvent=function(){
		   container.find(".batch_cancel_btn").click(function(){
			   
			   //获取已选择grid里选中的行
			   var allrows =  self.selected_grid.datagrid("getSelections");
				 for(i =0;i<allrows.length;i++){
					 var row  =  allrows[i];
					 
					 var goodsid  = row.goods_id;
					 var un_select_btn_ar =  container.find(".un_select_btn[goodsid="+goodsid+"]");
					
					 //如果在选择器列表中存在，则点击之
					 if( un_select_btn_ar.size() > 0){
						 un_select_btn_ar.click();
					 }else{
						 //如果选择器列表中不存在，直接删除
						 var index=  self.selected_grid.datagrid('getRowIndex',row)
						 if(index>=0){
							 self.selected_grid.datagrid('deleteRow',index);  //从已选择中移除
						 }
						
					 }
					 
					
					 
				 }
				 
				 //切换提示
				 self.toggleNoneMsg();
		   });		   
	   }
	   
	   /**
	    * goodsid隐藏域格式化
	    * 是用于向后台传值用的
	    */
	   this.goodsid_hiiden_formater=function(value,row,index){
		   
		   return "<input type='hidden' name='"+settings.goodsid_name+"' value='"+row.goods_id+"'>";
	   }
	   
	   
	   /**
	    * 已选择列中操作按钮的格式化操作
	    */
	   this.selected_btn_formater =function(value,row,index){
			return "<span class='cancel_select_btn'  goodsid='"+ row.goods_id +"'>"+settings.un_select_btn_text+"</span>"
		   
	   };	   
	   
	   /**
	    * 选择器中操作按钮的格式化操作
	    */
	   this.selector_btn_formater =function(value,row,index){
		   
			var selected_rows = self.selected_grid.datagrid("getRows");
			
			for(i =0;i<selected_rows.length;i++){
				
				var sel_row  =  selected_rows[i];
				
				//此商品已被选中了 
				if(sel_row.goods_id ==  row.goods_id){
					 row.selected="selected";
					 return "<span class='un_select_btn'   goodsid='"+ row.goods_id +"'>"+settings.selected_text+"</span>"
				}
				 
				
			}
			
			return "<span class='select_btn'  goodsid='"+ row.goods_id +"'>"+settings.select_btn_text+"</span>"
		   
	   };
	   
	   /**
	    * 选择器grid加载完成事件
	    * 绑定操作按钮的事件
	    */
	   this.selectorGridLoadSuccess=function(){

			 //绑定选择商品按钮事件
		    container.find(".select_btn").bind("click",self.chooseOne);		 
		    
		    //绑定选择商品按钮事件
		    container.find(".un_select_btn").bind("click",self.cancelChooseOne);	
		   
		    container.find(".un_select_btn,.select_btn").hover(
		    		
			    function(){
			    	if($(this).hasClass("un_select_btn")){
			    		$(this).text(settings.un_select_btn_text);
			    		
			    	}
			    },
			    function(){
			    	if($(this).hasClass("un_select_btn")){
			    		$(this).text(settings.selected_text);
			    	}
			    }
		    
		    );
		    
		    self.searchEvent();
	   };
	   

	   
	   
	   /**
	    * 已经选择grid加载完成事件
	    * 绑定操作按钮的事件
	    */
	   this.selectedGridLoadSuccess=function(){

		   
		   /**
	    	 *当已经选择的列表加载完成后，创建选择器的列表
	    	 *因为要在选择器列表中计算哪些已经选中了
	    	 */
	    	self.createSelector();
	    	
	    	
			 //绑定选择商品按钮事件
		    container.find(".cancel_select_btn").bind("click",self.cancelFromSelected)
		    
	   };
	   
	   /**
	    * 由选中区移出一行已选中的商品
	    */
	   this.cancelFromSelected=function( ){
		   var goodsid = $(this).attr("goodsid");
		   //如果在选择器中存在，则直接调用其点击事件
		   container.find(".un_select_btn[goodsid="+goodsid+"]").click();
		   
		 //有可能不在选择器中列出，所以要再重新查找当前选中列表，并移除
			 var allrows =  self.selected_grid.datagrid("getRows");
			 for(i =0;i<allrows.length;i++){
				 var row  =  allrows[i];
				 if(row.goods_id == goodsid){
					 row.selected="unselected";
					 
					 var index=  self.selected_grid.datagrid('getRowIndex',row)
					 self.selected_grid.datagrid('deleteRow',index);  //从已选择中移除
					 
					 break;
				 }
			 }
		   
	   };
	   
	   
	   /**
	    * 选中一个商品
	    */
	   this.chooseOne=function(){
		   
		   	 var btn =$(this);
		   	 
			 //根据当前商品id找到行
			 var goodsid = btn.attr("goodsid");

			 //所有选择器的row
			 var allrows =  self.selector_grid.datagrid("getRows");
			 for(i =0;i<allrows.length;i++){
				 var row  =  allrows[i];
				 if(row.goods_id == goodsid){
					 row.selected="selected";
					 self.selected_grid.datagrid('appendRow',row);  //添加到已选择
					 
					  container.find(".cancel_select_btn[goodsid="+goodsid+"]").bind("click",self.cancelFromSelected)
					  
					 btn.text(settings.selected_text)
					 
					 btn.unbind("click").bind("click",self.cancelChooseOne)
					.removeClass("select_btn").addClass("un_select_btn"); //重新绑定事件，并切换样式
					break;
				 }
			 }		   
	   };
	   
	   /**
	    * 取消选中一个商品
	    */
	   this.cancelChooseOne=function(){
		   	 var btn =$(this);
		   	 
			 //根据当前商品id找到行
			 var goodsid = btn.attr("goodsid");
			 
			 //所有选择器的row
			 var allrows =  self.selected_grid.datagrid("getRows");
			 for(i =0;i<allrows.length;i++){
				 var row  =  allrows[i];
				 if(row.goods_id == goodsid){
					 row.selected="unselected";
					 
					 var index=  self.selected_grid.datagrid('getRowIndex',row)
					 self.selected_grid.datagrid('deleteRow',index);  //从已选择中移除
					 btn.text(settings.select_btn_text)
					 
					 btn.unbind("click").bind("click",self.chooseOne)
					  .removeClass("un_select_btn").addClass("select_btn"); //重新绑定事件，并切换样式
					 
					break;
				 }
			 }
	   };
	   
	   /**
	    *创建选择器Grid
	    */
	   this.createSelector =function (){
		   
		   this.selector_grid.datagrid({
		       	collapsible:true,
		   	    url:settings.selector_data_url,
		   	    columns:[[
		   					{field:'g_temp_id',checkbox:true},	   
		   		            {field:'sn',title:'编号' },
		   		            {field:'name',title:'名称' },
		   		            {field:'price',title:'价格' },
		   		            {field:'store',title:'库存量' },
		   		            {field:'op_btn',title:'操作',formatter:self.selector_btn_formater}
		   	    ]],
		   	    pagination:true,
		   	    onLoadSuccess:self.selectorGridLoadSuccess
		   	});
		   
	   };
	   
	   /**
	    * 切换是否有商品选中的提示及选中grid的显示和隐藏
	    */
	   this.toggleNoneMsg=function(){
		   
		   //查找已经选中的个数
		   var selected_num = self.selected_grid.datagrid("getRows").length;
		   
		   if(selected_num==0){
			   
			   container.find(".selected_wrapper").hide();
			   
			   container.find(".none_msg").show();
			   
		   }else{
			   container.find(".selected_wrapper").show();
			   container.find(".none_msg").hide();
			   self.selected_grid.datagrid('resize',{
				    width:600 ,
					height:'auto' 
			   });
		   }
	   };
	   
	   /**
	    * 搜索商品
	    */
	   this.searchEvent=function (){
		   container.find(".serach_btn").click(function(){
			   var keyword = container.find(".keyword").val();
				
				self.selector_grid.datagrid('load', {
					 stype:0,
					 keyword:keyword,
					 page:1
			    }); 
		   });
		};
	   
	   
	   this.bindTabEvent();
	   this.createSelectedGrid();
	   this.batchChooseEvent();
	   this.batchCancelEvent();
	   
	   
   }
	 
	var tpl='<div class="contentTab">';
	tpl+='<ul class="tab">';
		tpl+='<li tabid="1" class="contentTabS" >选择商品</li> ';
			tpl+='<li tabid="2"  >已选商品</li> ';
			tpl+='</ul>';
	tpl+='</div>';
		
	tpl+='<div class="shadowBoxWhite wf100 whiteBox">';
		tpl+='<div class="text">';
			tpl+='<div class="tab-page">';
			
			
				tpl+='<div tabid="1"  class="tab-panel">';
					tpl+='<div class="buttonArea">';
					
						tpl+='<span id="searchbtn" style="float:left;display:block">';	
							tpl+='<a href="javascript:void(0)" class="button blueButton batch_sel_btn"  >批量选择</a>';
					    tpl+='</span>';
								
					    tpl+='<span style="float:right;height:28px;"> ';
					    	 tpl+='关键字:';
					    	  tpl+='<input class="input_text keyword" type="text"     />';
					    	 tpl+='<a href="javascript:void(0)" class="button b_fr serach_btn"  >搜索</a>';
					    tpl+='</span>';		
					 tpl+='</div>';
					
					 tpl+='<div class="division clearfix">';	
						 tpl+='<table   class="goods_sel_tb"></table>';	
					tpl+='</div>';	
				tpl+='</div>';	
				

				tpl+='<div tabid="2"  class="tab-panel selected_goods"  style="display:none">';
					tpl+='<div class="buttonArea">';
					
					tpl+='<span id="searchbtn" style="float:left;display:block">';	
						tpl+='<a href="javascript:void(0)" class="button blueButton batch_cancel_btn"  >批量取消</a>';
				    tpl+='</span>';
				tpl+='</div>';
				
					tpl+='<div class="division clearfix" style="width:500px">';
						tpl+=' <span class="none_msg">还没有选择任何商品</span>';
					tpl+='</div>';
				tpl+='</div>';
				
			tpl+='</div>';
		  tpl+='</div>';
		tpl+='</div>';
		
		
	var css_html="<style>"
			
		 
	+".tab-page .datagrid-body .datagrid-cell, .datagrid-footer .datagrid-cell {  "
	+"    line-height: unset;                                                     "
	+"}                                                                           "
	+".select_btn{                                                                "
	+"	background-color: #07d;                                                   "
	+"	border-color: #006cc9;                                                    "
	+"	color: #fff;                                                              "
	+"	cursor: pointer;                                                          "
	+"	margin-bottom: 0;                                                         "
	+"    padding: 4px 12px;                                                      "
	+"    vertical-align: middle;                                                 "
	+"    font-size:12px;                                                         "
	+"    border-radius: 2px;                                                     "
	+"}                                                                           "
	+".un_select_btn,.cancel_select_btn{                                          "
	+"	background-color: #fff;                                                   "
	+"	border-color: #006cc9;                                                    "
	+"	color: #888;                                                              "
	+"	cursor: pointer;                                                          "
	+"	margin-bottom: 0;                                                         "
	+"    padding: 4px 12px;                                                      "
	+"    vertical-align: middle;                                                 "
	+"    font-size:12px;                                                         "
	+"    border-radius: 2px;                                                     "
	+"}                                                                           "
	+".un_select_btn:hover,.cancel_select_btn:hover{                              "
	+"	background-color: #d00;                                                   "
	+"	 color: #fff;                                                             "
	+"}                                                                           "
		 
	+"</style>";
		
	
})(jQuery);
