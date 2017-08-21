package com.enation.app.shop.core.decorate.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.decorate.DecoratePluginsBundle;
import com.enation.app.shop.core.decorate.model.Subject;
import com.enation.app.shop.core.decorate.service.ISubjectManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 * 
 * 专题管理实现类
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Service
public class SubjectManager implements ISubjectManager{

	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private DecoratePluginsBundle decoratePluginsBundle;
	
	@Override
	public List listAll() {
		String sql="select * from es_subject order by sort";
		return this.daoSupport.queryForList(sql);
	}

	@Override
	@Log(type=LogType.FLOOR,detail="新增了一个名为${subject.title}的专题")
	public void save(Subject subject) throws RuntimeException {
		try {
			this.daoSupport.insert("es_subject", subject);
			Integer subject_id=this.daoSupport.getLastId("es_subject");
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),subject_id);
		} catch (Exception e) {
			throw new RuntimeException("保存专题失败");
		}
	}

	@Override
	public void saveSort(Integer[] subject_ids,Integer[] subject_sorts) throws RuntimeException {
		//拼接sql语句
		try {
			StringBuffer sqlsb=new StringBuffer();
			sqlsb.append("update es_subject set sort= case id ");
			for(int i=0;i<subject_ids.length;i++){
				sqlsb.append(" when "+subject_ids[i]);
				sqlsb.append(" then "+subject_sorts[i]);
			}
			sqlsb.append(" end ");
			sqlsb.append(" where id in ");
			sqlsb.append("(");
			for(int i=0;i<subject_ids.length;i++){
				sqlsb.append(subject_ids[i]+",");
			}
			sqlsb.deleteCharAt(sqlsb.lastIndexOf(","));
			sqlsb.append(")");
			String sql=sqlsb.toString();
			this.daoSupport.execute(sql);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),null);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	@Log(type=LogType.FLOOR,detail="修改ID为${id}的专题状态")
	public void saveDisplay(Integer id, Integer is_display)
			throws RuntimeException {
		try {
			Map<String,Object> fields=new HashMap<String,Object>();
			fields.put("is_display", is_display);
			this.daoSupport.update("es_subject", fields, "id="+id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),id);
		} catch (Exception e) {
			throw new RuntimeException("保存排序失败");
		}
	}

	@Override
	public Subject getSubjectById(Integer id) {
		String sql="select * from es_subject where id=? and is_display=0";
		return (Subject) this.daoSupport.queryForObject(sql, Subject.class, id);
	}

	
	@Override
	@Log(type=LogType.FLOOR,detail="修改名为${subject.title}的专题信息")
	public void saveEdit(Subject subject) throws RuntimeException {
		try {
			Map<String,Object> fields=new HashMap<String,Object>();
			fields.put("title", subject.getTitle());
			fields.put("sort", subject.getSort());
			fields.put("is_display", subject.getIs_display());
			fields.put("banner", subject.getBanner());
			this.daoSupport.update("es_subject", fields, "id="+subject.getId());
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),subject.getId());
		} catch (Exception e) {
			throw new RuntimeException("保存专题失败");
		}
	}

	@Override
	@Log(type=LogType.FLOOR,detail="删除ID为${id}专题")
	public void delete(Integer id) throws RuntimeException {
		try {
			String sql="delete from es_subject where id=?";
			this.daoSupport.execute(sql, id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),id);
		} catch (Exception e) {
			throw new RuntimeException("删除失败");
		}
		
	}

	@Override
	public List getGoodsByGoodsIds(String goods_id) {
		if(!StringUtil.isEmpty(goods_id)){
			Map<String,Object> goods_id_map=JsonUtil.toMap(goods_id);
			Integer key=0;
			List goodsList=new ArrayList();
			for(int i=0;i<goods_id_map.size();i++){
				String goods_id_line=goods_id_map.get(key.toString()).toString();
				String sql="";
				if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
					sql ="select * from es_goods where goods_id in ("+goods_id_line+") order by instr(?,goods_id)";
					goodsList.add(this.daoSupport.queryForList(sql,goods_id_line));
				}else{
					sql ="select * from es_goods where goods_id in ("+goods_id_line+") order by charindex(','+convert(varchar,goods_id)+',',',"+goods_id_line+",')";
					goodsList.add(this.daoSupport.queryForList(sql));
				}
				key++;
			}
			return goodsList;
		}
		return null;
	}
	@Override
	@Log(type=LogType.FLOOR,detail="ID为${subject_id}的专题设计，添加商品")
	public void saveGoodsIds(Integer subject_id, Integer[] goods_ids)
			throws RuntimeException {
		try {
			Subject subject=this.getSubjectByIdAboveAll(subject_id);
			String goods_ids_db=subject.getGoods_ids();
			Integer index=0;
			Map<String,Object> goods_ids_map=null;
			if(!StringUtil.isEmpty(goods_ids_db)){
				goods_ids_map=JsonUtil.toMap(goods_ids_db);
				index=goods_ids_map.size();
				
			}else{
				goods_ids_map=new TreeMap<String,Object>();
			}
			StringBuffer goods_id_sb=new StringBuffer();
			for (Integer goods_id : goods_ids) {
				goods_id_sb.append(goods_id.toString()+","); 
			}
			String goods_id_str=goods_id_sb.substring(0, goods_id_sb.length()-1);
			goods_ids_map.put(index.toString(), goods_id_str);
			String goods_ids_json=JsonUtil.MapToJson(goods_ids_map);
			String sql="update es_subject set goods_ids=? where id=?";
			this.daoSupport.execute(sql, goods_ids_json,subject_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),subject_id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("保存失败");
		}
		
	}

	@Override
	public List getSelectedGoodsByGoodsIds(String goods_ids, Integer index) {
		Map goods_ids_map=JsonUtil.toMap(goods_ids);
		String selected_goods_ids=goods_ids_map.get(index.toString()).toString();
		String sql="";
		if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
			sql ="select * from es_goods where goods_id in ("+selected_goods_ids+") order by instr(?,goods_id)";
			return this.daoSupport.queryForList(sql, selected_goods_ids);
		}else{
			sql ="select * from es_goods where goods_id in ("+selected_goods_ids+") order by charindex(','+convert(varchar,goods_id)+',',',"+selected_goods_ids+",')";
			return this.daoSupport.queryForList(sql);
		}
	}

	@Override
	@Log(type=LogType.FLOOR,detail="修改ID为${subject_id}的专题商品")
	public void saveEditGoods(Integer subject_id, Integer[] goods_ids,Integer index)
			throws RuntimeException {
		try {
			Subject subject=this.getSubjectById(subject_id);
			String goods_ids_json=subject.getGoods_ids();
			Map goods_ids_map=JsonUtil.toMap(goods_ids_json);
			StringBuffer goods_ids_sb=new StringBuffer();
			for (Integer goods_id : goods_ids) {
				goods_ids_sb.append(goods_id+",");
			}
			String goods_ids_str=goods_ids_sb.substring(0, goods_ids_sb.length()-1);
			goods_ids_map.put(index.toString(), goods_ids_str);
			String goods_ids_json2=JsonUtil.MapToJson(goods_ids_map);
			String sql="update es_subject set goods_ids=? where id=?";
			
			this.daoSupport.execute(sql, goods_ids_json2,subject_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),subject_id);
		} catch (Exception e) {
			throw new RuntimeException("修改失败");
		}
	}

	@Override
	@Log(type=LogType.FLOOR,detail="删除ID为${subject_id}的专题设计商品")
	public void deleteGoods(Integer subject_id, Integer index)
			throws RuntimeException {
		try {
			Subject subject=this.getSubjectById(subject_id);
			String goods_ids=subject.getGoods_ids();
			Map<String,Object> goods_ids_map=JsonUtil.toMap(goods_ids);
			goods_ids_map.remove(index.toString());
			
			Map<String,Object> goods_ids_map2=new TreeMap<String,Object>();
			Integer key=0;
			for(Map.Entry<String, Object> entry:goods_ids_map.entrySet()){
				goods_ids_map2.put(key.toString(), entry.getValue().toString());
				key++;
			}
			String goods_ids_json2=JsonUtil.MapToJson(goods_ids_map2);
			String sql="update es_subject set goods_ids=? where id=?";
			this.daoSupport.execute(sql,goods_ids_json2,subject_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),subject_id);
		} catch (Exception e) {
			throw new RuntimeException("删除失败");
		}
	}

	@Override
	@Log(type=LogType.FLOOR,detail="ID为${id}的专题设计，添加图片")
	public void saveImage(Integer id, String imagePath) throws RuntimeException {
		try {
			if(!StringUtil.isEmpty(imagePath)){
				Subject subject=this.getSubjectById(id);
				String pic_path_json=subject.getPicture_path();
				Integer index=0;
				String json2="";
				if(!StringUtil.isEmpty(pic_path_json)){
					Map pic_path_map=JsonUtil.toMap(pic_path_json);
					index=pic_path_map.size();
					pic_path_map.put(index.toString(), imagePath);
					json2=JsonUtil.MapToJson(pic_path_map);
				}else{
					Map map=new TreeMap<String,Object>();
					map.put(index, imagePath);
					json2=JsonUtil.MapToJson(map);
				}
				String sql="update es_subject set picture_path=? where id=?";
				this.daoSupport.execute(sql, json2,id);
				this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),id);
			}
		} catch (Exception e) {
			throw new RuntimeException("保存图片失败");
		}
	}

	@Override
	@Log(type=LogType.FLOOR,detail="删除ID为${subject_id}的专题设计图片")
	public void deleteImage(Integer subject_id, Integer index)
			throws RuntimeException {
		try {
			Subject subject=this.getSubjectById(subject_id);
			String picture_path_json=subject.getPicture_path();
			Map<String,Object> picture_path_map=JsonUtil.toMap(picture_path_json);
			picture_path_map.remove(index.toString());
			
			Map<String,Object> picture_path_map2=new TreeMap<String,Object>();
			Integer key=0;
			for(Map.Entry<String, Object> entry:picture_path_map.entrySet()){
				picture_path_map2.put(key.toString(), entry.getValue().toString());
				key++;
			}
			String picture_path_json2=JsonUtil.MapToJson(picture_path_map2);
			String sql="update es_subject set picture_path=? where id=?";
			this.daoSupport.execute(sql,picture_path_json2,subject_id);
			this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),subject_id);
		} catch (Exception e) {
			throw new RuntimeException("删除失败");
		}
	}

	@Override
	@Log(type=LogType.FLOOR,detail="修改ID为${id}的专题设计图片")
	public void saveEditImage(Integer id, String imagePath, Integer index)
			throws RuntimeException {
		try {
			if(!StringUtil.isEmpty(imagePath)){
				Subject subject=this.getSubjectById(id);
				String pic_path_json=subject.getPicture_path();
				String json2="";
				
				Map pic_path_map=JsonUtil.toMap(pic_path_json);
				pic_path_map.put(index.toString(), imagePath);
				json2=JsonUtil.MapToJson(pic_path_map);
				
				String sql="update es_subject set picture_path=? where id=?";
				this.daoSupport.execute(sql, json2,id);
				this.decoratePluginsBundle.onSave(DecorateTypeEnum.SUBJECT.toString(),id);
			}
		} catch (Exception e) {
			throw new RuntimeException("保存图片失败");
		}
	}

	/**
	 * @param id
	 * @return
	 */
	@Override
	public Subject getSubjectByIdAboveAll(Integer id) {
		String sql="select * from es_subject where id=? ";
		return (Subject) this.daoSupport.queryForObject(sql, Subject.class, id);
	}
	
}
