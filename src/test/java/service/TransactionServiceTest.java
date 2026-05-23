package service;

import model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.GecersizTutarException;
import util.AlanBosException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// TransactionService metodlarını test eder.
public class TransactionServiceTest {

    private TransactionService service;
    private List<Transaction> islemler;

    @BeforeEach
    public void setUp() {
        service = new TransactionService();
        islemler = new ArrayList<>();
    }

    @Test
    public void testAddIncome_gecerliTutar_islemOlusturulur() {
        Transaction t = service.addIncome("TXN-001", 1000.0, "Satış geliri");
        assertEquals("GELIR", t.getType());
        assertEquals(1000.0, t.getAmount());
    }

    @Test
    public void testAddIncome_negatifTutar_exceptionFirlatir() {
        assertThrows(GecersizTutarException.class, () ->
                service.addIncome("TXN-002", -500.0, "Geçersiz gelir"));
    }

    @Test
    public void testAddIncome_sifirTutar_exceptionFirlatir() {
        assertThrows(GecersizTutarException.class, () ->
                service.addIncome("TXN-003", 0, "Sıfır tutar"));
    }

    @Test
    public void testAddExpense_gecerliTutar_islemOlusturulur() {
        Transaction t = service.addExpense("TXN-004", 500.0, "Malzeme alımı");
        assertEquals("GIDER", t.getType());
        assertEquals(500.0, t.getAmount());
    }

    @Test
    public void testAddExpense_negatifTutar_exceptionFirlatir() {
        assertThrows(GecersizTutarException.class, () ->
                service.addExpense("TXN-005", -100.0, "Geçersiz gider"));
    }

    @Test
    public void testDeleteTransaction_mevcutId_trueDonerVeSilinir() {
        Transaction t = service.addIncome("TXN-006", 200.0, "Test");
        islemler.add(t);

        boolean sonuc = service.deleteTransaction(islemler, "TXN-006");

        assertTrue(sonuc);
        assertTrue(islemler.isEmpty());
    }

    @Test
    public void testDeleteTransaction_yanlisId_falseDoner() {
        boolean sonuc = service.deleteTransaction(islemler, "OLMAYAN-ID");
        assertFalse(sonuc);
    }

    @Test
    public void testListTransactions_gelirFiltresi_sadecaGelirler() {
        islemler.add(service.addIncome("TXN-007", 300.0, "Gelir 1"));
        islemler.add(service.addExpense("TXN-008", 150.0, "Gider 1"));

        List<Transaction> gelirler = service.listTransactions(islemler, "GELIR");

        assertEquals(1, gelirler.size());
        assertEquals("GELIR", gelirler.get(0).getType());
    }

    @Test
    public void testAttachDocument_boshYol_exceptionFirlatir() {
        Transaction t = service.addIncome("TXN-009", 100.0, "Test");
        islemler.add(t);

        assertThrows(AlanBosException.class, () ->
                service.attachDocument(islemler, "TXN-009", ""));
    }
}
