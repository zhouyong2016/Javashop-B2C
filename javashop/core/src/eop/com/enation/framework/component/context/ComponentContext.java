package com.enation.framework.component.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponent;
import com.enation.framework.component.PluginView;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.XMLUtil;

/**
 * 组件上下文管理
 * 用于记录系统中所有的组件
 * @author kingapex
 *2012-5-14下午9:18:47
 */
public class ComponentContext {
	private static List<ComponentView> componentList;
	private static Map<String, Boolean> siteComponentState;

	static {
		componentList = new ArrayList<ComponentView>();
		siteComponentState = new HashMap<String, Boolean>();
	}
	 
	/**
	 * 标记某个站点的组件状态为已起动
	 * 
	 * @param userid
	 * @param siteid
	 */
	public static void siteComponentStart(int userid, int siteid) {
		siteComponentState.put(userid + "_" + siteid, Boolean.TRUE);
	}

	/**
	 * 返回某个站的组件启动状态
	 * 
	 * @param userid
	 * @param siteid
	 * @return
	 */
	public static boolean getSiteComponentState(int userid, int siteid) {
		Boolean state = siteComponentState.get(userid + "_" + siteid);
		return state == null ? false : state;
	}

	public static void registerComponent(ComponentView componentView) {
		try {
			loadComponent(componentView);
			componentList.add(componentView);
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 获取组件列表
	 * @return
	 */
	public static List<ComponentView> getComponents() {
		return componentList;
	}

	/**
	 * 由component.xml中加载组件信息 此组件的名称及id 包含的挂件和插件
	 * 
	 * @param componentView
	 *            要加载的组件视图,会根据其component同包下的component.xml加载
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private static void loadComponent(ComponentView componentView) throws SAXException, IOException, ParserConfigurationException {
		IComponent component = componentView.getComponent();

		String path = component.getClass().getPackage().getName();
		path = path.replace('.', '/') + "/component.xml";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(FileUtil.getResourceAsStream(path));
		Element componentEl = (Element) doc.getFirstChild(); // component节点

		String needVersion = componentEl.getAttribute("javashop_version");
		String currentVersion = EopSetting.VERSION;  
		if(StringUtil.isEmpty(currentVersion)){
			currentVersion="4.0.0";
		}
		componentView.setName(componentEl.getAttribute("name"));
		componentView.setAuthor(componentEl.getAttribute("author"));
		componentView.setVersion(componentEl.getAttribute("version"));
		componentView.setJavashop_version(needVersion);
		componentView.setDescription(componentEl.getAttribute("description"));
		 	
		if (!versionLargerThen(currentVersion, needVersion)) {
			// if(Double.valueOf( needVersion) > Double.valueOf( currentVersion
			// )){
			componentView.setInstall_state(2);
			componentView.setError_message("当前的Javashop版本无法安装此组件，需要的Javashop版本[" + needVersion + "] ，当前版本[" + currentVersion + "]");
			// }
		}

		Element pluginsEl = XMLUtil.getChildByTagName(componentEl, "plugins");
		Element widgetsEl = XMLUtil.getChildByTagName(componentEl, "widgets"); // 挂件节点		 	

		if (pluginsEl != null) {
			/**
			 * --------------------------------- 查找所有插件，并压入组件视图的插件列表
			 * ---------------------------------
			 */
			NodeList pluginNodeList = pluginsEl.getElementsByTagName("plugin");// 插件列表

			if (pluginNodeList != null) {
				int length = pluginNodeList.getLength();
				for (int i = 0; i < length; i++) {
					Element pluginEl = (Element) pluginNodeList.item(i);
					String name = pluginEl.getAttribute("name");
					String pluginBeanid = pluginEl.getAttribute("id");

					PluginView pluginView = new PluginView();
					pluginView.setId(pluginBeanid);
					pluginView.setName(name);

					/**
					 * 加载此插件的桩
					 */
					NodeList bundleList = pluginEl.getElementsByTagName("bundle");
					if (bundleList != null) {
						int bundleLength = bundleList.getLength();
						for (int j = 0; j < bundleLength; j++) {
							Element bundleEl = (Element) bundleList.item(j);
							String beanid = bundleEl.getAttribute("id");
							pluginView.addBundle(beanid);
						}
					}
					componentView.addPlugin(pluginView);
				}
			}
		}		 	
		 	
		 	 	    
	}
		
		
	/**
	 * 判断ver1是否比ver2大
	 * 
	 * @param ver1
	 * @param ver2
	 * @return
	 */
	private static boolean versionLargerThen(String ver1, String ver2) {
		if (StringUtil.isEmpty(ver1))
			throw new IllegalArgumentException("ver1版本不能为空");
		if (StringUtil.isEmpty(ver2))
			throw new IllegalArgumentException("ver2版本不能为空");
		if (ver1.length() != ver2.length())
			throw new IllegalArgumentException("ver2与ver2版本号格式不相同");
		if (ver1.length() != 5)
			throw new IllegalArgumentException("版本号格式不正确，应为如：2.1.0");

		String[] ver1a = ver1.split("\\.");
		Integer ver1i = Integer.valueOf(ver1a[0]) * 1000000 + Integer.valueOf(ver1a[1]) * 1000 + Integer.valueOf(ver1a[2]);
		String[] ver2a = ver2.split("\\.");
		Integer ver2i = Integer.valueOf(ver2a[0]) * 1000000	+ Integer.valueOf(ver2a[1]) * 1000 + Integer.valueOf(ver2a[2]);

		return ver1i >= ver2i;
	}		

}