package com.backendtestka.customer;

import com.backendtestka.auth.config.JwtTokenUtil;
import com.backendtestka.helpers.Confirmation;
import com.backendtestka.helpers.InvalidAccountIdException;
import com.backendtestka.helpers.SQLResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
public class CustomerController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers(@RequestHeader(name = "Authorization", required = false) String token) {

        try {
            if (!jwtTokenUtil.getAdminStatusFromToken(token)) {
                return new ResponseEntity<>(Confirmation.failure("Non-admin monkey", null), HttpStatus.FORBIDDEN);
            }
        } catch (InvalidAccountIdException e) {
            return new ResponseEntity<>("Faulty Token", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final List<CustomerModel> customerModelList = customerService.getAllCustomers();
        return ResponseEntity.ok(customerModelList);
    }

    @PostMapping("/customers")
    public ResponseEntity<?> addCustomer(@RequestBody CustomerModel newCustomerModel,
                                         @RequestHeader(name = "Authorization", required =
                                                 false) String token) {
        try {
            if (!jwtTokenUtil.getAdminStatusFromToken(token)) {
                return new ResponseEntity<>(Confirmation.failure("Non-admin monkey", null), HttpStatus.FORBIDDEN);
            }
        } catch (InvalidAccountIdException e) {
            return new ResponseEntity<>("Faulty Token", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final CustomerModel persistedCustomerModel = customerService.addCustomer(newCustomerModel).withoutUUID();
        return ResponseEntity.ok(Confirmation.successful("New CustomerModel Created", persistedCustomerModel));

    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id, @RequestHeader(name =
            "Authorization", required = false) String token) {
        try {
            if (!jwtTokenUtil.getAdminStatusFromToken(token)) {
                return new ResponseEntity<>(Confirmation.failure("Non-admin monkey", null), HttpStatus.FORBIDDEN);
            }
        } catch (InvalidAccountIdException e) {
            return new ResponseEntity<>("Faulty Token", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            final CustomerModel deletedCustomerModel = customerService.deleteCustomerAtId(id).withoutUUID();
            return ResponseEntity.ok(Confirmation.successful("Customer Deleted", deletedCustomerModel));
        } catch (SQLException e) {
            if (e.getClass() == SQLResourceNotFoundException.class) {
                return new ResponseEntity<>(Confirmation.failure("No data for the specified identifier.", null),
                                            HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(Confirmation.failure("Unidentified network error", null),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
