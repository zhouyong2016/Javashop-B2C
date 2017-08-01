package com.enation.app.shop.core.decorate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.decorate.service.IPageManager;
import com.enation.framework.database.IDaoSupport;

/**
 * 
 * 主页管理 实现类
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Service
@SuppressWarnings(value={"rawtypes"})
public class PageManager implements IPageManager{

	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	public List listPage(int page_id) {
		String sql="select * from es_page ";
		
		if(page_id!=0){
			sql+="where id="+page_id;
		}
		return this.daoSupport.queryForList(sql);
		
	}
}
