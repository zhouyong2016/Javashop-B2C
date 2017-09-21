package com.enation.app.shop.core.goods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.goods.model.Attribute;
import com.enation.app.shop.core.goods.model.GoodsParam;
import com.enation.app.shop.core.goods.model.GoodsType;
import com.enation.app.shop.core.goods.model.support.GoodsTypeDTO;
import com.enation.app.shop.core.goods.model.support.ParamGroup;
import com.enation.app.shop.core.goods.service.GoodsTypeUtil;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 商品类型管理
 * @author kingapex
 * 2010-1-10下午05:53:40
 */
@Service("goodsTypeManager")
public class GoodsTypeManager  implements IGoodsTypeManager {
	private static final Log loger = LogFactory.getLog(GoodsTypeManager.class  );

	@Autowired
	private IDaoSupport  daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#listAll()
	 */
	@Override
	public List listAll() {
		String sql   = "select * from es_goods_type where disabled=0";
		List typeList = this.daoSupport.queryForList(sql,GoodsType.class);

		return typeList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#pageType(java.lang.String, int, int, java.util.Map)
	 */
	@Override
	public Page pageType(String order, int page, int pageSize,Map params) {

		String keyword = (String) params.get("keyword");		
		order = order == null ? " type_id desc" : order;

		String sql  = "select * from es_goods_type where disabled = 0";

		if(!StringUtil.isEmpty(keyword)){
			sql += " and name like '%"+keyword+"%'";
		}
		sql += " order by" + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#pageTrashType(java.lang.String, int, int)
	 */
	@Override
	public Page pageTrashType(String order,int page,int pageSize){
		order  = order==null?" type_id desc":order;

		String sql  = "select * from es_goods_type where disabled=1";
		sql+="  order by ";
		sql+=order;

		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}




	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#get(java.lang.Integer)
	 */
	@Override
	public GoodsTypeDTO get(Integer type_id) {
		String sql ="select * from es_goods_type where type_id=?";
		GoodsTypeDTO type=(GoodsTypeDTO) daoSupport.queryForObject(sql, GoodsTypeDTO.class, type_id);//(sql, new GoodsTypeMapper(), type_id);该DTO继承商品类型对象，因为要转换类型的属性、参数、品牌值并返回
		if(type==null){
			return null;
		}
		List<Attribute> propList = GoodsTypeUtil.converAttrFormString(type.getProps());
		ParamGroup[] paramGroups  = GoodsTypeUtil.converFormString(type.getParams());
		List brandList  = this.getBrandListByTypeId(type_id);
		type.setPropList(propList);
		type.setParamGroups(paramGroups);
		type.setBrandList(brandList);
		type.setSpecList(this.getSpecListByTypeId(type_id));
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#getById(int)
	 */
	@Override
	public GoodsType getById(int typeid) {
		String sql ="select * from es_goods_type where type_id=?";
		return this.daoSupport.queryForObject(sql, GoodsType.class, typeid);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#getBrandListByTypeId(int)
	 */
	@Override
	public List getBrandListByTypeId(int type_id) {
		String sql ="select b.name name ,b.brand_id brand_id,0 as num from es_type_brand tb inner join es_brand b  on    b.brand_id = tb.brand_id where tb.type_id=? and b.disabled=0";

		List list = this.daoSupport.queryForList(sql,type_id);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#listByTypeId(java.lang.Integer)
	 */
	@Override
	public List listByTypeId(Integer typeid) {
		String sql ="select b.* from es_type_brand tb inner join es_brand b  on    b.brand_id = tb.brand_id where tb.type_id=? and b.disabled=0";

		List list =	this.daoSupport.queryForList(sql,  typeid);

		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#getAttrListByTypeId(int)
	 */
	@Override
	public List<Attribute> getAttrListByTypeId(int type_id) {
		GoodsTypeDTO type = this.get(type_id);
		if(type.getHave_prop()==0) return new ArrayList<Attribute>();
		return type.getPropList();

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#getParamArByTypeId(int)
	 */
	@Override
	public ParamGroup[] getParamArByTypeId(int type_id) {
		String params = getParamsByTypeId(type_id);
		return GoodsTypeUtil.converFormString(params);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#getPropsString(java.lang.String[], int[], java.lang.String[], java.lang.String[], int[], java.lang.String[])
	 */
	@Override
	public String getPropsString(String[] propnames, int[] proptypes,
			String[] options,String[] unit, int[] required,String[] datatype) {
		List list = toAttrList(propnames, proptypes, options,unit,required,datatype);
		JSONArray jsonarray = JSONArray.fromObject(list);

		return jsonarray.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#getParamString(java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[])
	 */
	@Override
	public String getParamString(String[] paramnums, String[] groupnames,
			String[] paramnames, String[] paramvalues) {
		List list = toParamList(paramnums, groupnames, paramnames, paramvalues);
		JSONArray jsonarray = JSONArray.fromObject(list);
		return jsonarray.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#save(com.enation.app.shop.core.goods.model.GoodsType)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED) 
	@com.enation.framework.annotation.Log(type=LogType.GOODS,detail="添加商品类型名为${type.name}的商品")
	public Integer save(GoodsType type) {
		String typeTableName ="es_goods_type";
		Integer[] brand_id = type.getBrand_ids();

		type.setBrand_ids(null);
		if(type.getParams()!=null && type.getParams().equals("[]")){
			type.setParams("");
		}
		Integer type_id=null;
		if(type.getType_id()!=null){
			type_id = type.getType_id();
			if(type.getHave_prop()==0){
				type.setProps(null);
			}
			if(type.getHave_parm()==0){
				type.setParams(null);
			}
			this.daoSupport.update(typeTableName, type, "type_id="+ type_id);
			//			if(type.getJoin_brand()==0){
			//				String sql ="delete from es_type_brand where type_id = ?";
			//				this.daoSupport.execute(sql,type_id);
			//			}
		}else{ //新增
			daoSupport.insert(typeTableName, type);
			type_id  = this.daoSupport.getLastId(typeTableName);
			if(loger.isDebugEnabled()){
				loger.debug("增加商品类型成功 , id is " + type_id);
			}

		}

		return type_id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#saveTypeBrand(com.enation.app.shop.core.goods.model.GoodsType)
	 */
	@Override
	@com.enation.framework.annotation.Log(type=LogType.GOODS,detail="保存商品类型名为${type.name}的商品类型  品牌信息")
	public Integer saveTypeBrand(GoodsType type) {
		Integer[] brand_id = type.getBrand_ids();
		Integer type_id=type.getType_id();
		String sql ="delete from es_type_brand where type_id = ?";
		this.daoSupport.execute(sql,type_id);
		if (brand_id != null) {
			for (int i = 0; i < brand_id.length; i++) {
				Map map=new HashMap();
				map.put("type_id", type_id);
				map.put("brand_id", brand_id[i]);
				daoSupport.insert("es_type_brand", map);
			}
		}	
		return type_id;

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#saveTypeSpec(com.enation.app.shop.core.goods.model.GoodsType)
	 */
	@Override
	@com.enation.framework.annotation.Log(type=LogType.GOODS,detail="保存商品类型名为${type.name}的商品类型  规格信息")
	public Integer saveTypeSpec(GoodsType type){
		Integer[] spec_ids = type.getSpec_ids();
		Integer type_id=type.getType_id();
		String sql ="delete from es_type_spec where type_id = ?";
		this.daoSupport.execute(sql,type_id);
		if (spec_ids != null && spec_ids.length > 0) {
			for (Integer spec_id : spec_ids) {
				Map map=new HashMap();
				map.put("type_id", type_id);
				map.put("spec_id", spec_id);
				daoSupport.insert("es_type_spec", map);

			}
		}
		return type_id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#delete(java.lang.Integer[])
	 */
	@Override
	@com.enation.framework.annotation.Log(type=LogType.GOODS,detail="将商品类型放入回收站中")
	public int delete(Integer[] type_ids) {

		if(type_ids==null) return 1;

		String ids = "";
		for (int i = 0; i < type_ids.length; i++) {
			if(i!=0)
				ids+=",";
			ids+=type_ids[i];
		}
		String sql  ="select count(0) from es_goods where type_id in ("+ids+")";
		int count = this.daoSupport.queryForInt(sql);

		sql="select count(0) from es_goods_cat where type_id in ("+ids+")";
		int catcout=this.daoSupport.queryForInt(sql);
		if(catcout>0){
			return 0;
		} 

		if(count==0){
			sql  = "update  es_goods_type set disabled=1  where type_id in ("+ids+")";
			this.daoSupport.execute(sql) ;
			return 1;
		}
		return 0;

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#clean(java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED) 
	@com.enation.framework.annotation.Log(type=LogType.GOODS,detail="清楚商品类型及其关联的品牌")
	public void clean(Integer[] type_ids){
		if(type_ids==null) return ;
		String ids = "";
		for (int i = 0; i < type_ids.length; i++) {
			if(i!=0)
				ids+=",";
			ids+=type_ids[i];
		}
		String sql  ="delete from es_goods_type where type_id in("+ids+")";
		this.daoSupport.execute(sql);

		sql="delete from es_type_brand where type_id in("+ids+")";
		this.daoSupport.execute(sql);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#revert(java.lang.Integer[])
	 */
	@Override
	@com.enation.framework.annotation.Log(type=LogType.GOODS,detail="将商品类型还原")
	public void revert(Integer[] type_ids){

		if(type_ids==null) return ;
		String ids = "";
		for (int i = 0; i < type_ids.length; i++) {
			if(i!=0)
				ids+=",";
			ids+=type_ids[i];
		}
		String sql  = "update  es_goods_type set disabled=0  where type_id in ("+  ids +")";
		this.daoSupport.execute(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTypeManager#checkname(java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean checkname(String name, Integer typeid) {
		if(name!=null){
			name=name.trim();
		}
		String sql  ="select count(0) from es_goods_type where name=? and type_id!=? and disabled=0";
		if(typeid==null){
			typeid= 0;
		}
		int count = this.daoSupport.queryForInt(sql, name,typeid);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 根据页面用户输入的信息形成 Attribute 对象的List
	 * 
	 * @param propnames
	 * @param proptypes
	 * @param options
	 * @return
	 */
	private List<Attribute> toAttrList(String[] propnames, int[] proptypes,
			String[] options,String[] unit, int[] required,String[] datatype) {
		List<Attribute> attrList = new ArrayList<Attribute>();

		if(propnames!=null && proptypes!= null && options!= null){
			for (int i = 0; i < propnames.length; i++) {

				Attribute attribute = new Attribute();
				String name = propnames[i];
				String option = options[i];
				int type = proptypes[i];

				attribute.setName(name);
				attribute.setOptions(option);
				attribute.setType(type);
				attribute.setDatatype(datatype[i]);
				attribute.setRequired(required[i]);
				attribute.setUnit(unit[i]);
				attrList.add(attribute);

			}
		}
		return attrList;
	}

	/**
	 * 根据页面用户的参数信息 生成 ParamGroup 实体的List
	 * 
	 * @param paramnum
	 * @param groupnames
	 * @param paramnames
	 * @return
	 */
	private List<ParamGroup> toParamList(String[] ar_paramnum,
			String[] groupnames, String[] paramnames, String[] paramvalues) {

		List<ParamGroup> list = new ArrayList<ParamGroup>();
		if (groupnames != null) {
			for (int i = 0; i < groupnames.length; i++) {
				ParamGroup paramGroup = new ParamGroup();
				paramGroup.setName(groupnames[i]);
				List<GoodsParam> paramList = getParamList(ar_paramnum,
						paramnames, paramvalues, i);
				paramGroup.setParamList(paramList);
				list.add(paramGroup);
			}
		}
		return list;
	}

	/**
	 * 根据页面用户输入信息生成GoodsParam 对象的List,此list将被保存在相应的ParamGroup对象中 <br/>
	 * GoodsParam对象只有name 属性会被赋值,value属性不会被处理.
	 * 
	 * @param ar_paramnum
	 * @param paramnames
	 * @param index
	 * @return
	 */
	private List<GoodsParam> getParamList(String[] ar_paramnum,
			String[] paramnames, String[] paramvalues, int groupindex) {
		int[] pos = this.countPos(groupindex, ar_paramnum);
		List<GoodsParam> list = new ArrayList<GoodsParam>();
		for (int i = pos[0]; i < pos[1]; i++) {
			GoodsParam goodsParam = new GoodsParam();
			if(paramnames.length>0){
				goodsParam.setName(paramnames[i]);
			}
			if (paramvalues != null) {
				String value = paramvalues[i];
				goodsParam.setValue(value);
			}

			list.add(goodsParam);
		}
		return list;
	}

	/**
	 * 计算 某个参数组 的参数 在 从页面传递过来的paramnames 数组的位置
	 * 
	 * @param groupindex
	 * @param ar_paramnum
	 * @return
	 */
	private int[] countPos(int groupindex, String[] ar_paramnum) {

		int index = 0;
		int start = 0;
		int end = 0;
		int[] r = new int[2];
		for (int i = 0; i <= groupindex; i++) {
			int p_num = Integer.valueOf(ar_paramnum[i]);

			index = index + p_num;
			if (i == groupindex - 1) { // 上一个参数组的参数 结束
				start = index;
			}

			if (i == groupindex) { // 当前的 本参数组的参数开始
				end = index;
			}

		}

		r[0] = start;
		r[1] = end;

		return r;
	}

	/**
	 * 查询类型是否已经被类别关联
	 * @param type_ids
	 * @return
	 */
	private boolean checkUsed(Integer[] type_ids){
		String sql="select count(0) from goods_cat where type_id in";
		return false;
	}

	private List getSpecListByTypeId(int type_id) {
		String sql ="select s.spec_name spec_name ,s.spec_id spec_id, 0 as num from es_type_spec ts inner join es_specification s  on s.spec_id = ts.spec_id where ts.type_id=?";
		return this.daoSupport.queryForList(sql,type_id);
	}

	/**
	 * 将一个json字串转为list
	 * @param props
	 * @return
	 */
	private static List<Attribute> converAttrFormString(String props){
		if (props == null || props.equals(""))
			return new ArrayList();

		JSONArray jsonArray = JSONArray.fromObject(props);
		List<Attribute> list = (List) JSONArray.toCollection(jsonArray,
				Attribute.class);

		return list;
	}

	/**
	 * 读取某个类型的参数定义
	 * 
	 * @param type_id
	 * @return
	 */
	private String getParamsByTypeId(int type_id) {

		String sql ="select * from es_goods_type where disabled=0 and have_parm=1 and type_id="+type_id;
		List list = this.daoSupport.queryForList(sql);
		String props = 	"";
		if(!list.isEmpty()){
			Map map = (Map) list.get(0);
			props = (String) map.get("params");
		}

		return props;
	}
}
