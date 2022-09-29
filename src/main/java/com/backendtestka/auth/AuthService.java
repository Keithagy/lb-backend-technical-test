package com.backendtestka.auth;

import com.backendtestka.customer.CustomerModel;
import com.backendtestka.customer.CustomerService;
import com.backendtestka.helpers.InvalidAccountIdException;
import com.backendtestka.helpers.PasswordEmptyException;
import com.backendtestka.helpers.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    AccountModelRepository authRepository;

    @Autowired
    CustomerService customerService;

    public final void createNewUser(AccountModel newAccount) throws UserAlreadyExistsException, PasswordEmptyException {
        System.out.println(newAccount.getUsername());

        // Check that user does not already exist
        if (authRepository.existsByUsername(newAccount.getUsername())) {
            System.out.println(newAccount.getUsername());
            throw new UserAlreadyExistsException();
        }

        // Check that password is not empty string
        if (newAccount.getPassword().isEmpty()) {
            throw new PasswordEmptyException();
        }
        newAccount.setPassword(BCrypt.hashpw(newAccount.getPassword(), BCrypt.gensalt()));
        final CustomerModel createdCustomer = customerService.addCustomer(newAccount.getCustomer());
        newAccount.getCustomer().setId(createdCustomer.getId());
        authRepository.save(newAccount);
    }

    public AccountModel loadAccountById(String accountId) throws InvalidAccountIdException {
        return authRepository.findById(UUID.fromString(accountId)).orElseThrow(InvalidAccountIdException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AccountModel user = authRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        final ArrayList<GrantedAuthority> roles = new ArrayList<>();
        System.out.println("user.getAdmin()");
        System.out.println(user.getAdmin());
        if (user.getAdmin()) {
            roles.add(new SimpleGrantedAuthority("ADMIN"));
        }
        System.out.println(roles);

        return new User(user.getId().toString(), user.getPassword(),
                        roles);
    }

    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {

        AccountModel user =
                authRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with userId: " + userId));
        if (user == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }
        final ArrayList<GrantedAuthority> roles = new ArrayList<>();
        System.out.println("user.getAdmin()");
        System.out.println(user.getAdmin());
        if (user.getAdmin()) {
            roles.add(new SimpleGrantedAuthority("ADMIN"));
        }
        System.out.println(roles);

        return new User(user.getId().toString(), user.getPassword(),
                        roles);
    }

}
