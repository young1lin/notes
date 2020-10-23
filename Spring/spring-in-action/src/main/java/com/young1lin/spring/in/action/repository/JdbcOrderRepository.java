package com.young1lin.spring.in.action.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.young1lin.spring.in.action.domain.Order;
import com.young1lin.spring.in.action.domain.Taco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/7 9:45 上午
 */
@Repository
public class JdbcOrderRepository {

    private SimpleJdbcInsert orderInsert;

    private SimpleJdbcInsert orderTacoInsert;

    private ObjectMapper objectMapper;

    @Autowired
    public JdbcOrderRepository(JdbcTemplate jdbc) {
        this.orderInsert = new SimpleJdbcInsert(jdbc).withTableName("Taco_Order")
                .usingGeneratedKeyColumns("id");
        this.orderTacoInsert = new SimpleJdbcInsert(jdbc).withTableName("Taco_Order_Tacos");
        this.objectMapper = new ObjectMapper();
    }

    public Order save (Order order){
        order.setPlacedAt(new Date());
        long orderId = saveOrderDetails(order);
        order.setId(orderId);
        List<Taco> tacos = order.getTacos();
        for (Taco taco : tacos){
            saveTacoToOrder(taco,orderId);
        }
        return order;
    }

    private long saveOrderDetails(Order order) {
        Map<String,Object> values = objectMapper.convertValue(order, Map.class);
        values.put("placeAt",order.getPlacedAt());
        return orderInsert.executeAndReturnKey(values).longValue();
    }

    private void saveTacoToOrder(Taco taco, long orderId) {
        HashMap<String, Object> values = new HashMap<>(12);
        values.put("tacoOrder",orderId);
        values.put("taco",taco.getId());
        orderTacoInsert.execute(values);
    }
}
