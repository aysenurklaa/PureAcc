package db;

import db.dao.*;
import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class DatabaseVerification {

    private static final Set<String> REQUIRED_TABLES = Set.of(
            "company", "customer", "invoice", "financial_transaction", "reminder",
            "auth_session", "audit_log"
    );

    private DatabaseVerification() {}

    public static void main(String[] args) throws Exception {
        boolean installFirst = args.length > 0 && "--install".equalsIgnoreCase(args[0]);
        if (installFirst) {
            System.out.println("[1/5] Sema kuruluyor (--install)...");
            DatabaseBootstrap.installSchema();
        }
        System.out.println("[2/5] Baglanti ve butunluk kontrolleri...");
        try (Connection connection = ConnectionManager.open()) {
            if (isSqlite(connection)) {
                checkIntegrity(connection);
                checkForeignKeys(connection);
            }
            checkTablesExist(connection);
        }
        System.out.println("[3/5] Kisitlar ve DAO yolu (islem geri alinacak)...");
        try (Connection connection = ConnectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                runSampleTransactionalPath(connection);
                connection.rollback();
                System.out.println("      Ornek veri ROLLBACK ile silindi.");
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
        System.out.println("[4/5] Sema esleme bilgisi.");
        printRequirementMapping();
        System.out.println("[5/5] Sonuc: Tum otomatik kontroller gecti.");
    }

    private static boolean isSqlite(Connection c) throws SQLException {
        String name = c.getMetaData().getDatabaseProductName();
        return name != null && name.toLowerCase().contains("sqlite");
    }

    private static void checkIntegrity(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA integrity_check")) {
            if (!rs.next()) throw new SQLException("integrity_check bos sonuc dondu");
            String first = rs.getString(1);
            if (!"ok".equalsIgnoreCase(first)) throw new SQLException("integrity_check basarisiz: " + first);
        }
        System.out.println("      PRAGMA integrity_check = ok");
    }

    private static void checkForeignKeys(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA foreign_key_check")) {
            if (rs.next()) throw new SQLException("foreign_key_check ihlali: tablo=" + rs.getString(1));
        }
        System.out.println("      PRAGMA foreign_key_check = bos (ihlal yok)");
    }

    private static void checkTablesExist(Connection c) throws SQLException {
        Set<String> found = new HashSet<>();
        if (isSqlite(c)) {
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'";
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) found.add(rs.getString(1).toLowerCase());
            }
        } else {
            String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) found.add(rs.getString(1).toLowerCase());
            }
        }
        for (String need : REQUIRED_TABLES) {
            if (!found.contains(need.toLowerCase()))
                throw new SQLException("Eksik tablo: " + need);
        }
        System.out.println("      Gerekli tablolar mevcut: " + REQUIRED_TABLES);
    }

    private static void runSampleTransactionalPath(Connection c) throws SQLException {
        Company co = new Company("1234567890", "Deneme A.S.");
        long companyId = CompanyDao.insert(c, co);
        Customer cu = new Customer("C-001", "Test Cari");
        cu.setType(Customer.CustomerType.CUSTOMER);
        long customerId = CustomerDao.insert(c, companyId, cu);
        Invoice inv = new Invoice("FAT-2026-001", new Date(), InvoiceStatus.TASLAK);
        long invoiceId = InvoiceDao.insert(c, companyId, customerId, inv);
        String sessionToken = UUID.randomUUID().toString();
        String expires = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        AuthSessionDao.insert(c, companyId, sessionToken, expires);
        AuditLogDao.insert(c, companyId, "VERIFY_RUN", "customer", String.valueOf(customerId), "DatabaseVerification", null);
        String txRef = "TX-VERIFY-" + UUID.randomUUID();
        Transaction tx = new Transaction(txRef, "GELIR", 100.0);
        tx.setInvoiceId(invoiceId);
        tx.setAutomatic(true);
        FinancialTransactionDao.insert(c, companyId, tx);
        Reminder rem = new Reminder("EMAIL", new Date());
        ReminderDao.insert(c, companyId, invoiceId, rem);
        InvoiceDao.updateStatus(c, companyId, "FAT-2026-001", InvoiceStatus.BEKLEMEDE);
    }

    private static void printRequirementMapping() {
        System.out.println("  Oturum (auth_session), denetim (audit_log), fatura basina tek otomatik hareket kisiti eklendi.");
        System.out.println("  Parolalar icin security.PasswordHasher (BCrypt); oturum token ozeti icin TokenHasher (SHA-256).");
    }
}
