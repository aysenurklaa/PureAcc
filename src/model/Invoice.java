package model;

//Invoice — billing lifecycle and status tracking
import java.util.Date;
@SuppressWarnings("unused")
public class Invoice {
	private String invoiceId;
    private Date date;
    private InvoiceStatus status;

    public Invoice() {}

    public Invoice(String invoiceId, Date date, InvoiceStatus status) {
        this.invoiceId = invoiceId;
        this.date = date;
        this.status = status;
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
