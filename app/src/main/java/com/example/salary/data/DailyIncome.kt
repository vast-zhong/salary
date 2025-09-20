package com.example.salary.data

import java.time.LocalDate

/**
 * 每日收入数据类
 */
data class DailyIncome(
    val date: LocalDate,        // 日期
    val dailyIncome: Double,    // 基础日收入
    val extraIncome: Double = 0.0, // 意外之财
    val expense: Double = 0.0, // 遭遇打劫（支出）
    val cumulativeIncome: Double // 累计收入
) {
    // 实际当日收入 = 基础收入 + 意外收入 - 支出
    val actualDailyIncome: Double
        get() = dailyIncome + extraIncome - expense
}

/**
 * 收入统计数据类
 */
data class IncomeStats(
    val dailyIncomes: List<DailyIncome>,    // 每日收入列表
    val weeklyTotal: Double,                // 本周总收入
    val monthlyTotal: Double,               // 本月总收入
    val totalIncome: Double,                // 总收入
    val totalExtraIncome: Double, // 总意外收入
    val totalExpense: Double // 总支出
)