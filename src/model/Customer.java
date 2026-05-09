package model;
@SuppressWarnings("unused")
//Maps to the customer facet of UML Customer
public class Customer {
	private String customerId;
    private String name;

    public Customer() {}

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
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
    
    //class diyagraminda bu kisim belirtilmemistir
    public enum CustomerType{ //hangi kaydin musteri hangisinin tedarikci oldugu tip alanindan okunur 
        CUSTOMER,
        SUPPLIER
    }

    private CustomerType type;
}

