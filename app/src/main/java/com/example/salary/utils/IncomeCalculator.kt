package com.example.salary.utils

import com.example.salary.data.DailyIncome
import com.example.salary.data.IncomeStats
import com.example.salary.data.WorkInfo
import com.example.salary.data.ExtraTransaction
import com.example.salary.data.TransactionType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 收入计算工具类
 */
object IncomeCalculator {
    
    /**
     * 计算从开始日期到指定日期的每日收入列表
     */
    fun calculateDailyIncomes(
        workInfo: WorkInfo,
        extraTransactions: List<ExtraTransaction> = emptyList(),
        endDate: LocalDate = LocalDate.now()
    ): List<DailyIncome> {
        val dailyIncomes = mutableListOf<DailyIncome>()
        var currentDate = workInfo.startDate
        var cumulativeIncome = 0.0
        
        // 按日期分组额外交易
        val transactionsByDate = extraTransactions.groupBy { it.date }
        
        while (!currentDate.isAfter(endDate)) {
            val dailyIncome = if (isWorkingDay(currentDate, workInfo)) {
                workInfo.getDailyIncome()
            } else 0.0
            
            // 计算当日的额外收入和支出
            val dayTransactions = transactionsByDate[currentDate] ?: emptyList()
            val extraIncome = dayTransactions
                .filter { it.type == TransactionType.EXTRA_INCOME }
                .sumOf { it.amount }
            val expense = dayTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { kotlin.math.abs(it.amount) }
            
            val actualDailyIncome = dailyIncome + extraIncome - expense
            cumulativeIncome += actualDailyIncome
            
            dailyIncomes.add(
                DailyIncome(
                    date = currentDate,
                    dailyIncome = dailyIncome,
                    extraIncome = extraIncome,
                    expense = expense,
                    cumulativeIncome = cumulativeIncome
                )
            )
            
            currentDate = currentDate.plusDays(1)
        }
        
        return dailyIncomes
    }
    
    /**
     * 计算收入统计信息
     */
    fun calculateIncomeStats(
        workInfo: WorkInfo,
        extraTransactions: List<ExtraTransaction> = emptyList(),
        targetDate: LocalDate = LocalDate.now()
    ): IncomeStats {
        val dailyIncomes = calculateDailyIncomes(workInfo, extraTransactions, targetDate)
        
        // 计算本周收入（周一到周日）
        val weekStart = targetDate.minusDays(targetDate.dayOfWeek.value - 1L)
        val weekEnd = weekStart.plusDays(6)
        val weeklyTotal = dailyIncomes
            .filter { !it.date.isBefore(weekStart) && !it.date.isAfter(weekEnd) }
            .sumOf { it.actualDailyIncome }
        
        // 计算本月收入
        val monthStart = targetDate.withDayOfMonth(1)
        val monthEnd = targetDate.withDayOfMonth(targetDate.lengthOfMonth())
        val monthlyTotal = dailyIncomes
            .filter { !it.date.isBefore(monthStart) && !it.date.isAfter(monthEnd) }
            .sumOf { it.actualDailyIncome }
        
        // 总收入就是累计收入的最后一个值
        val totalIncome = dailyIncomes.lastOrNull()?.cumulativeIncome ?: 0.0
        
        // 计算总的额外收入和支出
        val totalExtraIncome = dailyIncomes.sumOf { it.extraIncome }
        val totalExpense = dailyIncomes.sumOf { it.expense }
        
        return IncomeStats(
            dailyIncomes = dailyIncomes,
            weeklyTotal = weeklyTotal,
            monthlyTotal = monthlyTotal,
            totalIncome = totalIncome,
            totalExtraIncome = totalExtraIncome,
            totalExpense = totalExpense
        )
    }
    
    /**
     * 获取指定月份的收入数据
     */
    fun getMonthlyIncomes(
        workInfo: WorkInfo,
        extraTransactions: List<ExtraTransaction> = emptyList(),
        year: Int,
        month: Int
    ): List<DailyIncome> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())
        
        return calculateDailyIncomes(workInfo, extraTransactions, endDate)
            .filter { !it.date.isBefore(startDate) && !it.date.isAfter(endDate) }
    }
    
    /**
     * 判断是否为工作日
     */
    private fun isWorkingDay(date: LocalDate, workInfo: WorkInfo): Boolean {
        // 根据工作天数判断是否为工作日
        return when (workInfo.workDaysPerMonth) {
            in 20..23 -> date.dayOfWeek.value <= 5 // 周一到周五
            in 24..26 -> true // 单休（周一到周六）
            else -> date.dayOfWeek.value <= 5 // 默认双休
        }
    }
    
    /**
     * 计算工作天数
     */
    fun calculateWorkDays(startDate: LocalDate, endDate: LocalDate = LocalDate.now()): Long {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1
    }
    
    /**
     * 计算指定月份的工作天数
     */
    fun calculateWorkingDaysInMonth(workInfo: WorkInfo, year: Int, month: Int): Int {
        val startOfMonth = LocalDate.of(year, month, 1)
        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)
        
        var workingDays = 0
        var currentDate = startOfMonth
        
        while (!currentDate.isAfter(endOfMonth)) {
            if (isWorkingDay(currentDate, workInfo)) {
                workingDays++
            }
            currentDate = currentDate.plusDays(1)
        }
        
        return workingDays
    }
}