package model;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
// Maps to the customer facet of UML Customer
public class Customer {

    private String customerId;
    private String name;
    private String taxId;
    private String email;
    private String phone;

    // class diyagraminda bu kisim belirtilmemistir —
    // hangi kaydın müşteri hangisinin tedarikçi olduğu tip alanından okunur
    public enum CustomerType {
        CUSTOMER,
        SUPPLIER
    }

    private CustomerType type;

    // UML ilişkileri — bu müşteriye ait faturalar ve işlemler
    private List<Invoice> invoices = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    public Customer() {}

    public Customer(String customerId, String name, String taxId,
                    String email, String phone, CustomerType type) {
        this.customerId = customerId;
        this.name = name;
        this.taxId = taxId;
        this.email = email;
        this.phone = phone;
        this.type = type;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public CustomerType getType() { return type; }
    public void setType(CustomerType type) { this.type = type; }

    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}

