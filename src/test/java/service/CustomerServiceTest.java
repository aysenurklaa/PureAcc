package service;

import model.Customer;
import model.CustDTO;
import model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.AlanBosException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// CustomerService metodlarını test eder.
public class CustomerServiceTest {

    private CustomerService service;
    private List<Customer> musteriler;

    @BeforeEach
    public void setUp() {
        service = new CustomerService();
        musteriler = new ArrayList<>();
    }

    @Test
    public void testAddCustomer_gecerliVeriler_musteriOlusturulur() {
        Customer c = service.addCustomer("CUS-001", "Ayşe Kaya", "111",
                "ayse@test.com", "05001111111", Customer.CustomerType.CUSTOMER);

        assertEquals("CUS-001", c.getCustomerId());
        assertEquals("Ayşe Kaya", c.getName());
        assertEquals(Customer.CustomerType.CUSTOMER, c.getType());
    }

    @Test
    public void testAddCustomer_gecersizEmail_exceptionFirlatir() {
        assertThrows(AlanBosException.class, () ->
                service.addCustomer("CUS-002", "Test", "222",
                        "gecersiz-email", "05002222222", Customer.CustomerType.CUSTOMER));
    }

    @Test
    public void testAddCustomer_boshIsim_exceptionFirlatir() {
        assertThrows(AlanBosException.class, () ->
                service.addCustomer("CUS-003", "", "333",
                        "test@test.com", "05003333333", Customer.CustomerType.CUSTOMER));
    }

    @Test
    public void testGetCurrentBalance_dogruHesaplama() {
        Customer c = service.addCustomer("CUS-004", "Mehmet", "444",
                "mehmet@test.com", "05004444444", Customer.CustomerType.CUSTOMER);

        Transaction gelir = new Transaction();
        gelir.setType("GELIR");
        gelir.setAmount(2000.0);

        Transaction gider = new Transaction();
        gider.setType("GIDER");
        gider.setAmount(800.0);

        c.getTransactions().add(gelir);
        c.getTransactions().add(gider);
        musteriler.add(c);

        double bakiye = service.getCurrentBalance(musteriler, "CUS-004");

        assertEquals(1200.0, bakiye);
    }

    @Test
    public void testGetCurrentBalance_islemYok_sifirDoner() {
        Customer c = service.addCustomer("CUS-005", "Ali", "555",
                "ali@test.com", "05005555555", Customer.CustomerType.SUPPLIER);
        musteriler.add(c);

        double bakiye = service.getCurrentBalance(musteriler, "CUS-005");

        assertEquals(0.0, bakiye);
    }

    @Test
    public void testListAllCustomers_tumMusterileriDoner() {
        musteriler.add(service.addCustomer("CUS-006", "M1", "1",
                "m1@t.com", "111", Customer.CustomerType.CUSTOMER));
        musteriler.add(service.addCustomer("CUS-007", "M2", "2",
                "m2@t.com", "222", Customer.CustomerType.SUPPLIER));

        List<Customer> liste = service.listAllCustomers(musteriler);

        assertEquals(2, liste.size());
    }

    @Test
    public void testGetCustomerDetail_mevcutMusteri_dtoDonerDogru() {
        Customer c = service.addCustomer("CUS-008", "Fatma", "888",
                "fatma@test.com", "05008888888", Customer.CustomerType.CUSTOMER);
        musteriler.add(c);

        CustDTO dto = service.getCustomerDetail(musteriler, "CUS-008");

        assertEquals("CUS-008", dto.getCustomerId());
        assertEquals("Fatma", dto.getName());
    }
}
