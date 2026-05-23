package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Customer modelinin temel özelliklerini test eder.
public class CustomerTest {

    @Test
    public void testBoshKurucu_nesneOlusturulur() {
        Customer c = new Customer();
        assertNotNull(c);
    }

    @Test
    public void testDoluKurucu_alanlarDogruAtanir() {
        Customer c = new Customer("CUS-001", "Ahmet Yılmaz", "1234567890",
                "ahmet@test.com", "05001234567", Customer.CustomerType.MUSTERI);

        assertEquals("CUS-001", c.getCustomerId());
        assertEquals("Ahmet Yılmaz", c.getName());
        assertEquals("1234567890", c.getTaxId());
        assertEquals("ahmet@test.com", c.getEmail());
        assertEquals("05001234567", c.getPhone());
        assertEquals(Customer.CustomerType.MUSTERI, c.getType());
    }

    @Test
    public void testCustomerType_supplier() {
        Customer c = new Customer();
        c.setType(Customer.CustomerType.TEDARIKCI);
        assertEquals(Customer.CustomerType.TEDARIKCI, c.getType());
    }

    @Test
    public void testTransactionListesi_baslangicdaBos() {
        Customer c = new Customer();
        assertNotNull(c.getTransactions());
        assertTrue(c.getTransactions().isEmpty());
    }

    @Test
    public void testFaturaListesi_baslangicdaBos() {
        Customer c = new Customer();
        assertNotNull(c.getInvoices());
        assertTrue(c.getInvoices().isEmpty());
    }
}
