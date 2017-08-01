package com.enation.app.shop.front.tag.goods.snapshot;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.service.IGoodsSnapshotManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * (商品快照详细标签) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 上午3:59:23
 */
@Service
public class GoodsSnapshotBaseDataTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IGoodsSnapshotManager goodsSnapshotManager;
	
	/**
	 *  商品快照详细标签
	 * @param snapshot_id:商品快照id ,int型
	 * @return 商品基本信息
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
			try{ 
				 Integer snapshot_id=(Integer)params.get("snapshot_id");
	
				 if(snapshot_id==null||snapshot_id==0){
					 snapshot_id= this.getSnapshotId(); 
				 }
				 Map snapshotMap = goodsSnapshotManager.get(snapshot_id); 
				 /**
				  * 如果商品快照不存在抛出页面找不到异常 
				  */
				 if(snapshotMap==null||snapshotMap.size()==0){
					// return goodsMap;
					throw new UrlNotFoundException();
				 }
				 
				 /**
				  * 
				  * 如果是草稿商品
				  */
				 if(StringUtil.toString(snapshotMap.get("market_enable"),false).equals("2")){
					//标记预览
					 snapshotMap.put("goods_off", 2);
				 }
				 
				 
				 String intro  =(String)snapshotMap.get("intro");
				 if(!StringUtil.isEmpty(intro)){
					 intro = StaticResourcesUtil.convertToUrl(intro);
					 snapshotMap.put("intro", intro);
				 }
				 this.getRequest().setAttribute("goods", snapshotMap);
				 return snapshotMap;
				
			}catch(ObjectNotFoundException e){
				 throw new UrlNotFoundException();
			}
	}

	
	private Integer getSnapshotId(){
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		String snapshot_id = this.paseSnapshotId(url);
		
		return Integer.valueOf(snapshot_id);
	}

	private  static String  paseSnapshotId(String url){
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
