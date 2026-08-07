

# Javashop-B2C
Javashop is an open-source e-commerce platform built on Java technology, adopting the EOP (Enation Open Platform) framework architecture. It provides a more user-friendly operational experience, features a built-in inventory management system, complete order processing workflows, rich statistical functions, and multiple payment methods, making it ideal for building stable and efficient B2C e-commerce platforms. Meanwhile, Javashop's template engine and component mechanism make extensions simple, with a wide array of third-party components available, significantly reducing secondary development costs.

# About the Database
By default, it will automatically enter the installation wizard. After installation, an `install.lock` file will be added to the `install` directory. If it has already been installed once and you do not wish to reinstall, you can manually create the `install.lock` file and update the JDBC configuration to point to your existing database.

# Open Source Plan
No other plans at this time.

# Maven Environment and Javashop Product Deployment
(Note: Javashop provides an Eclipse version with Maven and SVN plugins pre-integrated. Please download the corresponding version yourself. Link: https://pan.baidu.com/s/1i5fw9E1 Password: i9nk)

1. Prepare Tools
-- JDK 1.7 or above, Eclipse 4.5 or above, Tomcat 7.0 or above.
-- Please download and install the above tools.

2. Install Maven and integrate it into Eclipse > 
- [Install Maven on Windows](http://blog.csdn.net/wang379275614/article/details/43926959)
- [Install Maven on Mac](https://javashop.kf5.com/hc/kb/article/188302/?from=draft)

3.3 Configure Eclipse Development Environment
    ![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/175545_7008597f_1173911.png "图片1.png")
    ![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/175616_b2c5f104_1173911.png "图片2.png")

4. Import the project in Eclipse. Select "Import existing Maven projects" and proceed to the next step.
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/175742_b76b4d3c_1173911.png "图片3.png")

5. Select the project to import
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180520_aade18e8_1173911.png "图片4.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180558_1254845a_1173911.png "图片5.png") Click Finish.

6. After successful import, it will look like the following:
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180710_6b4e1542_1173911.png "图片6.png")

7. Create a Tomcat server in Eclipse to run the project. Open the Servers view and create a new Tomcat server.
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180823_9168cece_1173911.png "图片7.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/180914_58bc2dba_1173911.png "图片8.png")

Finish this step directly.
 **Note: Click "Add" to select your prepared Tomcat server.** 

8. Change the Tomcat encoding to UTF-8, otherwise search results may appear garbled. Open `server.xml`:
```
<Connector URIEncoding="UTF-8" connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"/>

```

9. Deploy the project to Tomcat. Right-click and select:
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181132_616d0bea_1173911.png "图片9.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181156_6beba6bb_1173911.png "图片10.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181223_9ca8f384_1173911.png "图片11.png")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181247_de701f34_1173911.png "图片12.png")

Click Finish, then publish the project.
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181324_e182cd58_1173911.png "图片13.png")
 **Project deployment is complete. You can now start the project.** 

10. Modify Related Configurations 
- Database connection modification file
![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181516_e63e8555_1173911.png "图片14.png")
- Note the `eop.properties` file
```
#eop.properties
#Thu Dec 29 23:57:32 CST 2016
dbtype=1  
version=6.2.1
thumbnailcreator=javaimageio
product=b2c
extension=do

```
Note: `dbtype` indicates the database type: 1 is MySQL, 2 is Oracle, 3 is SQL Server.
It is recommended not to modify other values.

- After the project starts successfully, sample data will be installed. If you have a database file and do not want to install sample data again, add an `install.lock` file in the directory shown in the image.

![输入图片说明](https://git.oschina.net/uploads/images/2017/0802/181706_41d83bf2_1173911.png "图片15.png")

 **Note: After modifying the related configurations, you need to restart the project.**
