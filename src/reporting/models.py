"""
PureAcc - Raporlama DTO Modelleri
Gelistirici: Simay Pense - 24100011025
Aciklama: Raporlama katmaninda kullanilan veri transfer nesneleri (DTO)
"""

from dataclasses import dataclass, field
from typing import List


@dataclass
class SumDTO:
    """
    Finansal ozet verisi.
    getFinancialSummary() ve getProfitLoss() metodlari bu nesneyi dondurur.
    """
    total_income: float = 0.0
    total_expense: float = 0.0
    net_balance: float = 0.0
    period: str = ""

    def __post_init__(self):
        self.net_balance = round(self.total_income - self.total_expense, 2)
        self.total_income = round(self.total_income, 2)
        self.total_expense = round(self.total_expense, 2)


@dataclass
class ChartDataPoint:
    """Grafik icin tek bir veri noktasi (ay/donem, gelir, gider)"""
    label: str
    income: float = 0.0
    expense: float = 0.0
    net: float = 0.0

    def __post_init__(self):
        self.net = round(self.income - self.expense, 2)
        self.income = round(self.income, 2)
        self.expense = round(self.expense, 2)


@dataclass
class ChartDTO:
    """
    Gelir/Gider grafik verisi.
    getIncomeExpenseChart() metodu bu nesneyi dondurur.
    """
    period_type: str = "monthly"
    data_points: List[ChartDataPoint] = field(default_factory=list)
    chart_type: str = "line"


@dataclass
class OverdueCustomerDTO:
    """Vadesi gecmis borc sahibi musteri bilgisi"""
    customer_id: int = 0
    customer_name: str = ""
    tax_number: str = ""
    overdue_amount: float = 0.0
    overdue_days: int = 0
    invoice_count: int = 0
    last_invoice_number: str = ""


@dataclass
class UnpaidInvoiceDTO:
    """Odenmemis fatura bilgisi"""
    invoice_id: int = 0
    invoice_number: str = ""
    customer_name: str = ""
    amount: float = 0.0
    total_amount: float = 0.0
    due_date: str = ""
    status: str = ""
    days_overdue: int = 0
