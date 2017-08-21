package com.enation.app.shop.component.spec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.resource.IMenuManager;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.data.IDataOperation;

/**
 * 商品规格组件
 * 
 * @author kingapex 2012-3-12下午5:00:10
 */

@Component
public class GoodsSpecComponent implements IComponent {
	
	@Autowired
	private IDataOperation dataOperation;
	
	
	private final String parentMenuName = "设置";
	
	private IMenuManager menuManager;
	private IAuthActionManager authActionManager;

	@Override
	public void install() {
		this.installMenu();
		this.dataOperation.imported("file:com/enation/app/shop/component/spec/spec_install.xml");
	}

	@Override
	public void unInstall() {
		unInstallMenu();
		this.dataOperation.imported("file:com/enation/app/shop/component/spec/spec_uninstall.xml");
	}

	private void unInstallMenu() {
		int superAdminAuthId = PermissionConfig.getAuthId("super_admin"); // 超级管理员权限id
		int addmenuid = menuManager.get("添加规格").getId();
		int listmenuid = menuManager.get("规格列表").getId();
		int menuid = menuManager.get("规格管理").getId();

		this.authActionManager.deleteMenu(superAdminAuthId, new Integer[] {	menuid, listmenuid, addmenuid });

		this.menuManager.delete("添加规格");
		this.menuManager.delete("规格列表");
		this.menuManager.delete("规格管理");
	}
	
	private void installMenu() {
//		int superAdminAuthId = PermissionConfig.getAuthId("super_admin"); // 超级管理员权限id
//
//		Menu parentMenu = menuManager.get(parentMenuName);
//		Menu menu = new Menu();
//		menu.setTitle("规格管理");
//		menu.setPid(parentMenu.getId());
//		menu.setUrl("#");
//		menu.setSorder(55);
//		menu.setMenutype(Menu.MENU_TYPE_APP);
//		int menuid = this.menuManager.add(menu);
//
//		Menu listMenu = new Menu();
//		listMenu.setPid(menuid);
//		listMenu.setTitle("规格列表");
//		listMenu.setUrl("/shop/admin/spec!list.do");
//		listMenu.setSorder(1);
//		listMenu.setTarget("ajax");
//		listMenu.setMenutype(Menu.MENU_TYPE_APP);
//		int listmenuid = menuManager.add(listMenu);
//
//		Menu addMenu = new Menu();
//		addMenu.setPid(menuid);
//		addMenu.setTitle("添加规格");
//		addMenu.setUrl("/shop/admin/spec!add.do");
//		addMenu.setSorder(1);
//		addMenu.setTarget("ajax");
//		addMenu.setMenutype(Menu.MENU_TYPE_APP);
//		int addmenuid = menuManager.add(addMenu);
//
//		this.authActionManager.addMenu(superAdminAuthId, new Integer[] { menuid, listmenuid, addmenuid });
	}

	public IMenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}

	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}

}
