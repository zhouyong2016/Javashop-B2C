package com.enation.app.shop.front.tag.member;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.app.shop.core.member.service.impl.MemberCommentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 评论详细标签
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
public class CommentDetailTag extends BaseFreeMarkerTag {
	
	@Autowired
     private MemberCommentManager memberCommentManager;
	/**
	 * @param comment_id 评论Id
	 */
    @Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer conmmentid = (Integer)params.get("comment_id");
		MemberComment memberComment=memberCommentManager.get(conmmentid);
		return memberComment;
	}
    
}
