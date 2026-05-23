"""
PureAcc - Raporlama Modulu Demo / Test
Gelistirici: Simay Pense - 24100011025

Kullanim:
    python main.py

Bu dosya Situation class'inin tum metodlarini test eder.
Sahte verilerle calisir (dev ortami - SQLite).
"""

import sqlite3
from datetime import date, timedelta
from src.reporting import Situation, DatabaseConnection


def seed_test_data(db_path: str = "purecc.db"):
    """Test icin sahte veri ekle"""
    db = DatabaseConnection()
    conn = db.connect(db_path)
    cursor = conn.cursor()

    cursor.execute("""
        INSERT OR IGNORE INTO COMPANY (id, company_name, tax_number, password_hash, email)
        VALUES (1, 'Test Sanayi A.S.', '1234567890', 'hash123', 'test@firma.com')
    """)

    musteriler = [
        (1, 1, 'Akdeniz Gida San.', '7898123456', 'akdeniz@gida.com', '05551234567', 'customer'),
        (2, 1, 'Ege Tarim Ltd.',    '1234567898', 'ege@tarim.com',    '05559876543', 'customer'),
        (3, 1, 'Konya Lojistik',    '5678901234', 'info@kl.com',      '05553456789', 'customer'),
    ]
    cursor.executemany("""
        INSERT OR IGNORE INTO CUSTOMER (id, company_id, name, tax_number, email, phone, customer_type)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """, musteriler)

    bugun = date.today()
    faturalar = [
        (1, 1, 1, 'INV-2026-0001', 15000, 18, 17700, str(bugun - timedelta(days=60)), str(bugun - timedelta(days=30)), 'overdue'),
        (2, 1, 2, 'INV-2026-0002', 8000,  18, 9440,  str(bugun - timedelta(days=30)), str(bugun + timedelta(days=15)), 'pending'),
        (3, 1, 3, 'INV-2026-0003', 12000, 18, 14160, str(bugun - timedelta(days=90)), str(bugun - timedelta(days=60)), 'overdue'),
        (4, 1, 1, 'INV-2026-0004', 5000,  18, 5900,  str(bugun - timedelta(days=15)), str(bugun + timedelta(days=15)), 'paid'),
    ]
    cursor.executemany("""
        INSERT OR IGNORE INTO INVOICE
            (id, company_id, customer_id, invoice_number, amount, tax_rate, total_amount, issue_date, due_date, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, faturalar)

    islemler = [
        (1, 1, 1,    'in',  17700, 'satis',  'Fatura INV-2026-0004 odemesi', str(bugun - timedelta(days=10))),
        (2, 1, None, 'out', 5000,  'kira',   'Fabrika kirasi',               str(bugun - timedelta(days=5))),
        (3, 1, None, 'out', 2500,  'maas',   'Personel maasi',               str(bugun - timedelta(days=3))),
        (4, 1, None, 'in',  3000,  'diger',  'Yan gelir',                    str(bugun - timedelta(days=1))),
        (5, 1, None, 'out', 1200,  'fatura', 'Elektrik faturasi',             str(bugun - timedelta(days=20))),
        (6, 1, None, 'in',  8500,  'satis',  'Nakit satis',                  str(bugun - timedelta(days=45))),
        (7, 1, None, 'out', 3500,  'tedarik','Hammadde alimi',               str(bugun - timedelta(days=40))),
    ]
    cursor.executemany("""
        INSERT OR IGNORE INTO "TRANSACTION"
            (id, company_id, invoice_id, type, amount, category, description, tx_date)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, islemler)

    cursor.execute("""
        INSERT OR IGNORE INTO REMINDER
            (id, company_id, customer_id, invoice_id, channel, sent_at, status)
        VALUES (1, 1, 1, 1, 'email', datetime('now', '-5 days'), 'sent')
    """)
    cursor.execute("""
        INSERT OR IGNORE INTO REMINDER
            (id, company_id, customer_id, invoice_id, channel, sent_at, status)
        VALUES (2, 1, 3, 3, 'sms', datetime('now', '-2 days'), 'sent')
    """)

    conn.commit()
    print("Test verisi eklendi.\n")


def main():
    db_path = "purecc.db"

    print("=" * 55)
    print("  PureAcc - Finansal Durum Raporlama Modulu")
    print("  Gelistirici: Simay Pense - 24100011025")
    print("=" * 55)

    seed_test_data(db_path)

    situation = Situation(company_id=1, db_path=db_path)

    print("\n[1] FINANSAL OZET (Aylik)")
    ozet = situation.getFinancialSummary("monthly")
    print(f"    Toplam Gelir  : {ozet.total_income:,.2f} TL")
    print(f"    Toplam Gider  : {ozet.total_expense:,.2f} TL")
    print(f"    Net Bakiye    : {ozet.net_balance:,.2f} TL")

    print("\n[2] KAR / ZARAR")
    for donem in ["monthly", "quarterly", "yearly"]:
        kar = situation.getProfitLoss(donem)
        durum = "KAR" if kar >= 0 else "ZARAR"
        print(f"    {donem:12s}: {kar:,.2f} TL  ({durum})")

    print("\n[3] GELIR/GIDER GRAFIK VERISI (Aylik)")
    grafik = situation.getIncomeExpenseChart("monthly")
    for nokta in grafik.data_points:
        print(f"    {nokta.label}  Gelir:{nokta.income:,.0f}  Gider:{nokta.expense:,.0f}  Net:{nokta.net:,.0f}")
    if not grafik.data_points:
        print("    (Veri bulunamadi)")

    print("\n[4] ODENMEMIS FATURALAR")
    odenmemisler = situation.getUnpaidList()
    for f in odenmemisler:
        print(f"    {f.invoice_number}  {f.customer_name:<20}  {f.total_amount:,.2f} TL  {f.status}")
    if not odenmemisler:
        print("    (Odenmemis fatura yok)")

    print("\n[5] VADESI GECMIS MUSTERILER")
    gecmisler = situation.getOverdueCustomers()
    for m in gecmisler:
        print(f"    {m.customer_name:<25}  {m.overdue_amount:,.2f} TL  {m.overdue_days} gun gecmis")
    if not gecmisler:
        print("    (Vadesi gecmis borc yok)")

    print("\n[6] HATIRLATMA GECMISI")
    hatirlatmalar = situation.getReminderHistory()
    for h in hatirlatmalar:
        print(f"    {h['customer_name']:<20}  {h['invoice_number']}  {h['channel']}  {h['status']}")
    if not hatirlatmalar:
        print("    (Hatirlatma gecmisi yok)")

    print("\n" + "=" * 55)
    print("  Tum testler basariyla tamamlandi.")
    print("=" * 55)


if __name__ == "__main__":
    main()
