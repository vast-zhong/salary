package com.example.salary.data

import java.time.LocalDate

/**
 * 额外交易记录（意外之财或遭遇打劫）
 */
data class ExtraTransaction(
    val date: LocalDate,
    val amount: Double, // 正数表示收入，负数表示支出
    val type: TransactionType,
    val description: String = ""
)

enum class TransactionType {
    EXTRA_INCOME, // 意外之财
    EXPENSE       // 遭遇打劫
}