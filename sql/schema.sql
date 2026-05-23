-- ============================================================================
-- PureAcc SQLite şeması (DDL)
-- ----------------------------------------------------------------------------
-- Bu dosya "veritabanının çizimi" gibidir: hangi tablolar, hangi sütunlar,
-- hangi kurallar (PRIMARY KEY, FOREIGN KEY, UNIQUE, CHECK) tanımlanır.
-- Uygulama açılışında SchemaInitializer bu dosyayı okuyup SQLite'a uygular.
--
-- NOT: PostgreSQL üretim şeması aynı dosyadan çalıştırılamaz; PG için Flyway
-- veya eşdeğeri ile ayrı betikler üretilmelidir (syntax farkları).
-- ============================================================================

-- İşletme (şirket) kök kaydı.
CREATE TABLE IF NOT EXISTS company (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    tax_id          TEXT    NOT NULL COLLATE NOCASE,
    name            TEXT    NOT NULL,
    password_hash   TEXT    CHECK (password_hash IS NULL OR length(password_hash) BETWEEN 55 AND 80),
    created_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    CONSTRAINT uq_company_tax UNIQUE (tax_id),
    CONSTRAINT ck_company_tax_len CHECK (length(trim(tax_id)) BETWEEN 1 AND 32),
    CONSTRAINT ck_company_name_len CHECK (length(trim(name)) BETWEEN 1 AND 256)
);

-- Cari: müşteri veya tedarikçi.
CREATE TABLE IF NOT EXISTS customer (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id      INTEGER NOT NULL REFERENCES company (id) ON DELETE CASCADE,
    customer_code   TEXT    NOT NULL,
    name            TEXT    NOT NULL,
    type            TEXT    NOT NULL CHECK (type IN ('CUSTOMER', 'SUPPLIER')),
    created_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    CONSTRAINT uq_customer_code_per_company UNIQUE (company_id, customer_code),
    CONSTRAINT ck_customer_code_len CHECK (length(trim(customer_code)) BETWEEN 1 AND 64),
    CONSTRAINT ck_customer_name_len CHECK (length(trim(name)) BETWEEN 1 AND 256)
);

-- Fatura. (company_id, invoice_number) bileşik tekil — çok şirketli senaryoda numara çakışması olmaz.
CREATE TABLE IF NOT EXISTS invoice (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id      INTEGER NOT NULL REFERENCES company (id) ON DELETE CASCADE,
    customer_id     INTEGER NOT NULL REFERENCES customer (id) ON DELETE RESTRICT,
    invoice_number  TEXT    NOT NULL,
    invoice_date    TEXT    NOT NULL,
    status          TEXT    NOT NULL CHECK (status IN (
                        'TASLAK', 'BEKLEMEDE', 'GONDERILDI', 'VADESI_YAKLASTI',
                        'GECIKTI', 'ODENDI', 'IPTAL'
                    )),
    created_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    CONSTRAINT uq_invoice_number_per_company UNIQUE (company_id, invoice_number),
    CONSTRAINT ck_invoice_number_len CHECK (length(trim(invoice_number)) BETWEEN 1 AND 64)
);

-- Finansal hareket. Otomatik (is_automatic=1) ve faturaya bağlı kayıtlar için fatura başına en fazla bir satır.
CREATE TABLE IF NOT EXISTS financial_transaction (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id      INTEGER NOT NULL REFERENCES company (id) ON DELETE CASCADE,
    ref             TEXT    NOT NULL,
    type            TEXT    NOT NULL,
    -- REAL ikili kayan nokta; para birimi için ileride kuruş cinsinden INTEGER tercih edilebilir
    amount          REAL    NOT NULL CHECK (abs(amount) <= 1e15),
    invoice_id      INTEGER REFERENCES invoice (id) ON DELETE SET NULL,
    is_automatic    INTEGER NOT NULL DEFAULT 0 CHECK (is_automatic IN (0, 1)),
    created_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    CONSTRAINT uq_financial_tx_ref UNIQUE (ref),
    CONSTRAINT ck_financial_tx_type_len CHECK (length(trim(type)) BETWEEN 1 AND 64)
);

