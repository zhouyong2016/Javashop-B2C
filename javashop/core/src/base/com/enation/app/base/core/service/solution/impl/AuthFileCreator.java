package com.enation.app.base.core.service.solution.impl;

import org.springframework.stereotype.Service;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.util.FileUtil;


/**
 * 权限文件创建器
 * @author kingapex
 * 2012-10-11下午3:18:00
 */
@Service
public class AuthFileCreator {
	
	/**
	 * 在指定的导出目录中生成权限文件
	 * @param temppath
	 */
	public static void create(String temppath){
		String[] tables = {"auth_action","role","role_auth","user_role"};
		String data = DBSolutionFactory.dbExport(tables,true,"es_");
		StringBuffer xmlFile = new StringBuffer();
		xmlFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlFile.append("<dbsolution>\n");
		xmlFile.append(data);
		xmlFile.append("</dbsolution>");
		
		FileUtil.write(temppath + "/auth.xml", xmlFile.toString());
		
	}
	
	
}
