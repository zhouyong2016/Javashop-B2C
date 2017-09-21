package com.enation.app.shop.core.goods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.plugin.search.GoodsDataFilterBundle;
import com.enation.app.shop.core.goods.plugin.search.GoodsSearchPluginBundle;
import com.enation.app.shop.core.goods.plugin.search.IGoodsFrontSearchFilter;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsSearchManager;
import com.enation.app.shop.core.goods.service.Separator;
import com.enation.app.shop.core.goods.utils.SortContainer;
import com.enation.app.shop.core.member.model.Favorite;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * @version 2016-02-26 修改Manager注入、使商品列表找到此Manager，去掉geter、seter
 * @author yinchao 6.0版本改造
 *
 */
@Service
public class GoodsSearchManager  implements IGoodsSearchManager{
 
	@Autowired
	private IDaoSupport  daoSupport;
	
	@Autowired
	private GoodsSearchPluginBundle goodsSearchPluginBundle;
	
	@Autowired
	private GoodsDataFilterBundle goodsDataFilterBundle;
	
	@Autowired
	private IGoodsCatManager goodsCatManager ;
	
	public Map<String,Object> getSelector() {
	 
		Cat cat  = this.getCat();
		Map selectorMap = new HashMap();
		
		this.goodsSearchPluginBundle.createSelectorList(selectorMap, cat);
		
		return selectorMap;
	}
	 
	/**
	 * 返回当前搜索的分类
	 * @param url
	 * @return
	 */
	public Cat getCat(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String cat = request.getParameter("cat");
		if(StringUtil.isEmpty(cat)){
			return null;
		}
		String[] catar = cat.split(Separator.separator_prop_vlaue);
		String catid =  catar[catar.length-1];
		Cat goodscat  =this.goodsCatManager.getById(StringUtil.toInt(catid,0));
 
		if(goodscat==null){
			throw new UrlNotFoundException();
		}
		
		
		return goodscat;
	}
	
	
	
	public Page search(int pageNo,int pageSize)  {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String uri = request.getServletPath();
		List list  = this.list(pageNo,pageSize);
		int count = this.count();
		Page webPage = new Page(0, count, pageSize, list);
		return webPage;
	}
	
	
	
	public List list(int pageNo,int pageSize){
		
		StringBuffer sql  =new StringBuffer();
		sql.append( "select g.* from ");
		sql.append("es_goods g ");
		/**
		 * add by jianghongyan 添加导航列表支持
		 */
		this.goodsSearchPluginBundle.filterFrontSql(sql);
		/**
		 * end
		 */
		
//		sql.append(" g where g.goods_type = 'normal' and g.disabled=0 and market_enable=1 ");
		sql.append(" where g.disabled=0 and market_enable=1 ");
		
		/***
		 * --------------------------
		 * 过滤搜索条件
		 * -------------------------
		 */
		this.filterTerm(sql);
		sql.append( this.getSort());
		//System.out.println(sql);
		
		List goodslist = this.daoSupport.queryForListPage(sql.toString(), pageNo, pageSize); 
		
		this.goodsDataFilterBundle.filterGoodsData(goodslist);
		return goodslist;
	}
	
	
	private String getSort(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();

		String sortfield = request.getParameter("sort");
		 
		Map<String,String> sortMap  = SortContainer.getSort(sortfield);
		 
		 String  desc ="desc".equals(sortMap.get("def_sort") )?"desc":"asc";
		
		 String sort_field = sortMap.get("id");
		 if("buynum".equals(sort_field) || "def".equals(sort_field) ){
			 sort_field="buy_count";
		 }
				 
		return " order by "+ sort_field +" "+desc;
	}
	
	 
	
	
 
	/**
	 * 计算搜索结果数量
	 * @param url
	 * @return
	 */
	private int count(){
		StringBuffer sql = new StringBuffer("select count(0) from es_goods g ");
		this.goodsSearchPluginBundle.filterFrontSql(sql);
		sql.append(" where g.disabled=0 and market_enable=1 ");
		this.filterTerm(sql);
		return this.daoSupport.queryForInt(sql.toString());
	}
	
	private String noSpace(String text){
		String s[] = text.split(" ");
		String r = "";
		for(int i=0;i<s.length;i++){
			if(!"".equals(s[i]))
				r = r + s[i];
		}
		return r;
	}
	
	/**
	 *过滤搜索条件 
	 * @param sql 要加条件的sql语句
	 */
	private void filterTerm(StringBuffer sql){
		/**
		 * 如果按类别搜索,则查询此类别,并传递给过虑器
		 * 如果未按类别搜索,则传递null
		 */
		Cat cat = getCat();
		this.goodsSearchPluginBundle.filter(sql, cat);
		
	}

	
	
}
