package com.enation.app.shop.core.goods.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPriceManager;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.JsonResultUtil;

@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/member-price")
public class MemberPriceController  {
	
	@Autowired
	private IMemberLvManager memberLvManager;
	
	@Autowired
	private IMemberPriceManager memberPriceManager;
	
	@ResponseBody
	@RequestMapping("/list")
	public ModelAndView list(Double price){
		
		ModelAndView view =new ModelAndView();
		view.addObject("price", price);
		view.setViewName("/shop/admin/member/member_price_dlg");
		this.processLv(price,view);
		return view;
	}

	/**
	 * 显示会员价格input列表，在生成规格时填充所用
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/dis-lv-input")
	public ModelAndView disLvInput(Double price){
		ModelAndView view =new ModelAndView();
		view.addObject("price", price);
		view.setViewName("/shop/admin/member/member_price_input");
		this.processLv(price,view);
		return view;
	}
	
	@ResponseBody
	@RequestMapping("/get-lv-price-json")
	public Object getLvPriceJson(Integer goodsid){
		
		try{
			List priceList  = this.memberPriceManager.listPriceByGid(goodsid);
			Map map=new HashMap();
			map.put("result", 1);
			map.put("priceData", priceList);
			return map;
		}catch(RuntimeException e){
			Logger logger = Logger.getLogger(getClass());
			logger.error(e.getMessage(), e.fillInStackTrace());
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	private void processLv(Double price,ModelAndView view){
		List<MemberLv > lvList = memberLvManager.list();
		price= price==null?0:price;
		for(MemberLv lv:lvList){
			double discount = lv.getDiscount()/100.0;
			 
			double lvprice  = CurrencyUtil.mul(price, discount);
			 lv.setLvPrice( lvprice);
		}
		view.addObject("lvList",lvList);
	}
	
}
