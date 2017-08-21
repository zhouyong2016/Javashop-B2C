package com.enation.app.base.core.service.solution.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.enation.app.base.core.service.solution.IAdminThemeInfoFileLoader;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.util.StringUtil;

@Service("adminThemeInfoLoader")
public class AdminThemeInfoLoaderImpl implements IAdminThemeInfoFileLoader {

	
	public Document load(String productId, String path,
			Boolean isCommonTheme) {
		String app_apth = StringUtil.getRootPath();

		String xmlFile = null;
		if (isCommonTheme) {
			xmlFile = app_apth+ "/adminthemes/" + path + "/themeini.xml";
		} else {
			xmlFile =app_apth+"/products/"+productId + "/adminthemes/" + path
					+ "/themeini.xml";//注意！现在资源ＳＤＫ未解决文件复制时的source目录问题
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			return document;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("load [" + productId + " , " + path + "] themeini error!FileName:"+xmlFile);
		}

	}

}
