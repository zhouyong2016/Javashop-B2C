package com.enation.app.shop.component.gallery.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.shop.component.gallery.model.GoodsGallery;

/**
 * 新相册明细数据管理接口
 * @author lzfwork
 * 2012-10-19上午9:14:18
 * ver 1.0
 */
@Component
public interface IGoodsGalleryManager {
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(GoodsGallery gallery);
	
	/**
	 * 根据图片的路径找到图片 并修改图片排序
	 * @param original 图片路径
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSort(String original, int sort);
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer[] goodsid);
	
	
	/**
	 * 删除某个商品的相册 
	 * 2013001-01新增by kingapex
	 * @param goodsid
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(int goodsid);
	
	
	/**
	 * 读取某个商品的相册
	 * @param goods_id
	 * @return
	 */
	public List<GoodsGallery> list(int goods_id);

	
	
	/**
	 * 上传商品图片<br>
	 * 生成商品图片名称，并且在用户上下文的目录里生成图片<br>
	 * 返回以静态资源服务器地址开头+用户上下文路径的全路径<br>
	 * 在保存入数据库时应该将静态资源服务器地址替换为fs:开头，并去掉上下文路径,如:<br>
	 * http://static.enationsoft.com/user/1/1/attachment/goods/1.jpg，存库应该为:<br>
	 * fs:/attachment/goods/1.jpg
	 * @param file
	 * @return
	 */
	public  String upload(MultipartFile file) ;
	
	
	/**
	 * 生成商品缩略图<br>
	 * 传递的图片地址中包含有静态资源服务器地址，替换为本地硬盘目录，然后生成。<br>
	 * 如果是公网上的静态资源则不处理
	 * @param photoName
	 * @return
	 */
	public void createThumb(String filepath,String thumbName,int width,int height);
	
	
	/**
	 * 删除指定的图片<br>
	 * 将本地存储的图片路径替换为实际硬盘路径<br>
	 * 不会删除远程服务器图片
	 * @param photoName
	 */
	public  void delete(String photoName);
	
 
	
	
	/**
	 * 重新生成所有商品相册图片
	 */
	public void recreate(int start,int end);
	
	/**
	 * 获取商品相册总数
	 */
	public int getTotal();
}
