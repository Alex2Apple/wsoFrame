package com.wang.frame.bean;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.wang.frame.config.FrameConstants;
import com.wang.frame.util.FrameHelperUtil;

/**
 * @author wangju
 *
 */
@Component
public class ProviderAnnotationBeanProcessor
		implements BeanDefinitionRegistryPostProcessor, BeanClassLoaderAware, EnvironmentAware, ResourceLoaderAware {

	private static final Logger LOGGER = Logger.getLogger(ProviderAnnotationBeanProcessor.class);

	private ClassLoader classLoader;
	private ResourceLoader resourceLoader;
	private Environment environment;

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		ProviderScanner scanner = new ProviderScanner(registry);
		scanner.setEnvironment(environment);
		scanner.setResourceLoader(resourceLoader);

		String pValue = FrameHelperUtil.readProperty(FrameConstants.APP_PROVIDER_SCAN_PACKAGES_KEY,
				FrameConstants.FRAME_PROPERTIES_FILE);
		Assert.notNull(pValue, "app.provider.scan.packages's value must be not null");
		Assert.hasText(pValue, "app.provider.scan.packages's must be not empty");

		String[] scanPackages = pValue.split(",");
		scanner.scan(scanPackages);

		return;
	}

	public class ProviderScanner extends ClassPathBeanDefinitionScanner {
		private BeanDefinitionRegistry registry;
		private String resourcePattern = "**/*.class";
		private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
				this.resourcePatternResolver);

		private String appName;
		private int port;

		public ProviderScanner(BeanDefinitionRegistry registry) {
			super(registry);
			this.registry = registry;
		}

		@Override
		public void registerDefaultFilters() {
			this.addIncludeFilter(new AnnotationTypeFilter(Provider.class));
		}

		@Override
		public Set<BeanDefinitionHolder> doScan(String... basePackages) {
			appName = FrameHelperUtil.readProperty(FrameConstants.APPLICATION_NAME_KEY,
					FrameConstants.FRAME_PROPERTIES_FILE);
			Assert.notNull(appName, "application.name's value must be not null");
			Assert.hasText(appName, "application.name's value must be not empty");

			String sPort = FrameHelperUtil.readProperty(FrameConstants.APPLICATION_PORT_KEY,
					FrameConstants.FRAME_PROPERTIES_FILE);
			Assert.notNull(sPort, "application.port's value must be not null");
			Assert.hasText(sPort, "application.port's value must be not empty");
			port = Integer.valueOf(sPort);

			Assert.notEmpty(basePackages, "At least one base package must be specified");
			Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<BeanDefinitionHolder>();

			for (String basePackage : basePackages) {
				Set<BeanDefinition> beanDefinitions = findCandidateComponents(basePackage);
				for (BeanDefinition beanDefinition : beanDefinitions) {
					Class<?> beanClass = ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), classLoader);
					Provider provider = AnnotationUtils.findAnnotation(beanClass, Provider.class);
					Class<?> service = parseProviderService(beanClass, provider);
					String beanName = generateBeanName(service.getName());
					registry.registerBeanDefinition(beanName, buildBeanDefinition(service, provider));

					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
					beanDefinitionHolders.add(definitionHolder);
				}
			}
			return beanDefinitionHolders;
		}

		/*
		 * 重载此方法, 用于扫描使用@Provider注解的接口, 生成RootBeanDefinition, 用于服务暴露. 没有直接使用父类的doScan方法,
		 * 一是不支持接口扫描来定义bean, 二是不需要生成两种bean
		 * 
		 * @see org.springframework.context.annotation.
		 * ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang
		 * .String)
		 */
		@Override
		public Set<BeanDefinition> findCandidateComponents(String basePackage) {
			Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
			try {
				String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
						+ resolveBasePackage(basePackage) + "/" + this.resourcePattern;
				Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
				for (Resource resource : resources) {
					if (resource.isReadable()) {
						try {
							MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
							if (isCandidateComponent(metadataReader)) {
								ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
								sbd.setResource(resource);
								sbd.setSource(resource);
								candidates.add(sbd);
							} else {
								LOGGER.debug("\"Identified candidate component class: \" + resource");
							}
						} catch (Throwable ex) {
							throw new BeanDefinitionStoreException(
									"Failed to read candidate component class: " + resource, ex);
						}
					} else {
						LOGGER.trace("Ignored because not readable: " + resource);
					}
				}
			} catch (IOException ex) {
				throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
			}
			return candidates;
		}

		private AbstractBeanDefinition buildBeanDefinition(Class<?> service, Provider provider) {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(ProviderBean.class)
					.addConstructorArgValue(provider).addPropertyValue("service", service)
					.addPropertyValue("id", provider.id()).addPropertyValue("appName", appName)
					.addPropertyValue("port", port);

			String version = provider.version();
			if (StringUtils.hasText(version)) {
				beanDefinitionBuilder.addPropertyValue("version", version);
			}

			String protocol = provider.protocol();
			if (StringUtils.hasText(protocol)) {
				beanDefinitionBuilder.addPropertyValue("protocol", protocol);
			}

			beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
			return beanDefinitionBuilder.getBeanDefinition();
		}

		private String generateBeanName(String name) {
			String[] p = name.split("\\.");
			return "vp_" + p[p.length - 1];
		}

		private Class<?> parseProviderService(Class<?> annotateServiceBeanClass, Provider provider) {
			Class<?> service = provider.service();
			Assert.notNull(service, "@Provider service() must be present!");
			Assert.isTrue(service.isInterface(), "@Provider service() is not an interface!");

			return service;
		}
	}
}
