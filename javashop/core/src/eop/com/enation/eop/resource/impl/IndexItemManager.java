package com.enation.eop.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.eop.resource.IIndexItemManager;
import com.enation.eop.resource.model.IndexItem;
import com.enation.framework.database.IDaoSupport;

/**
 * 首页项管理实现
 * 
 * @author kingapex 2010-10-12下午04:00:31
 */
@Service
public class IndexItemManager   implements IIndexItemManager {

	@Autowired
	private IDaoSupport daoSupport;
	/**
	 * 添加首页项
	 */
	public void add(IndexItem item) {
		daoSupport.insert("es_index_item", item);
	}

	/**
	 * 读取首页项列表
	 */
	public List<IndexItem> list() {
		String sql = "select * from es_index_item order by sort";
		return daoSupport.queryForList(sql, IndexItem.class);
	}

	public void clean() {
		daoSupport.execute("truncate table es_index_item");
	}

}
