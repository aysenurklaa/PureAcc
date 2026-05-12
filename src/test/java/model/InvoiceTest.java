package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

// Fatura modelinin temel fonksiyonlarını test eden sınıf.
public class InvoiceTest {

    @Test
    public void testInvoiceCreation() {
        // Yeni bir fatura nesnesi oluşturalım
        Date testDate = new Date();
        Invoice invoice = new Invoice("INV-001", testDate, InvoiceStatus.TASLAK);

        // Bilgiler doğru set edilmiş mi kontrol edilir
        assertEquals("INV-001", invoice.getInvoiceId()); // ID doğru mu?
        assertEquals(testDate, invoice.getDate()); // Tarih doğru mu?
        assertEquals(InvoiceStatus.TASLAK, invoice.getStatus()); // Durum doğru mu?
    }
    @Test
    public void testStatusChange() {
        Invoice invoice = new Invoice("INV-002", new Date(), InvoiceStatus.TASLAK);
        invoice.setStatus(InvoiceStatus.ODENDI);
        assertEquals(InvoiceStatus.ODENDI, invoice.getStatus());
}
}
