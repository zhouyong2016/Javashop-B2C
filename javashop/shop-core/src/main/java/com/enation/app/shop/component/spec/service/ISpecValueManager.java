package com.enation.app.shop.component.spec.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.goods.model.SpecValue;


/**
 * 规格值管理
 * @author kingapex
 *2010-3-7下午12:32:13
 */
public interface ISpecValueManager {
	
	/**
	 * 添加一个规格值 
	 * @param value
	 */
	public void add(SpecValue value);

	
	
	/**
	 * 更新一个规格值
	 * @param value
	 */
	public void update(SpecValue value);
	
	
	
	/**
	 * 读取某个规格的规格值
	 * @param spec_id
	 * @return
	 */
	public List<SpecValue> list(Integer spec_id);

	
	/**
	 * 根据id获取规格值的详细
	 * @param value_id
	 * @return
	 */
	public Map get(Integer value_id);
	
	/**
	 * 检查添加的规格是否存在
	 * @param specValue 规格值
	 * @return
	 */
	public int checkSpecValue(String specValue,String specImage);
	
	/**
	 * 修改规格商品，修改规格删除之前添加的新规格
	 * @param goodsId
	 */
	public void delInherentSpec(Integer goodsId);
	
	/**
	 * 根据规格类型ID跟规格值查询
	 * @param specId
	 * @param spec_value
	 */
	public int selSpecValAndId(int specId,String spec_value,String image);
}
