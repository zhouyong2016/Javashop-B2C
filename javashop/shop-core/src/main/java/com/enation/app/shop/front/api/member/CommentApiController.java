package com.enation.app.shop.front.api.member;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.app.shop.core.member.model.MemberOrderItem;
import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.app.shop.core.member.service.IMemberOrderItemManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 评论api
 * 
 * @author kingapex
 * @version 2.0,2016-02-18 wangxin v60版本改造
 */

@Controller
@RequestMapping("/api/shop/commentApi")
public class CommentApiController {

	@Autowired
	private IGoodsManager goodsManager;
	@Autowired
	private IMemberCommentManager memberCommentManager;
	@Autowired
	private IMemberOrderItemManager memberOrderItemManager;

	/**
	 * 发表评论
	 * 
	 * @param goods_id:商品id,int型，必填项
	 * @param commenttype:评论类型，int型，必填项，可选值：1或2，1为评论，2为咨询。
	 * @param content:评论内容，String型，必填项。
	 * @param file:评论的图片，File类型，可选项。
	 * @param grade
	 *            :评分
	 * @return 返回json串 result 为1表示添加成功，0表示失败 ，int型 message 为提示信息
	 */
	@ResponseBody
	@RequestMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult add(Integer commenttype, Integer goods_id, String content, Integer grade) {

		MemberComment memberComment = new MemberComment();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int type = commenttype; // StringUtil.toInt(request.getParameter("commenttype"),0);
		// 判断是不是评论或咨询
		if (type != 1 && type != 2) {
			return JsonResultUtil.getErrorJson("系统参数错误！");
		}
		memberComment.setType(type);

		// int goods_id = StringUtil.toInt(request.getParameter("goods_id"),0);
		if (goodsManager.get(goods_id) == null) {
			return JsonResultUtil.getErrorJson("此商品不存在！");
		}
		memberComment.setGoods_id(goods_id);

		// String content = request.getParameter("content");
		if (StringUtil.isEmpty(content)) {
			return JsonResultUtil.getErrorJson("评论或咨询内容不能为空！");
		} else if (content.length() > 1000) {
			return JsonResultUtil.getErrorJson("请输入1000以内的内容！");
		}
		content = StringUtil.htmlDecode(content);
		memberComment.setContent(content);

		Member member = UserConext.getCurrentMember();
		if (type == 1) {
			if (member == null) {
				return JsonResultUtil.getErrorJson("只有登录且成功购买过此商品的用户才能发表评论！");

			}
			int buyCount = memberOrderItemManager.count(member.getMember_id(), goods_id);
			int commentCount = memberOrderItemManager.count(member.getMember_id(), goods_id, 1);
			if (buyCount <= 0) {
				// this.showErrorJson("只有成功购买过此商品的用户才能发表评论！");
				// return this.JSON_MESSAGE;
			}
			// if(commentCount >= buyCount){
			// this.showErrorJson("对不起，您已经评论过此商品！");
			// return this.JSON_MESSAGE;
			// }
			// int grade = StringUtil.toInt(request.getParameter("grade"),0);
			if (grade < 0 || grade > 5) {
				memberComment.setGrade(5);
			} else {
				memberComment.setGrade(grade);
			}
		} else {
			//判断是否登录，没有登录不能咨询
			if (member == null) {
				return JsonResultUtil.getErrorJson("只有登录才能商品咨询！");
			}
			memberComment.setImg(null);
			memberComment.setGrade(0);
		}
		memberComment.setMember_id(member == null ? 0 : member.getMember_id());
		memberComment.setDateline(System.currentTimeMillis() / 1000);
		memberComment.setIp(request.getRemoteHost());

		try {
			memberCommentManager.add(memberComment);
			// 更新为已经评论过此商品
			if (type == 1) {
				MemberOrderItem memberOrderItem = memberOrderItemManager.get(member.getMember_id(), goods_id, 0);
				if (memberOrderItem != null) {
					memberOrderItem.setComment_time(System.currentTimeMillis());
					memberOrderItem.setCommented(1);
					memberOrderItemManager.update(memberOrderItem);
				}
			}

			return JsonResultUtil.getSuccessJson("发表成功");

		} catch (RuntimeException e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error("发表评论出错", e);

			return JsonResultUtil.getErrorJson("发表评论出错" + e.getMessage());

		}
	}

