package com.enation.app.shop.core.decorate.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.decorate.model.Floor;
import com.enation.app.shop.core.decorate.model.FloorProps;
import com.enation.app.shop.core.goods.model.Brand;
import com.enation.framework.database.Page;
/**
 * 
 * 楼层管理接口
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@SuppressWarnings(value={"rawtypes"})
public interface IFloorManager {

	/**
	 * 保存楼层
	 * @param floor 楼层对象
	 * @throws RuntimeException
	 */
	void save(Floor floor) throws RuntimeException;

	/**
	 * 获取子楼层
	 * @param parentid 父id
	 * @param pageid 页面id 
	 * @return 子楼层map
	 */
	List<Map> getListChildren(Integer parentid, Integer pageid);

	/**
	 * 保存排序
	 * @param id 楼层id
	 * @param sort 排序值
	 * @throws RuntimeException
	 */
	void saveSort(Integer[] floor_ids,Integer[] floor_sorts) throws RuntimeException;

	/**
	 * 通过id获取floor对象
	 * @param floorid 楼层id
	 * @return 楼层对象
	 */
	Floor getFloorById(Integer floorid);

	/**
	 * 删除楼层
	 * @param floor_id 楼层id
	 * @throws RuntimeException
	 */
	void delete(Integer floor_id) throws RuntimeException;

	/**
	 * 保存显示状态
	 * @param id 楼层id
	 * @param is_display 显示状态
	 * @return 子楼层的id 为页面显示提供级联操作
	 * @throws RuntimeException
	 */
	List saveDisplay(Integer id, Integer is_display) throws RuntimeException;

	/**
	 * 更新楼层
	 * @param floor 楼层对象
	 */
	void update(Floor floor);

	/**
	 * 保存标题
	 * @param id 楼层id
	 * @param title 楼层标题
	 * @throws RuntimeException
	 */
	void saveTitle(Integer id, String title) throws RuntimeException;

	/**
	 * 获取楼层和风格
	 * @param floor_id 楼层id
	 * @param limit 
	 * @param pageid 
	 * @return 楼层和风格集合
	 */
	List<Map> getChildFloorAndStyleById(Integer floor_id, Integer limit, Integer pageid);

	/**
	 * 保存导航分类
	 * @param catid 分类id
	 * @param floor_id 楼层id
	 * @throws RuntimeException
	 */
	void saveGuidCat(Integer catid, Integer floor_id) throws RuntimeException;


	/**
	 * 保存左侧分类id
	 * @param floor_id 楼层id
	 * @param cat_id 分类id
	 * @throws RuntimeException
	 */
	void saveCatId(Integer floor_id, Integer cat_id) throws RuntimeException;

	/**
	 * 获取全部广告信息
	 * @return 广告列表
	 */
	List getAllAdvList();

	/**
	 * 保存广告id
	 * @param adv_id
	 * @param floor_id
	 * @param position 
	 * @throws RuntimeException
	 */
	void saveAdvId( Integer floor_id,Integer[] adv_id, String position) throws RuntimeException;


	/**
	 * 获取顶级楼层和风格
	 * @param pageid 
	 * @return 顶级楼层风格列表
	 */
	List<Map> getTopFloorAndStyle(Integer pageid);

	/**
	 * 保存单个商品
	 * @param new_goods_id 新商品id
	 * @param floor_id 楼层id
	 * @param index 商品位置索引
	 */
	void saveEachGoods(Integer new_goods_id, 
			Integer floor_id, Integer index);

	/**
	 * 保存查询条件
	 * @param props 查询条件实体
	 * @param floor_id 楼层id
	 * @throws RuntimeException
	 */
	void saveProps( FloorProps props,Integer floor_id) throws RuntimeException;

	/**
	 * 批量保存商品
	 * @param goods_ids 商品id
	 * @param floor_id 楼层id
	 * @throws RuntimeException
	 */
	void saveBatchGoods(Integer[] goods_ids, Integer floor_id) throws RuntimeException;

	/**
	 * 根据id获取品牌
	 * @param id 品牌id
	 * @return 
	 */
	Brand getBrand(Integer id);

	/**
	 * 保存品牌id
	 * @param floor_id 楼层id
	 * @param brand_ids 品牌id
	 * @throws RuntimeException
	 */
	void saveBrandIds(Integer floor_id, Integer[] brand_ids) throws RuntimeException;

	/**
	 * 根据楼层id获取品牌
	 * @param floor_id 楼层id
	 * @return 品牌拼接字符串
	 */
	String getBrandIds(Integer floor_id);

	/**
	 * 根据品牌id json串获取品牌
	 * @param brand_Json 品牌id json串
	 * @return
	 */
	List<Brand> listBrands(String brand_Json);

	/**
	 * 根据楼层获取广告
	 * @param floor_id 楼层id
	 * @return 广告拼接字符串
	 */
	String getAdvIds(Integer floor_id);

	/**
	 * 根据商品id构成的map获取商品
	 * @param goods_id_map 商品id构成的map
	 * @return
	 */
	List getGoodsListByGoods_ids(Map<String,Object> goods_id_map);

	/**
	 * 根据品牌id构成的map获取品牌
	 * @param brand_id_map 品牌id构成的map
	 * @return
	 */
	List getBrandListByBrandIds(Map<String, Object> brand_id_map);

	/**
	 * 根据广告id构成的map获取广告
	 * @param adv_id_map 广告id构成的map
	 * @return
	 */
	List getAdvListByAids(Map<String, Object> adv_id_map);



	

}
