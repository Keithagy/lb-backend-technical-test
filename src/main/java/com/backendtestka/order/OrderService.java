package com.backendtestka.order;

import com.backendtestka.helpers.SQLResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public final List<OrderModel> getAllOrders() {
        return orderRepository.findAll();
    }

    public final OrderModel getOrderById(UUID orderId) throws SQLException {
        return orderRepository.findById(orderId).orElseThrow(() -> new SQLResourceNotFoundException("OrderService" +
                                                                                                            ".getOrderById" +
                                                                                                            "(id: " + orderId + "): attempted to retrieve nonexistent" + " " + "order"));
    }

    public final List<OrderModel> getOrdersByCustomerId(UUID customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public final OrderModel updateOrderByOrderId(UUID id, OrderModel newOrderDetailsModel) throws SQLException {
        final OrderModel retrievedOrderModel =
                orderRepository.findById(id).orElseThrow(() -> new SQLResourceNotFoundException("OrderService" +
                                                                                                        ".updateOrderByOrderId(id: " + id + "): attempted to update nonexistent" + " " + "order"));

        if (newOrderDetailsModel.getNoOfItems() != null) {
            retrievedOrderModel.setNoOfItems(newOrderDetailsModel.getNoOfItems());
        }

        if (newOrderDetailsModel.getTotalPrice() != null) {
            retrievedOrderModel.setTotalPrice(newOrderDetailsModel.getTotalPrice());
        }

        // retrievedOrderModel has been modified per fields passed
        return orderRepository.save(retrievedOrderModel);
    }

    public final OrderModel addOrder(OrderModel newOrderModel) {
        return orderRepository.save(newOrderModel);
    }

    public final OrderModel deleteOrderAtId(UUID id) throws SQLException {
        // TODO: Need to cascade-delete shipments

        final OrderModel orderModelToDelete =
                orderRepository.findById(id).orElseThrow(() -> new SQLResourceNotFoundException("OrderService" +
                                                                                                        ".deleteOrderAtId(id: " + id + "): attempted to delete nonexistent" + " " + "order"));

        orderRepository.deleteById(id);
        return orderModelToDelete;
    }
}
