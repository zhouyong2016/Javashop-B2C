package com.enation.app.shop.core.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.ShopApp;
import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.model.support.DlyTypeConfig;
import com.enation.app.shop.core.order.model.support.TypeArea;
import com.enation.app.shop.core.order.model.support.TypeAreaConfig;
import com.enation.app.shop.core.order.model.support.TypeAreaMapper;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

/**
 * 配送方式管理
 * 
 * @author Sylow
 * @version v2.0,2016年2月18日
 * @since v6.0
 */
@Service("dlyTypeManager")
public class DlyTypeManager implements IDlyTypeManager {

	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.SETTING,detail="删除配送方式")
	public void delete(Integer[] type_ids) {
		/**
		 * if (id == null || id.equals("")) return;
		 */
		String id = StringUtil.arrayToString(type_ids, ",");
		if (id == null || id.equals("")) {
			return;
		}

		String sql = "delete from es_dly_type_area where type_id in (" + id + ")";
		this.daoSupport.execute(sql);
		sql = "delete from es_dly_type where type_id in (" + id + ")";
		this.daoSupport.execute(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#getDlyTypeById(java.lang.Integer)
	 */
	@Override
	public DlyType getDlyTypeById(Integer typeId) {

		String sql = "select * from es_dly_type where type_id=?";
		DlyType dlyType = this.daoSupport.queryForObject(sql,
				DlyType.class, typeId);
		// 配置了地区费用
		if (dlyType.getIs_same() == 0) {
			dlyType.setTypeAreaList(this.listAreabyTypeId(dlyType.getType_id()));
		}
		this.convertTypeJson(dlyType);
		return dlyType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#listByAreaId(java.lang.Integer)
	 */
	@Override
	public List listByAreaId(Integer areaId) {
		String sql = "select a.* ,ta.price price from "
				+ " es_dly_area "
				+ " a left join (select  * from    "
				+ " es_dly_type_area "
				+ " where type_id=?)  ta     on ( a.area_id  = ta.area_id  )";
		List l = this.daoSupport.queryForList(sql, areaId);
		return l;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#list()
	 */
	@Override
	public List<DlyType> list() {
		String sql = "select * from es_dly_type order by ordernum";
		return this.daoSupport.queryForList(sql, DlyType.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#list(java.lang.Double, java.lang.Double, java.lang.String)
	 */
	@Override
	public List<DlyType> list(Double weight, Double orderPrice, String regoinId) {

		String sql = "select * from es_dly_type order by ordernum";
		List<DlyType> typeList = this.daoSupport.queryForList(sql,
				DlyType.class);

		sql = "select * from es_dly_type_area ";
		List typeAreaList = this.daoSupport.queryForList(sql,
				new TypeAreaMapper());

		List<DlyType> resultList = new ArrayList<DlyType>();

		for (DlyType dlyType : typeList) {

			this.convertTypeJson(dlyType);

			if (dlyType.getIs_same().intValue() == 0) {// 配置了地区费用
				List<TypeArea> areaList = this.filterTypeArea(
						dlyType.getType_id(), typeAreaList);
				Double price = this.countPrice(areaList, weight, orderPrice,
						regoinId);

				// 不在配送范围，使用统一配置
				if (price == null
						&& dlyType.getTypeConfig().getDefAreaFee() != null
						&& dlyType.getTypeConfig().getDefAreaFee().compareTo(1) == 0) {
					price = this.countExp(dlyType.getExpressions(), weight,
							orderPrice);
					if (price.compareTo(-1D) == 0) {// 如果公式出错了，则此配送方式不可用
						price = null;
					}
				}
				// 准则是null则不可用
				if (price != null) {
					dlyType.setPrice(price);
					resultList.add(dlyType);
				}

			} else {// 统一配置
				Double price = this.countExp(dlyType.getExpressions(), weight,
						orderPrice);
				if (price.compareTo(-1D) != 0) {// 如果公式出错了，则此配送方式不可用
					dlyType.setPrice(price);
					resultList.add(dlyType);
				}
			}
		}

		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#pageDlyType(int, int)
	 */
	@Override
	public Page pageDlyType(int page, int pageSize) {

		/**
		 * 如果是多店产品，说明是自营店查询
		 */
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			return this.pageSelf(page, pageSize);
		} else {
			String sql = "select * from es_dly_type order by ordernum";
			Page webpage = this.daoSupport
					.queryForPage(sql, page, pageSize);
			return webpage;
		}

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#add(com.enation.app.shop.core.order.model.DlyType, com.enation.app.shop.core.order.model.support.DlyTypeConfig)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="添加了一个名为${type.name}的配送方式【同一配配置式】")
	public Integer add(DlyType type, DlyTypeConfig config) {

		// 统一设置
		if (type.getIs_same().intValue() == 1) {
			type = this.fillType(type, config);
			this.daoSupport.insert("es_dly_type", type);

		} else {
			throw new RuntimeException("type not is same config,cant'add");
		}
		int typeId = this.daoSupport.getLastId("es_dly_type");

		/**
		 * 如果是多店产品，说明是自营店保存
		 */
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			this.selfStoreSetting(typeId);
		}

		return typeId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#add(com.enation.app.shop.core.order.model.DlyType, com.enation.app.shop.core.order.model.support.DlyTypeConfig, com.enation.app.shop.core.order.model.support.TypeAreaConfig[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="添加了一个名为${type.name}配送方式【分不同地区费用】")
	public void add(DlyType type, DlyTypeConfig config,
			TypeAreaConfig[] configArray) {

		type = this.fillType(type, config);
		this.daoSupport.insert("es_dly_type", type);
		Integer typeId = this.daoSupport.getLastId("es_dly_type");
		this.addTypeArea(typeId, configArray);

		/**
		 * 如果是多店产品，说明是自营店保存
		 */
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			this.selfStoreSetting(typeId);
		}
	}

	/**
	 * 为自营店做一些设置<br>
	 * 填充好templateid
	 * 
	 * @param typeId
	 */
	private void selfStoreSetting(int typeId) {
		try {
			// 查询出自营店的模板
			String sql = "select id from es_store_template where store_id=? and def_temp=1";
			Integer templateid = this.daoSupport.queryForInt(sql,
					ShopApp.self_storeid);

			// 为配送方式设置模板id
			sql = "update es_dly_type set template_id=? where type_id=?";
			this.daoSupport.execute(sql, templateid, typeId);

		} catch (Exception e) {
			// 捕获到异常说明不存在自营店的模板
			throw new RuntimeException("未找到自营店配送模板");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#edit(com.enation.app.shop.core.order.model.DlyType, com.enation.app.shop.core.order.model.support.DlyTypeConfig)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改名为${type.name}的配送方式")
	public void edit(DlyType type, DlyTypeConfig config) {
		if (type.getType_id() == null) {
			throw new RuntimeException("type id is null ,can't edit");
		}
		// 统一设置
		if (type.getIs_same().intValue() == 1) {
			Integer typeId = type.getType_id();
			this.daoSupport.execute("delete from es_dly_type_area where type_id=?", typeId);
			type = this.fillType(type, config);
			this.daoSupport.update("es_dly_type", type, "type_id=" + type.getType_id());
		} else {
			throw new RuntimeException("type  is not same config,cant'edit");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#edit(com.enation.app.shop.core.order.model.DlyType, com.enation.app.shop.core.order.model.support.DlyTypeConfig, com.enation.app.shop.core.order.model.support.TypeAreaConfig[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="修改了名为${type.name}的配送方式")
	public void edit(DlyType type, DlyTypeConfig config,
			TypeAreaConfig[] configArray) {

		if (type.getType_id() == null) {
			throw new RuntimeException("type id is null ,can't edit");
		}
		type = this.fillType(type, config);
		Integer typeId = type.getType_id();
		this.daoSupport.execute("delete from es_dly_type_area where type_id=?", typeId);

		this.addTypeArea(typeId, configArray);

		this.daoSupport.update("es_dly_type", type, "type_id=" + type.getType_id());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#countExp(java.lang.String, java.lang.Double, java.lang.Double)
	 */
	@Override
	public Double countExp(String exp, Double weight, Double orderprice) {
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects();
			String str = "var w=" + weight + ";";
			str += "p=" + orderprice + ";";
			str += "function tint(value){return value<0?0:value;}";
			str += exp;
			Object result = cx.evaluateString(scope, str, null, 1, null);
			Double res = Context.toNumber(result);

			return res;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.exit();
		}
		return -1D;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#countPrice(java.lang.Integer, java.lang.Double, java.lang.Double, java.lang.String)
	 */
	@Override
	public Double[] countPrice(Integer typeId, Double weight,
			Double orderPrice, String regionId) {

		DlyType dlyType = this.getDlyTypeById(typeId);
		Double dlyPrice = null;
		if (dlyType.getIs_same().compareTo(1) == 0) { // 统一的费用配置
			dlyPrice = this.countExp(dlyType.getExpressions(), weight,
					orderPrice);
		} else {
			dlyPrice = this.countPrice(dlyType.getTypeAreaList(), weight,
					orderPrice, regionId);
		}
		Double protectPrice = null;
		// b2b2c 在不影响 原来程序上加默认配送费用
		//修改原有逻辑的bug edit by jianghongyan
		if (dlyPrice == null) {
			dlyPrice = this.countExp(dlyType.getExpressions(), weight,
					orderPrice);
		}
		if("b2b2c".equals(EopSetting.PRODUCT)&&dlyPrice<0){
			dlyPrice = this.countExp(dlyType.getExpressions(), weight,
					orderPrice);
		}
		//edit end

		// 精度到小数点后两位 2015-08-31 by kingapex
		dlyPrice = CurrencyUtil.round(dlyPrice, 2);

		Double[] priceArray = { dlyPrice, protectPrice };
		return priceArray;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyTypeManager#getDlyTotal()
	 */
	@Override
	public int getDlyTotal() {
		int total = 0;
		String sql = "";
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			sql = "select count(0) from es_dly_type dt,es_store_template st where st.id=dt.template_id and st.store_id=? and def_temp=1 ";
			total = this.daoSupport.queryForInt(sql, ShopApp.self_storeid);
		} else {
			sql = "select count(0) from es_dly_type";
			total = this.daoSupport.queryForInt(sql);
		}
		
		return total;
	}
	
	/**
	 * 
	 * @param type
	 * @param config
	 * @return
	 */
	private DlyType fillType(DlyType type, DlyTypeConfig config) {
		Double firstprice = config.getFirstprice(); // 首重费用
		Double continueprice = config.getContinueprice();// 续重费用
		Integer firstunit = config.getFirstunit(); // 首重重量
		Integer continueunit = config.getContinueunit(); // 续重重量

		// 组合公式
		String expressions = null;

		if (config.getUseexp() == 0) {
			expressions = this.createExpression(firstprice, continueprice,
					firstunit, continueunit);
		} else {
			expressions = config.getExpression();
		}

		type.setExpressions(expressions);
		type.setConfig(JSONObject.fromObject(config).toString());
		return type;
	}

	/**
	 * 
	 * @param typeId
	 * @param configArray
	 */
	private void addTypeArea(Integer typeId, TypeAreaConfig[] configArray) {
		for (TypeAreaConfig areaConfig : configArray) {
			if (areaConfig != null) {

				TypeArea typeArea = new TypeArea();
				typeArea.setArea_id_group(areaConfig.getAreaId());
				typeArea.setArea_name_group(areaConfig.getAreaName());
				typeArea.setType_id(typeId);
				typeArea.setHas_cod(areaConfig.getHave_cod());

				typeArea.setConfig(JSONObject.fromObject(areaConfig).toString());
				String expressions = "";
				if (areaConfig.getUseexp().intValue() == 1) { // 启用公式
					expressions = areaConfig.getExpression();
				} else {
					// 组合公式
					expressions = createExpression(areaConfig);
				}
				typeArea.setExpressions(expressions);
				this.daoSupport.insert("es_dly_type_area", typeArea);
			}
		}
	}

	/**
	 * 生成公式字串
	 * 
	 * @param areaConfig
	 * @return
	 */
	private String createExpression(TypeAreaConfig areaConfig) {

		return this.createExpression(areaConfig.getFirstprice(),
				areaConfig.getContinueprice(), areaConfig.getFirstunit(),
				areaConfig.getContinueunit());
	}

	/**
	 * 生成公式字串
	 * 
	 * @param firstprice
	 * @param continueprice
	 * @param firstunit
	 * @param continueunit
	 * @return
	 */
	private String createExpression(Double firstprice, Double continueprice,
			Integer firstunit, Integer continueunit) {
		return firstprice + "+tint(w-" + firstunit + ")/" + continueunit + "*"
				+ continueprice;
	}

	/**
	 * 
	 * @param typeid
	 * @return
	 */
	private List listAreabyTypeId(Integer typeid) {
		String sql = "select * from es_dly_type_area where type_id=?";
		List typeAreaList = daoSupport.queryForList(sql,
				new TypeAreaMapper(), typeid);
		return typeAreaList;
	}
	
	/**
	 * 检测某配送方式是否在配送地区
	 * 
	 * @param areaList
	 *            配送方式的地区列表
	 * @param weight
	 *            商品重量
	 * @param orderPrice
	 *            订单金额
	 * @param regoinId
	 *            地区id
	 * @return 找到相应的配送地区的配送价格并计算配送费用，注：如果区域重合则找到最贵的配送方式并计算费用</br>
	 *         如果无匹配配送地区则返回null
	 */
	private Double countPrice(List<TypeArea> areaList, Double weight,
			Double orderPrice, String regoinId) {
		Double price = null;
		for (TypeArea typeArea : areaList) {
			String idGroup = typeArea.getArea_id_group();

			if (idGroup == null || idGroup.equals("")) {
				continue;
			}

			idGroup = idGroup == null ? "" : idGroup;
			String[] idArray = idGroup.split(",");

			// 检测所属地区是否在配送范围
			if (StringUtil.isInArray(regoinId, idArray)) {
				Double thePrice = this.countExp(typeArea.getExpressions(),
						weight, orderPrice);
				if (price != null)
					price = thePrice.compareTo(price) > 0 ? thePrice : price;
				else
					price = thePrice;
			}

		}

		return price;
	}

	/**
	 * 过滤某个配送方式的 地区对照表
	 * 
	 * @param type_id
	 *            配送方式id
	 * @param typeAreaList
	 *            要过滤的列表
	 * @return 此配送方式的地区对照表
	 */
	private List<TypeArea> filterTypeArea(Integer type_id, List typeAreaList) {

		List<TypeArea> areaList = new ArrayList<TypeArea>();
		for (int i = 0, len = typeAreaList.size(); i < len; i++) {
			TypeArea typeArea = (TypeArea) typeAreaList.get(i);
			if (typeArea.getType_id().compareTo(type_id) == 0) {
				areaList.add(typeArea);
			}
		}

		return areaList;
	}

	/**
	 * 转换type的json信息</br> 有两个json信息要转换：</br>
	 * 一个是将type的config字串转换为TypeConfig对象并填充给typeConfig属性</br>
	 * 另一个是将type对象本身转换为json并填充给json属性
	 * 
	 * @param dlyType
	 */
	private void convertTypeJson(DlyType dlyType) {

		String config = dlyType.getConfig();
		JSONObject typeJsonObject = JSONObject.fromObject(config);
		DlyTypeConfig typeConfig = (DlyTypeConfig) JSONObject.toBean(
				typeJsonObject, DlyTypeConfig.class);
		dlyType.setTypeConfig(typeConfig);
		dlyType.setJson(JSONObject.fromObject(dlyType).toString());
	}
	
	/**
	 * 查询自营店的配送方式<br>
	 * 此处将多店和单店的逻辑混在一起了，原因是配送方式的保存过于复杂<br>
	 * 复制一份放在b2b2c里会导致无法重用。<br>
	 * 如果有更好的方案应改进
	 * 
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            页大小
	 * @return 配送方式分页列表
	 */
	private Page pageSelf(int pageNo, int pageSize) {

		/**
		 * 联合es_dly_type表和es_store_template表查询 某店铺的所有配送方式，两表通过tempate_id字段关联。
		 */
		String sql = "select dt.* from  es_dly_type dt,es_store_template st  where st.id=dt.template_id and st.store_id=? and def_temp=1 order by type_id";
		Page page = daoSupport.queryForPage(sql, pageNo, pageSize,
				ShopApp.self_storeid);

		return page;

	}

	@Override
	public DlyType getDlyTypeByName(String name) {
		String sql = "select * from es_dly_type where name=?";
		DlyType dlyType = this.daoSupport.queryForObject(sql,
				DlyType.class, name);
		return dlyType;
	}

	@Override
	public DlyType getDlyTypeByName(String name, Integer isSame) {
		String sql = "select * from es_dly_type where name=? and is_same=?";
		DlyType dlyType = this.daoSupport.queryForObject(sql,
				DlyType.class, name, isSame);
		return dlyType;
	}
}
