package com.enation.app.base.core.service.solution.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.app.base.core.service.solution.InstallUtil;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.model.Theme;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.util.FileUtil;

/**
 * 前台主题安装器
 * 
 * @author kingapex 2010-1-20下午10:56:25
 */
@Service
public class ThemeInstaller implements IInstaller {
 
	@Autowired
	private IThemeManager themeManager;
	private ISiteManager siteManager;

	private String productId;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void install(String productId,  Node fragment) {
		//this.themeManager.clean();
		this.productId = productId;
		
//		String contextPath = EopContext.getContext().getContextPath();
		
//		String targetPath  = EopSetting.EOP_PATH+ contextPath+ "/themes/";
//		String sourcePath =   EopSetting.PRODUCTS_STORAGE_PATH +"/" + productId +"/themes/";
//		
//		FileUtil.copyFile(sourcePath+"findpass_email_template.html",  targetPath+"findpass_email_template.html");
//		FileUtil.copyFile(sourcePath+"order_create_email_template.html",  targetPath+"order_create_email_template.html");
//		FileUtil.copyFile(sourcePath+"price_filter.xml",  targetPath+"price_filter.xml");
//		FileUtil.copyFile(sourcePath+"reg_email_template.html",  targetPath+"reg_email_template.html");
//		FileUtil.copyFile(sourcePath+"success.html",  targetPath+"success.html");		
//		FileUtil.copyFile(sourcePath+"widgets_default.xml",  targetPath+"widgets_default.xml");
		
		NodeList themeList = fragment.getChildNodes(); 
		this.install(themeList); 
	}
	protected final Logger logger = Logger.getLogger(getClass());
	private void install(Element themeNode) {
		String isdefault = themeNode.getAttribute("default");
		Theme theme = new Theme();
		theme.setProductId(productId);
		theme.setPath(themeNode.getAttribute("id"));
		theme.setThemename(themeNode.getAttribute("name"));
		theme.setThumb("preview.png");
		theme.setSiteid(0);
		InstallUtil.putMessaage("安装主题"+theme.getThemename()+"...");
		String commonAttr= themeNode.getAttribute("isCommonTheme");
		commonAttr =commonAttr==null?"":commonAttr;
		Boolean isCommonTheme = (commonAttr.toUpperCase().equals("TRUE"));
		Integer themeid  = this.themeManager.add(theme,isCommonTheme);
		if(logger.isDebugEnabled()){
			logger.debug("install "+ theme.getThemename() +",default :" +isdefault);
		}
		if("yes".equals( isdefault)){
			if(logger.isDebugEnabled())
				logger.debug("change theme["+themeid+"] ");
			System.out.println("change theme["+themeid+"] ");
			themeManager.changetheme(themeid);
		}
		
	//	borderInstaller.install(productId,  themeNode);
		InstallUtil.putMessaage("完成!");
	}

	private void install(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				this.install((Element) node);
			}
		}
	}

 

	public IThemeManager getThemeManager() {
		return themeManager;
	}

	public void setThemeManager(IThemeManager themeManager) {
		this.themeManager = themeManager;
	}

	public ISiteManager getSiteManager() {
		return siteManager;
	}

	public void setSiteManager(ISiteManager siteManager) {
		this.siteManager = siteManager;
	}
	
		

}
