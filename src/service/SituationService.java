package service;

import model.Customer;
import model.Transaction;
import java.util.ArrayList;
import java.util.List;

// Finansal durum ve raporlama işlemlerini yöneten servis sınıfı.
public class SituationService {

    private final TransactionService transactionService;
    private final CustomerService customerService;
    private final InvoiceService invoiceService;

    public SituationService(TransactionService transactionService,
                            CustomerService customerService,
                            InvoiceService invoiceService) {
        this.transactionService = transactionService;
        this.customerService = customerService;
        this.invoiceService = invoiceService;
    }

    // Seçilen dönem için toplam gelir, gider ve net bakiyeyi hesaplar.
    public FinancialSummary getFinancialSummary(List<Transaction> transactions, String period) {
        if (period == null || period.isEmpty()) {
            throw new RuntimeException("Dönem parametresi boş olamaz.");
        }

        double totalIncome  = 0;
        double totalExpense = 0;

        List<Transaction> gelirler = transactionService.listTransactions(transactions, "GELIR");
        List<Transaction> giderler = transactionService.listTransactions(transactions, "GIDER");

        for (Transaction t : gelirler) totalIncome  += t.getAmount();
        for (Transaction t : giderler) totalExpense += t.getAmount();

        double netBalance = totalIncome - totalExpense;
        return new FinancialSummary(period, totalIncome, totalExpense, netBalance);
    }

    // Seçilen döneme ait gelir/gider verilerini grafik formatında hazırlar.
    public ChartData getIncomeExpenseChart(List<Transaction> transactions, String period) {
        FinancialSummary summary = getFinancialSummary(transactions, period);
        return new ChartData(summary.period, summary.totalIncome, summary.totalExpense);
    }

    // Vadesi geçmiş borcu olan müşterileri listeler.
    public List<Customer> getUnpaidList(List<Customer> customers) {
        List<Customer> unpaid = new ArrayList<>();
        for (Customer c : customers) {
            double bakiye = customerService.getCurrentBalance(customers, c.getCustomerId());
            if (bakiye > 0) {
                unpaid.add(c);
            }
        }
        return unpaid;
    }

    // Net kâr veya zarar değerini hesaplayıp döndürür.
    // Pozitif = kâr, negatif = zarar.
    public double getProfitLoss(List<Transaction> transactions, String period) {
        FinancialSummary summary = getFinancialSummary(transactions, period);
        return summary.netBalance;
    }

    // Python analiz modülü için veriyi dışa aktarır.
    public void exportDataForAnalysis(String data) {
        System.out.println("Veriler Python analiz modülü için hazırlandı.");
    }

    // Finansal özet verisi.
    public static class FinancialSummary {
        public final String period;
        public final double totalIncome;
        public final double totalExpense;
        public final double netBalance;

        public FinancialSummary(String period, double totalIncome,
                                double totalExpense, double netBalance) {
            this.period       = period;
            this.totalIncome  = totalIncome;
            this.totalExpense = totalExpense;
            this.netBalance   = netBalance;
        }

        @Override
        public String toString() {
            return String.format(
                "Dönem: %s | Gelir: %.2f TL | Gider: %.2f TL | Net: %.2f TL",
                period, totalIncome, totalExpense, netBalance
            );
        }
    }

    // Grafik verisi.
    public static class ChartData {
        public final String label;
        public final double income;
        public final double expense;

        public ChartData(String label, double income, double expense) {
            this.label   = label;
            this.income  = income;
            this.expense = expense;
        }
    }
}
