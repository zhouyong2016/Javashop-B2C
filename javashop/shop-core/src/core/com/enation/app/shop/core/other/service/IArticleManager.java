package com.enation.app.shop.core.other.service;

import java.util.List;

import com.enation.app.shop.core.other.model.Article;
import com.enation.framework.database.Page;

/**
 * 文章管理
 * 
 * @author lzf<br/>
 *         2010-4-1 下午05:00:10<br/>
 *         version 1.0<br/>
 */
public interface IArticleManager {
	/**
	 * 添加文章
	 * @param article 文章，Article
	 */
	public void saveAdd(Article article);
	/**
	 * 修改文章信息
	 * @param article 文章，Article
	 */
	public void saveEdit(Article article);
	/**
	 * 获取文章
	 * @param id 文章ID,Integer
	 * @return Article
	 */
	public Article get(Integer id);
	/**
	 * 根据文章分类获取文章列表
	 * @param cat_id 文章分类Id
	 * @return 文章列表
	 */
	public List listByCatId(Integer cat_id);
	/**
	 * 获取文章分页列表
	 * @param pageNo 页数,Integer
	 * @param pageSize 每页显示数量,Integer
	 * @param cat_id 文章Id,Integer
	 * @return 文章分页列表
	 */
	public Page pageByCatId(Integer pageNo, Integer pageSize, Integer cat_id);
	/**
	 * 获取前几条文章
	 * @param cat_id 分类Id,Integer
	 * @param top_num 数量,Integer
	 * @return list
	 */
	public List topListByCatId(Integer cat_id, Integer top_num);
	/**
	 * 根据分类Id，获取分类名称
	 * @param cat_id 文章分类Id,Integer
	 * @return 分类名称
	 */
	public String getCatName(Integer cat_id);
	/**
	 * 删除文章
	 * @param id, 文章列表,String
	 */
	public void delete(String id);

}
