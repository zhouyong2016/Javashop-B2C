package com.enation.app.base.core.service.solution.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.app.base.core.service.solution.IProfileLoader;
import com.enation.app.base.core.service.solution.ISetupLoader;
import com.enation.app.base.core.service.solution.ISolutionInstaller;
import com.enation.app.base.core.service.solution.InstallerFactory;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.model.EopProduct;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;

/**
 * 解决方案安装器实现
 * 
 * @author kingapex
 * 
 */
@Service
public class SolutionInstaller implements ISolutionInstaller {
	protected final Logger logger = Logger.getLogger(getClass());
	
 
	
	@Autowired
	private IProfileLoader profileLoader;
	
	@Autowired
	private InstallerFactory installerFactory;
 

	public void install(String productId) {
		// 将对应的productid写入到eop_site表的productid字段中
//		if (!productId.toUpperCase().equals("BASE")
//				&& !productId.startsWith("temp_")) {
//			siteManager.setSiteProduct(userid, siteid, productId);
//		}

		final String[] types = {
			InstallerFactory.TYPE_APP,	
			InstallerFactory.TYPE_SITE,
			InstallerFactory.TYPE_MENU,
			InstallerFactory.TYPE_ADMINTHEME, 
			InstallerFactory.TYPE_THEME,
			InstallerFactory.TYPE_URL,
			InstallerFactory.TYPE_INDEX_ITEM
			
		};

		// 加载产品的profile.xml文件
		Document proFileDoc = profileLoader.load(productId);
		long start = DateUtil.getDateline();
		long end = start;
		this.log("开始安装 installer");
		
		// 调用所有安装器进行安装，具体安装内容由profile.xml决定
		for (String type : types) {
			
			this.install(type, proFileDoc, productId);
			end = this.logEnd(type+" 完成", end);
		}


 
		//安装权限
		IInstaller installer  = SpringContextHolder.getBean("authInstaller");
		installer.install(productId, null);
		end = this.logEnd("authInstaller 完成", end);
		// 安装组件
		this.install(InstallerFactory.TYPE_COMPONENT, proFileDoc, productId);
		end = this.logEnd("COMPONENT, 完成", end);
		
		// 示例数据安装
		installer = SpringContextHolder.getBean("exampleDataInstaller");
		installer.install(productId, null);	
		end = this.logEnd("exampleDataInstaller, 完成", end);
		//系统设置安装
		installer = SpringContextHolder.getBean("systemSettingInstaller");
		installer.install(productId, null);	
		end = this.logEnd("systemSettingInstaller, 完成", end);
	}
	
	private long log(String msg){
		long now  = DateUtil.getDateline();
		System.out.println("		"+msg+"["+DateUtil.toString(System.currentTimeMillis(), "HH:MM:ss")+"]");
		return now;
	}
	private long logEnd(String msg,long start){
		long now  = DateUtil.getDateline();
		System.out.println("		"+msg+"["+DateUtil.toString(System.currentTimeMillis(), "HH:MM:ss")+"],耗时【"+(now-start)+"】");
		return now;
	}
	private void install(String type, Document proFileDoc, String productId) {
		if (logger.isDebugEnabled()) {
			logger.debug("install [" + type + "]");
		}

		NodeList nodeList = proFileDoc.getElementsByTagName(type);
		if (nodeList == null || nodeList.getLength() <= 0)
			return;

		if (nodeList != null) {
			IInstaller installer = installerFactory.getInstaller(type);
			if (logger.isDebugEnabled()) {
				logger.debug("user installer [" + installer + "]");
			}
			installer.install(productId, nodeList.item(0));
		}
	}
 

}
