"""
PureAcc - Veritabani Baglantisi
Gelistirici: Simay Pense - 24100011025
Aciklama: SQLite (dev) baglantisini yoneten modul
"""

import sqlite3
import os


class DatabaseConnection:
    """
    Singleton pattern ile SQLite baglantisi saglar.
    Uretim ortaminda PostgreSQL'e gecis icin db_path degistirilebilir.
    """

    _instance = None
    _connection = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(DatabaseConnection, cls).__new__(cls)
        return cls._instance

    def connect(self, db_path: str = "purecc.db"):
        """Veritabanina baglan"""
        if self._connection is None:
            self._connection = sqlite3.connect(db_path, check_same_thread=False)
            self._connection.row_factory = sqlite3.Row
            self._create_tables()
        return self._connection

    def get_connection(self):
        """Mevcut baglantıyı döndür"""
        if self._connection is None:
            return self.connect()
        return self._connection

    def _create_tables(self):
        cursor = self._connection.cursor()
        cursor.executescript("""
            CREATE TABLE IF NOT EXISTS COMPANY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                company_name VARCHAR(120) NOT NULL,
                tax_number VARCHAR(11) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                email VARCHAR(120),
                phone VARCHAR(20),
                failed_login_count INTEGER DEFAULT 0,
                locked_until DATETIME,
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
            );

            CREATE TABLE IF NOT EXISTS CUSTOMER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                company_id INTEGER NOT NULL,
                name VARCHAR(120) NOT NULL,
                tax_number VARCHAR(11) UNIQUE NOT NULL,
                email VARCHAR(120),
                phone VARCHAR(20),
                customer_type TEXT CHECK(customer_type IN ('customer', 'supplier')),
                current_balance DECIMAL(15,2) DEFAULT 0,
                is_deleted BOOLEAN DEFAULT 0,
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (company_id) REFERENCES COMPANY(id)
            );

            CREATE TABLE IF NOT EXISTS INVOICE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                company_id INTEGER NOT NULL,
                customer_id INTEGER NOT NULL,
                invoice_number VARCHAR(30) UNIQUE NOT NULL,
                amount DECIMAL(15,2) NOT NULL,
                tax_rate DECIMAL(5,2) DEFAULT 18,
                total_amount DECIMAL(15,2) NOT NULL,
                issue_date DATE NOT NULL,
                due_date DATE NOT NULL,
                status TEXT CHECK(status IN ('draft','pending','overdue','paid','cancelled')),
                pdf_path VARCHAR(255),
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (company_id) REFERENCES COMPANY(id),
                FOREIGN KEY (customer_id) REFERENCES CUSTOMER(id)
            );

            CREATE TABLE IF NOT EXISTS "TRANSACTION" (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                company_id INTEGER NOT NULL,
                invoice_id INTEGER,
                type TEXT CHECK(type IN ('in','out')),
                amount DECIMAL(15,2) NOT NULL,
                category VARCHAR(60),
                description TEXT,
                doc_path VARCHAR(255),
                is_auto BOOLEAN DEFAULT 0,
                tx_date DATE NOT NULL,
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME,
                FOREIGN KEY (company_id) REFERENCES COMPANY(id),
                FOREIGN KEY (invoice_id) REFERENCES INVOICE(id)
            );

            CREATE TABLE IF NOT EXISTS REMINDER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                company_id INTEGER NOT NULL,
                customer_id INTEGER NOT NULL,
                invoice_id INTEGER NOT NULL,
                channel TEXT CHECK(channel IN ('email','sms')),
                sent_at DATETIME NOT NULL,
                status TEXT CHECK(status IN ('sent','failed','retrying')),
                retry_count INTEGER DEFAULT 0,
                next_retry_at DATETIME,
                FOREIGN KEY (company_id) REFERENCES COMPANY(id),
                FOREIGN KEY (customer_id) REFERENCES CUSTOMER(id),
                FOREIGN KEY (invoice_id) REFERENCES INVOICE(id)
            );
        """)
        self._connection.commit()

    def close(self):
        """Baglantıyı kapat"""
        if self._connection:
            self._connection.close()
            self._connection = None
