/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * Factory hook that allows for custom modification of new bean instances,
 * e.g. checking for marker interfaces or wrapping them with proxies.
 *
 * 工厂钩子,允许自定义修改新的bean实例,比如检查标记接口或者用动态代理包裹他们
 *
 * <p>ApplicationContexts can autodetect BeanPostProcessor beans in their
 * bean definitions and apply them to any beans subsequently created.
 * Plain bean factories allow for programmatic registration of post-processors,
 * applying to all beans created through this factory.
 *
 * ApplicationContexts 可以自动在bean定义中监测BeanPostProcessor接口的beans,然后在bean创建的时候应用接口方法.
 *
 * <p>Typically, post-processors that populate beans via marker interfaces
 * or the like will implement {@link #postProcessBeforeInitialization},
 * while post-processors that wrap beans with proxies will normally
 * implement {@link #postProcessAfterInitialization}.
 *
 * 通常,填充bean的后置处理器通过标记接口或者实现{@link #postProcessBeforeInitialization}接口
 * 而使用代理包装bean的后置处理通过实现{@link #postProcessAfterInitialization}接口
 *
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor 对接口进行扩展,在初始化之前以及属性填充或自动装配之前进行了扩充,建议框架内部使用。
 * @see DestructionAwareBeanPostProcessor bean销毁接口的扩展,在特定的bean类型上调用自定义销毁回调.
 * @see ConfigurableBeanFactory#addBeanPostProcessor  添加一个新的BeanPostProcessor,它将应用于该工厂创建的bean。在bean出厂配置期间调用。注意:此处提交的后处理器将按照注册的顺序应用；通过实现 {@link org.springframework.core.Ordered}接口表示的任何排序语义都将被忽略。请注意自动检测到的后处理器（例如，作为ApplicationContext中的bean）将始终以编程方式注册后的处理器。
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean
	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 *
	 * 在任何bean初始化回调(例如InitializingBean的{@code afterPropertiesSet} 或自定义的init-method)之前.
	 * 将此BeanPostProcessor应用于给定的新bean实例.该bean对象将已经属性值填充完成。
	 * 返回的bean实例可能是原始实例的包装,默认实现按原样返回给定的bean对象对象。
	 *
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
	 * instance and the objects created by the FactoryBean (as of Spring 2.0). The
	 * post-processor can decide whether to apply to either the FactoryBean or created
	 * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
	 * <p>This callback will also be invoked after a short-circuiting triggered by a
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
	 * in contrast to all other BeanPostProcessor callbacks.
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 *
	 * 在任何bean初始化回调(例如InitializingBean的{@code afterPropertiesSet} 或自定义的init-method)之后.
	 * 将此BeanPostProcessor应用于给定的新bean实例。
	 * 该bean将已经完成属性值填充。返回的bean实例可能是原始实例的包装。
	 * 对于FactoryBean,将同时为FactoryBean实例和由FactoryBean创建的对象（从Spring 2.0开始）调用此回调。
	 * 后处理器可以通过相应的{@code bean instanceof FactoryBean}检查来决定是应用到FactoryBean还是创建的对象，还是两者都应用。
	 * 与所有其他BeanPostProcessor回调相比,在由{@link InstantiationAwareBeanPostProcessor＃postProcessBeforeInstantiation}方法触发短路后,也会调用此回调。
	 * 默认实现按原样返回给定的bean。
	 *
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
