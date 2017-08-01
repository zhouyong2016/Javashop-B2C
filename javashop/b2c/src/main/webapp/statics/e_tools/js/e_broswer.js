/**
 * Created by Andste on 2016/5/11.
 */
var dynamicLoading = {
    css : function(){
        if(!arguments || arguments.length === 0){
            throw new Error('argument "path" is required !');
        }
        for(var i = 0, len = arguments.length; i < len; i++) {
            var head = document.getElementsByTagName('head')[0];
            var link = document.createElement('link');
            link.href = arguments[i];
            link.rel = 'stylesheet';
            link.type = 'text/css';
            head.appendChild(link);
        }
    },
    js  : function(){
        if(!arguments || arguments.length === 0){
            throw new Error('argument "path" is required !');
        }
        for(var i = 0, len = arguments.length; i < len; i++) {
            var head = document.getElementsByTagName('head')[0];
            var script = document.createElement('script');
            script.src = arguments[i];
            script.type = 'text/javascript';
            head.appendChild(script);
        }
    }
}
var lteIE8 = false,   //  全局变量，当它为true时，说明浏览器为IE9【不包括IE9】以下
    Sys = {};         //  全局变量，此为对象。
                      // Sys.ie --> IE浏览器版本【IE11检测不到】 Sys.firefox --> 火狐浏览器版本  Sys.chrome  -->  chrome浏览器版本  Sys.opera  -->  opera浏览器版本
(function(){
    var jq183 = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/library/jquery.min-1.8.3.js"></script>',
        jq183_cdn = '<script type="text/javascript" src="//cdn.bootcss.com/jquery/1.8.3/jquery.min.js"></script>',

        jq214 = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/library/jquery.min-2.1.4.js"></script>',
        jq214_cdn = '<script type="text/javascript" src="//cdn.bootcss.com/jquery/2.1.4/jquery.min.js"></script>',

        paceJS = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/library/pace.js"></script>',
        paceCss = '<link rel="stylesheet" href="'+ ctx +'/statics/e_tools/css/library/pace.css">',

        resetCSS = '<link rel="stylesheet" href="'+ ctx +'/statics/e_tools/css/reset.css">',

        bootstrapJs335 = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/library/bootstrap.min-3.3.5.js"></script>',
        bootstrapJs335_cdn = '<script type="text/javascript" src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>',

        bootstrapCss335 = '<link rel="stylesheet" href="'+ ctx +'/statics/e_tools/css/library/bootstrap.min-3.3.5.css">',
        bootstrapCss335_cdn = '<link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">',

        bootstrapJs232 = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/library/bootstrap.min-2.3.2.js"></script>',
        bootstrapJs232_cdn = '<script type="text/javascript" src="//cdn.bootcss.com/bootstrap/2.3.2/js/bootstrap.min.js"></script>',

        bootstrapCss232 = '<link rel="stylesheet" href="'+ ctx +'/statics/e_tools/css/library/bootstrap.min-2.3.2.css">',
        bootstrapCss232_cdn = '<link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/2.3.2/css/bootstrap.min.css">',

        json2 = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/library/json2.min.js"></script>',
        json2_cdn = '<script type="text/javascript" src="//cdn.bootcss.com/json2/20150503/json2.min.js"></script>',

        cookie = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/library/jquery.cookie.min-1.4.1.js"></script>',
        cookie_cdn = '<script type="text/javascript" src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>',

        e_baseJS = '<script type="text/javascript" src="'+ ctx +'/statics/e_tools/js/e_base.js"></script>'
    var ua = navigator.userAgent.toLowerCase();
    var s;
    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1]
        : (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1]
        : (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1]
        : (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1]
        : (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;

    if (Object.hasOwnProperty.call(window, "ActiveXObject") && !window.ActiveXObject) {
        Sys.ie = 11.0
    };
    if (Sys.ie) {
        var ver = parseFloat(Sys.ie);
        ver >=9 ? heiLoad() : lowLoad();
        if(ver <=8 ){
            lteIE8 = true;
        };
    }else {
        heiLoad();
    };
    function lowLoad(){
        document.write(jq183);
        document.write(bootstrapJs232);
        document.write(bootstrapCss232);
        document.write(json2);
    };
    function heiLoad(){
        document.write(jq214);
        document.write(paceJS);
        document.write(paceCss);
        document.write(bootstrapJs335);
        document.write(bootstrapCss335);
    };

    (function(){
        document.write(cookie);
        document.write(e_baseJS);
        document.write(resetCSS);
    }())
})();

