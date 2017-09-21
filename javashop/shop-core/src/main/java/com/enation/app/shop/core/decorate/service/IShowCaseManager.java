package com.enation.app.shop.core.decorate.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.decorate.model.ShowCase;
import com.enation.app.shop.core.goods.model.Goods;

/**
 * 橱窗管理接口
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
public interface IShowCaseManager {

	/**
	 * 获取所有橱窗
	 * @return 橱窗集合
	 */
	List listAll();

	/**
	 * 保存橱窗
	 * @param showcase 橱窗实体
	 * @throws RuntimeException
	 */
	void saveShowCase(ShowCase showcase) throws RuntimeException;

	/**
	 * 保存橱窗顺序
	 * @param id 橱窗id
	 * @param sort 橱窗顺序
	 * @throws RuntimeException
	 */
	void saveSort(Integer[] showcase_ids,Integer[] showcase_sorts) throws RuntimeException;

	/**
	 * 保存橱窗显示状态
	 * @param id 橱窗id
	 * @param is_display 显示状态
	 * @throws RuntimeException
	 */
	void saveDisplay(Integer id, Integer is_display) throws RuntimeException;

	/**
	 * 根据id获取橱窗
	 * @param id 橱窗id
	 * @return
	 */
	ShowCase getShowCaseById(Integer id);

	/**
	 * 保存橱窗编辑
	 * @param showCase 橱窗实体
	 * @throws RuntimeException
	 */
	void saveEdit(ShowCase showCase) throws RuntimeException;

	/**
	 * 删除橱窗
	 * @param id 橱窗id
	 * @throws RuntimeException
	 */
	void delete(Integer id) throws RuntimeException;

	/**
	 * 获取已选择的橱窗商品 
	 * @param content 商品id内容
	 * @return
	 */
	List<Goods> getSelectGoods(String content);

	/**
	 * 根据标识获取橱窗
	 * @param flag 橱窗标识
	 * @return
	 */
	List getShowCaseByFlag(String flag);
	
	/**
	 * 获取已选择的橱窗商品Map集合 
	 * @param content 商品id内容
	 * @return
	 */
	List<Map> getSelectGoodsMap(String content);

}
