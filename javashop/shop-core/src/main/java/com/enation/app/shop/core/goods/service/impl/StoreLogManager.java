package com.enation.app.shop.core.goods.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.StoreLog;
import com.enation.app.shop.core.goods.service.IStoreLogManager;
import com.enation.framework.database.IDaoSupport;

/**
 * @author kingapex
 *2012-3-30上午9:13:44
 *@author Kanon 2016-2-26;6.0版本升级
 */
@Service("storeLogManager")
public class StoreLogManager implements IStoreLogManager {

	@Autowired
	private IDaoSupport  daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IStoreLogManager#add(com.enation.app.shop.core.goods.model.StoreLog)
	 */
	@Override
	public void add(StoreLog storeLog) {
		this.daoSupport.insert("es_store_log", storeLog);

	}

}
