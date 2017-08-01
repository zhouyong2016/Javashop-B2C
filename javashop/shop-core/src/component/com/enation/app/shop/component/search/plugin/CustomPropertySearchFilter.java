package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Attribute;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.plugin.search.IGoodsFrontSearchFilter;
import com.enation.app.shop.core.goods.plugin.search.SearchSelector;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.app.shop.core.goods.service.Separator;
import com.enation.app.shop.core.goods.utils.PropUrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;


/**
 * 商品属性过滤器
 * @author kingapex
 *
 */
@Component
public class CustomPropertySearchFilter extends AutoRegisterPlugin implements
		IGoodsFrontSearchFilter {
	
	@Autowired
	private IGoodsTypeManager goodsTypeManager;
	
	@Override
	public void createSelectorList(Map mainmap ,Cat cat) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String prop  = request.getParameter("prop");
		String servlet_path = request.getServletPath();

		if(cat==null)  //未按分类搜索，无法进行属性过滤
		{
			mainmap.put("prop", new HashMap());
			mainmap.put("selected_prop", new ArrayList());//已经选择的属性
			return ;
		}
 
		
		List<Attribute> attrList = this.goodsTypeManager.getAttrListByTypeId(cat.getType_id()); // 这个类型的属性
		attrList= attrList== null ?new ArrayList<Attribute>():attrList;
	
			Map<String,List<SearchSelector>> map = new LinkedHashMap<String,List<SearchSelector>>();
			
			String[] s_ar = StringUtil.isEmpty(prop )? new String[]{}:StringUtils.split(prop,"@") ;
			
			int i=0;
			
			List<SearchSelector> selectedList = new ArrayList<SearchSelector>(); //选中的selector
			
			for(Attribute attr: attrList){
				boolean attrSelected =false;
				String attrName =attr.getName();
				
				if (attr.getType() == 3||attr.getType() ==6) { //递进式搜索的属性
					List<SearchSelector> selectorList = new ArrayList<SearchSelector>();
					String[] optionAr = attr.getOptionAr(); //此属性的选项
					int j=0;
					
					//为每个选项生成选择器
					for(String option:optionAr){
						
						boolean haveSelected  = this.isSelected(s_ar, i, j);
						if(!attrSelected){
							attrSelected=haveSelected;
						}
						
						if( haveSelected){
							
							SearchSelector selected = new SearchSelector();
							selected.setName(attrName);
							selected.setValue(option);	
							String selected_url=PropUrlUtils.createPropUrlWithoutSome(""+i, ""+j);
							selected_url=servlet_path +"?"+selected_url;
							selected.setUrl(selected_url);
							selectedList.add(selected);
						 
							
						}else{
							SearchSelector selector = new SearchSelector();
							selector.setName(option);
				    		String propurl =servlet_path +"?"+ PropUrlUtils.createPropUrl(""+i, ""+j);

							selector.setUrl(propurl);
							selectorList.add(selector);
						}
						j++; 
					}
					if(!attrSelected){
						map.put(attrName, selectorList);
					}
				}
				
				i++;
			}
			
			mainmap.put("prop", map);
			mainmap.put("selected_prop", selectedList);//已经选择的属性
	 
	}
	
	/***
	 * 判断某个属性项是否已经选中
	 * @param s_ar
	 * @param attrIndex
	 * @param optionIndex
	 * @return
	 */
	private boolean isSelected(String[] s_ar,int attrIndex,int optionIndex){
		for (int i = 0; i < s_ar.length; i++) {
			String[] value = s_ar[i].split("\\_");
			int attr_index = Integer.valueOf(value[0]).intValue();
			int option_index = Integer.valueOf(value[1]).intValue();
			
			if(attrIndex == attr_index && option_index== optionIndex){
				 return true;
			}
		}
		
		return false;
	}
	
	
	
	public void filter(StringBuffer sql,Cat cat) {
		
		if(cat==null) return; //未按分类搜索，无法进行属性过滤
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String prop  = request.getParameter("prop");
		if(StringUtil.isEmpty(prop)) return;
		
		// 关于属性的过滤
		//属性值示例: 0_1@0_2
		if (prop != null && !prop.equals("")) {
			List<Attribute> prop_list = this.goodsTypeManager.getAttrListByTypeId(cat.getType_id()); // 这个类型的属性
			prop_list= prop_list== null ?new ArrayList<Attribute>():prop_list;			
			String[] s_ar = prop.split(Separator.separator_prop);
			Map<String,String> mutil= new HashMap<String,String>();
			
			for (int i = 0; i < s_ar.length; i++) {
				String[] value = s_ar[i].split(Separator.separator_prop_vlaue);
				int index = Integer.valueOf(value[0]).intValue();
				Attribute attr = prop_list.get(index);
				int type = attr.getType();
				if(type==2 || type==5 ) continue; //不可搜索跳过
				
				if(type==6){
					 //将多选条件以条件分组度入map
					String key = "g.p"+ (index + 1);
					String term =  key+" like '%#"+value[1]+"%'";
					String temp = mutil.get(key);
					
					if(temp!=null){
						term = temp+" or "+ term;
					}
					
					mutil.put(key ,term);
					
				}else{
					sql.append(" and g.p" + (index + 1));
					
					if(type==1){ //输入项搜索用like
						sql.append(" like'%");
						sql.append(value[1]);
						sql.append("%'");
					}
					if(type==3 || type==4){ //渐进式搜索直接=
						sql.append("='");
						sql.append(value[1]);
						sql.append("'");
						sql.append("and type_id="+cat.getType_id()+"");
					}	
				}
			

			}
			
			Iterator<String> keySet = mutil.keySet().iterator();
		    while(keySet.hasNext()){
		    	String key =keySet.next();
		    	String term =mutil.get(key);
		    	sql.append(" and ("+ term+")");
		    }
		    
		    
		} 

	}

	
	public String getFilterId() {
		
		return "prop";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "goodsPropertySearchFilter";
	}

	
	public String getName() {
		
		return "商品属性过滤器";
	}

	
	public String getType() {
		
		return "searchFilter";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {

	}
	public void register() {

	}


}
