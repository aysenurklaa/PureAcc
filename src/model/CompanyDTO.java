package dto;

 // CompanyDTO — Şirket profil bilgilerini katmanlar arasında taşıyan veri nesnesi.

public class CompanyDTO {

    private String taxId;  // Vergi numarası
    private String name;   // Şirket adı

    public CompanyDTO() {}

    public CompanyDTO(String taxId, String name) {
        this.taxId = taxId;
        this.name  = name;
    }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "CompanyDTO{taxId='" + taxId + "', name='" + name + "'}";
    }
}
