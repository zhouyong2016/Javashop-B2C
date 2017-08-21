package com.enation.app.shop.component.gallery.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.data.IDataExportEvent;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 相册导出插件
 * @author lzf
 * 2012-10-26上午11:43:40
 * ver 1.0
 */
@Component
public class GalleryDataExportPlugin extends AutoRegisterPlugin implements
		IDataExportEvent {

	@Override
	public String onDataExport() {
		String[] tables={"goods_gallery"}; 
		
		String insertdata = DBSolutionFactory.dbExport(tables, true, "es_");
		StringBuffer data= new StringBuffer();
		data.append("\t<action>\n");
		data.append("\t\t<command>truncate</command>\n");
		data.append("\t\t<table>es_goods_gallery</table>\n");
		data.append("\t</action>\n");
		data.append(insertdata);
		return data.toString();
		
	}

}
