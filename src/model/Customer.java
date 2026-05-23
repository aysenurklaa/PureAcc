package model;

/**
 * Customer — müşteri veya tedarikçi cari hesabı.
 * customerId alanı, tabloda customer_code sütununa karşılık gelir.
 */
@SuppressWarnings("unused")
public class Customer {
    private long id;
    private String customerId;
    private String name;

    /** Hangi kaydın müşteri hangisinin tedarikçi olduğu tip alanından okunur. */
    public enum CustomerType {
        CUSTOMER,
        SUPPLIER
    }

    private CustomerType type;

    public Customer() {}

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }
}
