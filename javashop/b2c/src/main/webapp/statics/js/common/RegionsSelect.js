/**
 * 地区下拉框jquery插件
 **/
(function($){ 
	
	$.fn.regionSelect = function(options){
		var opts = $.extend({}, $.fn.regionSelect.defaults, options);	
	    return this.each(function() {    
	    	createSelect($(this));
		});    
		/**
		 * 生成地区联动下拉框
		 */
		function createSelect(box){
			

			var selecthtml = '<select name="'+opts.provincename+'" id="'+opts.provincename+'" ><option value="0">请选择</option></select>';
			selecthtml += '<select name="'+opts.cityname+'" id="'+opts.cityname+'" ><option value="0">请选择</option></select>';
			selecthtml += '<select name="'+opts.regionname+'" id="'+opts.regionname+'" ><option value="0">请选择</option></select>';
			box.append(selecthtml);
			
			$.ajax({
				url:"/widget?type=regionsSelect&action=showJson&regionid=0",
				dataType:'json',
				success:function(areaAr){

					$.each(areaAr,function(i,area){
						
						var selected="";
						if(opts.provinceid== area.region_id){
							 selected="selected='selected'";
						}						
						box.find("[name='"+opts.provincename+"']").append("<option value='"+area.region_id+"' "+selected+">"+area.local_name+"</option>");
					});
				}
			});	
			if(opts.provinceid!=undefined){
				$.ajax({
					url:"/widget?type=regionsSelect&action=showJson&regionid="+opts.provinceid,
					dataType:'json',
					success:function(areaAr){

						$.each(areaAr,function(i,area){
							
							var selected="";
							if(opts.cityid== area.region_id){
								 selected="selected='selected'";
							}						
							box.find("[name='"+opts.cityname+"']").append("<option value='"+area.region_id+"' "+selected+">"+area.local_name+"</option>");
						});
					}
				});	
			}
			if(opts.cityid!=undefined){
				$.ajax({
					url:"/widget?type=regionsSelect&action=showJson&regionid="+opts.cityid,
					dataType:'json',
					success:function(areaAr){

						$.each(areaAr,function(i,area){
							
							var selected="";
							if(opts.regionid== area.region_id){
								 selected="selected='selected'";
							}						
							box.find("[name='"+opts.regionname+"']").append("<option value='"+area.region_id+"' "+selected+">"+area.local_name+"</option>");
						});
					}
				});	
			}
			$("#"+opts.provincename).change(function(){
				$("#"+opts.cityname+" option").remove(); 
				$("#"+opts.cityname).append('<option value="0">请选择</option></select>');
				$("#"+opts.regionname+" option").remove(); 
				$("#"+opts.regionname).append('<option value="0">请选择</option></select>');
				if($(this).val()==0)
					return;
				$.ajax({
					url:"/widget?type=regionsSelect&action=showJson&regionid="+$(this).val(),
					dataType:'json',
					success:function(areaAr){
						$.each(areaAr,function(i,area){
							box.find("[name='"+opts.cityname+"']").append("<option  value='"+area.region_id+"'>"+area.local_name+"</option>");
						});
					}
				});	
			});
			$("#"+opts.cityname).change(function(){
				$("#"+opts.regionname+" option").remove(); 
				$("#"+opts.regionname).append('<option value="0">请选择</option></select>');
				if($(this).val()==0)
					return;
				$.ajax({
					url:"/widget?type=regionsSelect&action=showJson&regionid="+$(this).val(),
					dataType:'json',
					success:function(areaAr){
						$.each(areaAr,function(i,area){
							box.find("[name='"+opts.regionname+"']").append("<option  value='"+area.region_id+"'>"+area.local_name+"</option>");
						});
					}
				});	
			});
		}
		
	}; 
 		
 	 $.fn.regionSelect.defaults={provincename:'province_id',cityname:'city_id',regionname:'region_id'};	
}
)(jQuery);