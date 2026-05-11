package repository;

import java.util.List;
import java.util.Optional;

import model.Transaction;

public interface IIslemRepository {
    void save(Transaction transaction);

    Optional<Transaction> findById(String id);

    List<Transaction> findAll();

    void update(Transaction transaction);

    void deleteById(String id);

    boolean existsById(String id);
}
