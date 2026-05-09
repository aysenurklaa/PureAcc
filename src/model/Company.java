package model;
@SuppressWarnings("unused")
//Company — the central business account entity
public class Company {
	private String taxId;
    private String name;

    public Company() {}

    public Company(String taxId, String name) {
        this.taxId = taxId;
        this.name = name;
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
