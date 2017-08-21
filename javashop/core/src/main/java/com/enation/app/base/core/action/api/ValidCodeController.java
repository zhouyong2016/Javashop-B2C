package com.enation.app.base.core.action.api;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 验证码生成controller
 * 
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月6日下午5:28:41
 */
@Controller
@RequestMapping("/api/validcode")
public class ValidCodeController {
	
	/**
	 * 生成验证码
	 * @param req HttpServletRequest
	 * @param resp HttpServletResponse
	 * @return null ,实际输出为image io
	 * @throws IOException
	 */
    @RequestMapping(value="/create")
	public ModelAndView create(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		ThreadContextHolder.setHttpRequest(req);
		ThreadContextHolder.setHttpResponse(resp);
		ThreadContextHolder.setSession(req.getSession());
		
		// 在内存中创建图象
				int width = 160, height = 80;
				resp.addHeader("Cache-Control", "no-cache");
				resp.addHeader("Cache-Control", "no-store");
				resp.addHeader("Cache-Control", "must-revalidate");
				resp.setHeader("Pragma", "no-cache");
				resp.setDateHeader("Expires", -1);

				String vtype = "";

				if (req.getParameter("vtype") != null) {
					vtype = req.getParameter("vtype");
				}
				
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


				// 获取图形上下文
				Graphics g = image.getGraphics();

				// 生成随机类
				Random random = new Random();

				// 设定背景色
				g.setColor(getRandColor(200, 250));
				g.fillRect(0, 0, width, height);

				// 设定字体
				g.setFont(this.getFont());

				// 画边框
				// g.setColor(new Color());
				// g.drawRect(0,0,width-1,height-1);

				// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
				g.setColor(getRandColor(160, 200));
				for (int i = 0; i < 155; i++) {
					int x = random.nextInt(width);
					int y = random.nextInt(height);
					int xl = random.nextInt(12);
					int yl = random.nextInt(12);
					g.drawLine(x, y, x + xl, y + yl);
				}

				// 取随机产生的认证码(4位数字和字母混合)
//				String sRand = "";
//				for (int i = 0; i < 4; i++) {
//					String rand = null;
//					int nu = random.nextInt(10);
//					if (nu <= 3) {
//						rand = String.valueOf((char) (random.nextInt(9) + 48));
//					} else if (nu > 3 && nu <= 7) {
//						rand = String.valueOf((char) (random.nextInt(25) + 65));
//					} else {
//						rand = String.valueOf((char) (random.nextInt(25) + 97));
//					}
//					sRand += rand;
//					// 将认证码显示到图象中
//					g.setColor(new Color(30 + random.nextInt(80), 30 + random.nextInt(80), 30 + random.nextInt(80)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
//					g.drawString(rand, 15 * i + 4, 16);
//				}

		        String sRand = "";
		        int car = captchars.length - 1;
		        for (int i = 0; i < 4; i++) {
		        	
		            String rand = "" + captchars[generator.nextInt(car) + 1];
		            
		            //测试模式随机数都是1
		            if(SystemSetting.getTest_mode()==1){
		            	rand="1";
		            }
		            sRand += rand;
		            g.setColor(new Color(30 + random.nextInt(80), 30 + random.nextInt(80), 30 + random.nextInt(80)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
					g.drawString(rand, 30 * i + 20, 58);
		        }
		        
		        //System.out.println(" put code ->"+ sRand);
				ThreadContextHolder.getSession().setAttribute(SESSION_VALID_CODE + vtype, sRand);
				//System.out.println("sessionId - >"+ThreadContextHolder.getSession().getId()+"  put code ->"+ sRand);

				// 图象生效
				g.dispose();
				// 输出图象到页面
				ImageIO.write(image, "JPEG", resp.getOutputStream());
				resp.getOutputStream().flush();
				resp.getOutputStream().close();
				
				return null;
	}
	
	
	public static final String SESSION_VALID_CODE = "valid_code";

	private Random generator = new Random();

	private static char[] captchars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'm',
			'n', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6' , '7','8' };

	/**
	 * 产生随机字体
	 * 
	 * @return
	 */
	private Font getFont() {
		int size =55;
		Random random = new Random();
		Font font[] = new Font[5];
		font[0] = new Font("Ravie", Font.PLAIN, size);
		font[1] = new Font("Antique Olive Compact", Font.PLAIN, size);
		font[2] = new Font("Forte", Font.PLAIN, size);
		font[3] = new Font("Wide Latin", Font.PLAIN, size);
		font[4] = new Font("Gill Sans Ultra Bold", Font.PLAIN, size);
		return font[random.nextInt(5)];
	}

	/**
	 * 随机产生定义的颜色
	 * 
	 * @return
	 */
	private Color getRandColor() {
		Random random = new Random();
		Color color[] = new Color[10];
		color[0] = new Color(32, 158, 25);
		color[1] = new Color(218, 42, 19);
		color[2] = new Color(31, 75, 208);
		return color[random.nextInt(3)];
	}

	protected Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
	
}
