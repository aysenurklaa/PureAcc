package model;

/**
 * Company — işletme (şirket) kök kaydı.
 * Sistemdeki diğer kayıtların "sahibi" veya kapsayıcısıdır.
 */
@SuppressWarnings("unused")
public class Company {
    private long id;
    private String taxId;
    private String name;

    public Company() {}

    public Company(String taxId, String name) {
        this.taxId = taxId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
