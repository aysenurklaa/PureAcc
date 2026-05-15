package service;

import model.Transaction;
import java.util.List;


 // RaporlamaService — Kar-zarar özeti ve dönemsel toplamları hesaplar.
 
public class SituationService {

    // Belirli bir dönemdeki net kâr-zararı hesapla
    public double calculateProfitLoss(List<Transaction> transactions) {
        double profitLoss = 0;
        for (Transaction t : transactions) {
            // Rapor: Gelirleri ekle, giderleri çıkar
            if ("income".equalsIgnoreCase(t.getType())) {
                profitLoss += t.getAmount();
            } else if ("expense".equalsIgnoreCase(t.getType())) {
                profitLoss -= t.getAmount();
            }
        }
        return profitLoss;
    }

    
     // Python analizi için CSV dışa aktarma metodu.
     
    public void exportDataForAnalysis(String data) {
        // Rapor: Veri dışa aktarma simülasyonu
        System.out.println("Veriler Python analiz modülü için hazırlandı.");
    }
}
