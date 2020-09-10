package com.demo.whereby.service.implementation;

import com.demo.whereby.entity.OrdersDtls;
import com.demo.whereby.repository.OrderDtlRepository;
import com.demo.whereby.service.interfaces.OrderDtlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDtlServiceImpl implements OrderDtlService {
    @Autowired
    private OrderDtlRepository orderDtlRepository;
    @Override
    public OrdersDtls save(OrdersDtls ordersDtls) {
        return orderDtlRepository.save(ordersDtls);
    }
}
