package service;

import model.CustDTO;
import model.Customer;
import model.Transaction;
import util.AlanBosException;

import java.util.ArrayList;
import java.util.List;

// Müşteri ve tedarikçi yönetimini sağlayan servis sınıfı.
public class CustomerService {

    // Yeni müşteri veya tedarikçi oluşturur.
    public Customer addCustomer(String customerId, String name, String taxId,
                                String email, String phone, Customer.CustomerType type) {
        if (name == null || name.isEmpty()) {
            throw new AlanBosException("Müşteri adı boş olamaz.");
        }
        if (email == null || !email.contains("@")) {
            throw new AlanBosException("Geçerli bir e-posta adresi giriniz.");
        }
        Customer c = new Customer(customerId, name, taxId, email, phone, type);
        System.out.println("Müşteri eklendi: " + name + " (" + type + ")");
        return c;
    }

    // ID'ye göre müşteriyi bulur, CustDTO olarak döndürür.
    public CustDTO getCustomerDetail(List<Customer> customers, String customerId) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(customerId)) {
                double bakiye = getCurrentBalance(customers, customerId);
                return new CustDTO(c.getCustomerId(), c.getName(), bakiye);
            }
        }
        throw new RuntimeException("Müşteri bulunamadı: " + customerId);
    }

    // Müşterinin güncel bakiyesini hesaplar (Gelir - Gider).
    public double getCurrentBalance(List<Customer> customers, String customerId) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(customerId)) {
                double bakiye = 0;
                for (Transaction t : c.getTransactions()) {
                    if ("GELIR".equalsIgnoreCase(t.getType())) {
                        bakiye += t.getAmount();
                    } else if ("GIDER".equalsIgnoreCase(t.getType())) {
                        bakiye -= t.getAmount();
                    }
                }
                return bakiye;
            }
        }
        throw new RuntimeException("Müşteri bulunamadı: " + customerId);
    }

    // Müşterinin toplam gelir, gider ve net bakiyesini özetler.
    public String getCustomerSummary(List<Customer> customers, String customerId) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(customerId)) {
                double toplamGelir = 0;
                double toplamGider = 0;
                for (Transaction t : c.getTransactions()) {
                    if ("GELIR".equalsIgnoreCase(t.getType())) {
                        toplamGelir += t.getAmount();
                    } else if ("GIDER".equalsIgnoreCase(t.getType())) {
                        toplamGider += t.getAmount();
                    }
                }
                double netBakiye = toplamGelir - toplamGider;
                return c.getName() + " | Gelir: " + toplamGelir
                        + " TL | Gider: " + toplamGider
                        + " TL | Net: " + netBakiye + " TL";
            }
        }
        throw new RuntimeException("Müşteri bulunamadı: " + customerId);
    }

    // Sistemdeki tüm müşterileri liste olarak döndürür.
    public List<Customer> listAllCustomers(List<Customer> customers) {
        return new ArrayList<>(customers);
    }
}
