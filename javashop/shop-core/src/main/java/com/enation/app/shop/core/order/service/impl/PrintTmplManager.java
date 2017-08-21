package com.enation.app.shop.core.order.service.impl;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.PrintTmpl;
import com.enation.app.shop.core.order.service.IPrintTmplManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

@Service("printTmplManager")
public class PrintTmplManager  implements IPrintTmplManager {
	@Autowired
	private IDaoSupport  daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#add(com.enation.app.shop.core.order.model.PrintTmpl)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="添加了一个名为${printTmpl.prt_tmpl_title}的快递单模板")
	public void add(PrintTmpl printTmpl) {
		
		String newTmpl_data = printTmpl.getPrt_tmpl_data();
		newTmpl_data= newTmpl_data.replace("LODOP.PRINT_INITA(0,0,2300,1250,\"Lodop page\");", "LODOP.NEWPAGEA();");
		newTmpl_data = this.replcePrintTmel(newTmpl_data, printTmpl.getBgimage());
		//获取配送方式code
		String code=this.daoSupport.queryForString("select code from es_logi_company where name='"+printTmpl.getPrt_tmpl_title()+"'");
		//保存html文件
		this.printhtml(newTmpl_data, code,printTmpl);
		
		this.daoSupport.insert("es_print_tmpl", printTmpl);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#clean(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="清空快递模板")
	public void clean(Integer[] id) {
		if(id== null  || id.length==0  ) return ;
		String app_apth = StringUtil.getRootPath();

		for (int i = 0; i < id.length; i++) {
			PrintTmpl printTmpl= this.get(id[i]);
			String code=this.daoSupport.queryForString("select code from es_logi_company where name='"+printTmpl.getPrt_tmpl_title()+"'");
			FileUtil.delete(app_apth+"\\shop\\admin\\printtpl\\express\\"+ code + ".html");
		}
		String ids = StringUtil.arrayToString(id, ",");
		this.daoSupport.execute("delete from es_print_tmpl where prt_tmpl_id in (" + ids + ")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="删除快递模板")
	public void delete(Integer[] id) {
		if(id== null  || id.length==0  ) return ;
		String ids = StringUtil.arrayToString(id, ",");
		this.daoSupport.execute("update es_print_tmpl set disabled = 'true' where prt_tmpl_id in (" + ids + ")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#edit(com.enation.app.shop.core.order.model.PrintTmpl)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="修改了名为${printTmpl.prt_tmpl_title}的快递模板信息")
	public void edit(PrintTmpl printTmpl) {
		try {
			String newTmpl_data = printTmpl.getPrt_tmpl_data();
			newTmpl_data= newTmpl_data.replace("LODOP.PRINT_INITA(0,0,2300,1250,\"Lodop page\");", "LODOP.NEWPAGEA();");
			newTmpl_data = this.replcePrintTmel(newTmpl_data, printTmpl.getBgimage());
			String code=this.daoSupport.queryForString("select code from es_logi_company where name='"+printTmpl.getPrt_tmpl_title()+"'");
			String app_apth = StringUtil.getRootPath();

			FileUtil.write(app_apth+"/shop/admin/printtpl/express/"+ code + ".html", newTmpl_data);
			this.daoSupport.update("es_print_tmpl", printTmpl, "prt_tmpl_id = " + printTmpl.getPrt_tmpl_id());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#list()
	 */
	@Override
	public List list() {
		return this.daoSupport.queryForList("select * from es_print_tmpl where disabled = 'false'", PrintTmpl.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#revert(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="还原快递模板")
	public void revert(Integer[] id) {
		if(id== null  || id.length==0  ) return ;
		String ids = StringUtil.arrayToString(id, ",");
		this.daoSupport.execute("update es_print_tmpl set disabled = 'false' where prt_tmpl_id in (" + ids + ")");

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#trash()
	 */
	@Override
	public List trash() {
		return this.daoSupport.queryForList("select * from es_print_tmpl where disabled = 'true'", PrintTmpl.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#get(int)
	 */
	@Override
	public PrintTmpl get(int prt_tmpl_id) {
		String app_apth = StringUtil.getRootPath();

		PrintTmpl printTmpl= this.daoSupport.queryForObject("select * from es_print_tmpl where prt_tmpl_id = ?", PrintTmpl.class, prt_tmpl_id);
		String code=this.daoSupport.queryForString("select code from es_logi_company where name='"+printTmpl.getPrt_tmpl_title()+"'");
		String lodopdata= FileUtil.read(app_apth+"/shop/admin/printtpl/express/"+code + ".html","" );
		String newTmpl_data = replcePrintTmelHTML(lodopdata);
		printTmpl.setPrt_tmpl_data(newTmpl_data);
		return printTmpl;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#listCanUse()
	 */
	@Override
	public List listCanUse() {
		return this.daoSupport.queryForList("select * from es_print_tmpl where disabled = 'false' and shortcut = 'true'", PrintTmpl.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPrintTmplManager#check(java.lang.String)
	 */
	@Override
	public boolean check(String title){
		if(this.daoSupport.queryForList("select * from es_print_tmpl where prt_tmpl_title=?", title).size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	private String replcePrintTmel(String newTmpl_data, String bgImage) {
		

		if (newTmpl_data.indexOf("1234567890") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "1234567890", "${order.ship_no!''}");
		}
		if (newTmpl_data.indexOf("订单编号") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "订单编号", "${order.sn!''}");
		}
		if (newTmpl_data.indexOf("发件人姓名") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人姓名", "${dlycenter.name!''}");
		}
		if (newTmpl_data.indexOf("发件人电话") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人电话", "${dlycenter.phone!''}");
		}
		if (newTmpl_data.indexOf("发件人手机号") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人手机号", "${dlycenter.cellphone!''}");
		}
		if (newTmpl_data.indexOf("发件人公司") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人公司", "${shop_name!''}");
		}
		if (newTmpl_data.indexOf("发件人-省") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人-省","${d_dly_province!''}");
		}
		if (newTmpl_data.indexOf("发件人-市") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人-市", "${d_dly_city!''}");
		}
		if (newTmpl_data.indexOf("发件人-区") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人-区", "${d_dly_region!''}");
		}
		if (newTmpl_data.indexOf("发件人地址") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人地址", "${dlycenter.address!''}");
		}
		if (newTmpl_data.indexOf("发件人-地区") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人-地区", "${dlycenter.address!''}");
		}
		if (newTmpl_data.indexOf("发件人邮编") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "发件人邮编", "${dlycenter.zip!''}");
		}
		if (newTmpl_data.indexOf("收件人姓名") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人姓名", "${order.ship_name!''}");
		}
		if (newTmpl_data.indexOf("收件人电话") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人电话", "${order.ship_tel!''}");
		}
		if (newTmpl_data.indexOf("收件人手机号") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人手机号","${order.ship_mobile!''}");
		}
		if (newTmpl_data.indexOf("收件人-省") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人-省","${o_ship_province!''}");
		}
		if (newTmpl_data.indexOf("收件人-市") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人-市", "${o_ship_city!''}");
		}
		if (newTmpl_data.indexOf("收件人-区") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人-区", "${o_ship_region!''}");
		}
		if (newTmpl_data.indexOf("收件人地址") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人地址", "${order.ship_addr!''}");
		}
		if (newTmpl_data.indexOf("收件人-地区") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人-地区", "${order.shipping_area!''}");
		}
		if (newTmpl_data.indexOf("收件人邮编") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "收件人邮编", "${order.ship_zip!''}");
		}
		if (newTmpl_data.indexOf("当日日期-年") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "当日日期-年", "${year!''}");
		}
		if (newTmpl_data.indexOf("当日日期-月") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "当日日期-月", "${month!''}");
		}
		if (newTmpl_data.indexOf("当日日期-日") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "当日日期-日", "${day!''}");
		}
		if (newTmpl_data.indexOf("订单总金额") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "订单总金额", "${order.need_pay_money!''}");
		}
		if (newTmpl_data.indexOf("订单运费用金额") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "订单运费用金额", "${order.shipping_amount!''}");
		}
		if (newTmpl_data.indexOf("订单物品总重量") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "订单物品总重量","${order.weight!''}");
		}
		if (newTmpl_data.indexOf("订单-物品数量") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "订单-物品数量","${itemCount!''}");
		}
		
		if (newTmpl_data.indexOf("备注") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "备注", "${order.remark!''}");
		}
		if (newTmpl_data.indexOf("订单-送件时间") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "订单-送件时间", "${ship_time!''}");
		}
		if (newTmpl_data.indexOf("自定义内容") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "自定义内容", "${text!''}");
		}
		if (newTmpl_data.indexOf("宋体") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "宋体", "${songti!''}");
		}
		if (newTmpl_data.indexOf("黑体") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "黑体", "${heiti!''}");
		}
		return newTmpl_data;
	}
	
	private String replcePrintTmelHTML(String newTmpl_data) {
		if (newTmpl_data.indexOf("${order.ship_no!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.ship_no!''}", "${1234567890}");
		}
		if (newTmpl_data.indexOf("${order.sn!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.sn!''}", "订单编号");
		}
		if (newTmpl_data.indexOf("${dlycenter.name!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${dlycenter.name!''}", "发件人姓名");
		}
		if (newTmpl_data.indexOf("${dlycenter.phone!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${dlycenter.phone!''}", "发件人电话");
		}
		if (newTmpl_data.indexOf("${dlycenter.cellphone!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${dlycenter.cellphone!''}", "发件人手机号");
		}
		if (newTmpl_data.indexOf("${shop_name!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${shop_name!''}", "发件人公司");
		}
		if (newTmpl_data.indexOf("${d_dly_province!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${d_dly_province!''}","发件人-省");
		}
		if (newTmpl_data.indexOf("${d_dly_city!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${d_dly_city!''}", "发件人-市");
		}
		if (newTmpl_data.indexOf("${d_dly_region!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${d_dly_region!''}", "发件人-区");
		}
		if (newTmpl_data.indexOf("${dlycenter.address!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${dlycenter.address!''}", "发件人地址");
		}
		if (newTmpl_data.indexOf("${dlycenter.address!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${dlycenter.address!''}", "发件人-地区");
		}
		if (newTmpl_data.indexOf("${dlycenter.zip!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${dlycenter.zip!''}", "发件人邮编");
		}
		if (newTmpl_data.indexOf("${order.ship_name!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.ship_name!''}", "收件人姓名");
		}
		if (newTmpl_data.indexOf("${order.ship_tel!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.ship_tel!''}", "收件人电话");
		}
		if (newTmpl_data.indexOf("${order.ship_mobile!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.ship_mobile!''}","收件人手机号");
		}
		if (newTmpl_data.indexOf("${o_ship_province!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${o_ship_province!''}","收件人-省");
		}
		if (newTmpl_data.indexOf("${o_ship_city!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${o_ship_city!''}", "收件人-市");
		}
		if (newTmpl_data.indexOf("${o_ship_region!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${o_ship_region!''}", "收件人-区");
		}
		if (newTmpl_data.indexOf("${order.ship_addr!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.ship_addr!''}", "收件人地址");
		}
		if (newTmpl_data.indexOf("${order.shipping_area!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.shipping_area!''}", "收件人-地区");
		}
		if (newTmpl_data.indexOf("${order.ship_zip!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.ship_zip!''}", "收件人邮编");
		}
		if (newTmpl_data.indexOf("${year!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${year!''}", "当日日期-年");
		}
		if (newTmpl_data.indexOf("${month!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${month!''}", "当日日期-月");
		}
		if (newTmpl_data.indexOf("${day!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${day!''}", "当日日期-日");
		}
		if (newTmpl_data.indexOf("${order.need_pay_money!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.need_pay_money!''}", "订单总金额");
		}
		if (newTmpl_data.indexOf("${order.shipping_amount!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.shipping_amount!''}", "订单运费用金额");
		}
		if (newTmpl_data.indexOf("${order.weight!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.weight!''}",
					"订单物品总重量");
		}
		if (newTmpl_data.indexOf("${itemCount!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${itemCount!''}",
					"订单-物品数量");
		}
		if (newTmpl_data.indexOf("${order.remark!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.remark!''}", "备注");
		}
		if (newTmpl_data.indexOf("${order.ship_time!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${order.ship_time!''}", "订单-送件时间");
		}
		if (newTmpl_data.indexOf("${text!''}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${text!''}", "自定义内容");
		}
		if (newTmpl_data.indexOf("${songti}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${songti}", "宋体");
		}
		if (newTmpl_data.indexOf("${heiti}") != -1) {
			newTmpl_data = newTmpl_data(newTmpl_data, "${heiti}", "黑体");
		}
		return newTmpl_data;
	}
	
	
	private void printhtml(String newTmpl_data, String code,PrintTmpl printTmpl) {
		String app_apth = StringUtil.getRootPath();
		File filename = new File(app_apth+"/shop/admin/printtpl/express/"+ code + ".html");
		newTmpl_data = newTmpl_data.substring(newTmpl_data.lastIndexOf("LODOP.ADD_PRINT_SETUP_BKIMG"));
		newTmpl_data = "LODOP.SET_PRINT_PAGESIZE(1,"+printTmpl.getPrt_tmpl_width()+","+printTmpl.getPrt_tmpl_height()+",\"\");"+ newTmpl_data;
		newTmpl_data = "LODOP.NEWPAGEA();" + newTmpl_data;
		FileUtil.createFile(filename, filename.toString());
		FileUtil.write(app_apth+"/shop/admin/printtpl/express/"+ code + ".html", newTmpl_data);
	}
		

	private String newTmpl_data(String oldString, String replaceString,
			String newString) {
		int i;
		String result = "";
		do {
			i = oldString.indexOf(replaceString);
			if (i != -1) {
				result = oldString.substring(0, i);
				result = result + newString;
				result = result + oldString.substring(i + replaceString.length());
				oldString = result;
			}
		} while (i != -1);
		return oldString;
	}
}
