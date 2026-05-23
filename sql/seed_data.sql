-- ============================================================================
-- PureAcc Demo / Seed Verileri
-- ----------------------------------------------------------------------------
-- Bu dosya, raporlarda ve UI'da gösterilen verilere uygun demo kayıtları içerir.
-- Uygulama ilk kez çalıştırıldığında veya test ortamında kullanılabilir.
-- schema.sql çalıştırıldıktan sonra bu dosya yürütülmelidir.
-- ============================================================================

-- ────────────────────────────────────────────────────────────────────────────
-- 1. ŞİRKET (Ana işletme kaydı)
-- ────────────────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO company (id, tax_id, name, created_at, updated_at)
VALUES (1, '9990001111', 'PureAcc Demo İşletme A.Ş.', datetime('now'), datetime('now'));

-- ────────────────────────────────────────────────────────────────────────────
-- 2. MÜŞTERİLER / CARİLER (UI → Müşteri Listesi sayfasındaki veriler)
-- ────────────────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO customer (id, company_id, customer_code, name, type, created_at, updated_at)
VALUES
    (1, 1, 'C-101', 'Ural Teknoloji A.Ş.',   'CUSTOMER', datetime('now'), datetime('now')),
    (2, 1, 'C-102', 'Erdem Lojistik Ltd.',    'CUSTOMER', datetime('now'), datetime('now')),
    (3, 1, 'C-103', 'Yılmaz Yazılım A.Ş.',   'CUSTOMER', datetime('now'), datetime('now')),
    (4, 1, 'C-104', 'Öztürk Gıda San.',       'SUPPLIER', datetime('now'), datetime('now')),
    (5, 1, 'C-105', 'Kaya İnşaat Ltd.',       'CUSTOMER', datetime('now'), datetime('now'));

-- ────────────────────────────────────────────────────────────────────────────
-- 3. FATURALAR (UI → Hatırlatıcılar ve Raporlar sayfasından)
-- ────────────────────────────────────────────────────────────────────────────
-- Gecikmiş faturalar (Hatırlatıcılar sayfasında görünüyor)
INSERT OR IGNORE INTO invoice (id, company_id, customer_id, invoice_number, invoice_date, status, created_at, updated_at)
VALUES
    (1, 1, 4, 'INV-2026-0041', '2026-04-15', 'GECIKTI',   datetime('now'), datetime('now')),
    (2, 1, 5, 'INV-2026-0038', '2026-04-28', 'GECIKTI',   datetime('now'), datetime('now')),
    (3, 1, 2, 'INV-2026-0039', '2026-05-10', 'GECIKTI',   datetime('now'), datetime('now'));

-- Ödenen faturalar
INSERT OR IGNORE INTO invoice (id, company_id, customer_id, invoice_number, invoice_date, status, created_at, updated_at)
VALUES
    (4, 1, 1, 'INV-2026-0035', '2026-03-15', 'ODENDI',    datetime('now'), datetime('now')),
    (5, 1, 3, 'INV-2026-0036', '2026-03-20', 'ODENDI',    datetime('now'), datetime('now')),
    (6, 1, 1, 'INV-2026-0040', '2026-05-01', 'ODENDI',    datetime('now'), datetime('now'));

-- Beklemede olan faturalar
INSERT OR IGNORE INTO invoice (id, company_id, customer_id, invoice_number, invoice_date, status, created_at, updated_at)
VALUES
    (7, 1, 2, 'INV-2026-0042', '2026-05-12', 'BEKLEMEDE', datetime('now'), datetime('now')),
    (8, 1, 5, 'INV-2026-0043', '2026-05-14', 'BEKLEMEDE', datetime('now'), datetime('now'));

-- Taslak fatura
INSERT OR IGNORE INTO invoice (id, company_id, customer_id, invoice_number, invoice_date, status, created_at, updated_at)
VALUES
    (9, 1, 3, 'INV-2026-0044', '2026-05-18', 'TASLAK',    datetime('now'), datetime('now'));

