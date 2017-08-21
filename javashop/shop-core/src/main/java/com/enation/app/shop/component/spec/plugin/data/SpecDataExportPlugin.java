package com.enation.app.shop.component.spec.plugin.data;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.data.IDataExportEvent;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 规格数据导出插件
 * @author kingapex
 * 2012-10-10下午10:42:43
 */
@Component
public class SpecDataExportPlugin extends AutoRegisterPlugin implements IDataExportEvent {

	@Override
	public String onDataExport() {
		String[] tables={"specification","spec_values","goods_spec"}; 
		
		String insertdata = DBSolutionFactory.dbExport(tables, true, "es_");
		StringBuffer data= new StringBuffer();
		data.append("\t<action>\n");
		data.append("\t\t<command>truncate</command>\n");
		data.append("\t\t<table>es_specification</table>\n");
		data.append("\t</action>\n");
		data.append("\t<action>\n");
		data.append("\t\t<command>truncate</command>\n");
		data.append("\t\t<table>es_spec_values</table>\n");
		data.append("\t</action>\n");
		data.append("\t<action>\n");
		data.append("\t\t<command>truncate</command>\n");
		data.append("\t\t<table>es_goods_spec</table>\n");
		data.append("\t</action>\n");
		data.append(insertdata);
		return data.toString();
	}

}
