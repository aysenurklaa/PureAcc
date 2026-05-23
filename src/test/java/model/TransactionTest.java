package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

// Transaction modelinin temel özelliklerini test eder.
public class TransactionTest {

    @Test
    public void testBoshKurucu_nesneOlusturulur() {
        Transaction t = new Transaction();
        assertNotNull(t);
    }

    @Test
    public void testDoluKurucu_alanlarDogruAtanir() {
        Date tarih = new Date();
        Transaction t = new Transaction("TXN-001", "GELIR", 1500.0,
                tarih, "Test geliri", null, null, null);

        assertEquals("TXN-001", t.getId());
        assertEquals("GELIR", t.getType());
        assertEquals(1500.0, t.getAmount());
        assertEquals(tarih, t.getDate());
        assertEquals("Test geliri", t.getDescription());
    }

    @Test
    public void testSetType_giderAtanir() {
        Transaction t = new Transaction();
        t.setType("GIDER");
        assertEquals("GIDER", t.getType());
    }

    @Test
    public void testSetAmount_degeriGunceller() {
        Transaction t = new Transaction();
        t.setAmount(2500.75);
        assertEquals(2500.75, t.getAmount());
    }

    @Test
    public void testSetDocumentPath_belgeyiGunceller() {
        Transaction t = new Transaction();
        t.setDocumentPath("/belgeler/dekont.pdf");
        assertEquals("/belgeler/dekont.pdf", t.getDocumentPath());
    }
}
