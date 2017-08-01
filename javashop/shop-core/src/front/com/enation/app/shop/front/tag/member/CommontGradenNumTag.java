/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：评级tag
 *  修改人：whj
 *  修改时间：2015-10-15
 *  修改内容：制定初版
 */
package com.enation.app.shop.front.tag.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品评级个数标签及该商品的总数
 * 
 * @author whj
 * @version v1.0,2015-10-15 
 * @since v5.2
 */
@Component
@Scope("prototype")
public class CommontGradenNumTag extends BaseFreeMarkerTag{

	@Autowired
	private IMemberCommentManager memberCommentManager;
	
	/**
	 * @param 商品的评论总数
	 * @param grade 评论等级  3表示好评，2表示中品，1表示差评
	 * @param gradeHigh =3, 好评， gradeTwo =2 中评  gradeLow =3 差评.
	 * @return 同时输出3中状态下评论的个数
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goods_id =  (Integer)params.get("goods_id");
		Map result = new HashMap();
		
		//将评级固定，固定输出好评（grade=3）下的评论数量
		int gradeHigh = memberCommentManager.getCommentsGradeCount(goods_id, 3); 
		
		//将评级固定，固定输出中评（grade=2）下的评论数量
		int gradeTwo = memberCommentManager.getCommentsGradeCount(goods_id, 2);
		
		//将评级固定，固定输出差评（grade=1）下的评论数量
		int gradeLow = memberCommentManager.getCommentsGradeCount(goods_id, 1);
		
		//该商品的评论总数
		int allCommentNum = memberCommentManager.getCommentsCount(goods_id);
		
		result.put("gradeHigh", gradeHigh);
		result.put("gradeTwo", gradeTwo);
		result.put("gradeLow", gradeLow);
		result.put("allCommentNum", allCommentNum);
		
		return result;
	}

}
