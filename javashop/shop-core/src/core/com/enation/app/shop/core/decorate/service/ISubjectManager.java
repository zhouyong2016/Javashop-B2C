package com.enation.app.shop.core.decorate.service;

import java.util.List;

import com.enation.app.shop.core.decorate.model.Subject;



/**
 * 专题管理接口
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
public interface ISubjectManager {

	/**
	 * 获取全部专题
	 * @return
	 */
	List listAll();

	/**
	 * 保存专题
	 * @param subject 专题实体
	 * @throws RuntimeException
	 */
	void save(Subject subject) throws RuntimeException;

	/**
	 * 保存排序
	 * @param id 专题id
	 * @param sort 专题顺序
	 * @throws RuntimeException
	 */
	void saveSort(Integer[] subject_ids,Integer[] subject_sorts) throws RuntimeException;

	/**
	 * 保存显示状态
	 * @param id 专题id
	 * @param is_display 专题显示状态
	 * @throws RuntimeException
	 */
	void saveDisplay(Integer id, Integer is_display) throws RuntimeException;

	/**
	 * 根据id获取专题
	 * @param id 专题id
	 * @return
	 */
	Subject getSubjectById(Integer id);

	/**
	 * 保存编辑
	 * @param subject 专题实体
	 * @throws RuntimeException
	 */
	void saveEdit(Subject subject) throws RuntimeException;

	/**
	 * 删除专题
	 * @param id 专题id
	 * @throws RuntimeException
	 */
	void delete(Integer id) throws RuntimeException;

	/**
	 * 根据商品id获取商品
	 * @param goods_id
	 * @return
	 */
	List getGoodsByGoodsIds(String goods_id);

	/**
	 * 保存商品id
	 * @param subject_id 专题id
	 * @param goods_ids 商品id
	 * @throws RuntimeException
	 */
	void saveGoodsIds(Integer subject_id, Integer[] goods_ids) throws RuntimeException;

	/**
	 * 获取已选择的商品
	 * @param goods_ids 商品id
	 * @param index 商品位置
	 * @return
	 */
	List getSelectedGoodsByGoodsIds(String goods_ids, Integer index);

	/**
	 * 保存商品编辑
	 * @param subject_id 专题id
	 * @param goods_ids 商品id
	 * @param index 商品位置索引
	 * @throws RuntimeException
	 */
	void saveEditGoods(Integer subject_id, Integer[] goods_ids, Integer index) throws RuntimeException;

	/**
	 * 删除专题商品
	 * @param subject_id 专题id
	 * @param index 商品位置
	 * @throws RuntimeException
	 */
	void deleteGoods(Integer subject_id, Integer index) throws RuntimeException;

	/**
	 * 保存图片
	 * @param id 专题id
	 * @param imagePath 图片路径
	 * @throws RuntimeException
	 */
	void saveImage(Integer id, String imagePath) throws RuntimeException;

	/**
	 * 删除图片
	 * @param subject_id 专题id
	 * @param index 图片位置索引
	 * @throws RuntimeException
	 */
	void deleteImage(Integer subject_id, Integer index) throws RuntimeException;

	/**
	 * 保存编辑图片
	 * @param id 专题id
	 * @param imagePath 图片路径
	 * @param index 图片位置索引
	 * @throws RuntimeException
	 */
	void saveEditImage(Integer id, String imagePath, Integer index) throws RuntimeException;

	/**
	 * @param id
	 * @return
	 */
	Subject getSubjectByIdAboveAll(Integer id);

}
