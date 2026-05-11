package model;

@SuppressWarnings("unused")
//Maps to the customer facet of UML Customer
public class Customer {
	private String customerId;
    private String name;
    private String taxId;
    private String email;
    private String phone;

    //class diyagraminda bu kisim belirtilmemistir
    public enum CustomerType { //hangi kaydin musteri hangisinin tedarikci oldugu tip alanindan okunur
        CUSTOMER,
        SUPPLIER
    }

    private CustomerType type;

    public Customer() {}

    public Customer(String customerId, String name, String taxId, String email, String phone, CustomerType type) {
        this.customerId = customerId;
        this.name = name;
        this.taxId = taxId;
        this.email = email;
        this.phone = phone;
        this.type = type;
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

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }
}

