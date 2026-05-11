package service;

import model.Invoice;
import model.InvoiceStatus;
import model.Customer;
import java.util.Date;

public class InvoiceService {
    
    public void createInvoice(Invoice invoice) {
        invoice.setStatus(InvoiceStatus.TASLAK);
        invoice.setDate(new Date());
        System.out.println("Fatura taslak olarak oluşturuldu: " + invoice.getInvoiceId());
    }

    public void markAsPaid(Invoice invoice, Customer customer) {
        invoice.setStatus(InvoiceStatus.ODENDI);
        System.out.println(invoice.getInvoiceId() + " nolu fatura ödendi.");
    }

    public void cancelInvoice(Invoice invoice) {
        invoice.setStatus(InvoiceStatus.IPTAL);
        System.out.println("Fatura iptal edildi.");
    }
}
