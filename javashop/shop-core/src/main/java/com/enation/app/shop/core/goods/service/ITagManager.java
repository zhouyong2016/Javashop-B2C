package com.enation.app.shop.core.goods.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.goods.model.Tag;
import com.enation.framework.database.Page;

/**
 * 标签管理
 * @author kingapex
 * 2010-1-17下午01:03:41
 */
public interface ITagManager {
	
	/**
	 * 检测标签名是否同名
	 * @param name 标签名
	 * @param tagid 要排除的标签id,编辑时判断重名所用
	 * @return 存在重名返回真，不存在返回假
	 */
	public boolean checkname(String name,Integer tagid);
	
	
	/**
	 * 检测某些标签是否已经关联商品
	 * @param tagids 标签id数组
	 * @return 有关联返回真，否则返回假
	 */
	public boolean checkJoinGoods(Integer[] tagids);
	
	/**
	 * 根据标签Id获取标签
	 * @param tagid 标签Id
	 * @return Tag
	 */
	public Tag getById(Integer tagid);
	/**
	 * 添加标签
	 * @param tag 标签
	 */
	public void add(Tag tag);
	/**
	 * 修改标签
	 * @param tag 标签
	 */
	public void update(Tag tag);
	/**
	 * 删除标签
	 * @param tagids 标签Id数组,Integer[]
	 */
	public void delete(Integer[] tagids);
	/**
	 * 标签列表
	 * @param pageNo 分页
	 * @param pageSize 分页每页显示数量
	 * @return Page
	 */
	public Page list(int pageNo,int pageSize);
	/**
	 * 标签列表
	 * @param pageNo 分页
	 * @param pageSize 分页每页显示数量
	 * @return Page
	 */
	public Page list(int pageNo,int pageSize,int type);
	/**
	 * 标签列表
	 * @return List<Tag>
	 */
	public List<Tag> list(); 
	/**
	 * 标签列表Map集合
	 * @return List<Map>
	 */
	public List<Map> listMap(); 
	
	/**
	 * 读取某个引用的标签id集合
	 * @param relid
	 * @return
	 */
	public List<Integer> list(Integer relid); 
	
	
	
	
	/**
	 * 某个引用设置标签
	 * @param relid
	 * @param tagids
	 */
	public void saveRels(Integer relid,Integer[] tagids);
	
 
	
	
}
