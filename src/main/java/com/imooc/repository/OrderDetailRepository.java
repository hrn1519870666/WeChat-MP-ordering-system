package com.imooc.repository;

import com.imooc.dataobject.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    // 思考查询订单详情的过程：先从order_master表查出订单id，再根据订单id到order_detail表查询
    // 所以需要自定义一个方法，根据orderId查询OrderDetail，查出的OrderDetail有多个，所以返回类型是List
    List<OrderDetail> findByOrderId(String orderId);
}
