package com.wang.frame.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.wang.frame.config.FrameConstants;
import com.wang.frame.util.FrameHelperUtil;

/**
 * @author wangju
 *
 */
@Component
public class ReferenceAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private static final Logger LOGGER = Logger.getLogger(ReferenceAnnotationBeanPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		String pValue = FrameHelperUtil.readProperty(FrameConstants.APP_REFERENCE_CONFIG_KEY,
				FrameConstants.FRAME_PROPERTIES_FILE);

		// 没有配置或者应用不引用外部接口, 按后者处理
		if (pValue == null) {
			return;
		}

		try {
			Class<?> clazz = Class.forName(pValue.trim());
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Referencer[] annotations = field.getAnnotationsByType(Referencer.class);
				if (annotations.length == 0) {
					continue;
				}

				Referencer referencer = annotations[0];

				if (referencer.generic()) {
					// TODO
					continue;
				}

				String serviceName = referencer.service();
				Assert.hasText(serviceName, "@Referencer service() must not be empty");

				Class<?> service = Class.forName(serviceName);
				Assert.isTrue(service.isInterface(), "@Referencer service() is not a interface");

				BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
						.rootBeanDefinition(ReferencerBean.class).addConstructorArgValue(referencer)
						.addPropertyValue("serviceName", serviceName).addPropertyValue("service", service);

				String id = referencer.id();
				if (StringUtils.hasText(id)) {
					beanDefinitionBuilder.addPropertyValue("id", id);
				}

				String version = referencer.version();
				if (StringUtils.hasText(version)) {
					beanDefinitionBuilder.addPropertyValue("version", version);
				}

				String[] filters = referencer.filters();
				List<String> list = new ArrayList<>();
				for (String filter : filters) {
					if (StringUtils.hasText(filter)) {
						list.add(filter);
					}
				}
				beanDefinitionBuilder.addPropertyValue("filters", list);

				beanDefinitionBuilder.addPropertyValue("timeout", referencer.timeout());
				beanDefinitionBuilder.addPropertyValue("force", referencer.force());
				beanDefinitionBuilder.addPropertyValue("generic", referencer.generic());
				beanDefinitionBuilder.addPropertyValue("lazy", referencer.lazy());
				beanDefinitionBuilder.setLazyInit(false);
				beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);

				String[] s = serviceName.split("\\.");
				String beanName = "vr_" + s[s.length - 1];
				registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error(String.format("the class #%s# not found specified by #%s#", pValue,
					FrameConstants.APP_REFERENCE_CONFIG_KEY), e);
			throw new FatalBeanException("Can't create reference bean");
		}
	}

}
