package com.enation.app.shop.front.tag.goods.other.regions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 地区下拉框控件
 * @author kingapex
 *2012-3-11下午9:41:19
 */
public class RegionSelectDirective implements TemplateDirectiveModel {
	
	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		
		Object provinceobj = params.get("province_id");
		Object cityObj = params.get("city_id");
		Object regionObj =  params.get("region_id");
		Object ctxObj =  params.get("ctx");
		
		Object province_name=params.get("province_name");
		Object city_name=params.get("city_name");
		Object region_name=params.get("region_name");
		
		//是否拥有小区
		Object has_community=params.get("has_community");
		Object communityObj =  params.get("community_id");
		
		IRegionsManager  regionsManager=SpringContextHolder.getBean("regionsManager");
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
			
		List provinceList = new ArrayList();
		provinceList=regionsManager.listProvince();
		freeMarkerPaser.putData("provinceList",provinceList);
		
		freeMarkerPaser.putData("province_id",provinceobj);
		freeMarkerPaser.putData("city_id",cityObj);
		freeMarkerPaser.putData("region_id",regionObj);
		freeMarkerPaser.putData("ctx",ctxObj);
		
		//判断是否拥有小区
		if(has_community!=null){
			freeMarkerPaser.setPageName("communitySelectWidget");
		}else{
			freeMarkerPaser.setPageName("RegionsSelectWidget");
		}
		
		
		//是否给地区写别名
		if(province_name!=null&& city_name!=null&& region_name!=null){
			freeMarkerPaser.putData("province_name",province_name);
			freeMarkerPaser.putData("city_name",city_name);
			freeMarkerPaser.putData("region_name",region_name);
		}
		
		
		String html=freeMarkerPaser.proessPageContent();
		if(province_name!=null){
			if(provinceobj!=null && cityObj!=null ){
				String province_id = params.get("province_id").toString();
				String city_id = params.get("city_id").toString();
				String region_id = params.get("region_id").toString();
				freeMarkerPaser.putData("ctx",ctxObj);
				
				if(has_community!=null&&communityObj!=null){
					String community_id = params.get("community_id").toString();
					if(!StringUtil.isEmpty(community_id)  &&!StringUtil.isEmpty(province_id)&&!StringUtil.isEmpty(city_id)&&	!StringUtil.isEmpty(region_id)){
						html +="<script>$(function(){ "+province_name+"RegionsSelect.load("+province_id+","+city_id+","+region_id+","+community_id+");});</script>";
					}
				}else if(!StringUtil.isEmpty(province_id)&&!StringUtil.isEmpty(city_id)&&	!StringUtil.isEmpty(region_id)){
					html +="<script>$(function(){ "+province_name+"RegionsSelect.load("+province_id+","+city_id+","+region_id+");});</script>";
				}
			}
		}else{
			if(provinceobj!=null && cityObj!=null ){
				String province_id = params.get("province_id").toString();
				String city_id = params.get("city_id").toString();
				String region_id = params.get("region_id").toString();
				freeMarkerPaser.putData("ctx",ctxObj);
				
				if(has_community!=null&&communityObj!=null){
					String community_id = params.get("community_id").toString();
					if(!StringUtil.isEmpty(community_id)  &&!StringUtil.isEmpty(province_id)&&!StringUtil.isEmpty(city_id)&&	!StringUtil.isEmpty(region_id)){
						html +="<script>$(function(){ RegionsSelect.load("+province_id+","+city_id+","+region_id+","+community_id+");});</script>";
					}
				}else if(!StringUtil.isEmpty(province_id)&&!StringUtil.isEmpty(city_id)&&	!StringUtil.isEmpty(region_id)){
					html +="<script>$(function(){ RegionsSelect.load("+province_id+","+city_id+","+region_id+");});</script>";
				}
			}
		}
		
		
		env.getOut().write(html.toString());
		
		
	}

}
