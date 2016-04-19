package com.jxlai.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.util.logging.resources.logging;

public class HelloServiceImpl implements HelloService {

	private static Logger logger = LoggerFactory
			.getLogger(HelloServiceImpl.class);

	@Override
	public String sayHello(String name) {
		return String.format("hello %s", name);
	}

}
