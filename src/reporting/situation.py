"""
PureAcc - Situation (Raporlama) Control Sinifi
Gelistirici: Simay Pense - 24100011025
Aciklama: Finansal durum analizi, grafik verisi ve rapor uretimi.
         UML tasariminda tanimlanan Situation <<control>> sinifinin Python implementasyonu.
"""

import sqlite3
from datetime import date, datetime, timedelta
from typing import List, Optional

from .db_connection import DatabaseConnection
from .models import SumDTO, ChartDTO, ChartDataPoint, OverdueCustomerDTO, UnpaidInvoiceDTO


class Situation:
    """
    Isletmenin genel mali durumunu analiz eden kontrol sinifi.
    
    UML'deki metodlar:
        + getFinancialSummary(p): SumDTO
        + getIncomeExpenseChart(p): Chart
        + getUnpaidList(): List
        + getProfitLoss(period): double
        + getOverdueCustomers(): List
        + getReminderHistory(): List
    """

    def __init__(self, company_id: int, db_path: str = "purecc.db"):
        self._company_id = company_id
        self._period = "monthly"
        self._db = DatabaseConnection()
        self._conn = self._db.connect(db_path)

    def getFinancialSummary(self, period: str = "monthly") -> SumDTO:
        """
        Secilen donem icin toplam gelir, gider ve net bakiye hesaplar.
        period: 'monthly' | 'quarterly' | 'yearly'
        """
        self._period = period
        start_date, end_date = self._get_date_range(period)

        cursor = self._conn.cursor()
        cursor.execute("""
            SELECT 
                SUM(CASE WHEN type = 'in' THEN amount ELSE 0 END) AS total_income,
                SUM(CASE WHEN type = 'out' THEN amount ELSE 0 END) AS total_expense
            FROM "TRANSACTION"
            WHERE company_id = ?
              AND tx_date BETWEEN ? AND ?
        """, (self._company_id, start_date, end_date))

        row = cursor.fetchone()
        total_income = float(row["total_income"] or 0.0)
        total_expense = float(row["total_expense"] or 0.0)

        return SumDTO(
            total_income=total_income,
            total_expense=total_expense,
            period=f"{period} ({start_date} - {end_date})"
        )

    def getIncomeExpenseChart(self, period: str = "monthly") -> ChartDTO:
        """
        Secilen doneme gore gelir/gider grafik verisi uretir.
        period: 'monthly' | 'quarterly' | 'yearly'
        """
        cursor = self._conn.cursor()

        if period == "monthly":
            cursor.execute("""
                SELECT 
                    strftime('%Y-%m', tx_date) AS label,
                    SUM(CASE WHEN type = 'in' THEN amount ELSE 0 END) AS income,
                    SUM(CASE WHEN type = 'out' THEN amount ELSE 0 END) AS expense
                FROM "TRANSACTION"
                WHERE company_id = ?
                  AND tx_date >= date('now', '-12 months')
                GROUP BY strftime('%Y-%m', tx_date)
                ORDER BY label ASC
            """, (self._company_id,))

        elif period == "quarterly":
            cursor.execute("""
                SELECT 
                    strftime('%Y', tx_date) || '-Q' ||
                    CASE 
                        WHEN strftime('%m', tx_date) BETWEEN '01' AND '03' THEN '1'
                        WHEN strftime('%m', tx_date) BETWEEN '04' AND '06' THEN '2'
                        WHEN strftime('%m', tx_date) BETWEEN '07' AND '09' THEN '3'
                        ELSE '4'
                    END AS label,
                    SUM(CASE WHEN type = 'in' THEN amount ELSE 0 END) AS income,
                    SUM(CASE WHEN type = 'out' THEN amount ELSE 0 END) AS expense
                FROM "TRANSACTION"
                WHERE company_id = ?
                  AND tx_date >= date('now', '-12 months')
                GROUP BY label
                ORDER BY label ASC
            """, (self._company_id,))

        else:  # yearly
            cursor.execute("""
                SELECT 
                    strftime('%Y', tx_date) AS label,
                    SUM(CASE WHEN type = 'in' THEN amount ELSE 0 END) AS income,
                    SUM(CASE WHEN type = 'out' THEN amount ELSE 0 END) AS expense
                FROM "TRANSACTION"
                WHERE company_id = ?
                  AND tx_date >= date('now', '-5 years')
                GROUP BY strftime('%Y', tx_date)
                ORDER BY label ASC
            """, (self._company_id,))

        rows = cursor.fetchall()
        data_points = [
            ChartDataPoint(
                label=row["label"],
                income=float(row["income"] or 0),
                expense=float(row["expense"] or 0)
            )
            for row in rows
        ]

        return ChartDTO(
            period_type=period,
            data_points=data_points,
            chart_type="line"
        )

    def getUnpaidList(self) -> List[UnpaidInvoiceDTO]:
        """Odenmemis (pending / overdue) tum faturalari listeler."""
        cursor = self._conn.cursor()
        cursor.execute("""
            SELECT 
                i.id,
                i.invoice_number,
                c.name AS customer_name,
                i.amount,
                i.total_amount,
                i.due_date,
                i.status,
                CAST(julianday('now') - julianday(i.due_date) AS INTEGER) AS days_overdue
            FROM INVOICE i
            JOIN CUSTOMER c ON i.customer_id = c.id
            WHERE i.company_id = ?
              AND i.status IN ('pending', 'overdue')
            ORDER BY i.due_date ASC
        """, (self._company_id,))

        rows = cursor.fetchall()
        return [
            UnpaidInvoiceDTO(
                invoice_id=row["id"],
                invoice_number=row["invoice_number"],
                customer_name=row["customer_name"],
                amount=float(row["amount"]),
                total_amount=float(row["total_amount"]),
                due_date=row["due_date"],
                status=row["status"],
                days_overdue=max(0, int(row["days_overdue"] or 0))
            )
            for row in rows
        ]

    def getProfitLoss(self, period: str = "monthly") -> float:
        """Net kar veya zarar degerini hesaplar."""
        summary = self.getFinancialSummary(period)
        return round(summary.net_balance, 2)

    def getOverdueCustomers(self) -> List[OverdueCustomerDTO]:
        """Vadesi gecmis borc sahibi musterileri listeler."""
        cursor = self._conn.cursor()
        cursor.execute("""
            SELECT 
                c.id AS customer_id,
                c.name AS customer_name,
                c.tax_number,
                SUM(i.total_amount) AS overdue_amount,
                MAX(CAST(julianday('now') - julianday(i.due_date) AS INTEGER)) AS overdue_days,
                COUNT(i.id) AS invoice_count,
                MAX(i.invoice_number) AS last_invoice_number
            FROM CUSTOMER c
            JOIN INVOICE i ON c.id = i.customer_id
            WHERE i.company_id = ?
              AND i.status = 'overdue'
              AND c.is_deleted = 0
            GROUP BY c.id, c.name, c.tax_number
            ORDER BY overdue_days DESC
        """, (self._company_id,))

        rows = cursor.fetchall()
        return [
            OverdueCustomerDTO(
                customer_id=row["customer_id"],
                customer_name=row["customer_name"],
                tax_number=row["tax_number"],
                overdue_amount=float(row["overdue_amount"] or 0),
                overdue_days=int(row["overdue_days"] or 0),
                invoice_count=int(row["invoice_count"]),
                last_invoice_number=row["last_invoice_number"] or ""
            )
            for row in rows
        ]

    def getReminderHistory(self) -> list:
        """Gonderilmis tum hatirlatmalarin gecmisini listeler."""
        cursor = self._conn.cursor()
        cursor.execute("""
            SELECT 
                r.id,
                c.name AS customer_name,
                i.invoice_number,
                r.channel,
                r.sent_at,
                r.status,
                r.retry_count
            FROM REMINDER r
            JOIN CUSTOMER c ON r.customer_id = c.id
            JOIN INVOICE i ON r.invoice_id = i.id
            WHERE r.company_id = ?
            ORDER BY r.sent_at DESC
        """, (self._company_id,))

        rows = cursor.fetchall()
        return [
            {
                "id": row["id"],
                "customer_name": row["customer_name"],
                "invoice_number": row["invoice_number"],
                "channel": row["channel"],
                "sent_at": row["sent_at"],
                "status": row["status"],
                "retry_count": row["retry_count"]
            }
            for row in rows
        ]

    def _get_date_range(self, period: str):
        """Secilen doneme gore baslangic ve bitis tarihi dondurur"""
        today = date.today()

        if period == "monthly":
            start = today.replace(day=1)
            end = today
        elif period == "quarterly":
            quarter_start_month = ((today.month - 1) // 3) * 3 + 1
            start = today.replace(month=quarter_start_month, day=1)
            end = today
        elif period == "yearly":
            start = today.replace(month=1, day=1)
            end = today
        else:
            start = today.replace(day=1)
            end = today

        return start.strftime("%Y-%m-%d"), end.strftime("%Y-%m-%d")
