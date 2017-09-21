package com.enation.app.shop.component.plugin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinUtil;
import com.enation.app.shop.core.member.plugin.IMemberLoginEvent;
import com.enation.app.shop.core.member.plugin.IMemberRegisterEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 微信绑定插件
 * 通过注册登录事件，实现微信绑定
 * @author Sylow
 * @version v1.0,2016年7月8日
 * @since v6.1
 */
@Component
public class WeixinBindPlugin  extends AutoRegisterPlugin implements IMemberLoginEvent, IMemberRegisterEvent {

	private static final Logger LOGGER = LoggerFactory.getLogger(WeixinBindPlugin.class);
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	public void onRegister(Member member) {
		// TODO Auto-generated method stub
		bindWeiChat(member);
	}

	@Override
	public void onLogin(Member member, Long upLogintime) {
		
		bindWeiChat(member);
		
	}
	
	/**
	 * 把会员和微信绑定
	 * @param member 会员
	 * @return 绑定结果
	 */
	private boolean bindWeiChat(Member member){
		try {
			boolean isWeiChat = WeixinUtil.isWeChat() == 1;		//值为1就是在微信中
			
			int memberId = member.getMember_id();
			
			HttpSession session = ThreadContextHolder.getSession();
			
			Object connectIdObj = session.getAttribute("connect_openid");
			Object connectTypeObj = session.getAttribute("connect_type");
			
			// 如果有值，说明是通过第三方注册的
			if (connectIdObj != null) {
				
				int connectType = Integer.parseInt(connectTypeObj.toString());
				String connectId = connectIdObj.toString();
				
				String field = "";

				switch (connectType) {
					case 1:
						field = "qq_id";
						break;
					case 2:
						field = "weibo_id";
						break;
					case 3:
						field = "wx_unionid";
						break;
					case 4:
						field = "wx_unionid";
						break;
					default:
						field = "qq_id";
						break;
				}
				
				Map<String, Object> memberMap = this.memberManager.getMember(memberId);
				Object idObject = memberMap.get(field);
				
				if (idObject == null || "".equals(idObject)) {
					this.daoSupport.execute("update es_member set " + field + "=? where member_id=?", connectId, memberId);
				}
				
			}
			
			
			// 如果是微信中
			if (isWeiChat) {
				Map<String, Object> memberMap = this.memberManager.getMember(memberId);
				Object unionId = memberMap.get("wx_unionid");
				
				if (unionId == null || "".equals(unionId)) {
					try {
						unionId = WeixinUtil.getUnionId();
					} catch (IOException e) {
						LOGGER.debug("跳转出错:", e);
					}
					this.daoSupport.execute("update es_member set wx_unionid=? where member_id=?", unionId, memberId);
				}
				
				Object openId = memberMap.get("wx_openId");
				if (openId == null || "".equals(openId)) {
					try {
						openId = WeixinUtil.getOpenId();
					} catch (IOException e) {
						LOGGER.debug("跳转出错:", e);
					}
					this.daoSupport.execute("update es_member set wx_openid=? where member_id=?", openId, memberId);
				}
			}
			return true;
		} catch(Exception e) {
			LOGGER.debug("绑定微信插件出错:", e);
			return false;
		}
		
		
	}

}
