package com.enation.app.base.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.base.core.service.solution.ISolutionInstaller;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.database.data.IDataOperation;
import com.enation.framework.util.DateUtil;

/***
 * eop安装Manager
 * @author kanon 2015-12-17 version 1.1 添加注释
 *
 */
@Service
public class EopInstallManager {
 
	@Autowired
	private IDataOperation dataOperation;
	
	
	@Autowired
	private ISolutionInstaller solutionInstaller;
	
	@Autowired
	private IAdminUserManager adminUserManager;
	
	/**
	 * 安装eop应用
	 * @param username 超级管理员用户名
	 * @param password 超级管理员密码
	 * @param productid 安装版本： b2c\b2b2c
	 */
	public void install(String username, String password,String productid) {
		
		long s= DateUtil.getDateline();
	    long start  = this.log("开始安装");
	    dataOperation.imported("file:com/enation/app/base/init.xml");
	    
		long end =this.logEnd("init xml安装完成",start);		
		 
		//安装
		solutionInstaller.install( productid);
		end =this.logEnd("simple product 安装完成",end);		
		
		//安装base应用
		solutionInstaller.install( "base");
		end =this.logEnd("base product 安装完成",end);	
		
		//安装管理员
		this.installUser(username, password);
		end  = this.logEnd("user 安装完成",end);
		 
		System.out.println("耗时{"+(end-s)+"}");
	}

	/**
	 * 记录日志
	 * 开始安装时调用
	 * @param msg 日志信息
	 * @return
	 */
	private long log(String msg){
		long now  = DateUtil.getDateline();
		System.out.println(msg+"["+DateUtil.toString(System.currentTimeMillis(), "HH:MM:ss")+"]");
		return now;
	}
	
	/**
	 * 记录日志
	 * 记录安装结束
	 * @param msg 日志信息
	 * @param start 开始时间
	 * @return
	 */
	private long logEnd(String msg,long start){
		long now  = DateUtil.getDateline();
		System.out.println(msg+"["+DateUtil.toString(System.currentTimeMillis(), "HH:MM:ss")+"],耗时【"+(now-start)+"】");
		return now;
	}

	/**
	 * 安装管理员
	 * @param username 超级管理员用户名
	 * @param password 超级管理员密码
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void installUser(String username, String password) {
		
		//超级管理员
		AdminUser adminUser = new AdminUser();
		adminUser.setUsername(username);
		adminUser.setPassword(password);
		adminUser.setState(1);
		adminUser.setFounder(1);		
		this.adminUserManager.add(adminUser);
		

		//添加产品部
		AdminUser chanpin = new AdminUser();
		chanpin.setUsername("chanpin");
		chanpin.setPassword("123456");
		chanpin.setState(1);
		chanpin.setRoleids(new int[]{2});
		adminUserManager.add(chanpin);
		
		//添加库管
		AdminUser kuguan = new AdminUser();
		kuguan.setUsername("kuguan");
		kuguan.setPassword("123456");
		kuguan.setState(1);
		kuguan.setRoleids(new int[]{3});
		adminUserManager.add(kuguan);
		
		//添加财务
		AdminUser caiwu = new AdminUser();
		caiwu.setUsername("caiwu");
		caiwu.setPassword("123456");
		caiwu.setState(1);
		caiwu.setRoleids(new int[]{4});
		adminUserManager.add(caiwu);
		
		//添加财务
		AdminUser kefu = new AdminUser();
		kefu.setUsername("kefu");
		kefu.setPassword("123456");
		kefu.setState(1);
		kefu.setRoleids(new int[]{5});
		adminUserManager.add(kefu);
		
	}
}
