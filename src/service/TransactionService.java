package service;

import model.Transaction;
import util.AlanBosException;
import util.GecersizTutarException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Gelir ve gider işlemlerini yöneten servis sınıfı.
public class TransactionService {

    // Yeni bir gelir kaydı oluşturur.
    public Transaction addIncome(String id, double amount, String description) {
        if (amount <= 0) {
            throw new GecersizTutarException("Gelir tutarı sıfırdan büyük olmalıdır.");
        }
        if (id == null || id.isEmpty()) {
            throw new AlanBosException("İşlem ID boş olamaz.");
        }
        Transaction t = new Transaction();
        t.setId(id);
        t.setType("GELİR");
        t.setAmount(amount);
        t.setDate(new Date());
        t.setDescription(description);
        System.out.println("Gelir kaydı oluşturuldu: " + id + " — " + amount + " TL");
        return t;
    }

    // Yeni bir gider kaydı oluşturur.
    public Transaction addExpense(String id, double amount, String description) {
        if (amount <= 0) {
            throw new GecersizTutarException("Gider tutarı sıfırdan büyük olmalıdır.");
        }
        if (id == null || id.isEmpty()) {
            throw new AlanBosException("İşlem ID boş olamaz.");
        }
        Transaction t = new Transaction();
        t.setId(id);
        t.setType("GİDER");
        t.setAmount(amount);
        t.setDate(new Date());
        t.setDescription(description);
        System.out.println("Gider kaydı oluşturuldu: " + id + " — " + amount + " TL");
        return t;
    }

    // ID'ye göre işlemi bulur ve günceller.
    public Transaction updateTransaction(List<Transaction> transactions, String id,
                                         Double newAmount, String newDescription) {
        for (Transaction t : transactions) {
            if (t.getId().equals(id)) {
                if (newAmount != null) {
                    if (newAmount <= 0) throw new GecersizTutarException("Tutar sıfırdan büyük olmalıdır.");
                    t.setAmount(newAmount);
                }
                if (newDescription != null) {
                    t.setDescription(newDescription);
                }
                System.out.println("İşlem güncellendi: " + id);
                return t;
            }
        }
        throw new RuntimeException("İşlem bulunamadı: " + id);
    }

    // ID'ye göre işlemi listeden siler. Başarılıysa true, bulunamazsa false döner.
    public boolean deleteTransaction(List<Transaction> transactions, String id) {
        boolean silindi = transactions.removeIf(t -> t.getId().equals(id));
        if (silindi) {
            System.out.println("İşlem silindi: " + id);
        } else {
            System.out.println("Silinecek işlem bulunamadı: " + id);
        }
        return silindi;
    }

    // Filtreye göre işlemleri listeler. typeFilter null ise tümünü döner.
    public List<Transaction> listTransactions(List<Transaction> transactions, String typeFilter) {
        if (typeFilter == null || typeFilter.isEmpty()) {
            return new ArrayList<>(transactions);
        }
        List<Transaction> sonuc = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase(typeFilter)) {
                sonuc.add(t);
            }
        }
        return sonuc;
    }

    // İşleme belge (dekont, fiş vb.) ekler.
    public void attachDocument(List<Transaction> transactions, String id, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new AlanBosException("Belge yolu boş olamaz.");
        }
        for (Transaction t : transactions) {
            if (t.getId().equals(id)) {
                t.setDocumentPath(filePath);
                System.out.println("Belge eklendi: " + id + " → " + filePath);
                return;
            }
        }
        throw new RuntimeException("İşlem bulunamadı: " + id);
    }
}
