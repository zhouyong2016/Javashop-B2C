package com.enation.app.shop.component.shortmsg.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.plugin.shortmsg.IShortMessageEvent;
import com.enation.app.shop.ShopApp;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

import net.sf.json.JSONObject;

/**
 * 
 * (货品预警短消息提醒插件) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 上午7:06:13
 */
@Component
public class GoodsWarningShortMsgPlugin extends AutoRegisterPlugin implements IShortMessageEvent{
	
	@Autowired
	private IDaoSupport daoSupport;

	@Override
	public List<ShortMsg> getMessage() {
		List<ShortMsg> msgList = new ArrayList<ShortMsg>();
		String sql_settings = "select s.cfg_value from es_settings s where s.cfg_group = 'inventory'";
		Map settingmap = daoSupport.queryForMap(sql_settings, null);
		String setting_value = (String) settingmap.get("cfg_value");
		JSONObject jsonObject = JSONObject.fromObject( setting_value );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		Integer inventory_warning_count = Integer.parseInt((String) itemMap.get("inventory_warning_count"));
		String  sql = "select g.goods_id,g.sn,g.name,c.name cname "+
				" from (select distinct goods_id "+
				" from es_product p "+
				" where p.product_id  not in (select productid "+
				" from (select productid,count(*) count "+
				" from es_product_store "+
        		" where enable_store> ? "+
        		" group by productid) tem "+
        		" where count=(select count(*) from es_depot))) tem,";
		String sql_end = ",es_goods_cat c "+
        		" where g.cat_id=c.cat_id "+	
        		" and g.goods_id=tem.goods_id "+
        		" and g.market_enable=1 "+
				" and g.disabled=0 ";
		if(EopSetting.PRODUCT.equals("b2c")){
			String sql_b2c =sql+" es_goods g "+sql_end;
			String countsql = " select count(*) from ("+sql_b2c+") temp_table ";
			Integer count = this.daoSupport.queryForInt(countsql,inventory_warning_count);
			if(count>0){
				ShortMsg shotMsg = getShotMsg(count);
				shotMsg.setUrl("/shop/admin/goods-warning-store/list-goods-warningstore.do?optype=view");		
				msgList.add(shotMsg);
				return msgList;
			}	
		}
		
		if(EopSetting.PRODUCT.equals("b2b2c")){
			String sql_b2b2c =sql+" (select * from es_goods  where store_id=?) g "+sql_end;
			String countsql = " select count(*) from ("+sql_b2b2c+") temp_table ";
			int count = this.daoSupport.queryForInt(countsql,inventory_warning_count,ShopApp.self_storeid);
			if(count>0){
				ShortMsg shotMsg = getShotMsg(count);
				shotMsg.setUrl("/b2b2c/admin/self-store-goods-stock/self-list-goods-warning-store.do?optype=view");
				msgList.add(shotMsg);
				return msgList;
			}
		}	
		return null;
	}
	/**
	 * 消息发送设置
	 * @param count 预警商品数
	 * @return 消息对象
	 */
	private ShortMsg getShotMsg(Integer count){
		ShortMsg msg=new ShortMsg();
		msg.setContent("有"+count +"个商品库存不足");
		msg.setTitle("预警货品");
		msg.setTarget("ajax");
		return msg;
	}
	
}
