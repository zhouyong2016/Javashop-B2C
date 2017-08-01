package com.enation.app.shop.core.log;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.ILogCreator;
import com.enation.framework.model.Log;
import com.enation.framework.util.DateUtil;

/**
 * 单店日志创建实现
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月13日 上午10:38:36
 */
@Component
public class B2cLogCreator implements ILogCreator{

	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.log.ILogCreator#createLog()
	 */
	@Override
	public Map createLog() {
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		Map map = new HashMap();
		map.put("admin_user", adminUser);
		return map;
	}

}
