package com.enation.eop.resource;

import com.enation.eop.resource.model.EopUser;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;

/**
 * @author lzf
 *         <p>
 *         created_time 2009-12-11 下午02:02:19
 *         </p>
 * @version 1.0
 */
public interface IUserManager {


	/**
	 * 获取某用户的详细信息
	 * 
	 * @param userid
	 * @return
	 */
	public EopUser getCurrentMember(Integer userid);
 

}
