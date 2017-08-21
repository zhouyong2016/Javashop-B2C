package com.enation.app.shop.component.ordercore.plugin.snapshot;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.core.goods.model.Gallery;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.model.GoodsSnapshotGallery;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.model.ProductSnapshot;
import com.enation.app.shop.core.goods.service.IProductSnapshotManager;
import com.enation.app.shop.core.goods.model.GoodsSnapshot;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.order.IBeforeOrderCreateEvent;
import com.enation.app.shop.core.order.service.IOrderSnapshotManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
/**
 * 
 * (商品快照插件) 
 * 用户下单时，将用户的购买的商品信息存入快照表，用户查看订单时如果商品已经修改则显示快照按钮，
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 上午11:01:11
 */
@Component
public class OrderSnapshotPlugin extends AutoRegisterPlugin implements IBeforeOrderCreateEvent{
	@Autowired
	private IOrderSnapshotManager  orderSnapshotManager;
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private IProductSnapshotManager productSnapshotManager;
	/**
	 * 订单创建前方法
	 */
	@Override
	public void onBeforeOrderCreate(Order order, List<CartItem> itemList, String sessionid) {
		//遍历购物车，将购物车内的商品存入快照表
		for (CartItem cartItem : itemList) {
			//查询快照表中是否有快照商品
			String sql = "select * from es_goods_snapshot where goods_id=?";
			List list = daoSupport.queryForList(sql, cartItem.getGoods_id());
			//查询商品表中的商品
			sql = "select * from es_goods where goods_id=?";
			GoodsSnapshot goodsSnapshot = daoSupport.queryForObject(sql, GoodsSnapshot.class, cartItem.getGoods_id());
			//判断快照表中是否存在当前商品的快照，不存在则创建快照
			if(list.size()==0){
				this.addSnapshotData(goodsSnapshot, cartItem);
			}else{
				//如果存在，相同商品可能存在多个快照表，获取最新的快照表
				Map map_snapshot = (Map) list.get(list.size()-1);
				//判断商品最后更新时间是否相同，如果不相同需要重新生成快照
				if(!map_snapshot.get("last_modify").equals(goodsSnapshot.getLast_modify())){	
					this.addSnapshotData(goodsSnapshot, cartItem);
				}else{
					Integer snapshot_id = (Integer) map_snapshot.get("snapshot_id");
					cartItem.setSnapshot_id(snapshot_id);
				}
			} 
		}
	}
	private void addSnapshotData(GoodsSnapshot goodsSnapshot,CartItem cartItem){
		goodsSnapshot.setEdit_time(DateUtil.getDateline());
		//将快照数据添加到数据库
		orderSnapshotManager.addSnapshot(goodsSnapshot);
		String sql = "select max(snapshot_id) from es_goods_snapshot ";
		Integer snapshot_id = daoSupport.queryForInt(sql, null);
		cartItem.setSnapshot_id(snapshot_id);
		//将货品快照数据添加到数据库
		sql = "select * from es_product where goods_id=? ";
		List<ProductSnapshot> products = daoSupport.queryForList(sql,ProductSnapshot.class,cartItem.getGoods_id());
		for (ProductSnapshot productSnapshot : products) {
			productSnapshot.setSnapshot_id(snapshot_id);
			productSnapshotManager.add(productSnapshot);
		}
		//查询商品图片信息表，将图片信息存入快照图片表
		sql = "select * from es_goods_gallery where goods_id=?";
		List<GoodsGallery> queryForList = daoSupport.queryForList(sql, GoodsGallery.class, cartItem.getGoods_id());
		for (GoodsGallery goodsGallery : queryForList) {
			GoodsSnapshotGallery goodsSnapshotGallery = new GoodsSnapshotGallery();
			BeanUtils.copyProperties(goodsGallery, goodsSnapshotGallery);
			goodsSnapshotGallery.setSnapshot_id(snapshot_id);
			daoSupport.insert("es_goods_snapshot_gallery", goodsSnapshotGallery);
		}
	}
}