-- Aynı fatura için yalnızca bir "otomatik" hareket (çift gelir/gider önlemi).
CREATE UNIQUE INDEX IF NOT EXISTS uq_financial_tx_auto_per_invoice
    ON financial_transaction (invoice_id)
    WHERE is_automatic = 1 AND invoice_id IS NOT NULL;

-- Hatırlatıcı: aynı fatura + kanal + gün için tek kayıt (tekrar gönderim sınırı).
CREATE TABLE IF NOT EXISTS reminder (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id      INTEGER NOT NULL REFERENCES invoice (id) ON DELETE CASCADE,
    channel         TEXT    NOT NULL,
    scheduled_at    TEXT    NOT NULL,
    CONSTRAINT ck_reminder_channel_len CHECK (length(trim(channel)) BETWEEN 1 AND 64)
);

-- T ayırıcılı ISO metinde SQLite date() NULL dönebildiği için replace ile gün çıkarımı güvenli hale getirildi.
CREATE UNIQUE INDEX IF NOT EXISTS uq_reminder_invoice_channel_day
    ON reminder (invoice_id, channel, date(replace(scheduled_at, 'T', ' ')));

-- Oturum: istemciye verilen opak token yerine yalnızca hash saklanır (DB sızıntısında token çalınamaz).
CREATE TABLE IF NOT EXISTS auth_session (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id      INTEGER NOT NULL REFERENCES company (id) ON DELETE CASCADE,
    token_hash      TEXT    NOT NULL CHECK (length(token_hash) BETWEEN 32 AND 128),
    created_at      TEXT    NOT NULL DEFAULT (datetime('now')),
    expires_at      TEXT    NOT NULL,
    invalidated     INTEGER NOT NULL DEFAULT 0 CHECK (invalidated IN (0, 1)),
    CONSTRAINT uq_auth_session_token_hash UNIQUE (token_hash)
);

-- Denetim: finansal hareket dışındaki işlemler (giriş, iptal, silme) için append-only kayıt.
CREATE TABLE IF NOT EXISTS audit_log (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id      INTEGER REFERENCES company (id) ON DELETE SET NULL,
    event_type      TEXT    NOT NULL CHECK (length(trim(event_type)) BETWEEN 1 AND 128),
    entity_table    TEXT CHECK (entity_table IS NULL OR length(entity_table) <= 128),
    entity_id       TEXT CHECK (entity_id IS NULL OR length(entity_id) <= 256),
    actor_hint      TEXT CHECK (actor_hint IS NULL OR length(actor_hint) <= 256),
    payload_json    TEXT CHECK (payload_json IS NULL OR length(payload_json) <= 10000),
    created_at      TEXT    NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_customer_company ON customer (company_id);
CREATE INDEX IF NOT EXISTS idx_invoice_company_date ON invoice (company_id, invoice_date);
CREATE INDEX IF NOT EXISTS idx_invoice_customer ON invoice (customer_id);
CREATE INDEX IF NOT EXISTS idx_financial_tx_company ON financial_transaction (company_id);
CREATE INDEX IF NOT EXISTS idx_financial_tx_invoice ON financial_transaction (invoice_id);
CREATE INDEX IF NOT EXISTS idx_reminder_invoice ON reminder (invoice_id);
CREATE INDEX IF NOT EXISTS idx_reminder_scheduled ON reminder (scheduled_at);
CREATE INDEX IF NOT EXISTS idx_auth_session_company ON auth_session (company_id);
CREATE INDEX IF NOT EXISTS idx_auth_session_expires ON auth_session (expires_at);
CREATE INDEX IF NOT EXISTS idx_audit_log_company_time ON audit_log (company_id, created_at);
