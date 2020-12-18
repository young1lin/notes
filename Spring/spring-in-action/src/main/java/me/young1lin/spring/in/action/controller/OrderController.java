package me.young1lin.spring.in.action.controller;

import me.young1lin.spring.in.action.domain.Order;
import me.young1lin.spring.in.action.service.OrderMessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Random;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 8:26 下午
 */
@Controller
@RequestMapping("/orders")
public class OrderController extends BaseController {

	private OrderMessagingService orderMessagingService;


	@Autowired
	public OrderController(
			@Qualifier("kafkaOrderMessagingServiceImpl") OrderMessagingService
					orderMessagingService) {
		this.orderMessagingService = orderMessagingService;
	}

	@GetMapping("/current")
	public String orderForm(Model model) {
		model.addAttribute("order", new Order());
		return "orderForm";
	}

	@PostMapping
	public String processOrder(@Valid Order order, Errors errors) {
		if (errors.hasErrors()) {
			return "orderForm";
		}
		log.info("Order submitted:[{}]", order);
		return "redirect:/";
	}

	@GetMapping("/message")
	@ResponseBody
	public String message() {
		Long id = new Random().nextLong();
		Order order = new Order();
		order.setId(id);
		order.setName(id + "张三");
		order.setZip("12221");
		order.setCity("Hangzhou");
		orderMessagingService.sendOrder(order);
		return "send message ok!";
	}

}
