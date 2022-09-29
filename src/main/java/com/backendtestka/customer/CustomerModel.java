package com.backendtestka.customer;

import com.backendtestka.helpers.ConstructableWithoutIdentifier;
import org.hibernate.annotations.UuidGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class CustomerModel implements ConstructableWithoutIdentifier<CustomerModel> {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID id;
    @Column(length = 50, nullable = false)
    private String name;
    @Column(length = 100, nullable = false)
    private String email;
    @Column(length = 15, nullable = false)
    private String contactNumber;

    public CustomerModel() {
    }

    public CustomerModel(UUID id, String name, String email, String contactNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
//        this.orders = orders;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public CustomerModel withoutUUID() {
        return new CustomerModel(null, this.name, this.email, this.contactNumber);
    }

//    public List<OrderModel> getOrders() {
//        return orders;
//    }
//
//    public void setOrders(List<OrderModel> orders) {
//        this.orders = orders;
//    }

    // to add one-one address relationship
    // to add one-one account relationship (for auth)
    // to add one-many order relationship
}
