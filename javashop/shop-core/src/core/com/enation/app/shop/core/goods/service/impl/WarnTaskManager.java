package com.enation.app.shop.core.goods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.service.IWarnTaskManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;


public class WarnTaskManager   implements IWarnTaskManager {
	
	@Autowired
	private IDaoSupport  daoSupport;
	
	
	/**
	 * 根据商品获取颜色
	 * @param goodsId
	 * @return
	 */
	public List<Map> listColor(Integer goodsId){
		String sql = "select  pc.productid,pc.color  from es_product  p left join es_product_color  pc  on p.product_id=pc.productid where p.goods_id="+goodsId;
		return this.daoSupport.queryForList(sql);
	}
	/**
	 * 保存维护任务
	 * @param map
	 * map中变量goods 商品对象; depotval;//仓库串 sphereval;//度数串 cylinderval;//散光串 productval;//产品串 产品和颜色是一一对应的关系
	 */
	public void saveTask(Map map){
		Goods goods = (Goods)map.get("goods");
		String depotval = map.get("depotval").toString();
		String sphereval = map.get("sphereval").toString();
		String cylinderval = map.get("cylinderval").toString();
		String productval = map.get("productval").toString();
		String[] deptArr = depotval.split(",");
		for (int i = 0; i < deptArr.length; i++) {
			Map tempMap = new HashMap();
			tempMap.put("goodsid", goods.getGoods_id());
			tempMap.put("catid", goods.getCat_id());
			tempMap.put("depotid", deptArr[i]);
			tempMap.put("sphere", sphereval);
			tempMap.put("cylinder", cylinderval);
			tempMap.put("flag", 1);
			tempMap.put("productids", productval);
			this.daoSupport.insert("es_warn_task",tempMap);
		}
	}
	@Override
	public Page listAll(Integer page,Integer pageSize ) {
		String sql = "SELECT d.name as depotname,g.sn,g.name,gc.name as catname,w.* FROM es_warn_task w  "
				+" left join  es_goods g on w.goodsid = g.goods_id "
				+" left join es_goods_cat  gc on w.catid = gc.cat_id "
				+" left join es_depot  d on d.id = w.depotid ";
		Page webpage = this.daoSupport.queryForPage(sql, page,
				pageSize);
		List<Map> list = (List<Map>)webpage.getResult();
		for (Map map : list) {
			StringBuilder product_color = new StringBuilder("");
			if(map.get("catid").toString().equals("3") || map.get("catid").toString().equals("4") || map.get("catid").toString().equals("12") ||map.get("catid").toString().equals("18")  ){
				String[] productids = map.get("productids").toString().split(",");
				int flag = 0;
				for (String productid : productids) {
					if(flag != 0){
						product_color.append(",");
					}
					product_color.append(this.daoSupport.queryForString("select color from es_product_color where productid  =" + productid));
					flag ++;
				}
				map.put("color",product_color.toString() );
			}
			//TODO 此处代码有明显的眼镜项目痕迹，请王总整理
			StringBuilder glasphere = new StringBuilder("");
			if(map.get("catid").toString().equals("6")  && !map.get("sphere").equals("") ){
				String[] spheres = map.get("sphere").toString().split(",");
				String[] sylinderes = map.get("cylinder").toString().split("\\|");
				if(sylinderes!=null && sylinderes.length>0){
					for (int i = 0; i < spheres.length; i++) {
						sylinderes[i]  = sylinderes[i].substring(0, sylinderes[i].lastIndexOf(",") );
						glasphere.append("[度数："+spheres[i]+",散光："+sylinderes[i]+"]");
					}
				}
				map.put("glasses_sphere",glasphere.toString());
			}
		}
		return webpage;
	}

	@Override
	public Page listdepot(Integer depotId,String name,String sn,int page,int pageSize) {
		//String sql = "select  g.*  from "+this.getTableName("goods")+" g left join "+this.getTableName("warn_task")+" wt  on g.goods_id=wt.goodsid where wt.flag=1 and wt.depotid="+depotId;
		String sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name,wt.id as task_id,wt.productids,wt.sphere,wt.cylinder from es_goods"
	            +" g left join es_goods_cat"
	            +" c on g.cat_id=c.cat_id left join es_brand"
	            +" b on g.brand_id = b.brand_id and b.disabled=0 left join es_goods_type"
	            +" t on g.type_id =t.type_id  left join es_warn_task"
	            +" wt  on g.goods_id=wt.goodsid "
			    +" where wt.flag=1 and g.disabled=0 and wt.depotid="+depotId; // g.goods_type = 'normal' and 
		if(name!=null && !name.equals("")){
			name = name.trim();
			String[] keys = name.split("\\s");
			
			for(String key:keys){
				sql+=(" and g.name like '%");
				sql+=(key);
				sql+=("%'");
			}			 
		}
		
		if(sn!=null && !sn.equals("")){
			sql+="   and g.sn = '"+ sn+ "'" ;
		}
		//return this.daoSupport.queryForList(sql);
		Page webpage  = this.daoSupport.queryForPage(sql.toString(), page, pageSize);
		List<Map> list = (List<Map>)webpage.getResult();
		for (Map map : list) {
			StringBuilder product_color = new StringBuilder("");
			if(map.get("cat_id").toString().equals("3") || map.get("cat_id").toString().equals("4") || map.get("cat_id").toString().equals("12") ||map.get("cat_id").toString().equals("18")  ){
				String[] productids = map.get("productids").toString().split(",");
				int flag = 0;
				for (String productid : productids) {
					if(flag != 0){
						product_color.append(",");
					}
					product_color.append(this.daoSupport.queryForString("select color from product_color where productid  =" + productid));
					flag ++;
				}
				map.put("color",product_color.toString() );
			}
			//TODO 请王总整理
			StringBuilder glasphere = new StringBuilder("");
			if(map.get("cat_id").toString().equals("6") && !map.get("sphere").equals("")  ){
				String[] spheres = map.get("sphere").toString().split(",");
				String[] sylinderes = map.get("cylinder").toString().split("\\|");
				for (int i = 0; i < spheres.length; i++) {
					sylinderes[i]  = sylinderes[i].substring(0, sylinderes[i].lastIndexOf(",") );
					glasphere.append("[度数："+spheres[i]+",散光："+sylinderes[i]+"]");
				}
				map.put("glasses_sphere",glasphere.toString());
			}
		}
		return webpage;
	}

	@Override
	public Map listTask(Integer taskId) {
		String sql = "select wt.* from es_warn_task wt where wt.id=?";
		return this.daoSupport.queryForMap(sql, taskId);
	}
	@Override
	public Integer getProductId(Integer goodsid){
		String sql  = "select product_id from es_product where goods_id = ?";
		Integer productid  = this.daoSupport.queryForInt(sql, goodsid);
		return productid;
	}
	@Override
	public void updateTask(Integer taskId) {
		this.daoSupport
		.execute(
				"update es_warn_task set flag=2 where id=? ",
				taskId);
	}

}
