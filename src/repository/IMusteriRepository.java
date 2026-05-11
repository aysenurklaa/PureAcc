package repository;

import java.util.List;
import java.util.Optional;

import model.Customer;

public interface IMusteriRepository {
    void save(Customer customer);

    Optional<Customer> findByCustomerId(String customerId);

    List<Customer> findAll();

    void update(Customer customer);

    void deleteByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);
}
