package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.Transaction;

public class InMemoryIslemRepository implements IIslemRepository {
    private final Map<String, Transaction> transactions = new HashMap<>();

    @Override
    public void save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public void update(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public void deleteById(String id) {
        transactions.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return transactions.containsKey(id);
    }
}
