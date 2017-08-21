package com.enation.app.base.security.domain;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * 可通过配置文件扩展Shiro Filter Chain的定义。
 * @author John Zhang
 *
 */
public class ExtensibleFilterChainDefinitions implements FactoryBean<String> {
	
	private static final String NEW_LINE = "\n";

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtensibleFilterChainDefinitions.class);

	private String filterChainDefinitions;
	
	private Resource[] locations;
	
	private Map<String, String> filterChainDefinitionMap;

	@Override
	public String getObject() throws Exception {
		StringBuilder builder = new StringBuilder();
		//首先读取Spring XML内字符串方式配置
		if (filterChainDefinitions != null) {
			builder.append(StringUtils.clean(filterChainDefinitions)).append(NEW_LINE);
		}
		//再读取Map方式配置
		if( filterChainDefinitionMap != null ) {
			for (String key : filterChainDefinitionMap.keySet()) {
				builder.append(key)
						.append("=")
						.append(filterChainDefinitionMap.get(key))
						.append(NEW_LINE);
			}
		}
		//最后读取properties配置文件方式
		if(locations != null) {
			for (Resource resource : locations) {
                if (resource != null && resource.exists() && resource.isReadable()) {
                    String string = IOUtils.toString(resource.getInputStream());
                    builder.append(string).append(NEW_LINE);
                }
            }
		}
		String config = builder.toString();
		LOGGER.debug("自定义Shiro 扩展配置加载完成：\n{}", config);
		return config;
	}
	
	@Override
	public Class<?> getObjectType() {
		return getClass();
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public String getFilterChainDefinitions() {
		return filterChainDefinitions;
	}

	public void setFilterChainDefinitions(String filterChainDefinitions) {
		this.filterChainDefinitions = filterChainDefinitions;
	}

	public Map<String, String> getFilterChainDefinitionMap() {
		return filterChainDefinitionMap;
	}

	public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
		this.filterChainDefinitionMap = filterChainDefinitionMap;
	}

	public Resource[] getLocations() {
		return locations;
	}

	public void setLocations(Resource[] locations) {
		this.locations = locations;
	}

}
