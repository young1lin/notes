package me.young1lin.spring.in.action.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 2:14 下午
 * @version 1.0
 */
public abstract class BaseController {

	/**
	 *  @see {@link AbstractApplicationContext} logger field
	 */
	protected final Logger log = LoggerFactory.getLogger(getClass());

}
