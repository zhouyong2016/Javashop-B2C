package com.enation.app.shop.front.api.goods;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.goods.service.IGnotifyManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;


/**
 * 会员缺货登记
 * @author whj	
 *2014-02-26下午5:00:00
 */
@Controller
@RequestMapping("/api/shop/gnotify")
public class MemberGnotifyApiController {
	
	@Autowired
	private IGnotifyManager gnotifyManager;
	
	@ResponseBody
	@RequestMapping(value="/gnotify-del")
	public JsonResult gnotifyDel(Integer gnotify_id) {
		try {
			this.gnotifyManager.deleteGnotify(gnotify_id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			Logger logger = Logger.getLogger(getClass());
			if (logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			return JsonResultUtil.getErrorJson("删除失败[" + e.getMessage() + "]");

		}
	}
	
	@ResponseBody
	@RequestMapping(value="/add")
	public JsonResult add(Integer goodsid,Integer productid){
		try {
			
			int num = this.gnotifyManager.getGnotifyBymember(goodsid, productid);
			if(num==0){
				this.gnotifyManager.addGnotify(goodsid,productid);
				
				return JsonResultUtil.getSuccessJson("登记成功");
			}else{
				return JsonResultUtil.getErrorJson("已经登记过了");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("登记失败，请重试");
		}
		
	}
	
}
