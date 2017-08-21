package com.enation.app.shop.component.gallery.action;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;


/**
 * 商品相册action
 * @author kingapex
 *
 */

@Controller
@RequestMapping("/api/shop/goodsGallery")
public class GoodsGalleryController {

	
	@Autowired
	private IGoodsGalleryManager goodsGalleryManager;
	
	/**
	 * 上传
	 * @param filedata
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/upload")
	public String upload(MultipartFile file){

		if (file != null) {
			String name = goodsGalleryManager.upload(file);
			return  name;
		}
		return null;
	}
	
	/**
	 * 删除图片
	 * @param photoName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public JsonResult delete(String photoName) {
		if (!this.checkAdminPerm()) {
			return JsonResultUtil.getErrorJson("您无权限访问此API");
		}

		this.goodsGalleryManager.delete(photoName);
		return JsonResultUtil.getSuccessJson("图片删除成功");
	}
	
	private boolean checkAdminPerm() {
		if (UserConext.getCurrentAdminUser()==null) {
			return false;
		}
		return true;
	}

}
