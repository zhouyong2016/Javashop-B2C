package com.enation.app.shop.core.order.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.SellBackChild;
import com.enation.app.shop.core.order.model.SellBackGoodsList;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.framework.database.Page;


/**
 * 退货管理<br>
 * 进行重构，以前的逻辑太乱
 * @author [kingapex]
 * @version [2.0]
 * @since [5.1]
 * 2015年11月19日下午6:28:17
 */
public interface ISellBackManager {
	
	
	/**
	 * 创建退货单<br>
	 * 在顾客申请时调用
	 * 
	 * @param sellBack 退货单
	 * @param goodsList 退货单明细
	 * @return 创建的退货单id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer addSellBack(SellBack sellBack,List<SellBackGoodsList> goodsList );
	
	
	/**
	 * 创建退货单<br>
	 * 后台管理员操作时调用
	 * 
	 * @param sellBack 退货单
	 * @param goodsList 退货单明细
	 * @return 创建的退货单id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer addSellBackAdmin(SellBack sellBack,List<SellBackGoodsList> goodsList );
	
	
	
	/**
	 * 分页显示退货单列表
	 * @param status 退货单状态
	 * @param page 分页数
	 * @param pageSize 每页显示数量
	 * @param type 类型 1.退款单。2.退货单
	 * @return
	 */
	public Page list(Integer page,Integer pageSize,Integer status,Integer type);
	
	
	/**
	 * 分页显示退货搜索
	 * @param keyword
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page search(String keyword,int page,int pageSize);
	
	/**
	 * 获取退货单详细信息
	 * @param tradeno
	 * @return
	 */
	public SellBack get(String tradeno);
	public SellBack get(Integer id); 
	
	/**
	 * 申请退货
	 * @param data
	 * @return
	 */
	public void apply(SellBack data);
	
	/**
	 * 取消申请
	 * @param data
	 * @return
	 */
	public void cancle(SellBack data);
	
	
	
	/**
	 * 保存退货商品
	 * @param data
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer saveGoodsList(SellBackGoodsList data);
	
	/**
	 * 根据退货单id及商品id获取该退货商品详细
	 * @param id
	 * @param goodsid
	 * @return
	 */
	public SellBackGoodsList getSellBackGoods(Integer id,Integer goodsid);
	
	/**
	 * 获取该退货id的商品列表
	 * @param id
	 * @return SellBackGoodsList
	 */
	public List getGoodsList(Integer id);
	
	
	/**
	 * 修改退货商品数量
	 * @param recid
	 * @param goodsid
	 */
	public void editGoodsNum(Map data);
	
	/**
	 * 修改入库数量
	 * @param id sellbackid
	 * @param productid 商品id
	 * @param num 退货数量
	 * @param item_id 
	 */
	public void editStorageNum(Integer id,Integer productid,Integer num, Integer item_id);
	/**
	 * 修改套餐商品中的子项库存
	 * @param orderId
	 * @param parentId
	 * @param goods_id
	 * @param num
	 */
	public void editChildStorageNum(Integer orderId,Integer parentId,Integer goods_id, Integer num);
	
	/**
	 * 获取该退货id的操作日志
	 * @param id
	 * @return
	 */
	public List sellBackLogList(Integer id);
	
	
	/**
	 * 记录退货操作日志
	 * @param id 退货单id
	 * @param status 状态
	 * @param logdetail 日志描述
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveLog(Integer id,String logdetail);
	/**
	 * 记录退货操作日志
	 * @param id 退货单id
	 * @param status 状态
	 * @param logdetail 日志描述
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveLog(Integer id,String logdetail,String memberName);
	
	/**
	 * 申请退货时查询是否已申请过
	 * @param sn
	 * @return
	 */
	public int searchSn(String sn);
	/**
	 * 根据会员获取退货申请列表
	 * @param member_id 会员ID
	 * @param page 分页
	 * @param pageSize 每页显示数量
	 * @return
	 */
	public Page list(Integer member_id,Integer page,Integer pageSize);
	
	
	
