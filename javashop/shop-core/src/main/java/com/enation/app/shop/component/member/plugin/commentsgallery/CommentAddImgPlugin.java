package com.enation.app.shop.component.member.plugin.commentsgallery;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.plugin.IGoodsCommentsAddEvent;
import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 商品评论时上传评论相册插件
 * @author DMRain 2016-5-9
 * @version 1.0
 */
@Component
public class CommentAddImgPlugin extends AutoRegisterPlugin implements IGoodsCommentsAddEvent{

	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	public void beforeAdd(MemberComment memberComment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterAdd(MemberComment memberComment) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		if(request!=null){
			String[] picnames = request.getParameterValues("picnames_"+memberComment.getProduct_id());
			if (picnames != null) {
				for (int i = 0; i < picnames.length; i++) {
					Map map = new HashMap();
					map.put("comment_id", memberComment.getComment_id());
					map.put("original", this.transformPath(picnames[i]));
					map.put("sort", i);
					this.daoSupport.insert("es_member_comment_gallery", map);
				}
			}
		}
		
	}
	
	/**
	 * 页面中传递过来的图片地址为:http://<staticserver>/<image path>如:
	 * http://static.enationsoft.com/attachment/goods/1.jpg
	 * 存在库中的为fs:/attachment/goods/1.jpg 生成fs式路径
	 * 
	 * @param path
	 * @return
	 */
	private String transformPath(String path) {
		String static_server_domain= SystemSetting.getStatic_server_domain();

		String regx =static_server_domain;
		path = path.replace(regx, EopSetting.FILE_STORE_PREFIX);
		return path;
	}

}
