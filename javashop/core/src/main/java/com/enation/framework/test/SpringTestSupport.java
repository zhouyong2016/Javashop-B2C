package com.enation.framework.test;

import javax.servlet.Filter;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.component.IComponentManager;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
 

/**
 * 单元测试支撑类<br>
 * 提供基于spring的测试环境<br>
 * 包括对web环境的支持<br>
 * @author kingapex
 * @version v1.0
 * @since v6.2
 * 2016年10月6日下午4:06:50
 */
@RunWith(EopJunit4ClassRunner.class)
@WebAppConfiguration(value = "src/main/webapp")
@ContextConfiguration("classpath*:spring_cfg/*.xml")
@Rollback(true)
@Transactional
//mock 静态方法未成功，暂时注释
//@PrepareForTest( StringUtil.class)
public class SpringTestSupport {
	
	
	@Autowired
	protected WebApplicationContext wac;
	
	
	protected MockMvc mockMvc;
	
	@Autowired
	private IComponentManager componentManager ;
	
	protected MockHttpSession session;
	
 
	protected boolean getInstallTest( ){
		 return false;
	}
	
	
//	 mock 静态方法未成功，暂时注释	
//	 @Rule
//	 public PowerMockRule rule = new PowerMockRule();
	 
	 
	@Before
	public void setUp() throws Exception {
		
		 session = new MockHttpSession();
		//构建 spring的mockMvc
		Filter f  = new CharacterEncodingFilter("UTF-8",true);
		Filter f1 = new org.springframework.web.filter.DelegatingFilterProxy("shiroFilter",wac);
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter( f , "/*").addFilter(f1, "*.do").build();
		
//		mock 静态方法未成功，暂时注释
//		ServletContext sc= wac.getServletContext();
//		mockStatic(StringUtil.class);
//	    when(StringUtil.getRootPath()).thenReturn(sc.getRealPath(""));
        
		//设置为测试模式
		SystemSetting.setTest_mode(1);
		
		//初始化EOP的上下文
		mockMvc.perform(MockMvcRequestBuilders.get("/api/eoptest/init.do").session(session)  );
		
		
		if(this.getInstallTest()){
			String app_apth = StringUtil.getRootPath();
			FileUtil.delete(app_apth+"/install/install.lock");
			System.out.println( "install path is "+ app_apth);
			EopSetting.init();
		}
		
		
	 
		if( EopSetting.INSTALL_LOCK.equals("YES")){
			System.out.println(" component start ! ");
			componentManager.startComponents();
		}
	}
	
    
	
}
