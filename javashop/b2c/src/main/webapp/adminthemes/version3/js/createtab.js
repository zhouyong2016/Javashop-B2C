/**
 * Created by lishida on 2017/1/11.
 */
layui.config({
    base: 'js/'
}).use(['layer','element','tab','code'], function() {
    var element = layui.element(),
        layer = layui.layer,
        $ = layui.jquery;
   layui.code({
       about:false
   });

    $(".drop-down-show").on("click",function(event){
        event.stopImmediatePropagation();
        //$(".dropdown").toggle();
        if (this.nextElementSibling.style.display==""||this.nextElementSibling.style.display=="none"){
            this.nextElementSibling.style.display="block";
        }else {
            this.nextElementSibling.style.display="";
        }
    });
    $(document).bind("click",function(){
        $(".dropdown-menu").hide();
    });

    newTab= function  (title,icon,href){



        window.parent.tab.tabAdd({
            title: title,
            icon: icon,
            href: href
        });

    }

})

    function jsCopy(id){
        const range = document.createRange();
        range.selectNode(document.getElementById(id));

        const selection = window.getSelection();
        if(selection.rangeCount > 0) selection.removeAllRanges();
        selection.addRange(range);

        document.execCommand('copy');
    }


