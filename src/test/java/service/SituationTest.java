package service;

import model.Transaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

// SituationService'in kar-zarar hesaplama fonksiyonunu test eden sınıf.
public class SituationTest {

    private SituationService createService() {
        TransactionService transactionService = new TransactionService();
        CustomerService customerService       = new CustomerService();
        InvoiceService invoiceService         = new InvoiceService();
        return new SituationService(transactionService, customerService, invoiceService);
    }

    @Test
    public void testProfitCalculation() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction income  = new Transaction("T-001", "GELIR", 1000.0);
        Transaction expense = new Transaction("T-002", "GIDER", 400.0);
        transactions.add(income);
        transactions.add(expense);

        SituationService service = createService();
        double result = service.getProfitLoss(transactions, "monthly");

        assertEquals(600.0, result);
    }

    @Test
    public void testLossCalculation() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction income  = new Transaction("T-003", "GELIR", 200.0);
        Transaction expense = new Transaction("T-004", "GIDER", 800.0);
        transactions.add(income);
        transactions.add(expense);

        SituationService service = createService();
        double result = service.getProfitLoss(transactions, "monthly");

        assertEquals(-600.0, result);
    }

    @Test
    public void testEmptyTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        SituationService service = createService();
        double result = service.getProfitLoss(transactions, "monthly");

        assertEquals(0.0, result);
    }
}
