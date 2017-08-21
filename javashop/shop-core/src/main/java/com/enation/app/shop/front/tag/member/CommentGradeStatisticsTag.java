package com.enation.app.shop.front.tag.member;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品评论值标签
 * @author kingapex
 *2013-8-8下午9:05:38
 */
@Component
@Scope("prototype")
public class CommentGradeStatisticsTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IMemberCommentManager memberCommentManager;

	/**
	 * 输入商品id
	 * @return 返回本商品的评价值统计Map,通过其key可以输出相应的值，如：
	 *  假设返回的变量定义为：commentStat，那么通过${commentStat.grade_1}输出评分为1的评论个数。
	 *  此Map的key为 "grade_评分值"
	 *  如：假设评分有1至3分，那么此Map的key为grade_1,grade_2,grade_3。
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goodsid  =(Integer)params.get("goods_id");
		
		if(goodsid==null){
			throw new TemplateModelException("必须传递goods_id参数");
		}
		return memberCommentManager.statistics(goodsid);
		
	}
	
}
