package com.enation.framework.log;

import java.util.Map;

import com.enation.eop.resource.model.AdminUser;

/**
 * 日志存储接口
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月13日 上午9:59:44
 */
public interface ILogCreator {

	/**
	 * 存储日志
	 * @return Map
	 */
	public Map  createLog();
}
