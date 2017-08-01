//数字输入插件
//author:kingapex
(function($) {

	$.fn.numinput = function(options) {
		
		var opts = $.extend({}, $.fn.numinput.defaults, options);
		
		return this.each(function(){
			createEl($(this));
			bindEvent($(this));
		});		
	
		
		function createEl(target){
			var value=1;
			if(target.attr("value")) value=target.attr("value");
            var input = '<input type="text" value="'+ value +'" oldValue="'+ value +'" size="5" name="'+ opts.name +'" autocomplete="off" style="width: 40px;" />'
			var incBtn =$('<span class="numadjust increase">+</span>');
			var decBtn =$('<span class="numadjust decrease">-</span>');
			target.append(decBtn).append(input).append(incBtn);
		}
		 
		
		function fireEvent(input){			
			input.attr("oldValue",input.val());
			if(opts.onChange){
				if(input.val()=="" ){alert("数字格式不正确");input.val(input.attr("oldValue"));}
				opts.onChange(input);
			}
		}
        
		function bindEvent(target){
			var input,incBtn,decBtn;
			var input =target.children("input");
			var incBtn =target.children("span.increase");
			var decBtn =target.children("span.decrease");
			incBtn
			.mousedown(function(){
				$(this).addClass("active");
			})
			.mouseup(function(){
				$(this).removeClass("active");
				if(parseInt(input.val()) < 100){
					input.val(parseInt(input.val())+1);
					fireEvent(input);
				}
			});

			decBtn
			.mousedown(function(){
				$(this).addClass("active");
			})
			.mouseup(function(){
				$(this).removeClass("active");
				input.val( parseInt(input.val())== opts.min ? opts.min :parseInt(input.val()) -1);
				fireEvent(input);
			});
			
			input.keypress(function(event) {  
			         if (!$.browser.mozilla) {  
				             if (event.keyCode && (event.keyCode < 48 || event.keyCode > 57)) {  
				                 event.preventDefault();  
				             }  
				         } else {  
				             if (event.charCode && (event.charCode < 48 || event.charCode > 57)) {  
				                 event.preventDefault();  
				             }  
				         }  
			}); 
			
			input.change(function(){
				var $this = $(this);
				var value =$this.val();
			
				var result = true;
				if( $.trim(value)==''){
					alert("请输入数量！");
					input.val($this.attr("oldValue"));
					return false;
				}
				
				if(result && parseInt($.trim(value)) < opts.min ){
					alert("数量不能小于" + opts.min + "！");
					input.val($this.attr("oldValue"));
					return false;
				}
				//数量大于库存 而不是固定数字100
				if(result && parseInt($.trim(value)) > 100){
					alert("数量不能大于库存！");
					input.val($this.attr("oldValue"));
					return false;
				}
								
				if(  result && !$.isNumber(value) ){
					alert("数量必须是数字！");
					input.val($this.attr("oldValue"));
					return false;
				}
				
				if(result){
					fireEvent($this);
				}else{
					$this.val(1);
				}
				
			});
		}
		
	};
	
	$.fn.numinput.defaults={min:1};
})(jQuery);