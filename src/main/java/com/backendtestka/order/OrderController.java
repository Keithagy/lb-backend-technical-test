package com.backendtestka.order;

import com.backendtestka.auth.AuthService;
import com.backendtestka.auth.config.JwtTokenUtil;
import com.backendtestka.customer.CustomerModel;
import com.backendtestka.helpers.Confirmation;
import com.backendtestka.helpers.InvalidAccountIdException;
import com.backendtestka.helpers.SQLResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class OrderController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderModel>> getAllOrders() {
        // Admin only
        final List<OrderModel> orderModels = orderService.getAllOrders();
        return ResponseEntity.ok(orderModels);
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<List<OrderModel>> getOrdersByCustomerId(@PathVariable UUID customerId) {
        // Validate that request comes from this customer OR admin
        final List<OrderModel> ordersForCustomerId = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(ordersForCustomerId);
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<Confirmation<OrderModel>> updateOrderByOrderId(@PathVariable UUID orderId,
                                                                         @RequestBody OrderModel newOrderDetailsModel) throws SQLException {
        // Validate that request comes from order owner OR admin
        final OrderModel persistedOrderRecordModel = orderService.updateOrderByOrderId(orderId, newOrderDetailsModel);
        return ResponseEntity.ok(Confirmation.successful("OrderModel successfully updated", persistedOrderRecordModel));
    }

    @PostMapping("/orders")
    public ResponseEntity<Confirmation<?>> createNewOrder(@RequestHeader(name = "Authorization") String token,
                                                          @RequestBody OrderModel newOrderModel) {

        final boolean requestorIsAdmin;
        try {
            requestorIsAdmin = jwtTokenUtil.getAdminStatusFromToken(token);
        } catch (InvalidAccountIdException e) {
            return new ResponseEntity<>(Confirmation.failure("Token provided does not encode valid user.", null),
                                        HttpStatus.FORBIDDEN);
        }

        // If request comes from admin, admin must provide a valid customerId
        if (requestorIsAdmin) {
            if (newOrderModel.getCustomer().getId() == null || newOrderModel.getCustomer().getId().toString().isEmpty()) {
                return new ResponseEntity<>(Confirmation.failure("Admin user needs to provide a customer Id.",
                                                                 null),
                                            HttpStatus.BAD_REQUEST);
            }

            return ResponseEntity.ok(Confirmation.successful("New order added for customer" + newOrderModel.getCustomer().getId(),
                                                             orderService.addOrder(newOrderModel)));
        }

        // Else, order is created with customerId associated with the accountId encoded in the JWT
        final String customerId;

        try {
            customerId = jwtTokenUtil.getAccountFromToken(token).getCustomer().getId().toString();
        } catch (InvalidAccountIdException e) {
            return new ResponseEntity<>(Confirmation.failure("Token provided does not encode valid customer.", null),
                                        HttpStatus.FORBIDDEN);
        }

        final CustomerModel customerAssociation = new CustomerModel();
        customerAssociation.setId(UUID.fromString(customerId));
        newOrderModel.setCustomer(customerAssociation);
        final OrderModel persistedOrderModel = orderService.addOrder(newOrderModel).withoutUUID();
        return ResponseEntity.ok(Confirmation.successful("New OrderModel Created", persistedOrderModel));
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Confirmation<OrderModel>> deleteOrder(@PathVariable UUID orderId, @RequestHeader(name =
            "Authorization") String token) {


        // Validate that request comes from order owner OR admin
        // First check that request comes from order's owner
        try {
            final String customerIdAssociatedWithToken =
                    jwtTokenUtil.getAccountFromToken(token).getCustomer().getId().toString();
            final String customerIdAssociatedWithOrder =
                    orderService.getOrderById(orderId).getCustomer().getId().toString();

            // request does not come from order's owner, so check that request comes from admin
            if (!Objects.equals(customerIdAssociatedWithToken, customerIdAssociatedWithOrder) && !jwtTokenUtil.getAdminStatusFromToken(token)) {
                return new ResponseEntity<>(Confirmation.failure("Orders can only be deleted by their owners or an " +
                                                                         "admin.",
                                                                 null),
                                            HttpStatus.FORBIDDEN);
            }

        } catch (InvalidAccountIdException e) {
            return new ResponseEntity<>(Confirmation.failure("Token provided does not encode valid user.", null),
                                        HttpStatus.FORBIDDEN);
        } catch (SQLException e) {
            return new ResponseEntity<>(Confirmation.failure("Error retrieving the specified order.", null),
                                        HttpStatus.NOT_FOUND);
        }
        
        try {
            final OrderModel deletedOrderModel = orderService.deleteOrderAtId(orderId).withoutUUID();
            return ResponseEntity.ok(Confirmation.successful("OrderModel Deleted", deletedOrderModel));
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
