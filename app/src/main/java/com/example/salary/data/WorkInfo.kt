package com.example.salary.data

import java.time.LocalDate

/**
 * 工作信息数据类
 */
data class WorkInfo(
    val monthlySalary: Double,      // 月薪
    val workDaysPerMonth: Int,      // 每月工作天数
    val workHoursPerDay: Int,       // 每天工作小时数
    val startDate: LocalDate        // 开始工作日期
) {
    /**
     * 计算每日收入
     */
    fun getDailyIncome(): Double {
        return monthlySalary / workDaysPerMonth
    }
    
    /**
     * 计算每小时收入
     */
    fun getHourlyIncome(): Double {
        return getDailyIncome() / workHoursPerDay
    }
}