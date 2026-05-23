package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Company modelinin temel özelliklerini test eder.
public class CompanyTest {

    @Test
    public void testBoshKurucu_nesneOlusturulur() {
        Company c = new Company();
        assertNotNull(c);
    }

    @Test
    public void testDoluKurucu_alanlarDogruAtanir() {
        Company c = new Company("1234567890", "Kula Sanayi A.Ş.");
        assertEquals("1234567890", c.getTaxId());
        assertEquals("Kula Sanayi A.Ş.", c.getName());
    }

    @Test
    public void testSetTaxId_degeriGunceller() {
        Company c = new Company();
        c.setTaxId("9999999999");
        assertEquals("9999999999", c.getTaxId());
    }

    @Test
    public void testSetName_degeriGunceller() {
        Company c = new Company();
        c.setName("Test Şirketi");
        assertEquals("Test Şirketi", c.getName());
    }
}
