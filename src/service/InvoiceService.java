package service;

import model.Invoice;
import model.InvoiceStatus;
import model.Customer;
import java.util.Date;

// Fatura işlemlerini (oluşturma, ödeme, iptal) yöneten servis sınıfı.
public class InvoiceService {
    
    // Yeni bir fatura oluşturur ve durumunu başlangıç olarak TASLAK yapar.
    public void createInvoice(Invoice invoice) {
        invoice.setStatus(InvoiceStatus.TASLAK); // İlk durum her zaman taslaktır.
        invoice.setDate(new Date()); // Oluşturulma tarihini o anki zaman olarak ayarlar.
        System.out.println("Fatura taslak olarak oluşturuldu: " + invoice.getInvoiceId());
    }

    // Faturayı ödendi olarak işaretler.
    public void markAsPaid(Invoice invoice, Customer customer) {
        invoice.setStatus(InvoiceStatus.ODENDI); 
        System.out.println(invoice.getInvoiceId() + " nolu fatura ödendi.");
    }

    // Faturayı iptal eder, ancak önce ödenip ödenmediğini kontrol eder.
    public void cancelInvoice(Invoice invoice) {
        // Eğer fatura zaten ödenmişse iptal işlemine izin vermez.
        if (invoice.getStatus() == InvoiceStatus.ODENDI) {
            System.out.println("Hata: Ödenmiş fatura iptal edilemez.");
            return; 
        }
        
        // Ödenmemişse durumu IPTAL yapar.
        invoice.setStatus(InvoiceStatus.IPTAL);
        System.out.println("Fatura iptal edildi.");
    }
}