	/**
	 * 修改退货单状态
	 * @param status 状态
	 * @param id 退货单ID
	 * @param depotid 退货仓库id
	 * @param alltotal_pay 退款金额 
	 * @param seller_remark 备注
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void editStatus(Integer status,Integer id,int depotid,double  alltotal_pay,String seller_remark);
	
	/**
	 * 获取一个整箱商品中的商品list(捆绑插件暂时移植到核心类里)
	 * @param goods_id
	 * @return
	 */
	public List<Map> list(int goods_id);

	/**
	 * 获取一个套餐中的子项详情(捆绑插件暂时移植到核心类里)
	 * @param goodsId
	 * @param relGoodsId
	 * @return
	 */
	public Map getPackInfo(int goodsId,int relGoodsId);
	/**
	 * 判断一个货品是否是套餐(捆绑插件暂时移植到核心类里)
	 * @param productid
	 * @return
	 */
	public int isPack(int productid);
	
	
	/**
	 * 保存退货整箱子项
	 * @param orderId	订单id
	 * @param goodsId	商品id
	 * @param parentId	整箱商品 的id
	 * @param returnNum	退货数量
	 * @param storageNum	购买数量
	 */
	public void saveSellbackChild(int orderId,int goodsId,int parentId,int returnNum);
	
	/**
	 * 修改退货整箱子项	
	 * @param orderId 订单id
	 * @param goodsId 商品id
	 * @param returnNum 退货数量
	 */
	public void updateSellbackChild(int orderId,int goodsId,int returnNum,int storageNum);
	
	/**
	 * 获取订单内整箱内的子项
	 * @param orderId 订单id
	 * @param goodsId 商品id
	 * @param returnNum
	 */
	public SellBackChild getSellbackChild(int orderId,int goodsId);
	
	/**
	 * 获取订单内一个整箱内的所有子项
	 * @param orderId
	 * @param parentGoodsId
	 * @return
	 */
	public List getSellbackChilds(int orderId ,int parentGoodsId);

	/**
	 * 根据订单id 清空退货子项表
	 * @param orderId
	 */
	public void delSellerBackChilds(int orderId);

	/**
	 * 添加售后申请
	 * 主要用于退款申请
	 * @param sellBackList 售后申请 
	 */
	public void addSellBack(SellBack sellBackList);

	
	/**
	 * 审核退款单
	 * @param id 退款单Id
	 * @param status 审核状态 0未审核，1审核通过，2审核不通过
	 * @param seller_remark 操作备注
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void authRetund(Integer id,Integer status,String seller_remark);


	/**
	 * 根据itemId查询退货单 add by jianghongyan 
	 * @param id
	 * @param integer
	 * @return
	 */
	public SellBackGoodsList getSellBackGoodsByItemId(int id, Integer itemId);
	
	
	/**
	 * 退货入库
	 * @param depotid 仓库
	 * @param id sellbacklist id
	 * @param goods_id	商品id
	 * @param num	退货数量
	 * @param product_id	productid
	 * 考虑到如果是入库就会商家销售，破损的商品入库就会出现问题。所以增加功能是否全部入库
	 * @param itemId 
	 */
	public void  inStorage(Integer id,Integer sid,Integer goods_id,Integer num,Integer product_id,Integer itemId);
	
	
	
	/**
	 * 根据订单状态获取退货（退款单）
	 * @param order_id 订单Id
	 */
	public SellBack getSellBack(Integer order_id);
	
	/**
	 * 根据ID退货入库修改入库仓库
	 * @param id
	 */
	public void editSellBackDepotId(Integer id,Integer order_id);
	/**
	 * 系统记录退货操作日志
	 * @param id 退货单id
	 * @param status 状态
	 * @param logdetail 日志描述
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveLogBySystem(Integer id,String logdetail);
}
