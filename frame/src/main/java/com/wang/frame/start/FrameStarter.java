package com.wang.frame.start;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.wang.frame.config.FrameConstants;
import com.wang.frame.util.FrameHelperUtil;

public class FrameStarter {

	public static void main(String[] args) {
		String pValue = FrameHelperUtil.readProperty(FrameConstants.FRAME_SPRING_CONFIGURATION_KEY,
				FrameConstants.FRAME_PROPERTIES_FILE);

		// 没有配置或者应用不引用外部接口, 按后者处理
		if (pValue == null) {
			return;
		}
		AnnotationConfigApplicationContext applicationContext;
		try {
			applicationContext = new AnnotationConfigApplicationContext(Class.forName(pValue));
			applicationContext.start();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
