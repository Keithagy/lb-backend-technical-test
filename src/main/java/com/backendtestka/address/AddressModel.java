package com.backendtestka.address;

import com.backendtestka.customer.CustomerModel;
import org.hibernate.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class AddressModel {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id")
    private CustomerModel customer;
    @Column(length = 150, nullable = false)
    private String address1;
    @Column(length = 150, nullable = true)
    private String address2;
    @Column(length = 15, nullable = false)
    private String postalCode; // weird edge cases if postalCode stored as int
    @Column(length = 100, nullable = true)
    private String country;

    public AddressModel() {
    }

    public AddressModel(UUID id, CustomerModel customer, String address1, String address2, String postalCode,
                        String country) {
        this.id = id;
        this.customer = customer;
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.country = country;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
