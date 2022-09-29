package com.backendtestka.customer;

import com.backendtestka.helpers.SQLResourceNotFoundException;
import com.backendtestka.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    public final List<CustomerModel> getAllCustomers() {
        return customerRepository.findAll();
    }

    public final CustomerModel addCustomer(CustomerModel newCustomerModel) {
        return customerRepository.save(newCustomerModel);
    }

    public final CustomerModel deleteCustomerAtId(UUID id) throws SQLException {
        // Need to cascade-delete all orders. Doing it this way probably violates atomicity though.
        // How would you write this to ensure single transaction?
        // For now I use the @OnDelete annotation on orderModel.
//        orderRepository.deleteAllByCustomerId(id);

        final CustomerModel customerModelToDelete =
                customerRepository.findById(id).orElseThrow(() -> new SQLResourceNotFoundException(
                        // lol my IDE reformat options are weird. What do you folks use?
                        "CustomerService.deleteCustomerAtId(id: " + id + "): attempted to delete " +
                                "from" + " nonexistent id"));

        customerRepository.deleteById(id);

        return customerModelToDelete;
    }
}
