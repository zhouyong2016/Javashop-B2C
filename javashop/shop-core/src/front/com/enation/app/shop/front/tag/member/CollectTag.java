package com.enation.app.shop.front.tag.member;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.service.IFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 *  我的收藏标签
 * @author lina
 *2013-8-15下午8:54:05
 *
 * @version v1.1,2015-12-17 whj
 */
@Component
@Scope("prototype")
public class CollectTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IFavoriteManager favoriteManager;
	
	/**
	 * 我的收藏标签
	 *  @param member 当前登录会员
	 *  @param page 我的收藏分页列表
	 *  @param goodsnum 每页展示商品数量
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签");
		}
		Integer pageSize = (Integer)params.get("goodsnum");
		
		//判断如果不传递每页展示商品数量，默认每页10个商品
		if(pageSize == null){
			 pageSize = 10;
		}
		Page page = favoriteManager.list(member.getMember_id(),this.getPage(), pageSize);

		return page;
	}

}
