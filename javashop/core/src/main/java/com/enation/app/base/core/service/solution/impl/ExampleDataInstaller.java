package com.enation.app.base.core.service.solution.impl;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.app.base.core.service.solution.InstallUtil;
import com.enation.framework.database.data.IDataOperation;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;


/**
 * 示例数据安装器
 * @author kingapex
 *
 */
@Service
public class ExampleDataInstaller implements IInstaller {
	 
	@Autowired
	private IDataOperation dataOperation;
	protected final Logger logger = Logger.getLogger(getClass());
	protected String eopsiteSQL;
	
	public String parseConst(String content){
		eopsiteSQL = "";
		Map<String,String> constMap = new HashMap<String,String>();
		char buf[] = new char[content.length()];
		content.getChars(0,content.length(), buf, 0);
		CharArrayReader in = new CharArrayReader(buf);
		BufferedReader reader = new BufferedReader(in);
		while(true){
			try{
				String line = reader.readLine();
				if(line==null)
					break;
				else if(line.startsWith("CONST")){
					String[] value = StringUtils.split(line.substring(line.indexOf('!')),"=");
					constMap.put(value[0], value[1]);
					content = content.replaceFirst(line + "\n", "");
				} else if(line.startsWith("EOPSITE")){
					eopsiteSQL = "\n" + line.replaceFirst("EOPSITE", "update eop_site");
					content = content.replaceFirst(line + "\n", "");
				}
				else
					break;
			}catch(Exception e){
				break;
			}
		}
		Set<String> keys = constMap.keySet();
		for (Iterator it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = constMap.get(key);
			content = content.replaceAll(key,value);
			eopsiteSQL = eopsiteSQL.replaceAll(key,value);
		}
		return content;
	}
	
	
	/**
	 * 使用新的数据库中间件来导入数据
	 * @param productId
	 * @param fragment
	 * @return
	 */
	private void anyDBInstall(String dataSqlPath) throws Exception{
		dataOperation.imported(dataSqlPath);
	}

	private void loggerPrint(String text){
		if(logger.isDebugEnabled()){
			this.logger.debug(text);
		}
	}
	
	public void install(String productId, Node fragment) {
		boolean xmlData = true;
		String app_apth = StringUtil.getRootPath();
		String dataSqlPath = app_apth+ "/products/" + productId  +"/example_data.xml";
		try {		
			File xmlFile = new File(dataSqlPath);
			
			loggerPrint("安装datasqlPath:" + dataSqlPath);
			
			if(!"base".equals(productId)){
				InstallUtil.putMessaage("正在安装示例数据，可能要花费较长时间，请稍后 <img src='resource/com/enation/app/saas/component/widget/product/loading.gif'");
			}else{
				InstallUtil.putMessaage("正在安装基础数据...");
			}
			
				anyDBInstall(dataSqlPath);
			 
			loggerPrint("示例数据安装完毕");
			
			FileUtil.copyFolder( 
					app_apth+ "/products/" + productId + "/attachment/",
					
					app_apth+ "/statics/attachment/");
		 
		 
			
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.debug(e.getMessage(),e);
			throw new RuntimeException("安装示例数据出错...");
		}		 
	}
	
	/**
	 * 过滤drop/truncate/delete语句/以及eop_开头的语句
	 * @param input
	 * @return
	 */
	private String filter(String input){
		Pattern pattern = Pattern.compile("delete\\s?.*?\\s?;", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		input = matcher.replaceAll("");
		pattern = Pattern.compile("truncate\\s?.*?\\s?;", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(input);
		input = matcher.replaceAll("");
		pattern = Pattern.compile("drop\\s?.*?\\s?;", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(input);
		input = matcher.replaceAll("");
		pattern = Pattern.compile("(delete|drop|truncate|insert|update)\\s?eop_.*?;", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(input);
		input = matcher.replaceAll("");
		return input;
	}
 

}
