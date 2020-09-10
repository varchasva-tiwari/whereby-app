package com.demo.whereby.repository;

import com.demo.whereby.entity.OrdersDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDtlRepository extends JpaRepository<OrdersDtls , Integer> {
}
