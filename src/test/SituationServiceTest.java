package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Transaction;
import service.SituationService;
import service.TransactionService;
import service.CustomerService;
import service.InvoiceService;
import java.util.List;
import java.util.ArrayList;

// SituationService'in kar-zarar hesaplama fonksiyonunu test eden sınıf.
public class SituationTest {

    private SituationService createService() {
        TransactionService transactionService = new TransactionService();
        CustomerService customerService       = new CustomerService();
        InvoiceService invoiceService         = new InvoiceService(customerService, transactionService);
        return new SituationService(transactionService, customerService, invoiceService);
    }

    @Test
    public void testProfitCalculation() {
        // Test için işlem listesi oluşturalım
        List<Transaction> transactions = new ArrayList<>();
        // 1000 gelir ve 400 gider ekleyelim
        Transaction income  = new Transaction("T-001", "GELIR", 1000.0);
        Transaction expense = new Transaction("T-002", "GIDER", 400.0);
        transactions.add(income);
        transactions.add(expense);

        SituationService service = createService();
        double result = service.getProfitLoss(transactions, "monthly");

        // Beklenen sonuç: 1000 - 400 = 600
        assertEquals(600.0, result); // Kâr doğru hesaplandı mı?
    }

    @Test
    public void testLossCalculation() {
        // Gider gelirden fazla olduğunda zarar çıkmalı
        List<Transaction> transactions = new ArrayList<>();
        Transaction income  = new Transaction("T-003", "GELIR", 200.0);
        Transaction expense = new Transaction("T-004", "GIDER", 800.0);
        transactions.add(income);
        transactions.add(expense);

        SituationService service = createService();
        double result = service.getProfitLoss(transactions, "monthly");

        // Beklenen sonuç: 200 - 800 = -600 (zarar)
        assertEquals(-600.0, result); // Zarar doğru hesaplandı mı?
    }

    @Test
    public void testEmptyTransactions() {
        // Hiç işlem yoksa sonuç 0 olmalı
        List<Transaction> transactions = new ArrayList<>();

        SituationService service = createService();
        double result = service.getProfitLoss(transactions, "monthly");

        assertEquals(0.0, result); // Boş liste için 0 döndü mü?
    }
}
