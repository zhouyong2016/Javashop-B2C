package com.enation.app.base.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.model.Theme;
import com.enation.framework.test.SpringTestSupport;

/**
 * theme manager 单元测试
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月20日下午9:18:41
 */
public class ThemeManagerTest extends SpringTestSupport {
	
	@Autowired
	private  IThemeManager themeManager;
	
	/**
	 * 测试添加方法
	 */
	@Test
	public void testAdd(){
		
		Theme theme = new Theme();
		theme.setProductId("base");
		theme.setPath("kaben");
		theme.setThemename("kabn");
		theme.setThumb("preview.png");
		theme.setSiteid(0);
		int themeid = this.themeManager.add(theme, false);
		//Assert.assertEquals(1, themeid);
	}
	
	
	
}
