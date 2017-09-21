package com.enation.app.shop.component.spec.plugin.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.component.spec.service.ISpecValueManager;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.model.SpecValue;
import com.enation.app.shop.core.goods.model.Specification;
import com.enation.app.shop.core.goods.plugin.AbstractGoodsPlugin;
import com.enation.app.shop.core.goods.plugin.IGoodsDeleteEvent;
import com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.goods.service.SnDuplicateException;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.StringUtil;

/**
 * 商品规格插件
 * @author enation
 *
 */
@Component
public class GoodsSpecPlugin extends AbstractGoodsPlugin implements IGoodsDeleteEvent,IGoodsTabShowEvent,IAjaxExecuteEnable{
 
	@Autowired
	private IProductManager productManager;
	
	@Autowired
	private IMemberLvManager memberLvManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private ISpecManager specManager;
	
	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	@Autowired
	private ICartManager cartManager;
	
	@Autowired
	private ISpecValueManager specValueManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGoodsBeforeAddEvent#onBeforeGoodsAdd(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		this.processGoods(goods, request);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGoodsAfterEditEvent#onAfterGoodsEdit(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request)  {
		this.processSpec(goods, request);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGoodsBeforeEditEvent#onBeforeGoodsEdit(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request)  {
		this.processGoods(goods, request);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGoodsAfterAddEvent#onAfterGoodsAdd(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request) {
		this.processSpec(goods, request);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGetGoodsEditHtmlEvent#getEditHtml(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String getEditHtml(Map goods, HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		Integer goods_id = Integer.valueOf(goods.get("goods_id").toString());
		List<String> specNameList = productManager.listSpecName(goods_id);
		List<Product> productList = productManager.list(goods_id);

		int type_id = NumberUtils.toInt(goods.get("type_id").toString(), 0);

		List<Specification> specList = this.specManager.listSpecAndValueByType(type_id,goods_id);
		freeMarkerPaser.putData("specList", specList);

		List lvList = this.memberLvManager.list();
		freeMarkerPaser.putData("lvList", lvList);

