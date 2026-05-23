package model;

import java.util.Date;

@SuppressWarnings("unused")
// Invoice — billing lifecycle and status tracking
public class Invoice {

    private String invoiceId;
    private Date date;
    private InvoiceStatus status;
    private Date dueDate;
    private double amount;
    private double kdvRate;

    public Invoice() {}

    public Invoice(String invoiceId, Date date, InvoiceStatus status) {
        this.invoiceId = invoiceId;
        this.date = date;
        this.status = status;
    }

    public Invoice(String invoiceId, Date date, InvoiceStatus status,
                   Date dueDate, double amount, double kdvRate) {
        this.invoiceId = invoiceId;
        this.date = date;
        this.status = status;
        this.dueDate = dueDate;
        this.amount = amount;
        this.kdvRate = kdvRate;
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getKdvRate() { return kdvRate; }
    public void setKdvRate(double kdvRate) { this.kdvRate = kdvRate; }

    // KDV dahil toplam tutar
    public double getTotalWithKdv() { return amount + (amount * kdvRate); }
}
