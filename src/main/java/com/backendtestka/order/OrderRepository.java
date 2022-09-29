package com.backendtestka.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderModel, UUID> {

    List<OrderModel> findByCustomerId(UUID customerId);

}