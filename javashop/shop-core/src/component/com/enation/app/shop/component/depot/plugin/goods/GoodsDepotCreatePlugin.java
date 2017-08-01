package com.enation.app.shop.component.depot.plugin.goods;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IShortMsgManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.goods.plugin.IGoodsAfterAddEvent;
import com.enation.app.shop.core.goods.plugin.IGoodsDeleteEvent;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 生成商品仓库库存
 * @author kingapex
 *
 */
@Component
public class GoodsDepotCreatePlugin extends AutoRegisterPlugin implements
		IGoodsAfterAddEvent,IGoodsDeleteEvent {
	
	@Autowired
	private IDepotManager depotManager;
	
	@Autowired
	private IDaoSupport daoSupport;
 
	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		Integer goodsid  = (Integer) goods.get("goods_id");
		List<Depot> depotList = this.depotManager.list();
		for(Depot depot:depotList){
			this.daoSupport.execute("insert into es_goods_depot(goodsid,depotid,iscmpl)values(?,?,?)", goodsid,depot.getId(),0);
			
		}
		
	 
		
	}
	 
	
	@Override
	public void onGoodsDelete(Integer[] goodsid) {
		if(goodsid==null || goodsid.length==0) return ;
		String goodsidstr = StringUtil.arrayToString(goodsid, ",");
		this.daoSupport.execute("delete from es_goods_depot where goodsid in ("+goodsidstr +")");
	}

 

}
