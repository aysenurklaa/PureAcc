package model;

// Müşteri detayı döndürürken kullanılan veri taşıma objesi (DTO).
public class CustDTO {

    private String customerId;
    private String name;
    private double balance;

    public CustDTO(String customerId, String name, double balance) {
        this.customerId = customerId;
        this.name = name;
        this.balance = balance;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
}
