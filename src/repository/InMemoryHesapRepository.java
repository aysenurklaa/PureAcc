package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.Company;

public class InMemoryHesapRepository implements IHesapRepository {
    private final Map<String, Company> companies = new HashMap<>();

    @Override
    public void save(Company company) {
        companies.put(company.getTaxId(), company);
    }

    @Override
    public Optional<Company> findByTaxId(String taxId) {
        return Optional.ofNullable(companies.get(taxId));
    }

    @Override
    public List<Company> findAll() {
        return new ArrayList<>(companies.values());
    }

    @Override
    public void update(Company company) {
        companies.put(company.getTaxId(), company);
    }

    @Override
    public void deleteByTaxId(String taxId) {
        companies.remove(taxId);
    }

    @Override
    public boolean existsByTaxId(String taxId) {
        return companies.containsKey(taxId);
    }
}
