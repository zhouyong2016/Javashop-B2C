package com.enation.app.base.security.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.config.Ini;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.context.EopSetting;

public class ShiroFilterChain implements FactoryBean<Ini.Section> {

	@Autowired
	private IMenuManager menuManager;

	private String filterChainDefinitions = null;

	public Ini.Section getObject() throws Exception {
		Ini ini = new Ini();
		ini.load(filterChainDefinitions);
		Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
		
		if(EopSetting.INSTALL_LOCK == "yes")
		{
			List<Menu> allMenus = menuManager.getMenuList();
			Map<String, Menu> map = new HashMap<String, Menu>();
			if (CollectionUtils.isNotEmpty(allMenus)) {
				for (Menu menu : allMenus) {
					if(Menu.MENU_TYPE_SYS != menu.getMenutype() && StringUtils.isNoneBlank(menu.getUrl()))
					{
						System.out.println(menu.getUrl().trim());
						map.put(menu.getUrl().trim(), menu);
						section.put(menu.getUrl().trim(), "sysAuth["+ menu.getId() +"]");
					}
				}
			}
		}
		return section;
	}

	public void setFilterChainDefinitions(String filterChainDefinitions) {
		this.filterChainDefinitions = filterChainDefinitions;
	}

	public Class<?> getObjectType() {
		return this.getClass();
	}

	public boolean isSingleton() {
		return false;
	}
}