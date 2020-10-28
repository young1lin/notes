package me.young1lin.spring.in.action.repository;

import me.young1lin.spring.in.action.domain.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/7 10:20 上午
 */
@Repository
public interface OrderRepository extends CrudRepository<Order,Long> {
    /**
     * 获取投递到制定邮编的订单
     * @param zip
     * @return
     */
    List<Order> findByZip(String zip);

    /**
     * 查找投递到制定邮编在一定时间范围内的订单
     * @param zip
     * @param startDate
     * @param endDate
     * @return
     */
    List<Order> readOrdersByZipAndPlacedAtBetween(String zip, Date startDate,Date endDate);

    /**
     * 根据 deliveryTo 排序
     * @param city
     * @return
     */
    List<Order> findByCityOrderByCcNumber(String city);

    /**
     * 使用 sql 查询，这里是用的 hql 语句，直接从 from 开始
     * @return
     */
    //@Query("Order o where o.deliveryCity='Seattle'")
    //List<Order> readOrdersDeliveredInSeattle();
}
