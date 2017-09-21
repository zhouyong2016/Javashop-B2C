/**
 * Created by lishida on 2017/1/11.
 */

$(function () {
	
    //全选or全不选
    $("#selected-all").on("click", function () {
        if ($(this).prop("checked") === true) {
            $("input[class='fly-checkbox']").prop("checked", true);
            $("input[class='fly-checkbox']").parents('tr').addClass('selected');
        } else {
            $("input[class='fly-checkbox']").prop("checked", false);
            $("input[class='fly-checkbox']").parents('tr').removeClass('selected');
        }
    });
    
    $('.layui-table').on('click', 'tbody tr input[class="fly-checkbox"] ', function () {
        var $this = $(this);
        if($this.prop("checked")==true){
        	$this.prop("checked", false);
        	$this.parents('tr').removeClass('selected');
        	 
        }
        else{
        	$this.prop("checked", true);
        	$this.parents('tr').addClass('selected');
        }
    });
    
    //点击空白选中本行
    $('.layui-table').on('click', 'tbody tr ', function () {
        var $this = $(this);
        var $input = $this.children('td').eq(0).find('input');
        if($input.length>0){
        	if ($input[0].checked) {
                $input.prop("checked", false);
                $this.removeClass('selected');
            } else {
                $input.prop("checked", true);
                $this.addClass('selected');
            }
        }else{
        	 $this.toggleClass('selected');
        }
    });
    
  
    
    //点击空白隐藏高级搜索
   /* $(document).bind("click",function(){
        $(".high_searchcontent").hide();
    });*/


    //页面说明的显示隐藏
    $("#explain").on("click",function(){
        $('.page_explain').slideToggle("1000");
    })

    //关闭说明功能的显示隐藏
    $("#explain,.explain_control").on("mousemove",function(){
        $(".explain_control").css("display","block");
    })

    $("#explain ,.explain_control").on("mouseout",function(){
        $(".explain_control").css("display","none");
    })

    //关闭本页的说明
    $("#self_close").on("click",function(){
        $('.page_explain').slideUp("1000");
    })

})



