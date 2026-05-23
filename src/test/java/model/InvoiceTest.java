package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

// Fatura modelinin temel fonksiyonlarını test eden sınıf.
public class InvoiceTest {

    @Test
    public void testInvoiceCreation() {
        Date testDate = new Date();
        Invoice invoice = new Invoice("INV-001", testDate, InvoiceStatus.TASLAK);

        assertEquals("INV-001", invoice.getInvoiceId());
        assertEquals(testDate, invoice.getDate());
        assertEquals(InvoiceStatus.TASLAK, invoice.getStatus());
    }

    @Test
    public void testStatusChange() {
        Invoice invoice = new Invoice("INV-002", new Date(), InvoiceStatus.TASLAK);
        invoice.setStatus(InvoiceStatus.ODENDI);
        assertEquals(InvoiceStatus.ODENDI, invoice.getStatus());
    }

    @Test
    public void testAmountAndKdv() {
        Invoice invoice = new Invoice("INV-003", new Date(), InvoiceStatus.TASLAK,
                                      new Date(), 1000.0, 0.18);
        assertEquals(1000.0, invoice.getAmount());
        assertEquals(0.18, invoice.getKdvRate());
        assertEquals(1180.0, invoice.getTotalWithKdv());
    }

    @Test
    public void testDueDate() {
        Date dueDate = new Date();
        Invoice invoice = new Invoice("INV-004", new Date(), InvoiceStatus.BEKLEMEDE,
                                      dueDate, 500.0, 0.18);
        assertEquals(dueDate, invoice.getDueDate());
    }

    @Test
    public void testCancelStatus() {
        Invoice invoice = new Invoice("INV-005", new Date(), InvoiceStatus.BEKLEMEDE);
        invoice.setStatus(InvoiceStatus.IPTAL);
        assertEquals(InvoiceStatus.IPTAL, invoice.getStatus());
    }
}
