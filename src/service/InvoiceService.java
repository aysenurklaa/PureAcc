package service;

import model.Customer;
import model.Invoice;
import model.InvoiceStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Fatura işlemlerini yöneten servis sınıfı.
public class InvoiceService {

    private final List<Invoice> store = new ArrayList<>();

    // Yeni bir fatura oluşturur, durumu TASLAK olarak başlar.
    public Invoice createInvoice(Customer customer, double amount, double kdvRate, Date dueDate) {
        if (customer == null) {
            throw new IllegalArgumentException("Müşteri bilgisi boş olamaz.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Fatura tutarı sıfırdan büyük olmalıdır.");
        }

        String invoiceId = "INV-" + System.currentTimeMillis();
        Invoice invoice  = new Invoice(invoiceId, new Date(), InvoiceStatus.TASLAK, dueDate, amount, kdvRate);
        store.add(invoice);

        System.out.println("Fatura taslak olarak oluşturuldu: " + invoiceId);
        return invoice;
    }

    // Faturayı ödendi olarak işaretler.
    public void markAsPaid(String invoiceId, Customer customer) {
        Invoice invoice = findById(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Fatura bulunamadı: " + invoiceId);
        }
        if (invoice.getStatus() == InvoiceStatus.IPTAL) {
            throw new IllegalStateException("İptal edilen fatura ödendi olarak işaretlenemez.");
        }

        invoice.setStatus(InvoiceStatus.ODENDI);
        System.out.println(invoiceId + " nolu fatura ödendi. Müşteri: " + customer.getName());
    }

    // Faturayı iptal eder; ödenmiş fatura iptal edilemez.
    public void cancelInvoice(String invoiceId) {
        Invoice invoice = findById(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Fatura bulunamadı: " + invoiceId);
        }
        if (invoice.getStatus() == InvoiceStatus.ODENDI) {
            System.out.println("Hata: Ödenmiş fatura iptal edilemez.");
            return;
        }

        invoice.setStatus(InvoiceStatus.IPTAL);
        System.out.println("Fatura iptal edildi: " + invoiceId);
    }

    // Vadesi geçmiş faturaları listeler.
    public List<Invoice> listOverdue() {
        List<Invoice> overdue = new ArrayList<>();
        for (Invoice invoice : store) {
            if (invoice.getStatus() == InvoiceStatus.GECIKTI ||
                invoice.getStatus() == InvoiceStatus.VADESI_YAKLASTI) {
                overdue.add(invoice);
            }
        }
        return overdue;
    }

    // Belirli bir müşteriye ait faturaları listeler.
    public List<Invoice> getByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Müşteri ID boş olamaz.");
        }
        // TODO: Invoice içine customerId eklenince burada filtrelenecek.
        return new ArrayList<>(store);
    }

    // Vadesi geçen faturaların durumunu otomatik günceller.
    public void updateOverdueStatuses() {
        Date now = new Date();
        for (Invoice invoice : store) {
            if (invoice.getStatus() == InvoiceStatus.BEKLEMEDE ||
                invoice.getStatus() == InvoiceStatus.GONDERILDI) {
                if (invoice.getDueDate() != null && invoice.getDueDate().before(now)) {
                    invoice.setStatus(InvoiceStatus.GECIKTI);
                    System.out.println("Fatura gecikmiş durumuna alındı: " + invoice.getInvoiceId());
                }
            }
        }
    }

    // ID'ye göre fatura arar.
    public Invoice findById(String invoiceId) {
        for (Invoice inv : store) {
            if (inv.getInvoiceId().equals(invoiceId)) return inv;
        }
        return null;
    }
}
