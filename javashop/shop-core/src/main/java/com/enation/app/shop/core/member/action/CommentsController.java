package com.enation.app.shop.core.member.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 产品评论管理
 * 
 * @author Dawei
 * @version2.0  wangxin 2016-2-26; 6.0版本改造
 */


@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/comments")
public class CommentsController extends GridController {
	@Autowired
	private IMemberCommentManager memberCommentManager;
	@Autowired
	private IMemberPointManger memberPointManger;
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private IGoodsManager goodsManager;

	/**
	 * 评论(咨询)列表
	 * @author LiFenLong 
	 * @param type 状态,2为咨询,1为评论,Integer
	 * @return 评论、咨询列表页面
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Integer type) {
	ModelAndView view =this.getGridModelAndView();
		if (type==2) {
			view.setViewName("/shop/admin/comments/gm_list");
		} else {
			view.setViewName("/shop/admin/comments/list");
		}
		return view;
	}
	
	/**
	 * 评论(咨询)列表json
	 * @param pageNo 分页页数,Integer
	 * @param pageSize  每页分页的数量,Integer
	 * @param type 状态,2为咨询,1为评论,Integer
	 * @return  评论、咨询列表json
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(Integer type) {
		webpage = memberCommentManager.getAllComments(getPage(), getPageSize(), type);
		return JsonResultUtil.getGridJson(webpage);

	}
	
	/**
	 * 根据会员id获取评论（咨询）列表json
	 * @param type 状态
	 * @param member_id 会员id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json-by-member")
	public GridJsonResult listJsonByMember(Integer type,Integer member_id){
		webpage =memberCommentManager.getMemberComments(getPage(), getPageSize(), type, member_id);
		return JsonResultUtil.getGridJson(webpage);
	}
	/**
	 * 查看所有带状态的评论或问答
	 * @param pageNo 分页页数,Integer
	 * @param pageSize  每页分页的数量,Integer
	 * @param type 状态,2为咨询,1为评论,Integer
	 * @param status 回复评论
	 * @return 评论列表
	 */
	@RequestMapping(value="/msg-list")
	 public String msgList(Integer type,Integer status){
	 this.webpage = memberCommentManager.getCommentsByStatus(getPage(), getPageSize(), type, status);
	 return "/shop/admin/comments/list";
	 }
	/**
	 * 查看评论、咨询详细
	 * @param commentId 评论、咨询Id,Integer
	 * @param memberComment 会员评论对象,MemberComment
	 * @return 查看评论、咨询详细页面
	 */
	@RequestMapping(value="/detail")
	public ModelAndView detail(Integer commentId) {
		ModelAndView view = new ModelAndView();
		MemberComment memberComment = this.memberCommentManager.get(commentId);
		if (memberComment.getMember_id() != null&& memberComment.getMember_id().intValue() != 0) {
			Member member = memberManager.get(memberComment.getMember_id());
			view.addObject("member",member);
		}
		if (StringUtil.isEmpty(memberComment.getImg())) {
			memberComment.setImgPath(StaticResourcesUtil.convertToUrl(memberComment.getImg()));
		}
		
		//获取商品评论相册 add by DMRain 2016-5-9
		List<Map> list = this.memberCommentManager.getCommentGallery(commentId);
		List galleryList = new ArrayList();
		if (list != null) {
			for (Map l : list) {
				l.put("original", StaticResourcesUtil.convertToUrl(l.get("original").toString()));
				galleryList.add(l);
			}
		}
		
		view.addObject("galleryList", galleryList);
		view.addObject("memberComment",memberComment);
		view.setViewName("/shop/admin/comments/detail");
		return view;
	}

