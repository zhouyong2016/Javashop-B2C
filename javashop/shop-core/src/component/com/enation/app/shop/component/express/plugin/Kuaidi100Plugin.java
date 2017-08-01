package com.enation.app.shop.component.express.plugin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.express.AbstractExpressPlugin;
import com.enation.app.base.core.plugin.express.IExpressEvent;
import com.enation.app.shop.component.express.plugin.util.HttpRequest;
import com.enation.app.shop.component.express.plugin.util.MD5;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.framework.util.JsonUtil;

/**
 * 快递100插件接口
 * @author xulipeng
 */

@Component
public class Kuaidi100Plugin extends AbstractExpressPlugin implements IExpressEvent {

	@Override
	public 	Map getExpressDetail(String com, String nu, Map params) {
		String keyid = (String) params.get("keyid");
		String code = (String) params.get("code");	//公司代码
		Map map = new HashMap();
		try {
			Request remoteRequest  = new RemoteRequest();
			String kuaidiurl="";
			//当user为0的时候是普通用户 当user为1的时候是企业用户
			if(params.get("user").toString().equals("0")){
				kuaidiurl="http://api.kuaidi100.com/api?id="+keyid+"&nu="+nu+"&com="+com+"&muti=1&order=asc";
				Response remoteResponse = remoteRequest.execute(kuaidiurl);
				String content  = remoteResponse.getContent();
				map = JsonUtil.toMap(content);
				if(map.get("status").equals("1")){
					map.put("message", "ok");
				}
			}else{
				kuaidiurl="http://poll.kuaidi100.com/poll/query.do";
				String param ="{\"com\":\""+com+"\",\"num\":\""+nu+"\"}";
				String customer =code;  //公司代码
				String key = keyid;
				String sign = MD5.encode(param+key+customer);
				HashMap<String, String> parms = new HashMap<String, String>();
				parms.put("param",param);
				parms.put("sign",sign);
				parms.put("customer",customer);
				String content;
				try {
					content = new HttpRequest().postData(kuaidiurl, parms, "utf-8").toString();
					map = JsonUtil.toMap(content);
				} catch (Exception e) {
					this.logger.error("查询出错",e);
				}
			}


		} catch (Exception e) {
			map.put("message", "快递100接口出现错误，请稍后重试！");
		}

		return map;
	}

	@Override
	public String getId() {
		return "kuaidi100Plugin";
	}

	@Override
	public String getName() {
		return "快递100接口插件";
	}



}
