使用步骤：
后台安装：在设置|网店设置|组件管理中找到"新相册组件"点击安装，出现成功提示后点击启用；
挂件名称为：goods_gallery2，
详细页中大图显示代码参考：
<div class="preview">
			 <a href="${goods.big!''}"  id="zoom" rel="zoom-width:400px;zoom-height:400px;"  class="MagicZoom"> 
			 <img   src="${goods.small!''}" />
			 </a>
		</div>
图片列表参考：
<div class="thumblist">
				<ul>
				<#list galleryList as gallery>
					<li <#if !gallery_has_next>class="last"</#if> <#if (gallery.isdefault!0)==1>class="selected"</#if>> <@image src="${gallery.thumbnail  }"  big="${gallery.big}" small="${gallery.small}"/></li>
				</#list>	
				</ul>
			</div>
对其它挂件影响：
请删除所有的带有postfix的属性段，如
<img   alt="${item.name}" src="<@GoodsPic pic='${item.image_default}' postfix='_thumbnail' />">
改为
<img   alt="${item.name}" src="${item.thumbnail}">

从旧版相册升级：
1、备份/WEB-INF/classes/eop.properties、/WEB-INF/classes/config/jdbc.properties、/themes、/statics、/install/install.lock
2、代码升级为最新版
3、将第1步中备份的文件恢复
4、安装新相册，参考上文
5、在浏览器地址栏内输入“http://你的地址:你的端口/shop/admin/galleryImport.do”并回车
6、点击“执行转换”