-- ────────────────────────────────────────────────────────────────────────────
-- 4. FİNANSAL HAREKETLER (UI → Ana Sayfa ve Finansal Raporlar)
-- Raporlara göre: Toplam Gelir ₺84.500, Toplam Gider ₺31.200, Net ₺53.300
-- ────────────────────────────────────────────────────────────────────────────
-- GELİRLER (income)
-- Fatura Gelirleri: ₺72.000
INSERT OR IGNORE INTO financial_transaction (id, company_id, ref, type, amount, invoice_id, is_automatic, created_at, updated_at)
VALUES
    (1,  1, 'TX-GEL-001', 'income', 25000.00, 4, 1, datetime('now'), datetime('now')),
    (2,  1, 'TX-GEL-002', 'income', 18000.00, 5, 1, datetime('now'), datetime('now')),
    (3,  1, 'TX-GEL-003', 'income', 15500.00, 6, 1, datetime('now'), datetime('now')),
    (4,  1, 'TX-GEL-004', 'income', 13500.00, NULL, 0, datetime('now'), datetime('now'));

-- Diğer Gelirler: ₺12.500
INSERT OR IGNORE INTO financial_transaction (id, company_id, ref, type, amount, invoice_id, is_automatic, created_at, updated_at)
VALUES
    (5,  1, 'TX-GEL-005', 'income', 7500.00,  NULL, 0, datetime('now'), datetime('now')),
    (6,  1, 'TX-GEL-006', 'income', 5000.00,  NULL, 0, datetime('now'), datetime('now'));

-- GİDERLER (expense)
-- Operasyonel Giderler: ₺18.000
INSERT OR IGNORE INTO financial_transaction (id, company_id, ref, type, amount, invoice_id, is_automatic, created_at, updated_at)
VALUES
    (7,  1, 'TX-GID-001', 'expense', 8000.00,  NULL, 0, datetime('now'), datetime('now')),
    (8,  1, 'TX-GID-002', 'expense', 6000.00,  NULL, 0, datetime('now'), datetime('now')),
    (9,  1, 'TX-GID-003', 'expense', 4000.00,  NULL, 0, datetime('now'), datetime('now'));

-- Tedarikçi Ödemeleri: ₺13.200
INSERT OR IGNORE INTO financial_transaction (id, company_id, ref, type, amount, invoice_id, is_automatic, created_at, updated_at)
VALUES
    (10, 1, 'TX-GID-004', 'expense', 7200.00,  NULL, 0, datetime('now'), datetime('now')),
    (11, 1, 'TX-GID-005', 'expense', 6000.00,  NULL, 0, datetime('now'), datetime('now'));

-- ────────────────────────────────────────────────────────────────────────────
-- 5. HATIRLATICILAR (UI → Hatırlatıcılar sayfası gönderim geçmişi)
-- ────────────────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO reminder (id, invoice_id, channel, scheduled_at)
VALUES
    (1, 1, 'EMAIL', '2026-05-07 09:00:00'),
    (2, 2, 'SMS',   '2026-05-05 10:00:00'),
    (3, 3, 'EMAIL', '2026-05-10 09:00:00'),
    (4, 1, 'EMAIL', '2026-05-12 09:00:00'),
    (5, 3, 'SMS',   '2026-05-10 14:00:00');

-- ────────────────────────────────────────────────────────────────────────────
-- 6. DENETİM KAYITLARI (audit_log)
-- ────────────────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO audit_log (id, company_id, event_type, entity_table, entity_id, actor_hint, payload_json, created_at)
VALUES
    (1, 1, 'SYSTEM_INIT',     NULL,       NULL, 'SeedDataLoader', '{"message":"Demo verileri yüklendi"}', datetime('now')),
    (2, 1, 'INVOICE_CREATED', 'invoice',  '1',  'admin',          '{"invoice_number":"INV-2026-0041"}',   datetime('now')),
    (3, 1, 'INVOICE_CREATED', 'invoice',  '4',  'admin',          '{"invoice_number":"INV-2026-0035"}',   datetime('now')),
    (4, 1, 'PAYMENT_RECEIVED','invoice',  '4',  'admin',          '{"amount":25000.00}',                  datetime('now')),
    (5, 1, 'REMINDER_SENT',   'reminder', '1',  'system',         '{"channel":"EMAIL","customer":"Öztürk Gıda San."}', datetime('now'));
