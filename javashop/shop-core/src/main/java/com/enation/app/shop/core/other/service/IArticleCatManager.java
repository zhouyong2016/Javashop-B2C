package com.enation.app.shop.core.other.service;

import java.util.List;

import com.enation.app.shop.core.other.model.ArticleCat;


/**
 * 文章分类管理
 * 
 * @author lzf<br/>
 *         2010-4-1 下午05:00:40<br/>
 *         version 1.0<br/>
 */
public interface IArticleCatManager {
	/**
	 * 根据Id获取文章分类
	 * @param cat_id 文章分类Id,Int
	 * @return ArticleCat
	 */
	public ArticleCat getById(int cat_id);
	
	/**
	 * 添加文章分类
	 * @param cat 文章分类,ArticleCat
	 */
	public void saveAdd(ArticleCat cat);
	
	/**
	 * 修改文章分类
	 * @param cat 商品分类,ArticleCat
	 */
	public void update(ArticleCat cat);
	
	/**
	 * 删除文章分类
	 * @param cat_id 商品分类,Int
	 * @return 0.删除成功.1.存在文章不能删除
	 */
	public int delete(int cat_id);
	/**
	 * 保存排序
	 * @param cat_ids 分类数组,int[]
	 * @param cat_sorts 排序数组,int[]
	 */
	public void saveSort(int[] cat_ids, int[] cat_sorts);
	/**
	 * 获取子分类
	 * @param cat_id 父分类Id,Integer
	 * @return 子分类列表
	 */
	public List listChildById(Integer cat_id);

	public List listHelp(int cat_id);
}
