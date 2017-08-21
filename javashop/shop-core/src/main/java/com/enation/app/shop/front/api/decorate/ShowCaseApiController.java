package com.enation.app.shop.front.api.decorate;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.decorate.model.ShowCase;
import com.enation.app.shop.core.decorate.service.IShowCaseManager;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.JsonResult;
import com.enation.framework.directive.ImageDirectiveModel;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;
/**
 * 
 * 橱窗前台api
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1.1
 */
@Controller
@RequestMapping("/api/core/showcase")
public class ShowCaseApiController {
	
	@Autowired
	private IShowCaseManager showCaseManager;
	/**
	 * 换一批功能
	 * @param id 橱窗id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="refresh")
	public JsonResult refresh(Integer id){
		ShowCase showCase=this.showCaseManager.getShowCaseById(id);
		String goods_ids=showCase.getContent();
		if(!StringUtil.isEmpty(goods_ids)){
			List<Goods> list=this.showCaseManager.getSelectGoods(goods_ids);
			Collections.shuffle(list);
			int i=0;
			StringBuffer htmlSb=new StringBuffer("<ul>");
			for (Goods goods : list) {
				if(i<6){
					htmlSb.append("<li>");
					htmlSb.append("<dl>");
					htmlSb.append("<dt class='showcase_goods_name'>");
					htmlSb.append("<a href='goods-"+goods.getGoods_id()+".html' target='_blank' >");
					htmlSb.append(""+goods.getName()+"'");
					htmlSb.append("</a>");
					htmlSb.append("</dt>");
					htmlSb.append("<dd class='showcase_goods_img'>");
					htmlSb.append("<a href='goods-"+goods.getGoods_id()+".html' target='_blank' >");
					htmlSb.append("<img class='scrollLoading' alt='"+goods.getName()+"' style='width:100%;' src='"+getImageUrl(goods.getThumbnail(),null)+"' >");
					htmlSb.append("</a>");
					htmlSb.append("</dd>");
					htmlSb.append("<dd class='showcase_goods_price'>");
					htmlSb.append("商城价：￥"+String.format("%.2f",goods.getPrice())+"");
					htmlSb.append("</dd>");
					htmlSb.append("</dl>");
					htmlSb.append("</li>");
				}
				
				i++;
			}
			htmlSb.append("</ul>");
			return JsonResultUtil.getObjectJson(htmlSb.toString());
		}
		return JsonResultUtil.getErrorJson("查询失败");
	}
	
	private static String getImageUrl(String pic,String postfix){
		if (StringUtil.isEmpty(pic))
			pic = SystemSetting.getDefault_img_url();
		
		
		//由王峰去掉，为什么要加这个限制呢？如果有如此需求呢：
		//显示这个地址：http://www.abc.com/a.jpg为：http://www.abc.com/a_thumbail.jpg
		//if(pic.toUpperCase().startsWith("HTTP"))//lzf add 20120321
		//	return pic;
		if (pic.startsWith("fs:")) {//静态资源式分离式存储
			pic = StaticResourcesUtil.convertToUrl(pic);
		}
		if (!StringUtil.isEmpty(postfix )) {
			return StaticResourcesUtil.getThumbPath(pic, postfix);
		} else {
			return pic;
		}
	}
}
