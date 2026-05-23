package model;

import java.util.Date;

/**
 * Invoice — fatura kaydı, yaşam döngüsü ve durum izlemesi.
 * invoiceId alanı, tabloda invoice_number sütununa karşılık gelir.
 */
@SuppressWarnings("unused")
public class Invoice {
    private long id;
    private String invoiceId;
    private Date date;
    private InvoiceStatus status;

    public Invoice() {}

    public Invoice(String invoiceId, Date date, InvoiceStatus status) {
        this.invoiceId = invoiceId;
        this.date = date;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
}
