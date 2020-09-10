package com.demo.whereby.service.implementation;

import com.demo.whereby.entity.Orders;
import com.demo.whereby.repository.OrderRepository;
import com.demo.whereby.service.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public Orders save(Orders order) {
        return orderRepository.save(order);
    }
}
