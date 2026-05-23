package model;

import java.util.Date;

@SuppressWarnings("unused")
// Transaction — income and expense movements
public class Transaction {

    private String id;
    private String type;          // "GELIR" veya "GIDER"
    private double amount;
    private Date date;
    private String description;
    private String documentPath;

    // Zehra — model ilişkileri
    private Company company;      // bu işlem hangi şirkete ait?
    private Invoice invoice;      // bu işleme bağlı fatura var mı?

    public Transaction() {}

    // Kısa constructor — sadece id, type, amount (testlerde kullanım kolaylığı için)
    public Transaction(String id, String type, double amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = new Date();
    }

    public Transaction(String id, String type, double amount, Date date,
                       String description, String documentPath,
                       Company company, Invoice invoice) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.documentPath = documentPath;
        this.company = company;
        this.invoice = invoice;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
}
