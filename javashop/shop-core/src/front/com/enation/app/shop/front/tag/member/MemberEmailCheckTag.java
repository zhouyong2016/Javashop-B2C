package com.enation.app.shop.front.tag.member;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.EncryptionUtil1;

import freemarker.template.TemplateModelException;
/**
 *  邮箱验证标签
 * @author lina
 *2014-6-11下午5:54:05
 */
@Component
@Scope("prototype")
public class MemberEmailCheckTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IMemberManager memberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result = new HashMap();
		try{
			String s = ThreadContextHolder.getHttpRequest().getParameter("s");
			String str = EncryptionUtil1.authcode(s, "DECODE","",0);
			String[] array = StringUtils.split(str,",");
			if(array.length!=2) throw new RuntimeException("验证字串不正确");
			int memberid  = Integer.valueOf(array[0]);
			long regtime = Long.valueOf(array[1]);
			
			Member member = memberManager.get(memberid);
			if(member.getRegtime() != regtime){
				result.put("result", 0);
				result.put("message", "验证字串不正确");
				return result;
			}
			if(member.getIs_cheked()==0){
				memberManager.checkEmailSuccess( member);
				result.put("result", 1);
				result.put("message", member.getUname() +"您好，您的邮箱验证成功!");
			}else{
				result.put("result", 0);
				result.put("message", member.getUname() +"您好，验证失败，您已经验证过该邮箱!");
			}
		}catch(RuntimeException e){
			result.put("result", 0);
			result.put("message", "验证地址不正确");
		}
		return result;
	}


}
