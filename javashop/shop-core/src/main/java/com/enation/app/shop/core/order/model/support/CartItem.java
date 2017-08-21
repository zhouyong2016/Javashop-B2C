package com.enation.app.shop.core.order.model.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.order.model.Promotion;
import com.enation.framework.database.NotDbField;
import com.enation.framework.util.CurrencyUtil;

/**
 * 购物项
 * @author kingapex
 *2010-5-5上午10:13:27
 */
public class CartItem implements Serializable{

	private Integer id;
	private Integer product_id;
	private Integer goods_id;
	private String name;

	private Double mktprice;
	private Double price;

	private Double coupPrice; // 优惠后的价格
	private Double subtotal; // 小计

	private int num;
	private Integer limitnum; //限购数量(对于赠品)
	private String image_default;
	private Integer point;
	private Integer itemtype; //物品类型(0商品，1捆绑商品，2赠品)
	private String sn; // 捆绑促销的货号 
	private String addon; //配件字串
	private String specs;
	private int catid; //商品分类id
	private Map others; //扩展项,可通过 ICartItemFilter 进行过滤修改

	private String exchange;//积分商品积分json add by jianghongyan 2016-06-15
	private String unit;
	
	/**商品类型 2016-02-10 add_by_Sylow*/
	private int goods_type;
	
	//此购物项所享有的优惠规则
	private List<Promotion > pmtList;
	
	
	private double weight;
	
	private Integer activity_id;	//促销活动ID add by DMRain 2016-1-29
	
	private Integer is_check;		//是否選中
	
	private Integer snapshot_id;	//商品快照字段  2016-12-5 张继平
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setPmtList(List<Promotion > pmtList){
		this.pmtList = pmtList;
	}
	
	@NotDbField
	public List<Promotion > getPmtList(){
		return this.pmtList;
	}
	
	
	@NotDbField
	public Map getOthers() {
		if(others==null ) others = new HashMap();
		return others;
	}

	public void setOthers(Map others) {
		this.others = others;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer productId) {
		product_id = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getMktprice() {
		return mktprice;
	}

	public void setMktprice(Double mktprice) {
		this.mktprice = mktprice;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getCoupPrice() {
		return coupPrice;
	}

	public void setCoupPrice(Double coupPrice) {
		this.coupPrice = coupPrice;
	}

	public Double getSubtotal() {
 		this.subtotal= CurrencyUtil.mul(this.num , this.coupPrice);
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getImage_default() {
		return image_default;
	}

	public void setImage_default(String imageDefault) {
		image_default = imageDefault;
	}

	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goodsId) {
		goods_id = goodsId;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

 
	public Integer getLimitnum() {
		return limitnum;
	}

	public void setLimitnum(Integer limitnum) {
		this.limitnum = limitnum;
	}

	public Integer getItemtype() {
		return itemtype;
	}

	public void setItemtype(Integer itemtype) {
		this.itemtype = itemtype;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getAddon() {
		return addon;
	}

	public void setAddon(String addon) {
		this.addon = addon;
	}

	public String getSpecs() {
		return specs;
	}

	public void setSpecs(String specs) {
		this.specs = specs;
	}

	public int getCatid() {
		return catid;
	}

	public void setCatid(int catid) {
		this.catid = catid;
	}



	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}

	public int getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public Integer getIs_check() {
		return is_check;
	}

	public void setIs_check(Integer is_check) {
		this.is_check = is_check;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String string) {
		this.exchange = string;
	}
	@NotDbField
	public Integer getSnapshot_id() {
		return snapshot_id;
	}

	public void setSnapshot_id(Integer snapshot_id) {
		this.snapshot_id = snapshot_id;
	}
	
	
}
