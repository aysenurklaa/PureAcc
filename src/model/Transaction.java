package model;

/**
 * Transaction — gelir ve gider hareketleri.
 * ref = iş referansı (tabloda ref sütunu); invoiceId = opsiyonel fatura FK.
 */
@SuppressWarnings("unused")
public class Transaction {
    /** Veritabanının otomatik ürettiği ID. */
    private long dbId;
    /** Business reference (ref sütununa karşılık gelir). */
    private String id;
    private String type;
    private double amount;
    /** Veritabanındaki invoice.id (fatura satırı); null ise faturaya bağlı değildir. */
    private Long invoiceId;
    /** true ise otomatik üretilmiş hareket; aynı fatura için en fazla bir tane (şema indeksi). */
    private boolean automatic;

    public Transaction() {
    }

    public Transaction(String id, String type, double amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
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

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }
}
