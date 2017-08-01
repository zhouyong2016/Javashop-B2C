package com.enation.app.shop.core.goods.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.eop.sdk.utils.StaticResourcesUtil;

/**
 * 类别Mapper
 * 会将本地文件存储的图片地址前缀替换为静态资源服务器地址。
 * @author kingapex
 * 2010-7-16下午03:41:42
 */
public class CatMapper implements RowMapper {

	
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		Cat cat =  new Cat();
		cat.setCat_id(rs.getInt("cat_id"));
		cat.setCat_order(rs.getInt("cat_order"));
		cat.setCat_path(rs.getString("cat_path"));
		String image = rs.getString("image");
		if(image!=null){
			image  =StaticResourcesUtil.convertToUrl(image);
		}
		cat.setImage(image);
		cat.setList_show(rs.getString("list_show"));
		cat.setName(rs.getString("name"));
		cat.setParent_id(rs.getInt("parent_id"));
		cat.setType_id(rs.getInt("type_id"));
		cat.setType_name(rs.getString("type_name"));
		//cat.setTotle_num(rs.getInt("totle_num"));
		return cat;
	}

}
