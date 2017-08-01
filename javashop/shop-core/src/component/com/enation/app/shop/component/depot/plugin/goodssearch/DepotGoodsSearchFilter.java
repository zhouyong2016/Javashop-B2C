package com.enation.app.shop.component.depot.plugin.goodssearch;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.goods.model.DepotUser;
import com.enation.app.shop.core.goods.plugin.IGoodsBackendSearchFilter;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 仓库搜索过滤器
 * @author kingapex
 *
 */
@Component
public class DepotGoodsSearchFilter extends AutoRegisterPlugin implements
		IGoodsBackendSearchFilter {

	private IAdminUserManager  adminUserManager;
	private ISettingService settingService ;
	 

	@Override
	public String getSelector() {
		return "";
	}
 

	@Override
	public String getFrom() {
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		String optype = request.getParameter("optype");
		
		AdminUser user = UserConext.getCurrentAdminUser();
		if(("mng".equals(optype) || "stock".equals(optype)) && user.getFounder()==0 ){
			return " inner join es_goods_depot gd on g.goods_id=gd.goodsid ";
		}
		else if( "monitor".equals(optype))
			return " inner join es_goods_depot gd on g.goods_id=gd.goodsid ";
		else
			return "";
	}
 
	@Override
	public void filter(StringBuffer sql) {
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		String optype = request.getParameter("optype");
		
		AdminUser user  =UserConext.getCurrentAdminUser();
		if( "stock".equals(optype)  ){
			if(user.getFounder()==0){
				DepotUser depotUser =(DepotUser)user;
				sql.append(" and gd.iscmpl=0 and gd.depotid="+depotUser.getDepotid());
			}else{
				sql.append(" and g.goods_id not in(select goodsid from es_goods_depot  where iscmpl=1)");
			}
		}
		else if( "mng".equals(optype)  && user.getFounder()==0){
			DepotUser depotUser =(DepotUser)user;
			
			sql.append(" and gd.iscmpl=1 and gd.depotid="+depotUser.getDepotid());
		}
		//超级管理员 仓库监控
		else if("monitor".equals(optype)){
			String depotid = request.getParameter("depotid");
			sql.append(" and gd.iscmpl=0 and gd.depotid="+depotid);
		} 
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

}
