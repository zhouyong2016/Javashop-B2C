package com.enation.app.shop.front.api.member;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.Favorite;
import com.enation.app.shop.core.member.service.IFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 收藏API
 * @author fenlongli
 * @version 2.0,2016-02-18 wangxin v60版本改造
 */
@Controller
@RequestMapping("/api/shop/favorite")
@Scope("prototype")
public class FavoriteApiController {
	
	@Autowired
	private IFavoriteManager favoriteManager;
	
	/**
	 * 删除一个会员收藏  whj 0225 下午18：25
	 * @param favorite_id ：要删除的会员收藏地址id,String型
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value="/delete",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult delete(Integer favorite_id) {
		try {
			Member member = UserConext.getCurrentMember();
			if (member != null) {
				//增加操作权限校验
				Favorite favorite = favoriteManager.get(favorite_id);
				if(favorite==null || !member.getMember_id().equals(favorite.getMember_id())){
					return JsonResultUtil.getSuccessJson("您没有操作权限");
				}
				this.favoriteManager.delete(favorite_id); 
				return JsonResultUtil.getSuccessJson("删除成功");
			}else{
				return JsonResultUtil.getErrorJson("请先登录");
			}
		} catch (Exception e) {
			Logger logger = Logger.getLogger(getClass());
			if (logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			return JsonResultUtil.getErrorJson("删除失败[" + e.getMessage() + "]");
		}
	}
}
