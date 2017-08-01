package com.enation.app.shop.front.api.member;

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
 * 红酒星空详细页收藏
 * @author lina
 * @version 2.0,2016-02-18 wangxin v60版本改造
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/api/shop/collect")
public class CollectApiController {
	
	@Autowired
	private IFavoriteManager favoriteManager;
	
	
	
	/**
	 * 收藏一个商品，必须登录才能调用此api
	 * @param goods_id ：要收藏的商品id,int型
	 * @return
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	@ResponseBody
	@RequestMapping(value="/add-collect",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult addCollect(Integer goods_id){
		Member memberLogin = UserConext.getCurrentMember();
		if(memberLogin!=null){
			int count = favoriteManager.getCount(goods_id,memberLogin.getMember_id());
			if (count == 0){
				favoriteManager.add(goods_id);
				return JsonResultUtil.getSuccessJson("添加收藏成功");
			}else{
				return JsonResultUtil.getErrorJson("已收藏该商品");				
			}
		}else{
			return JsonResultUtil.getErrorJson("对不起，请您登录后再收藏该商品！");				
		}
	}
	
	
	/**
	 * 取消一个商品的收藏，必须登录才能调用此api
	 * @param favorite_id ：收藏id，即Favorite.favorite_id
	 * @return
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 * 
	 * {@link Favorite}
	 */
	@ResponseBody
	@RequestMapping(value="/cancel-collect",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult cancelCollect(Integer favorite_id){
		favoriteManager.delete(favorite_id);
		return JsonResultUtil.getSuccessJson("取消成功");
	}
	
	
	

	
}
