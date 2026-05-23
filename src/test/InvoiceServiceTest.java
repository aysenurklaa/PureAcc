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
        assertEquals(testDate, invoice.getDate());        // Tarih doğru mu?
        assertEquals(InvoiceStatus.TASLAK, invoice.getStatus()); // Durum doğru mu?
    }

    @Test
    public void testStatusChange() {
        // Durum değişikliği doğru çalışıyor mu?
        Invoice invoice = new Invoice("INV-002", new Date(), InvoiceStatus.TASLAK);
        invoice.setStatus(InvoiceStatus.ODENDI);
        assertEquals(InvoiceStatus.ODENDI, invoice.getStatus());
    }

    @Test
    public void testAmountAndKdv() {
        // Tutar ve KDV alanları doğru set ediliyor mu?
        Invoice invoice = new Invoice("INV-003", new Date(), InvoiceStatus.TASLAK,
                                      new Date(), 1000.0, 0.18);
        assertEquals(1000.0, invoice.getAmount());   // Tutar doğru mu?
        assertEquals(0.18, invoice.getKdvRate());    // KDV oranı doğru mu?
        assertEquals(1180.0, invoice.getTotalWithKdv()); // KDV dahil toplam doğru mu?
    }

    @Test
    public void testDueDate() {
        // Vade tarihi doğru set ediliyor mu?
        Date dueDate = new Date();
        Invoice invoice = new Invoice("INV-004", new Date(), InvoiceStatus.BEKLEMEDE,
                                      dueDate, 500.0, 0.18);
        assertEquals(dueDate, invoice.getDueDate()); // Vade tarihi doğru mu?
    }

    @Test
    public void testCancelStatus() {
        // İptal durumuna geçiş doğru çalışıyor mu?
        Invoice invoice = new Invoice("INV-005", new Date(), InvoiceStatus.BEKLEMEDE);
        invoice.setStatus(InvoiceStatus.IPTAL);
        assertEquals(InvoiceStatus.IPTAL, invoice.getStatus()); // İptal durumu doğru mu?
    }
}
