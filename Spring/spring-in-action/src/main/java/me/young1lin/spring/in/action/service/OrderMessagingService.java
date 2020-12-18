package me.young1lin.spring.in.action.service;

import me.young1lin.spring.in.action.domain.Order;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 1:42 下午
 * @version 1.0
 */
public interface OrderMessagingService {

	/**
	 * broadcast Order info
	 * @param order the order of restaurant
	 */
	void sendOrder(Order order);

}
