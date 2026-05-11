package model;
@SuppressWarnings("unused")
//Transaction — income and expense movements
public class Transaction {
	private String id;
    private String type;
    private double amount;
    
    private Company company; //bu islem hangi sirkete ait?
    private Invoice invoice; //bu isleme bagli bir fatura var mi?

    public Transaction() {}

    public Transaction(String id, String type, double amount,Company company) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.company=company;
    }
    
    public Company getCompany() {
    	return company;
    }
    
    public void setCompany(Company company) {
    	this.company=company;
    }
    
    public Invoice getInvoice() {
    	return invoice;
    }
    
    public void setInvoice(Invoice invoice)
    {
    	this.invoice=invoice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
