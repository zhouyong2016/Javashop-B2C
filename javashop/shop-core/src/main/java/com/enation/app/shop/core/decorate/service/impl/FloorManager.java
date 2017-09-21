package com.enation.app.shop.core.decorate.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.shop.core.decorate.DecoratePluginsBundle;
import com.enation.app.shop.core.decorate.model.Floor;
import com.enation.app.shop.core.decorate.model.FloorProps;
import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.app.shop.core.goods.model.Brand;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;
/**
 * 
 * 楼层管理实现类
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Service
@SuppressWarnings(value={"unchecked", "rawtypes"})
public class FloorManager implements IFloorManager{


	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private DecoratePluginsBundle decoratePluginsBundle;

	@Override
	@Log(type=LogType.FLOOR,detail="添加了一个名为${floor.title}的楼层")
	public void save(Floor floor) throws RuntimeException {
		try {
			this.daoSupport.insert("es_floor", floor);
			Integer floor_id=this.daoSupport.getLastId("es_floor");
			floor.setId(floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("插入数据库出错");
		}
	}


	@Override
	public List<Map> getListChildren(Integer parentid, Integer pageid) {
		String sql = "select f.* ,tt.totleNum as totle_num from es_floor f left join "
				+ " (select ff.parent_id  ffid, count(ff.id) as totleNum from es_floor ff  "
				+ " where ff.parent_id in (select id from es_floor fff where fff.parent_id=? ) GROUP BY ff.parent_id )  tt on f.id=tt.ffid "
				+ " where f.parent_id=?";

		if(pageid!=null){
			sql+=" and f.page_id="+pageid;
		}

		sql+=" order by f.sort asc";

		List<Map> floorlist = this.daoSupport.queryForList(sql, parentid,parentid);

		for(Map map :floorlist){
			if(map.get("totle_num")==null){		//判断某一个分类下的子分类数量 null赋值为0
				map.put("totle_num", 0);
			}
			int totle_num = Integer.parseInt(map.get("totle_num").toString());
			if(totle_num!=0){		//判断某一个分类下的子分类数量 不为0 则是(easyui)文件夹并且是闭合状态。
				map.put("state", "closed");
			}
		}

		return floorlist;
	}


	@Override
	@Log(type=LogType.FLOOR,detail="修改楼层ID为${id}的楼层显示状态")
	public List saveDisplay(Integer id, Integer is_display) throws RuntimeException{

		try {
			String sql="select id from es_floor where id=? or parent_id=?";
			List<Map> list=this.daoSupport.queryForList(sql,id,id);
			List<Integer> list2=new ArrayList<Integer>();
			for(Map map:list){
				list2.add(Integer.valueOf(map.get("id").toString()));
			}
			sql="update es_floor set is_display=? where id=? or parent_id=?";
			this.daoSupport.execute(sql, is_display,id,id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),id);
			return list2;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("保存显示状态失败");
		}
	}


	@Override
	public Floor getFloorById(Integer floorid) {
		String sql="select * from es_floor where id=?";
		return (Floor) this.daoSupport.queryForObject(sql, Floor.class, floorid);
	}


	@Override
	@Log(type=LogType.FLOOR,detail="删除楼层ID为${floor_id}的楼层信息")
	public void delete(Integer floor_id) throws RuntimeException{
		String sql="delete  from es_floor where id=? or parent_id=?";
		try {
			this.daoSupport.execute(sql, floor_id,floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("删除失败");
		}
	}


	@Override
	@Log(type=LogType.FLOOR,detail="保存楼层排序")
	public void saveSort(Integer[] floor_ids,Integer[] floor_sorts)
			throws RuntimeException {
		//拼接sql语句
		try {
			StringBuffer sqlsb=new StringBuffer();
			sqlsb.append("update es_floor set sort= case id ");
			for(int i=0;i<floor_ids.length;i++){
				sqlsb.append(" when "+floor_ids[i]);
				sqlsb.append(" then "+floor_sorts[i]);
			}
			sqlsb.append(" end ");
			sqlsb.append(" where id in ");
			sqlsb.append("(");
			for(int i=0;i<floor_ids.length;i++){
				sqlsb.append(floor_ids[i]+",");
			}
			sqlsb.deleteCharAt(sqlsb.lastIndexOf(","));
			sqlsb.append(")");
			String sql=sqlsb.toString();
			this.daoSupport.execute(sql);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),null);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		
	}


	@Override
	@Log(type=LogType.FLOOR,detail="修改了名为${floor.title}的楼层信息")
	public void update(Floor floor) {
		Map<String,Object> fields=new HashMap<String, Object>();
		fields.put("title", floor.getTitle());
		fields.put("style", floor.getStyle());
		fields.put("logo", floor.getLogo());
		fields.put("is_display", floor.getIs_display());
		fields.put("sort", floor.getSort());

		this.daoSupport.update("es_floor", fields, "id="+floor.getId());
		this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor.getId());
	}


	@Override
	public void saveTitle(Integer id, String title) throws RuntimeException {
		String sql="update es_floor set title=? where id=?";
		try {
			this.daoSupport.execute(sql, title,id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),id);
		} catch (Exception e) {
			throw new RuntimeException("保存标题失败");
		}
	}


	@Override
	public List<Map> getChildFloorAndStyleById(Integer floor_id, Integer limit ,Integer pageid) {
		String sql="";
		if("1".equals(EopSetting.DBTYPE)){
			sql="select f.*,s.path path,s.is_top_style is_top_style,s.is_default_style is_default_style from es_floor f INNER JOIN es_style s "
				+"ON f.page_id=s.page_id AND f.style=s.style "
				+ " where f.parent_id=? AND f.is_display=0 AND f.page_id=? AND s.page_id=? AND"
				+ "(CASE WHEN f.style in ('style1','style2') "
				+ "THEN s.style=f.style ELSE is_default_style=1 AND is_top_style=0 END)"
				+ " order by f.sort asc limit 0,?";
			return this.daoSupport.queryForList(sql, floor_id,pageid,pageid,limit);
		}
		else if("2".equals(EopSetting.DBTYPE)){
			String innersql="select f.*,s.path path,s.is_top_style is_top_style,s.is_default_style is_default_style,ROWNUM rn  from es_floor f INNER JOIN es_style s "
					+"ON f.page_id=s.page_id AND f.style=s.style "
					+ " where f.parent_id=? AND f.is_display=0 AND f.page_id=? AND s.page_id=? AND s.style="
					+ " (CASE WHEN f.style in (select style from es_style where is_top_style!=1) "
					+ " THEN f.style ELSE s.style END) " 
					+ " AND is_default_style="
					+ " (CASE WHEN f.style in (select style from es_style where is_top_style!=1) "
					+ " THEN is_default_style ELSE 1 END) "
					+ " AND is_top_style="
					+ " (CASE WHEN f.style in (select style from es_style where is_top_style!=1) "
					+ " THEN is_top_style ELSE 0 END) "
					+ " order by f.sort asc ";
			sql="select * from ("+innersql+") ib where ib.rn<?"	;
			return this.daoSupport.queryForList(sql, floor_id,pageid,pageid,limit+1);
		}else{//sql server
			sql="select top "+limit+" f.* from es_floor f where f.parent_id=? and f.is_display = 0 and f.page_id =? ";
			List<Map> list = this.daoSupport.queryForList(sql, floor_id,pageid);
			sql="select * from es_style where page_id = ? and  is_top_style!=1";
			List<Map<String,Object>> styleList = this.daoSupport.queryForList(sql,pageid);
			//这里可能以树形式更好处理
			Map<String,Object> styleMap=new HashMap<String,Object>();
			for (Map<String, Object> map : styleList) {
				styleMap.put(map.get("style").toString(), map);
				if("1".equals(map.get("is_default_style").toString())){
					styleMap.put("default", map);
				}
			}
			for (Map<String, Object> map : list) {
				String style=map.get("style").toString();
				Map<String,Object> map2=null;
				if(styleMap.containsKey(style)){
					map2=(Map<String, Object>) styleMap.get(style);
				}else{
					map2=(Map<String, Object>) styleMap.get("default");
				}
				map.put("path", map2.get("path").toString());
				map.put("is_top_style", map2.get("is_top_style"));
				map.put("is_default_style",  map2.get("is_default_style"));
			}
			
			
			return list;
		}
		
		
	}


	@Override
	@Log(type=LogType.FLOOR,detail="新添加或修改楼层ID为${floor_id}的楼层分类导航")
	public void saveGuidCat(Integer catid, Integer floor_id)
			throws RuntimeException {
		String sql="update es_floor set guid_cat_id=? where id=?";
		try {
			this.daoSupport.execute(sql, catid,floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("更新导航分类失败");
		}
	}




	@Override
	@Log(type=LogType.FLOOR,detail="新添加或修改楼层左侧分类")
	public void saveCatId(Integer floor_id, Integer cat_id)
			throws RuntimeException {
		try {
			String sql="update es_floor set cat_id=? where id=?";
			this.daoSupport.execute(sql, cat_id,floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("保存左侧分类失败");
		}

	}


	@Override
	public List getAllAdvList() {
		String sql="select a.*,'' cname from es_adv a where isclose = 0";
		List list=this.daoSupport.queryForList(sql, new AdvMapper());
		return list;
	}


	@Override
	@Log(type=LogType.FLOOR,detail="楼层新添加或修改广告图片")
	public void saveAdvId(Integer floor_id,Integer[] adv_id,String position)
			throws RuntimeException {
		try {
			Map jsonMap=new TreeMap<String,Object>();
			Integer i=0;
			for (Integer integer : adv_id) {
				jsonMap.put(i.toString(), integer.toString());
				i++;
			}
			Floor floor=this.getFloorById(floor_id);
			String adv_id_json=floor.getAdv_ids();
			Map jsonMap2=null;
			if(StringUtil.isEmpty(adv_id_json)){
				jsonMap2=new TreeMap<String,Object>();
				jsonMap2.put(position, jsonMap);
			}else{
				jsonMap2=JsonUtil.toMap(adv_id_json);
				jsonMap2.put(position, jsonMap);
			}
			String json=JsonUtil.MapToJson(jsonMap2);
			String sql="update es_floor set adv_ids=? where id=?";
			this.daoSupport.execute(sql, json,floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("保存楼层广告失败");
		}
	}




	@Override
	public List<Map> getTopFloorAndStyle(Integer pageid) {
		String sql="";
		if("1".equals(EopSetting.DBTYPE)){
			sql="select f.*,s.path path,s.is_top_style is_top_style,s.is_default_style is_default_style from es_floor f INNER JOIN es_style s "
				+"ON f.page_id=s.page_id AND f.style=s.style "
				+ "where f.parent_id=? "
				+ "AND f.is_display=0 AND f.page_id=? AND s.page_id=? AND  "
				+ "(CASE WHEN f.style in (select style from es_style where is_top_style=1) "
				+ "THEN s.style=f.style ELSE is_default_style=1 AND is_top_style=1 END)"
				+ " order by f.sort asc";
			return this.daoSupport.queryForList(sql, 0,pageid,pageid);
		}else if("2".equals(EopSetting.DBTYPE)){
			sql="select f.*,s.path path,s.is_top_style is_top_style,s.is_default_style is_default_style from es_floor f INNER JOIN es_style s "
					+ " ON f.page_id=s.page_id AND f.style=s.style "
					+ " where f.parent_id=? "
					+ " AND f.is_display=0 AND f.page_id=? AND s.page_id=? AND  s.style= "
					+ " (CASE WHEN f.style in (select style from es_style where is_top_style=1) "
					+ " THEN f.style ELSE s.style END) "
					+ " AND is_default_style="
					+ " (CASE WHEN f.style in (select style from es_style where is_top_style=1) "
					+ " THEN is_default_style ELSE 1 END) "
					+ " AND is_top_style="
					+ " (CASE WHEN f.style in (select style from es_style where is_top_style=1) "
					+ " THEN is_top_style ELSE 1 END) "
					+ " order by f.sort asc";
			return this.daoSupport.queryForList(sql, 0,pageid,pageid);
		}else{
			sql="select  f.* from es_floor f where f.parent_id=? and f.is_display = 0 and f.page_id =? ";
			List<Map> list = this.daoSupport.queryForList(sql, 0,pageid);
			sql="select * from es_style where page_id = ? and  is_top_style=1";
			List<Map<String,Object>> styleList = this.daoSupport.queryForList(sql,pageid);
			//这里可能以树形式更好处理
			Map<String,Object> styleMap=new HashMap<String,Object>();
			for (Map<String, Object> map : styleList) {
				styleMap.put(map.get("style").toString(), map);
				if("1".equals(map.get("is_default_style").toString())){
					styleMap.put("default", map);
				}
			}
			for (Map<String, Object> map : list) {
				String style=map.get("style").toString();
				Map<String,Object> map2=null;
				if(styleMap.containsKey(style)){
					map2=(Map<String, Object>) styleMap.get(style);
				}else{
					map2=(Map<String, Object>) styleMap.get("default");
				}
				map.put("path", map2.get("path").toString());
				map.put("is_top_style", map2.get("is_top_style"));
				map.put("is_default_style",  map2.get("is_default_style"));
			}
			
			
			return list;
		}
		
	}


	@Override
	@Log(type=LogType.FLOOR,detail="新添加楼层商品，新商品ID为${new_goods_id}")
	public void saveEachGoods(Integer new_goods_id,
			Integer floor_id,Integer index) throws RuntimeException {


		try {
			String sql="select goods_ids from es_floor where id="+floor_id;
			String goods_id_json=this.daoSupport.queryForString(sql);
			Map goods_id_map;
			if(StringUtil.isEmpty(goods_id_json)){
				goods_id_map=new LinkedHashMap<String,Object>();
			}else{

				goods_id_map=JsonUtil.toMap(goods_id_json);
			}
			goods_id_map.put(index.toString(), new_goods_id.toString());
			String new_goods_id_json=JsonUtil.MapToJson(goods_id_map);
			sql="update es_floor set goods_ids=? where id=?";
			this.daoSupport.execute(sql, new_goods_id_json,floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("保存失败");
		}

	}




	/**
	 * 这个方法有待思考
	 * @param props
	 * @param floor_id
	 * @throws RuntimeException
	 */
	@Override
	public void saveProps(FloorProps props, Integer floor_id) throws RuntimeException {
		try {
			//TODO
			//			Map map = new HashMap<String,Object>();
			//			map.put("keyword", keyword);
			//			map.put("goods_num", goods_num);
			//			map.put("goods_sort", goods_sort);
			//			map.put("goods_order", goods_order);
			//			map.put("catid", cat_id);
			//			String props=JsonUtil.MapToJson(map);
			//			String sql="update es_floor set props=? where id=?";
			//			this.daoSupport.execute(sql, props,floor_id);
		} catch (Exception e) {
			throw new RuntimeException("保存props失败");
		}

	}


	@Override
	public void saveBatchGoods(Integer[] goods_ids, Integer floor_id) throws RuntimeException {
		try {
			Map<String,Object> jsonMap=new TreeMap<String,Object>();
			Integer i=0;
			for (Integer goods_id : goods_ids) {
				jsonMap.put(i.toString(), goods_id.toString());
				i++;
			}
			String json=JsonUtil.MapToJson(jsonMap);
			String sql="update es_floor set goods_ids=? where id=?";
			this.daoSupport.execute(sql,json,floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("保存商品失败");
		}

	}


	@Override
	public Brand getBrand(Integer id) {
		String sql="select * from es_brand where brand_id=?";
		return (Brand) this.daoSupport.queryForObject(sql, Brand.class, id);
	}


	@Override
	@Log(type=LogType.FLOOR,detail="新增或添加楼层品牌")
	public void saveBrandIds(Integer floor_id, Integer[] brand_ids)
			throws RuntimeException {
		try {
			Map jsonMap=new TreeMap<String, Object>();
			Integer i=0;
			for (Integer brand_id : brand_ids) {
				jsonMap.put(i.toString(), brand_id.toString());
				i++;
			}
			String json=JsonUtil.MapToJson(jsonMap);
			String sql="update es_floor set brand_ids=? where id=?";
			this.daoSupport.execute(sql, json,floor_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.FLOOR.toString(),floor_id);
		} catch (Exception e) {
			throw new RuntimeException("保存失败");
		}
	}


	@Override
	public String getBrandIds(Integer floor_id) {
		String sql="select brand_ids from es_floor where id="+floor_id;
		return this.daoSupport.queryForString(sql);
	}


	@Override
	public List<Brand> listBrands(String brand_Json) {
		Map<String,Object> brand_ids_Map=JsonUtil.toMap(brand_Json);
		StringBuffer brand_id_sb=new StringBuffer();
		for(Integer i=0;i<brand_ids_Map.size();i++){
			String value=brand_ids_Map.get(i.toString()).toString();
			brand_id_sb.append(value+",");
		}
		String brand_id_str=brand_id_sb.substring(0, brand_id_sb.length()-1);
		String sql="";
		if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
			sql="select * from es_brand where brand_id in ("+brand_id_str+") order by instr('"+brand_id_str+"',brand_id)";
		}else{
			sql="select * from es_brand where brand_id in ("+brand_id_str+") order by charindex(','+convert(varchar,brand_id)+',',',"+brand_id_str+",')";
		}
		List<Brand> brandList  =this.daoSupport.queryForList(sql);
		return brandList;
	}


	@Override
	public String getAdvIds(Integer floor_id) {
		String sql="select adv_ids from es_floor where id="+floor_id;
		String json=this.daoSupport.queryForString(sql);
		return json;
	}


	@Override
	public List getGoodsListByGoods_ids(Map<String,Object> goods_id_map) {

		StringBuffer goods_ids_sb=new StringBuffer();
		for(Integer i=0;i<goods_id_map.size();i++){
			String value=goods_id_map.get(i.toString()).toString();
			goods_ids_sb.append(value+",");
		}
		String goods_ids_str="";
		if(goods_id_map.size()!=0){

			goods_ids_str=goods_ids_sb.substring(0, goods_ids_sb.length()-1);
			String sql="";
			if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
				sql="select * from es_goods where goods_id in ("+goods_ids_str+") order by instr('"+goods_ids_str+"',goods_id)";
			}else{
				sql="select * from es_goods where goods_id in ("+goods_ids_str+") order by charindex(','+convert(varchar,goods_id)+',',',"+goods_ids_str+",')";
			}
			return this.daoSupport.queryForList(sql);
		}
		return new ArrayList();
	}


	@Override
	public List getBrandListByBrandIds(Map<String, Object> brand_id_map) {
		StringBuffer brand_ids_sb=new StringBuffer();
		for(Integer i=0;i<brand_id_map.size();i++){
			String value=brand_id_map.get(i.toString()).toString();
			brand_ids_sb.append(value+",");
		}
		String brand_ids_str="";
		if(brand_id_map.size()!=0){

			brand_ids_str=brand_ids_sb.substring(0, brand_ids_sb.length()-1);
			String sql="";
			if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
				sql="select * from es_brand where brand_id in ("+brand_ids_str+") order by instr('"+brand_ids_str+"',brand_id)";
			}else{
				sql="select * from es_brand where brand_id in ("+brand_ids_str+") order by charindex(','+convert(varchar,brand_id)+',',',"+brand_ids_str+",')";
			}
			return this.daoSupport.queryForList(sql);
		}
		return new ArrayList();
	}


	@Override
	public List getAdvListByAids(Map<String, Object> adv_id_map) {
		StringBuffer adv_ids_sb=new StringBuffer();
		for(Integer i=0;i<adv_id_map.size();i++){
			String value=adv_id_map.get(i.toString()).toString();
			adv_ids_sb.append(value+",");
		}
		String adv_ids_str="";
		if(adv_id_map.size()!=0){

			adv_ids_str=adv_ids_sb.substring(0, adv_ids_sb.length()-1);
			String sql="";
			if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
				sql="select * from es_adv where aid in ("+adv_ids_str+") order by instr('"+adv_ids_str+"',aid)";
			}else{
				sql="select * from es_adv where aid in ("+adv_ids_str+") order by charindex(','+convert(varchar,aid)+',',',"+adv_ids_str+",')";
				
			}
			return this.daoSupport.queryForList(sql);
		}
		return new ArrayList();
	}


}
