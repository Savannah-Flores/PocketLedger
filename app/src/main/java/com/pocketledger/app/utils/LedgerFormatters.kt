package com.pocketledger.app.utils

import com.pocketledger.app.data.local.TransactionEntity
import com.pocketledger.app.ui.model.RecordItemUiModel
import com.pocketledger.app.viewmodel.RecordType
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

const val MONTHLY_LIVING_BUDGET = 2000.0

private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)
private val recordTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun TransactionEntity.toRecordItemUiModel(): RecordItemUiModel {
    val recordType = RecordType.fromStorage(type)
    val prefix = if (recordType == RecordType.Expense) "-" else "+"
    return RecordItemUiModel(
        id = id,
        content = title,
        amount = prefix + formatCurrency(amount),
        type = recordType,
        category = category,
        time = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(recordTimeFormatter),
    )
}

fun formatCurrency(amount: Double): String = currencyFormatter.format(amount)

fun calculateMonthlyBalance(transactions: List<TransactionEntity>, month: YearMonth = YearMonth.now()): Double {
    val monthlyTransactions = transactions.filter { transaction ->
        val date = Instant.ofEpochMilli(transaction.timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        YearMonth.from(date) == month
    }

    return MONTHLY_LIVING_BUDGET + monthlyTransactions.sumOf { transaction ->
        when (RecordType.fromStorage(transaction.type)) {
            RecordType.Expense -> -transaction.amount
            RecordType.Refund, RecordType.Income -> transaction.amount
        }
    }
}

fun calculateTodayExpense(transactions: List<TransactionEntity>, today: LocalDate = LocalDate.now()): Double {
    return transactions
        .filter { transaction ->
            val date = Instant.ofEpochMilli(transaction.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            date == today && RecordType.fromStorage(transaction.type) == RecordType.Expense
        }
        .sumOf { it.amount }
}

fun calculateMonthExpense(transactions: List<TransactionEntity>, month: YearMonth = YearMonth.now()): Double {
    return transactions
        .filter { transaction ->
            val date = Instant.ofEpochMilli(transaction.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            YearMonth.from(date) == month && RecordType.fromStorage(transaction.type) == RecordType.Expense
        }
        .sumOf { it.amount }
}

fun buildTimestamp(date: String, time: String): Long {
    val dateTime = LocalDateTime.parse("${date}T${time}:00")
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun timestampToDate(timestamp: Long): String {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(dateFormatter)
}

fun timestampToTime(timestamp: Long): String {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(timeFormatter)
}
