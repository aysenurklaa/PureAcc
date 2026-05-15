package model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Transaction;
import java.util.List;
import java.util.ArrayList;

// SituationService'in kar-zarar hesaplama fonksiyonunu test eden sınıf.
public class SituationTest {

    @Test
    public void testProfitCalculation() {
        // Test için işlem listesi oluşturalım
        List<Transaction> transactions = new ArrayList<>();

        // 1000 gelir ve 400 gider ekleyelim
        Transaction income = new Transaction("T-001", "income", 1000.0);
        Transaction expense = new Transaction("T-002", "expense", 400.0);
        transactions.add(income);
        transactions.add(expense);

        // Servisi çalıştır
        SituationService service = new SituationService();
        double result = service.calculateProfitLoss(transactions);

        // Beklenen sonuç: 1000 - 400 = 600
        assertEquals(600.0, result); // Kâr doğru hesaplandı mı?
    }

    @Test
    public void testLossCalculation() {
        // Gider gelirden fazla olduğunda zarar çıkmalı
        List<Transaction> transactions = new ArrayList<>();

        Transaction income = new Transaction("T-003", "income", 200.0);
        Transaction expense = new Transaction("T-004", "expense", 800.0);
        transactions.add(income);
        transactions.add(expense);

        SituationService service = new SituationService();
        double result = service.calculateProfitLoss(transactions);

        // Beklenen sonuç: 200 - 800 = -600 (zarar)
        assertEquals(-600.0, result); // Zarar doğru hesaplandı mı?
    }

    @Test
    public void testEmptyTransactions() {
        // Hiç işlem yoksa sonuç 0 olmalı
        List<Transaction> transactions = new ArrayList<>();

        SituationService service = new SituationService();
        double result = service.calculateProfitLoss(transactions);

        assertEquals(0.0, result); // Boş liste için 0 döndü mü?
    }
}