		freeMarkerPaser.putData("productList", productList);
		freeMarkerPaser.putData("specNameList", specNameList);
		freeMarkerPaser.setPageName("spec2");
		return freeMarkerPaser.proessPageContent();
	}	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGetGoodsAddHtmlEvent#getAddHtml(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();

		List<Specification> specList = null;
		int cat_id = NumberUtils.toInt(request.getParameter("catid"),0);
		Cat goodsCat = goodsCatManager.getById(cat_id);
		if(goodsCat == null || goodsCat.getType_id() == null){
			specList = new ArrayList<Specification>();
		}else{
			specList = this.specManager.listSpecAndValueByType(goodsCat.getType_id(),0);
		}
		freeMarkerPaser.putData("specList", specList);
		freeMarkerPaser.setPageName("spec2");
		return freeMarkerPaser.proessPageContent();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGoodsDeleteEvent#onGoodsDelete(java.lang.Integer[])
	 */
	@Override
	public void onGoodsDelete(Integer[] goodsid) {
		 this.productManager.delete(goodsid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent#getTabName()
	 */
	@Override
	public String getTabName() {
		return "规格";
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent#getOrder()
	 */
	@Override
	public int getOrder() {
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.plugin.IAjaxExecuteEnable#execute()
	 */
	@Override
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		if ("check-pro-in-order".equals(action)) {
			int productid = StringUtil.toInt(request.getParameter("productid"),	true);
			boolean isinorder = this.orderManager.checkProInOrder(productid);
			if (isinorder) {
				return "{result:1}";
			} else {
				return "{result:0}";
			}
		} else if ("check-goods-in-order".equals(action)) {
			int goodsid = StringUtil.toInt(request.getParameter("goodsid"),	true);
			boolean isinorder = this.orderManager.checkGoodsInOrder(goodsid);
			if (isinorder) {
				return "{result:1}";
			} else {
				return "{result:0}";
			}
		}
		return "";
	}
	
	/**
	 * 2011-01-12新增：修复保存商品时，其货品的id会全部重新生成的问题<br>
	 * 处理有规格商品的逻辑
	 * @param goods
	 * @param request
	 */
	private void processSpec(Map goods, HttpServletRequest request) {
		if (goods.get("goods_id") == null)
			throw new RuntimeException("商品id不能为空");
	
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();

		String haveSpec = httpRequest.getParameter("haveSpec");
		String[] sns = httpRequest.getParameterValues("sns");
		
		//判断开启规格并且选择了规格才可添加货品
		if ("1".equals(haveSpec)&&sns!=null) {
			createSpecGoods(goods,sns);
		} else {
			createNotSpecGoods(goods);
		}
	}
	
	/**
	 * 添加带有规格的商品
	 * 每一行（即每一个货品product）,有如下hidden：
	 * specids(规格id数组，以,号隔开)如：1,2
	 * specvids(规格值数组，以,号隔开)如21,20
	 * productids货品id数组，如果为新增，则为空
	 * @param goods 商品信息
	 */
	private void createSpecGoods(Map goods,String[] sns){
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String[] specidsAr = httpRequest.getParameterValues("specids"); //规格id数组:规格	
		String[] specvidsAr = httpRequest.getParameterValues("specvids");//规格值id数组:规格值
		
		
		String[] productids = httpRequest.getParameterValues("productids"); //货品id数组
		String[] prices = httpRequest.getParameterValues("prices");
		String[] costs = httpRequest.getParameterValues("costs");
		
		String[] weights = httpRequest.getParameterValues("weights");
		
		String[] image = httpRequest.getParameterValues("spec_image");
		String[] specValues = httpRequest.getParameterValues("spec_value");
		Integer goodsId = Integer.valueOf(goods.get("goods_id").toString());
		List<Product> productList = new ArrayList<Product>();
		
		int i = 0;
		int snIndex = this.getSnsSize(sns);
		for (String sn : sns) {
			Integer productId = StringUtil.isEmpty(productids[i]) ? null : Integer.valueOf(productids[i]);
			if (sn == null || sn.equals("")) {
				sn = goods.get("sn") + "-" + (snIndex + 1);
				snIndex++;
			}
		
				/*
				 * 组合商品、货品、规格值、规格对应关系
				 */
				List<SpecValue> valueList = new ArrayList<SpecValue>();
				int j = 0;
				String[] specids = specidsAr[i].split(","); // 此货品的规格
				String[] specvids = specvidsAr[i].split(","); // 此货品的规格值
				String[] specvalues = httpRequest.getParameterValues("specvalue_" + i);
				//此货品的规格值list
				for (String specid : specids) {
					SpecValue specvalue = new SpecValue();
					
					Integer index = -1;
					if(specValues!=null){
						for(int l = 0; l < specValues.length; l++) {  		          
					        if(specvids[j].equals(specValues[l]) ) {   
					        	index =  l;           
					        }
					    }
					}
					String specImage = "";
					if(index!=-1){
						specImage = image[index];
					}
					//添加规格
					if(specValueManager.checkSpecValue(specvalues[j].toString(),specImage)!=1){
						if(Integer.parseInt(specid)==1){
							specvalue.setSpec_type(1);
							
							if(index!=-1){
								specvalue.setSpec_image(image[index]);
							}
						}else{
							specvalue.setSpec_type(0);
							specvalue.setSpec_image("http://static.v4.javamall.com.cn/spec/spec_def.gif");
						}
						specvalue.setSpec_id(Integer.parseInt(specid));
						specvalue.setInherent_or_add(1);
						specvalue.setSpec_value(specvalues[j].trim());
						specValueManager.delInherentSpec(goodsId);
						specValueManager.add(specvalue);
						specvalue.getSpec_value_id();
					}else{
						specvalue.setSpec_value(specvalues[j].trim());
						int spec_value_id = specValueManager.selSpecValAndId(Integer.parseInt(specid), specvalues[j],specImage);
						specvalue.setSpec_value_id(spec_value_id);
						specvalue.setSpec_id(Integer.valueOf(specid.trim()));
					}
					valueList.add(specvalue);
					j++;
				} 
			// 生成货品对象
			Product product = new Product();
			product.setGoods_id(goodsId);
			product.setSpecList(valueList);// 设置此货品的规格list
			product.setName((String) goods.get("name"));
			product.setSn(sn);
			product.setProduct_id(productId);

			product.setSpecs(StringUtil.arrayToString(specvalues, "、"));
			// 价格
			if (null == prices[i] || "".equals(prices[i])){
				product.setPrice(0D);
			}else{
				product.setPrice(Double.valueOf(prices[i]));
			}
			
			//修改商品的价格，同时把购物车中的商品价格修改.
			if(productId!=null){
				Product pro = this.productManager.get(productId);
				if(!(pro.getPrice().doubleValue()==Double.valueOf(prices[i].toString()).doubleValue())){
					this.cartManager.updatePriceByProductid(productId, Double.valueOf(prices[i].toString()));
				}
			}
			
			// 成本价
			if (null == costs[i] || "".equals(costs[i])){
				product.setCost(0D);
			}else{
				product.setCost(Double.valueOf(costs[i]));
			}
			// 重量
			if (null == weights[i] || "".equals(weights[i])){
				product.setWeight(0D);
			}else{
				product.setWeight(Double.valueOf(weights[i]));
			}
			productList.add(product);
			i++;
		} 
		
		this.productManager.add(productList);
	}
	
	/**
	 * 创建非规格的商品
	 * @param goods 商品信息
	 */
	private void createNotSpecGoods(Map goods){
		Integer goodsId = Integer.valueOf(goods.get("goods_id").toString());
		Product product = this.productManager.getByGoodsId(goodsId);
		if (product == null) {
			product = new Product();
		}
		
		
		if(product.getProduct_id()!=null){
			if(!(product.getPrice().doubleValue()==Double.valueOf("" + goods.get("price")).doubleValue())){
				this.cartManager.updatePriceByProductid(product.getProduct_id(), Double.valueOf("" + goods.get("price")));
			}
		}
		
		product.setGoods_id(goodsId);
		product.setCost(Double.valueOf("" + goods.get("cost")));
		product.setPrice(Double.valueOf("" + goods.get("price")));
		product.setSn((String) goods.get("sn"));
		product.setWeight(Double.valueOf("" + goods.get("weight")));
		product.setName((String) goods.get("name"));
		
		List<Product> productList = new ArrayList<Product>();
		productList.add(product);
		this.productManager.add(productList);
	}
	
	/**
	 * 在添加商品之前处理商品要添加的数据
	 * @param goods
	 * @param request
	 */
	private void processGoods(Map goods, HttpServletRequest request) {
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String haveSpec = httpRequest.getParameter("haveSpec");
		goods.put("have_spec", haveSpec);
		//未开启规格
		if("0".equals(haveSpec) || haveSpec == null){
			goods.put("cost", StringUtil.toDouble(httpRequest.getParameter("cost"),0d));
			goods.put("price", StringUtil.toDouble(httpRequest.getParameter("price"),0d));
			goods.put("weight", StringUtil.toDouble(httpRequest.getParameter("weight"),0d));
		} else if ("1".equals(haveSpec)) {
			
			if(EopSetting.PRODUCT.equals("b2b2c")){
				
				Integer storeid =null;
				
				if(goods.get("store_id")==null){
					storeid= StringUtil.toInt( httpRequest.getParameter("storeid"),null);
				}else{
					storeid = Integer.parseInt(goods.get("store_id").toString());
				}
				
				if(storeid==null){
					throw new RuntimeException("参数错误，storeid必须传递");
				}
				String[] sns = httpRequest.getParameterValues("sns");
				if(sns!=null){
					//本次添加编辑的货号是否相等。 如果相等抛出异常
					for (int i = 0; i < sns.length; i++) {
						int equalNum = 0;
						for (int j = 0; j < sns.length; j++) {
							if(sns[i].equals(sns[j])){
								equalNum++;
							}
						}
						if(equalNum>=2){
							throw new SnDuplicateException(sns[i]);
						}
					}
				}
			}
			
			if (!"yes".equals(httpRequest.getParameter("isedit"))) {
				// 默认值
				goods.put("cost", 0);
				goods.put("price", 0);
				goods.put("weight", 0);
			}
			//记录规格相册关系
			String specs_img = httpRequest.getParameter("spec_imgs");
			specs_img = specs_img == null ? "{}" : specs_img;
			goods.put("specs", specs_img);
			
			String[] prices = httpRequest.getParameterValues("prices");
			String[] costs = httpRequest.getParameterValues("costs");
			String[] weights = httpRequest.getParameterValues("weights");
			
			if (prices != null && prices.length > 0) {
				goods.put("price", prices[0]);
			}
			if (costs != null && costs.length > 0) {
				goods.put("cost", costs[0]);
			}
			if (weights != null && weights.length > 0) {
				goods.put("weight", weights[0]);
			}
		}
	}
	
	/**
	 * 获取已有的货号数量
	 * @param sns
	 * @return
	 */
	private int getSnsSize(String[] sns) {
		int i = 0;
		for (String sn : sns) {
			if (!StringUtil.isEmpty(sn)) {
				i++;
			}
		}
		return i;
	}
}
