package com.enation.app.shop.core.decorate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
/**
 * 
 * 装修组件安装器
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
public class DecorateInstaller implements IInstaller {

	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	public void install(String productId, Node fragment) {
		// TODO Auto-generated method stub
		
		
		this.initStyle();
		this.initSubject();
		this.initPage();
		if("b2b2c".equals(productId)){
			//初始化多店数据库
			this.installB2b2cStyle();
		}else{
			//初始化单店数据库
		}
	}

	/**
	 * 初始化多店风格
	 */
	private void installB2b2cStyle() {
		// TODO Auto-generated method stub
		String sql="delete from es_style where style=?";
		this.daoSupport.execute(sql, "style2");
	}

	
	
	

	
	/**
	 * 初始化风格表
	 */
	public void initStyle(){
		String sql="insert into es_style (style,path,is_top_style,is_default_style,page_id) values "
				+ "('new_storey','/floorstyle/new_style.html',1,1,1), "
				+ "('style1','/floorstyle/style1.html',0,1,1), "
				+ "('style2','/floorstyle/style2.html',0,0,1)";
		this.daoSupport.execute(sql);
	}
	/**
	 * 初始化页面表
	 */
	public void initPage(){
		String sql="insert into es_page (name,path,type) values ('PC首页','/index.html',0)";
		this.daoSupport.execute(sql);
	}
	
	/**
	 * 初始化专题表
	 */
	public void initSubject(){
		String sql="insert into es_themeuri (uri,path,pagename,point) values (?,?,?,?)";
		this.daoSupport.execute(sql, "/subject-(\\d+).html","/subject/subject.html","专题页面",0);
		this.daoSupport.execute(sql, "/subject-index.html","/subject/subject-index.html","专题首页",0);
	}
}
