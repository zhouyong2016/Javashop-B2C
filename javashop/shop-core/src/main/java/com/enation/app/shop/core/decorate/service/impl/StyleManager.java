package com.enation.app.shop.core.decorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.decorate.model.Style;
import com.enation.app.shop.core.decorate.service.IStyleManager;
import com.enation.framework.database.IDaoSupport;

/**
 * 
 * 风格管理实现类
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Service
@SuppressWarnings(value={"rawtypes"})
public class StyleManager implements IStyleManager{
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	public Style getStyleByFloorId(Integer floor_id,Integer page_id) {
		String sql="SELECT s.* FROM es_style s "
					+ "LEFT JOIN es_floor f on s.style=f.style "
					+ "where f.id=? and s.page_id=?";
		Style style=(Style) this.daoSupport.queryForObject(sql, Style.class, floor_id,page_id);
		if(style==null){
			sql="SELECT s.* FROM es_style s , "
					+ "(SELECT f.parent_id pid FROM es_floor f WHERE f.id=?) AS t "
					+ "WHERE s.is_default_style=1 and s.is_top_style=(CASE WHEN t.pid=0 THEN 1 ELSE 0 END) and s.page_id=?";
			style=(Style) this.daoSupport.queryForObject(sql, Style.class, floor_id,page_id);
		}
		if(style==null){
			throw new RuntimeException("加载模板失败");
		}
		return style;
	}
}
