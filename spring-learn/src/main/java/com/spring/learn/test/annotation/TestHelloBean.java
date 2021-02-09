package com.spring.learn.test.annotation;

import com.spring.learn.config.FirstConfig;
import com.spring.learn.config.HelloWorldBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestHelloBean {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FirstConfig.class);
		HelloWorldBean bean = context.getBean(HelloWorldBean.class);
		bean.hello();
	}

}
