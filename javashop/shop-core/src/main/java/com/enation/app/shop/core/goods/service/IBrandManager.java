package com.enation.app.shop.core.goods.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.goods.model.Brand;
import com.enation.framework.database.Page;

/**
 * 品牌管理
 * @author kingapex
 * 2010-1-6上午12:39:09
 */
public interface IBrandManager {
	
	/**
	 * 检测品牌是否已经被商品使用
	 * @param ids id列表
	 * @return 已经被使用返回真，否则返回假
	 */
	public boolean checkUsed(Integer[] ids);
	/**
	 *  检测品牌名称是否存在
	 * @param name 品牌名称
	 * @param brandid 要排除的brandid,用于修改时的检测
	 * @return 存在返回true 不存在返回false
	 */
	public boolean checkname(String name,Integer brandid);
	/**
	 * 添加品牌
	 * @param brand 品牌,Brand
	 */
	public  void add(Brand brand);
	/**
	 * 修改品牌
	 * @param brand 品牌,Brand
	 */
	public void update(Brand brand);
	/**
	 * 分页读取品牌
	 * @param order 排序
	 * @param page 分页数
	 * @param pageSize 每页数量
	 * @return
	 */
	public Page list(String order,int page,int pageSize);
	
	/**
	 * 分页读取回收站列表
	 * @param order 排序
	 * @param page 分页数
	 * @param pageSize 每页数量
	 * @return
	 */
	public Page listTrash(String order,int page,int pageSize);
	
	
	/**
	 * 将回收站中的品牌还原
	 * @param bid 品牌Id数组
	 */
	public void revert(Integer[] bid);
	
	
	/**
	 * 将品牌放入回收站
	 * @param bid 品牌Id数组
	 */
	public void delete(Integer[] bid);
	
	/**
	 * 品牌删除,真正的删除。
	 * @param bid 品牌Id数组
	 */
	public void clean(Integer[] bid);
	/**
	 * 获取品牌列表
	 * @return 品牌列表
	 */
	public List<Brand> list() ;
	/**
	 * 获取前几位品牌
	 * @param count 前几位
	 * @return 品牌列表 
	 */
	public List<Brand> list(int count);
	/**
	 * 读取某个类型的所有品牌列表
	 * @param typeid 类型Id
	 * @return 品牌列表
	 */
	public List<Brand> listByTypeId(Integer typeid);
	/**
	 * 读取某类别下的品牌列搁
	 * @param catid 分类Id
	 * @return 品牌列表
	 */
	public List<Brand>  listByCatId(Integer catid);
	
	/**
	 * 按商品分类的第一级进行分组显示<br/>
	 * lzf add<br/>
	 * 20101210
	 * @return 品牌列表
	 */
	public List groupByCat();
	/**
	 * 读取品牌详细
	 * @param brand_id 品牌
	 * @return 品牌
	 */
	public Brand get(Integer brand_id);


	/**
	 * 分页读取某个品牌下的商品
	 * @param brand_id
	 * @return 商品分页列表
	 */
	public Page getGoods(Integer brand_id,int pageNo,int pageSize);

	/**
	 * 分页读取某个标签下的品牌
	 */
	public Page listBrands(Integer tag_id,int pageNo,int pageSize);
	/**
	 * 分页读取某个标签下的品牌
	 */
	public List<Brand> listBrands(Integer tag_id);
	
	/**
	 * 获取所有商品类型
	 * @return 
	 */
	public List<Map> queryAllTypeNameAndId();
	
	/**
	 * 按品牌名字和商品类型搜索
	 * @param page 分页页数
	 * @param pageSize 分页每页数量
	 * @param brandname 品牌名字
	 * @param type_id 商品类型
	 * @return 品牌分页列表
	 */
	public Page search(int page, int pageSize,String brandname,Integer type_id) ;
	
	/**
	 * 按照条件搜索品牌列表
	 * @param brandMap 搜索参数
	 * @param page 分页数
	 * @param pageSize 分页每页显示数量
	 * @return 品牌列表
	 */
	public Page searchBrand(Map brandMap,int page,int pageSize);
	
	
	/**
	 * 品牌排序
	 * @param ordernums
	 * @param ids
	 */
	public void saveOrder(int[] ordernums,int[] ids);
}