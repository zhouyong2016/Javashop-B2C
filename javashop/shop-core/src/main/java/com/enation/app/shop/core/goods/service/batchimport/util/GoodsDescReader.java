package com.enation.app.shop.core.goods.service.batchimport.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.enation.eop.SystemSetting;
import com.enation.framework.util.FileUtil;

/**
 * 商品描述读取
 * @author kingapex
 *
 */
public class GoodsDescReader {
	protected final Logger logger = Logger.getLogger(getClass());
	public String read(String folder,String goodsid){
		
		 
		String descFilePath  = folder+"/desc.htm";

		if(!new File(descFilePath).exists() ) {
			 descFilePath  = folder+"/desc.html";
			 if(!new File(descFilePath).exists() ) {
					if(this.logger.isDebugEnabled()){
						logger.debug("描述["+descFilePath+"]文件不存,跳过");
					}	
			 }
	
			return null;
		}
		

		
		String bodyHtml=null;
		/**
		 * --------------------
		 * 读取商品描述信息
		 * --------------------
		 */
		Document doc = Jsoup.parse(FileUtil.read(descFilePath, "GBK"));
		Elements bodys =doc.select("body");
		if(bodys!=null && !bodys.isEmpty() ) {
			org.jsoup.nodes.Element bodyEl =bodys.get(0);
			bodyHtml = bodyEl.html();
			bodyHtml=bodyHtml.replaceAll("src=\"desc.files/", "src=\"fs:/attachment/ckeditor/"+goodsid+"/");
			bodyHtml=bodyHtml.replaceAll("src=\"desc_files/", "src=\"fs:/attachment/ckeditor/"+goodsid+"/");
			//this.daoSupport.execute("update es_goods set intro=? where goods_id=?", bodyHtml,goodsid);
			
			if(this.logger.isDebugEnabled()){
				logger.debug("read商品["+goodsid+"]描述信息完成");
			}	
		}
		
		
		
		
		/**
		 * --------------------
		 * copy描述所用文件
		 * --------------------
		 */
		String folderPath  =folder+"/desc.files";
		String static_server_path= SystemSetting.getStatic_server_path();
		if(new File(folderPath).exists()){
			

			FileUtil.copyFolder(folderPath, static_server_path +"/attachment/ckeditor/"+goodsid);
			if(this.logger.isDebugEnabled()){
				logger.debug("copy商品["+goodsid+"]描述图片完成");
			}	
		}else{
			folderPath  =folder+"/desc_files";
			
			if(new File(folderPath).exists()){
				FileUtil.copyFolder(folderPath,static_server_path +"/attachment/ckeditor/"+goodsid);
				if(this.logger.isDebugEnabled()){
					logger.debug("copy商品["+goodsid+"]描述图片完成");
				}
			}else{
				if(this.logger.isDebugEnabled()){
					logger.debug("商品["+goodsid+"]描述图片不存在，跳过导入描述图片");
				}
			}
		}
		
		if(this.logger.isDebugEnabled()){
			logger.debug("导入商品["+goodsid+"]描述 完成");
		}
		
		return bodyHtml;
		
	}
}
