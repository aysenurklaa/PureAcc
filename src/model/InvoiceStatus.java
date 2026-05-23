package model;

/**
 * Fatura yaşam döngüsü durumları.
 * Şemadaki CHECK kısıtı ile birebir uyumludur.
 */
@SuppressWarnings("unused")
public enum InvoiceStatus {
    TASLAK,
    BEKLEMEDE,
    GONDERILDI,
    VADESI_YAKLASTI,
    GECIKTI,
    ODENDI,
    IPTAL
}
