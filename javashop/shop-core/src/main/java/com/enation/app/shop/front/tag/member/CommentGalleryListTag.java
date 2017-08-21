package com.enation.app.shop.front.tag.member;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品评论相册标签
 * @author DMRain 2016-5-3
 * @version 1.0
 */
@Component
public class CommentGalleryListTag extends BaseFreeMarkerTag{

	@Autowired
	private IMemberCommentManager memberCommentManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer comment_id = (Integer) params.get("comment_id");
		List galleryList = this.memberCommentManager.getCommentGallery(comment_id);
		return galleryList;
	}

}
