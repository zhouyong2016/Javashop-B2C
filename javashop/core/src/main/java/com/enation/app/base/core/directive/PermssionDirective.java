package com.enation.app.base.core.directive;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.framework.context.spring.SpringContextHolder;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
/**
 * 权限命令指令
 * 指令用于前台使用
 * @author kanon 2015-12-16 version 1.1 添加注释
 *
 */
public class PermssionDirective implements TemplateDirectiveModel {

	/**
	 * @param env 系统环境变量，通常用它来输出相关内容
	 * @param params:指令参数
	 * @param loopVars  循环替代变量
	 * @param body:指令内容
	 */
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
		IPermissionManager permissionManager = SpringContextHolder.getBean("permissionManager");
		String  actid =params.get("actid").toString();
		String[] arr = StringUtils.split(actid, ",");
		boolean result = false;
		for (String item : arr) {
			result = permissionManager.checkHaveAuth(PermissionConfig.getAuthId(item));
			if(result){
				break;
			}
		}
		 
		if(result){
			body.render(env.getOut());
		}
	}

}
