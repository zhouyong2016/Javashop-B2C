package com.enation.app.base.test;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.SystemSetting;
import com.enation.framework.test.SpringTestSupport;

import net.sf.json.JSONObject;

/**
 * 图片上传测试类
 * 
 * @author Chopper
 * @version v1.0
 * @since v6.2.1 2016年12月28日 下午5:18:15
 *
 */
public class UploadImageTestError extends SpringTestSupport {

	/**
	 * 测试不登陆进行修改，是没有权限的
	 * 
	 * @throws Exception
	 */
	@Test
	public void uploadImage() throws Exception {
		// 进行图片上传
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/base/upload-image/upload-base64.do").param("base64Len", "33")
				.param("base64", "data:image/jpeg;base64,/9j/4AAQSk").param("type", "avatar"))
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1)).andReturn();

 		String resultStr= 	result.getResponse().getContentAsString();
 		JSONObject orderResult = JSONObject.fromObject(resultStr);
 		Map orderData =(Map) orderResult.get("data");
 		//静态资源路径问题，自己处理一波！！
 		File file = new File(SystemSetting.getStatic_server_path().replace("/statics", "")+orderData.get("img").toString()); 
 		//断言文件是否存在
		Assert.assertTrue(file.exists());

	}
}
