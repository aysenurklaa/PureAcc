package repository;

import java.util.List;
import java.util.Optional;

import model.Company;

public interface IHesapRepository {
    void save(Company company);

    Optional<Company> findByTaxId(String taxId);

    List<Company> findAll();

    void update(Company company);

    void deleteByTaxId(String taxId);

    boolean existsByTaxId(String taxId);
}