	/**
	 * 修改会员评论
	 * 
	 * @param comment_id
	 *            评论Id
	 * @param grade
	 *            评级
	 * @param content
	 *            评论信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult update(int comment_id, int grade, String content) {

		try {
			MemberComment memberComment = new MemberComment();
			memberComment = this.memberCommentManager.get(comment_id);
			memberComment.setComment_id(comment_id);
			memberComment.setGrade(grade);
			memberComment.setContent(content);
			memberCommentManager.update(memberComment);
			return JsonResultUtil.getSuccessJson("更新成功");

		} catch (RuntimeException e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error("修改评论出错", e);
			return JsonResultUtil.getErrorJson("更新失败");
		}
	}

	/**
	 * 发表评论
	 * 
	 * @param goods_id:商品id,int型，必填项
	 * @param commenttype:评论类型，int型，必填项，
	 * @param content:评论内容，String型，必填项。
	 * @param product_id:货品id,int型，必填项
	 * @return 返回json串 result 为1表示添加成功，0表示失败 ，int型 message 为提示信息
	 */
	@ResponseBody
	@RequestMapping(value = "/addComment", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult addComment(Integer commenttype[],Integer product_id[], Integer goods_id[], String content[],Integer orderid) {
		try {
			MemberComment memberComment = new MemberComment();
	
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Integer contentotal = 0; //添加字段用于判断该商品是否被评论
			for(int i=0;i<goods_id.length;i++){
		
				memberComment.setType(commenttype[i]);
		
				Map goods = goodsManager.get(goods_id[i]);
				
				// 判断是否存在此商品
				if (goods == null) {
					return JsonResultUtil.getErrorJson("此商品不存在！");
				}
				memberComment.setGoods_id(goods_id[i]);
				memberComment.setProduct_id(product_id[i]);
		
				Integer contentcount = 0; //添加字段用于判断该货品是否被评论
				if(content.length>0){
					if (!(StringUtil.isEmpty(content[i])||"请在此处输入您的评论".equals(content[i]))) {
						contentcount++;
						contentotal++;
					} 
					if (content[i].length() > 1000) {
						return JsonResultUtil.getErrorJson("请输入1000以内的内容！");
					} 
					String contents = StringUtil.htmlDecode(content[i]);
					memberComment.setContent(contents);
				}
		
				Member member = UserConext.getCurrentMember();
				
				if (member == null) {
					return JsonResultUtil.getErrorJson("只有登录且成功购买过此商品的用户才能发表评论！");
				}
				memberComment.setStatus(0);
				String parameter = request.getParameter("grade_"+product_id[i]);
				Integer grade = StringUtil.toInt(parameter);	
				if (grade < 1 || grade > 3) {
					return JsonResultUtil.getErrorJson("请选择对商品的评价！");
				} else {
					memberComment.setGrade(grade);
				}
				
				memberComment.setMember_id(member == null ? 0 : member.getMember_id());
				memberComment.setDateline(System.currentTimeMillis() / 1000);
				memberComment.setIp(request.getRemoteHost());
	
				if(contentcount>0){
					memberCommentManager.add(memberComment);
					// 更新为已经评论过此商品
					
					MemberOrderItem memberOrderItem = memberOrderItemManager.getMemberOrderItem(orderid, product_id[i], 0);
					if (memberOrderItem != null) {
						memberOrderItem.setComment_time(System.currentTimeMillis());
						memberOrderItem.setCommented(1);
						memberOrderItemManager.updateComment(memberOrderItem);
					}
				}
			}
			//判断订单中是否有商品被评论；
			if(contentotal==0){
				return JsonResultUtil.getErrorJson("请至少评论一个商品！");
			}
			return JsonResultUtil.getSuccessJson("发表成功");
		} catch (RuntimeException e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error("发表评论出错", e);

			return JsonResultUtil.getErrorJson("发表评论出错" + e.getMessage());

		}
	}
	
}
