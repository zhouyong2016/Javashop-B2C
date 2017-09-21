package com.enation.app.base.core.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 数据导出Action
 * @author kingapex
 * 2015-5-6下午3:07:46
 * @version 2.0   2016-2-24  6.0升级改造   wangxin
 */
@Controller 
@RequestMapping("/core/admin/data-export")
public class DataExportController extends GridController {
	/**
	 * 跳转至数据导出页面
	 * @return 数据导出页面
	 */
	@RequestMapping(value="/export")
	public Object execute(HttpServletRequest request){
		String a;
		// 判定是否是b2b2c
				if (EopSetting.PRODUCT.equals("b2b2c")) {
					 a="";
						request.setAttribute("a", a);
					return "/core/admin/data/export";
				} else {
					 a="0";
					request.setAttribute("a", a);
					return "/core/admin/data/export";
				}
	}
	
	@ResponseBody
	@RequestMapping(value="/get-input-stream")
	public InputStream getInputStream(String data) {
		InputStream   in   =   new   ByteArrayInputStream(data.getBytes());  
		return in;
	}
	/**
	 * 导出数据
	 * @param type 判断导出类型,0为B2C数据，1为B2B2C
	 * @param b2c_tables B2C数据表
	 * @param b2b2c_tables B2B2C数据表
	 * @return
	 */	
	@RequestMapping(value="/do-export")
	public void doExport(HttpServletResponse response,int type) throws IOException{
		 String[] b2b2c_tables=new String[]{"es_adv","es_adcolumn","es_smtp","es_goods_cat","es_brand","es_goods_type","es_type_brand","es_goods","es_goods_gallery","es_product","es_product_store","es_store_log","es_goods_spec","es_tags","es_tag_rel","es_member","es_member_lv","es_dly_type","es_logi_company","es_payment_cfg","es_print_tmpl","es_depot","es_data_cat","es_data_model","es_data_field","es_hot_keyword","es_helpcenter","es_store","es_store_silde","es_store_cat","es_store_template","es_site_menu","es_tag_relb","es_floor","es_page","es_style","es_subject","es_showcase"};
		 String[] b2c_tables=new String[]{"es_adv","es_adcolumn","es_smtp","es_goods_cat","es_brand","es_goods_type","es_type_brand","es_goods","es_goods_gallery","es_product","es_product_store","es_store_log","es_goods_spec","es_tags","es_tag_rel","es_member","es_member_lv","es_dly_type","es_logi_company","es_payment_cfg","es_print_tmpl","es_depot","es_data_cat","es_data_model","es_data_field","es_hot_keyword","es_helpcenter","es_site_menu","es_floor","es_page","es_style","es_subject","es_showcase"};
		 String data;
		 
		String[] tables =null;
		if(type==0){
			tables = b2c_tables;
		}else{ 
			tables = b2b2c_tables;
		}
		data= DBSolutionFactory.dbExport(tables, true, "");
		data = this.getSettingXml() + data; 
		data ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><dbsolution>"+data+"</dbsolution>";
		data = data.replaceAll(SystemSetting.getStatic_server_domain(), "fs:");
		response.setContentType("application/octet-stream");response.getHeader("Content-Length");
		response.setContentLength(data.getBytes().length);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"data.xml\""));
		
		// Copy bytes from source to destination(outputstream in this example), closes both streams.
		response.getWriter().write(data, 0, data.length());  //等同，FileCopyUtils.copy(data, response.getWriter());
	}
	
	/**
	 * 读取导出设置
	 * @return 导出数据
	 */
	private String getSettingXml(){
		return	FileUtil.read(StringUtil.getRootPath()+"/core/admin/data/setting.xml", null);
	}
	/**
	 * 获取文件名称
	 * @return 文件名称
	 */
/*	public String getFileName(){
		return "data.xml";
	}*/

	 
	 
	 
}
