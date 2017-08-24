#Javashop-B2C
Javashop是基于Java技术构建的开源电子商务平台，采用EOP(Enation Open Platform易族开放平台)框架体系，更具人性化的操作体验，内置库存管理系统，完备的订单流程，丰富的统计功能，多种支付方式，适合搭建稳定、高效的B2C电子商务平台。 同时Javashop的模板引擎及组件机制让扩展变得简单，更有大量第三方组件可供选择，极大的降低二次开发成本。

#关于数据库

默认会自动进入安装程序，安装后会在install目录下增加install.lock文件。若第一次已安装，后续不想要重新安装的话，则可以手动增加install.lock 文件，同时修改jdbc配置为已安装数据库，即可。

#开源计划

暂无其他计划

#Maven环境及Javashop产品部署
（备注：javashop提供集成好maven和svn插件的eclipse ,请自行下载相应的版本  链接: https://pan.baidu.com/s/1i5fw9E1 密码: i9nk）

1.准备工具
--JDK1.7以上、eclipse 4.5以上、tomcat 7.0以上。
--请下载并安装好上述工具。

2.安装maven并集成到eclipse> 
-[windows安装maven](http://blog.csdn.net/wang379275614/article/details/43926959)
-[mac上安装maven](https://javashop.kf5.com/hc/kb/article/188302/?from=draft)

3.3配置eclipse开发环境
    ![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/175545_7008597f_1173911.png "图片1.png")
    ![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/175616_b2c5f104_1173911.png "图片2.png")

4.eclipse中导入项目，选择已存在maven项目导入，进入下一步
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/175742_b76b4d3c_1173911.png "图片3.png")
5.选择项目导入
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180520_aade18e8_1173911.png "图片4.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180558_1254845a_1173911.png "图片5.png")点击完成。

6.导入成功后，如下图

![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180710_6b4e1542_1173911.png "图片6.png")

7.Eclipse创建tomcat启动项目，打开server窗口，创建tomcat
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180823_9168cece_1173911.png "图片7.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180914_58bc2dba_1173911.png "图片8.png")

到这一步直接完成。
 **说明：点击add选择准备好的tomcat** 

8.修改tomcat编码格式为UTF-8，否则搜索会出现乱码，打开server.xml,
```
<Connector URIEncoding="UTF-8" connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"/>

```

9.部署项目到tomcat中，右键选中
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181132_616d0bea_1173911.png "图片9.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181156_6beba6bb_1173911.png "图片10.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181223_9ca8f384_1173911.png "图片11.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181247_de701f34_1173911.png "图片12.png")

点击完成，然后publish项目
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181324_e182cd58_1173911.png "图片13.png")
 **到此项目部署结束，可以启动项目运行了。** 

10.相关配置修改 
-数据库连接修改文件
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181516_e63e8555_1173911.png "图片14.png")
-注意eop.properties文件
```
#eop.properties
#Thu Dec 29 23:57:32 CST 2016
dbtype=1  
version=6.2.1
thumbnailcreator=javaimageio
product=b2c
extension=do

```
注意:dbtype表示数据库类型，1是mysql， 2为oracle， 3为sqlserver
其他值建议不要修改

-项目启动成功后，会安装示例数据，如果有数据库文件，不想再安装示例数据，在图中文件加下添加install.lock文件

![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181706_41d83bf2_1173911.png "图片15.png")

 **注意：相关配置修改完成后，需要重新启动项目。** 





















