package model;

 //LineItem — Fatura içindeki tek bir kalem (ürün veya hizmet satırı).
 
@SuppressWarnings("unused")
public class LineItem {

    private String description;  // Kalem açıklaması (örn: "Demir çubuk")
    private int    quantity;     // Miktar (örn: 50)
    private double unitPrice;    // Birim fiyat (örn: 120.0)

    public LineItem() {}

    public LineItem(String description, int quantity, double unitPrice) {
        this.description = description;
        this.quantity    = quantity;
        this.unitPrice   = unitPrice;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    /** Miktar × birim fiyat = bu kalemin toplam tutarı. */
    public double getTotalPrice() {
        return quantity * unitPrice;
    }
}
