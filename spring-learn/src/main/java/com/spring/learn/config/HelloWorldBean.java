package com.spring.learn.config;

import org.springframework.stereotype.Repository;

@Repository
public class HelloWorldBean {

	public void hello(){
		System.out.println("Hello,spring - learn!");
	}
}
