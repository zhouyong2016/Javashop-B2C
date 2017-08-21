package com.enation.app.shop.front.tag.decorate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.httpClient.HttpResponse;
import com.enation.app.shop.core.decorate.model.Subject;
import com.enation.app.shop.core.decorate.service.ISubjectManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * 单一专题获取标签
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
@Scope("prototype")
public class SubjectTag extends BaseFreeMarkerTag{
	
	@Autowired
	private ISubjectManager subjectManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer id=(Integer) params.get("subjectid");
		if(id==null||id==0){
			id=this.getSubjectId();
		}
		Subject subject=this.subjectManager.getSubjectByIdAboveAll(id);
		try {
			String goods_id=subject.getGoods_ids();
			String picture_path=subject.getPicture_path();
			List goodsList= this.subjectManager.getGoodsByGoodsIds(goods_id);
			List pictureList=this.formatPicturePath(picture_path);
			Map map=new HashMap();
			map.put("goodsList", goodsList);
			map.put("pictureList", pictureList);
			return map;
		} catch (NullPointerException e) {
			this.redirectTo404Html();
		}
		return new HashMap();
	}

	private List formatPicturePath(String picture_path) {
		// TODO Auto-generated method stub
		if(StringUtil.isEmpty(picture_path)){
			return null;
		}
		Map picture_path_map=JsonUtil.toMap(picture_path);
		List<String> list=new ArrayList<String>();
		for(Integer i=0;i<picture_path_map.size();i++){
			list.add(picture_path_map.get(i.toString()).toString());
		}
		return list;
	}

	private Integer getSubjectId(){
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		String subject_id = this.paseSubjectId(url);
		return Integer.valueOf(subject_id);
	}

	private  static String  paseSubjectId(String url){
		String pattern = "(-)(\\d+)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value=m.group(2);
		}
		return value;
	}
}
