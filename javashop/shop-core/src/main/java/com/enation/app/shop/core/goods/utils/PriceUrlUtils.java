/**
 * 
 */
package com.enation.app.shop.core.goods.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enation.app.shop.component.search.plugin.Price;
import com.enation.app.shop.core.goods.plugin.search.SearchSelector;
import com.enation.app.shop.core.goods.service.Separator;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 价格url生成工具
 * @author kingapex
 *2015-5-7
 */
public class PriceUrlUtils {
	private static Map<String,List<Price>> priceMap;
	
	public static void createAndPut(Map<String, Object> map) {
		List<SearchSelector>  list= new ArrayList<SearchSelector>();

		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String servlet_path = request.getServletPath();
		
		String cat  =request.getParameter("cat");
		if(StringUtil.isEmpty(cat)){
			cat="0";
		}
		 String[] catar = cat.split(Separator.separator_prop_vlaue);
		 String catid =  catar[catar.length-1];
		

		 Map<String,List<Price>> pMap  =getPriceMap();
		 List<Price> defList  = pMap.get("0");
		 List<Price> priceList  = pMap.get(catid);
		 if(priceList==null){
			 priceList=defList;
			
		 } 
		 for(Price price :priceList){
			 
			 String max = price.getMax();
			 String min = price.getMin();
			 
			 min = min==null ?"":min;
			 max = max==null ?"":max;
			 
			 String text  = price.getText();
			 
			 SearchSelector selector = new SearchSelector();
			 selector.setName(text);
			 String priceUrl  = min+"_"+max;
			 
			 priceUrl=servlet_path+"?"+ createPriceUrl(priceUrl);
			 
			 selector.setUrl(priceUrl);
			 
			 list.add(selector);
			 
		 }
		 map.put("price", list);
		 map.put("selected_price", getSelectedPrice(servlet_path));
		
	}

	
	/**
	 * 生成price url
	 * @param priceUrl
	 * @return
	 */
	private static String createPriceUrl(String priceUrl){
		
		 Map params = ParamsUtils.getReqParams();
		 params.put("price", priceUrl);
		 
		return ParamsUtils.paramsToUrlString(params);
	}

	
	
	private static List<SearchSelector> getSelectedPrice(String servlet_path){
		
		List<SearchSelector>  list= new ArrayList<SearchSelector>();

		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String price_str  = request.getParameter("price");
		if(StringUtil.isEmpty(price_str)){
			return  list;
		}
		
		String[] price_ar=  price_str.split("_");
		String min_str= price_ar[0];
		String max_str="";
		if(price_ar.length==2){
			max_str= price_ar[1];
		}
		
		 String text  = min_str;
		 if(text.equals("")){
			 text="0";
		 }
		 if( StringUtil.isEmpty(max_str) ){
			 text =text+"元以上";
		 }else{
			 text =text+"至"+max_str+"元";
		 }
		 SearchSelector selector = new SearchSelector();
		 selector.setName(text);
		 String priceUrl="";
		 
		 priceUrl=servlet_path+"?"+ createPriceUrl(priceUrl);
		 
		 selector.setUrl(priceUrl);
		 
		 list.add(selector);
		 
		return list;
	}
	
	
	private  static Map<String,List<Price>> getPriceMap(){
		if(priceMap!=null){
			return priceMap;
		}
		String app_apth = StringUtil.getRootPath();

		String xmlFile =app_apth+"/themes/price_filter.xml";
		try {
			
		    DocumentBuilderFactory factory = 
		    DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document document = builder.parse(xmlFile);
		    
		     priceMap = new HashMap<String, List<Price>>();
		    
		    NodeList catList  = document.getElementsByTagName("cat");
		    for( int i = 0;i<catList.getLength();i++){
		    	
		    	List<Price> priceList  = new ArrayList<Price>();
		    	
		    	Element catNode  = (Element) catList.item(i);
		    	NodeList priceNodeList  = catNode.getElementsByTagName("price");
		    	for (int j=0;j<priceNodeList.getLength();j++){
		    		
		    		
		    		Element priceEl  = (Element)priceNodeList.item(j);
		    		String text = priceEl.getFirstChild().getNodeValue() ;
		    		String minPrice  = priceEl.getAttribute("min");
		    		String maxPrice  = priceEl.getAttribute("max");
		    		Price price  = new Price();
		    		price.setText(text);
		    		
		    		if(!StringUtil.isEmpty(minPrice)){
		    			price.setMin(  minPrice  );
		    		}
		    		if(!StringUtil.isEmpty(maxPrice)){
		    			price.setMax(  maxPrice  );
		    		}

		    		priceList.add(price);
		    		
		    	}
		    	
		    	priceMap.put(catNode.getAttribute("id"), priceList);
		    	
		    }
		    
		    return priceMap;
		}
		catch (Exception e) {
			 
			e.printStackTrace();
			throw new RuntimeException("load  price_filter.xml   error" );
		} 	 
		 
	}
}
