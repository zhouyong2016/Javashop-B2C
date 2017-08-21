package com.enation.framework.test;

import java.io.FileNotFoundException;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

public class EopJunit4ClassRunner extends SpringJUnit4ClassRunner {
	public EopJunit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}
	static {
		try {
			Log4jConfigurer.initLogging("classpath:spring_cfg/log4Junit.properties");
		} catch (FileNotFoundException ex) {
			System.err.println("Cannot Initialize log4j");
		}
	}


}
