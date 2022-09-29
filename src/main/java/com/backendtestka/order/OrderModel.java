package com.backendtestka.order;

import com.backendtestka.customer.CustomerModel;
import com.backendtestka.helpers.ConstructableWithoutIdentifier;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
// I could actually implement an abstract class to apply the @Entity annotation and control to identifier-scrubbing...?
public class OrderModel implements ConstructableWithoutIdentifier<OrderModel> {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID id;
    @Column(nullable = false)
    private Timestamp orderDate;
    @Column(nullable = false)
    private Double totalPrice;
    @Column(nullable = false)
    private Integer noOfItems;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CustomerModel customer;

    public OrderModel() {
    }

    public OrderModel(UUID id, Timestamp orderDate, Double totalPrice, Integer noOfItems, CustomerModel customer) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.noOfItems = noOfItems;
        this.customer = customer;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(Integer noOfItems) {
        this.noOfItems = noOfItems;
    }

    @Override
    public OrderModel withoutUUID() {
        return new OrderModel(null, this.orderDate, this.totalPrice, this.noOfItems, this.customer);
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }
}
