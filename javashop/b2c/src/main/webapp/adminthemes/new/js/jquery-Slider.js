(function($) {
	$.fn.Scroll = function() {
		var _btnUp = $("#btnUp"); // 向上按钮
		var _btnDown = $("#btnDown"); //向下按钮
		var _this = this.eq(0).find("ul:first");
		var lineH = 70; // 获取行高
		var line = 1; // 每次滚动的行数
		var speed = 500; // 卷动速度，数值越大，速度越慢（毫秒）
		var m = line; // 用于计算的变量
		var count = _this.find(".parentMenu").length; // 总共的<li>元素的个数
		var upHeight = line * lineH;
		var c = 0;
//		setParentMenuTop(0);
		function scrollUp() {
			if (!_this.is(":animated")) { // 判断元素是否正处于动画，如果不处于动画状态，则追加动画。
				if (m < count-5) { // 判断 m 是否小于一屏的个数(每屏7个)
					m += line;
					_this.animate({
						marginTop : "-=" + upHeight + "px"
					}, speed);
					c = c+1;
					$(".upHeight").val("");  //将滚动的高度记录在页面中
					$(".upHeight").val(upHeight);  //将滚动的高度记录在页面中
//					setParentMenuTop(c);
				}else{
					_btnUp.removeClass("hover");
				}
			}
		}
//		function setParentMenuTop(c){
//			for(var i=1;i<=count;i++){
//				if(i-c==7){
//					$(".secondFLoat"+i).css("bottom","0px");
//					$(".secondFLoat"+i).css("top","auto");
//				}else{
//					var height =  $(".secondFLoat"+i).height();
//					var num = 520-(i-c-1)*lineH;
//					if(height>num){
//						var top = (i-c-1)*lineH-(height-num);
//					}else{
//						var top = (i-c-1)*lineH+22;
//					}
//					$(".secondFLoat"+i).css("top",top+"px");
//				}
//			}
//		}
		function scrollDown() {
			if (!_this.is(":animated")) {
				if (m > line) { // 判断m 是否大于一屏个数
					m -= line;
					_this.animate({
						marginTop : "+=" + upHeight + "px"
					}, speed);
					c = c-1;
//					setParentMenuTop(c);
				}else{
					_btnDown.removeClass("hover");
				}
			}
		}
		function init(){
			
		}
		_btnUp.mouseover(function(){
			if (!_this.is(":animated")) { // 判断元素是否正处于动画，如果不处于动画状态，则追加动画。
				if (m < count-6) {
					_btnUp.addClass("hover");
				}
			}
		});
		_btnUp.mouseout(function(){
			_btnUp.removeClass("hover");
		});
		_btnDown.mouseover(function(){
			if (!_this.is(":animated")) {
				if (m > line) {
					_btnDown.addClass("hover");
				}
			}
		});
		_btnDown.mouseout(function(){
			_btnDown.removeClass("hover");
		});
		_btnUp.bind("click", scrollUp);
		_btnDown.bind("click", scrollDown);
	};
})(jQuery);
$(function(){
})