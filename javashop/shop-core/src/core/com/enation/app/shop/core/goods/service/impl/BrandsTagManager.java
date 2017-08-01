package com.enation.app.shop.core.goods.service.impl;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.service.IBrandsTagManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;

@Service("brandsTagManager")
public class BrandsTagManager implements IBrandsTagManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IBrandsTagManager#del(int, int)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="删除关联品牌")
	public void del(int tag_id, int rel_id) {
		daoSupport.execute("delete from es_tag_relb where rel_id = ? and tag_id =?", rel_id,tag_id);  
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IBrandsTagManager#add(int, int[])
	 */
	@Override
	@Log(type=LogType.GOODS,detail="添加品牌")
	public void add(int tag_id, int[] brand_id) {
		for (int i : brand_id) { 
			this.daoSupport.execute("insert into es_tag_relb values(?,?,0)", tag_id,i);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IBrandsTagManager#saveOrder(int, int[], int[])
	 */
	@Override
	@Log(type=LogType.GOODS,detail="保存排序")
	public void saveOrder(int tag_id, int[] rel_id, int[] order) { 
		for (int i = 0; i < order.length; i++) {
			this.daoSupport.execute("update es_tag_relb set ordernum = ? where tag_id = ? and rel_id = ?", order[i],tag_id,rel_id[i]);
		} 
	}

}