	/**
	 * 回复
	 * @param reply 回复内容,String
	 * @param commentId 评论、咨询Id
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/add")
	public JsonResult add(String reply,int commentId) {
		
		try {
			if (StringUtil.isEmpty(reply)) {
				return JsonResultUtil.getErrorJson("回复不能为空！");
			}
			MemberComment dbMemberComment = memberCommentManager.get(commentId);
			if (dbMemberComment == null) {
				return JsonResultUtil.getErrorJson("此评论或咨询不存在！");
			}
			dbMemberComment.setReply(reply);
			dbMemberComment.setReplystatus(1);
			dbMemberComment.setReplytime(DateUtil.getDateline());
			memberCommentManager.update(dbMemberComment);
			return JsonResultUtil.getSuccessJson("回复成功");
		} catch (Exception e) {
			//e.printStackTrace();
			return JsonResultUtil.getErrorJson("出现错误");
		}
		
	}
	/**
	 * 删除
	 * @param comment_id 评论、咨询Id数组,Integer[]
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] comment_id) {
		
		try {
			memberCommentManager.delete(comment_id);
			return JsonResultUtil.getSuccessJson("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("操作失败");	
		}
	}
	/**
	 * 评论、咨询不通过
	 * @param commentId 评论、咨询Id,Integer
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/hide")
	public JsonResult hide(int commentId) {
		try {
			MemberComment memberComment = memberCommentManager.get(commentId);
			memberComment.setStatus(2);
			memberCommentManager.update(memberComment);
			return JsonResultUtil.getSuccessJson("操作成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("操作失败");
		}
	}
	/**
	 * 评论、咨询通过
	 * @param commentId 评论、咨询Id,Integer
	 * @param cat_id 商品分类,Integer
	 * @param point 积分,Integer
	 * @param reson 评论类型,图片、文字,String
	 * @param mp 获取积分值,Integer 
	 * @return  json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/show")
	public JsonResult show(int commentId) {
		try {
			MemberComment memberComment = memberCommentManager.get(commentId);
			boolean isFirst = false;

			// 首次评论额外50积分
			if (memberCommentManager.getGoodsCommentsCount(memberComment
					.getGoods_id()) == 0&&memberComment.getType()!=2) {
				isFirst = true;
			}

			memberComment.setStatus(1);
			memberCommentManager.update(memberComment);

			// 清除浏览器缓存
			Map goods = goodsManager.get(memberComment.getGoods_id());
			if (goods != null) {
				String url = "";
				if (goods.get("cat_id") != null) {
					switch (StringUtil.toInt(goods.get("cat_id").toString())) {
					case 1:
					case 2:
						url = "yxgoods";
						break;
					case 3:
					case 4:
					case 12:
					case 18:
						url = "jkgoods";
						break;
					case 6:
						url = "jpgoods";
						break;
					case 5:
					case 7:
					case 8:
					case 9:
						url = "goods";
						break;
					case 19:
						url = "gngoods";
						break;
					}
				}
//				HttpCacheManager.updateUrlModified("/" + url + "-"
//						+ memberComment.getGoods_id() + ".html");
			}

			/**
			 * ------------ 增加会员积分 ------------
			 */
			// 判断是否是带图片的评论
			String reson ="文字评论";
			String type = IMemberPointManger.TYPE_COMMENT;
			if (!StringUtil.isEmpty(memberComment.getImg())) {
				type = IMemberPointManger.TYPE_COMMENT_IMG;
				reson="上传图片评论";
			}

			if (memberPointManger.checkIsOpen(type)) {
				if (memberComment.getMember_id() != null && memberComment.getMember_id().intValue() != 0&&memberComment.getType()!=2) {
					int point = memberPointManger.getItemPoint(type + "_num");
					int mp = memberPointManger.getItemPoint(type + "_num_mp");
					//这里错误的逻辑 进行修改  by chopper 11-24
//					memberPointManger.add(UserConext.getCurrentMember(), point, reson, null, mp);
					memberPointManger.add(memberManager.get(memberComment.getMember_id()), point, reson, null, 0,0);
					memberPointManger.add(memberManager.get(memberComment.getMember_id()), 0, reson, null, mp,1);
				 
				}
			}

			// 首次评论额外50积分
			if (isFirst) {
				if (memberPointManger
						.checkIsOpen(IMemberPointManger.TYPE_FIRST_COMMENT)) {
					int point = memberPointManger
							.getItemPoint(IMemberPointManger.TYPE_FIRST_COMMENT
									+ "_num");
					int mp = memberPointManger
							.getItemPoint(IMemberPointManger.TYPE_FIRST_COMMENT
									+ "_num_mp");
					memberPointManger.add(memberManager.get(memberComment.getMember_id()), point,
							"首次评论", null, 0,0);
					memberPointManger.add(memberManager.get(memberComment.getMember_id()), 0,
							"首次评论", null, mp,1);
				}
			}
			return JsonResultUtil.getSuccessJson("操作成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("操作失败");
		}
	}

	/**
	 * 删除一条信息
	 * @param commentId 评论、咨询Id
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/deletealone")
	public JsonResult deletealone(int commentId) {
		MemberComment memberComment = memberCommentManager.get(commentId);
		if (memberComment != null) {
			memberCommentManager.deletealone(commentId);
		}
		return JsonResultUtil.getSuccessJson("删除成功");
	}

	/**
	 * 查看评论和咨询详细
	 * add by DMRain 2016-4-20
	 * @return
	 */
	@RequestMapping(value="/look-detail")
	public ModelAndView lookDetail(Integer commentId, MemberComment memberComment){
		ModelAndView view = this.getGridModelAndView();
		memberComment = memberCommentManager.get(commentId);
		if (memberComment.getMember_id() != null && memberComment.getMember_id().intValue() != 0) {
			view.addObject("member", memberManager.get(memberComment.getMember_id()));
		}
		if (StringUtil.isEmpty(memberComment.getImg())) {
			memberComment.setImgPath(StaticResourcesUtil.convertToUrl(memberComment.getImg()));
		}
		List<Map> list = this.memberCommentManager.getCommentGallery(commentId);
		List galleryList = new ArrayList();
		if (list != null) {
			for (Map l : list) {
				l.put("original", StaticResourcesUtil.convertToUrl(l.get("original").toString()));
				galleryList.add(l);
			}
		}
		view.addObject("galleryList", galleryList);
		view.addObject("memberComment", memberComment);
		view.setViewName("/shop/admin/comments/look_detail");
		return view;
	}
	

}
