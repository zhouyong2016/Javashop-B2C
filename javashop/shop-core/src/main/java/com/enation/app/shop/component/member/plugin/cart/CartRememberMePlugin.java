/**
 * 
 */
package com.enation.app.shop.component.member.plugin.cart;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.plugin.IMemberLoginEvent;
import com.enation.app.shop.core.order.service.impl.CartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 购物车“记住我”插件
 * @author kingapex
 * @version [v1.0 2015年12月22日]
 * @since v6.0
 */

@Component
public class CartRememberMePlugin extends AutoRegisterPlugin implements IMemberLoginEvent {

	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private CartManager cartManager;


	/**
	 * 当会员登陆时更新cart表中的member_id字段，以便按会员记录
	 * 思路：
	 * 1、更改购物车为登录加入购物车商品的member_id
	 * 2、查询此用户购物车商品(List<Map<String, Object>>)循环此用户购物车商品，发现相同商品 标记mark 为 0,数量相加,当循环到标记处跳过
	 * 3、删除已标记购物车重复数据
	 * 4、修改购物车正确数量商品数量
	 */
	@Override
	public void onLogin(Member member, Long upLogintime) {

		String sessionid  = ThreadContextHolder.getHttpRequest().getSession().getId();
		//修改不相同商品的member_id
		this.daoSupport.execute("update es_cart set member_id=? where session_id=? and member_id is NULL ", member.getMember_id(),sessionid);

		List<Map<String, Object>> carts=this.daoSupport.queryForList("select * from es_cart WHERE member_id=? ", member.getMember_id());

		for (int i = 0; i < carts.size(); i++) { 
			for (int j = 0; j < carts.size(); j++) {
				if(carts.get(i).get("product_id").equals(carts.get(j).get("product_id") )&& carts.get(i).get("cart_id")!= carts.get(j).get("cart_id")){
					if(carts.get(i).get("mark") == null){
						carts.get(i).put("num", Integer.parseInt(carts.get(i).get("num").toString())+Integer.parseInt(carts.get(j).get("num").toString()));
						carts.get(j).put("mark", 0);
					}
				}
			}
		}
		for (int i = 0; i < carts.size(); i++) {
			if(carts.get(i).get("mark") != null){
				//删除已标记购物车重复数据
				String sql="delete from es_cart where cart_id = ?";
				this.daoSupport.execute(sql, carts.get(i).get("cart_id"));
			}else{
				//修改无标记购物车正确数据
				String sql="update es_cart set num=? where cart_id = ? ";
				this.daoSupport.execute(sql,carts.get(i).get("num"),carts.get(i).get("cart_id"));
			}	

		}

	}
}
