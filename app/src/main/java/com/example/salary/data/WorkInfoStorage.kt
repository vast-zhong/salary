package com.example.salary.data

import android.content.Context
import java.time.LocalDate

/**
 * 简单的本地持久化：使用 SharedPreferences 保存/读取 WorkInfo。
 * 目的：应用重启后仍能恢复工作信息。
 */
object WorkInfoStorage {
    private const val PREFS_NAME = "work_info_prefs"
    private const val KEY_MONTHLY_SALARY = "monthly_salary"
    private const val KEY_WORK_DAYS_PER_MONTH = "work_days_per_month"
    private const val KEY_WORK_HOURS_PER_DAY = "work_hours_per_day"
    private const val KEY_START_DATE_EPOCH = "start_date_epoch"

    fun save(context: Context, info: WorkInfo) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit()
            .putString(KEY_MONTHLY_SALARY, info.monthlySalary.toString()) // 用 String 避免 Double 精度丢失
            .putInt(KEY_WORK_DAYS_PER_MONTH, info.workDaysPerMonth)
            .putInt(KEY_WORK_HOURS_PER_DAY, info.workHoursPerDay)
            .putLong(KEY_START_DATE_EPOCH, info.startDate.toEpochDay())
            .apply()
    }

    fun load(context: Context): WorkInfo? {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val salaryStr = sp.getString(KEY_MONTHLY_SALARY, null) ?: return null
        val workDays = sp.getInt(KEY_WORK_DAYS_PER_MONTH, -1)
        val workHours = sp.getInt(KEY_WORK_HOURS_PER_DAY, -1)
        val epoch = sp.getLong(KEY_START_DATE_EPOCH, Long.MIN_VALUE)
        val salary = salaryStr.toDoubleOrNull()

        if (salary == null || workDays <= 0 || workHours <= 0 || epoch == Long.MIN_VALUE) return null
        return WorkInfo(
            monthlySalary = salary,
            workDaysPerMonth = workDays,
            workHoursPerDay = workHours,
            startDate = LocalDate.ofEpochDay(epoch)
        )
    }

    fun clear(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit().clear().apply()
    }
}