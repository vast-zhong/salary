package com.example.salary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salary.data.DailyIncome
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarView(
    dailyIncomes: List<DailyIncome>,
    currentMonth: YearMonth,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

    // 创建日历网格数据
    val calendarDays = mutableListOf<CalendarDay>()

    // 添加上个月的空白天数
    repeat(firstDayOfWeek) {
        calendarDays.add(CalendarDay.Empty)
    }

    // 添加当月的天数
    for (day in 1..lastDayOfMonth.dayOfMonth) {
        val date = currentMonth.atDay(day)
        val income = dailyIncomes.find { it.date == date }
        calendarDays.add(
            CalendarDay.Day(
                date = date,
                income = income,
                isToday = date == today
            )
        )
    }

    // 固定的中性色板（与深浅色模式无关）
    val NeutralDayBg = Color(0xFFE0E0E0)       // 灰 300
    val NeutralTodayBg = Color(0xFFBDBDBD)     // 灰 400（今日略突出）
    val IncomeBg = Color(0xFFFF8A80)           // 固定浅红色表示有收入
    val NeutralText = Color(0xFF212121)        // 深灰文字
    val NeutralHint = Color(0xFF757575)        // 次级文字
    val WeekdayText = Color(0xFF9E9E9E)        // 星期标题文字

    Column(modifier = modifier) {
        // 月份标题（保持现有主题样式，不强制固定颜色）
        Text(
            text = "${currentMonth.year}年 ${currentMonth.monthValue}月",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        // 星期标题（固定为中性灰）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeekdayText
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 日历网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(calendarDays) { calendarDay ->
                when (calendarDay) {
                    is CalendarDay.Empty -> {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                        )
                    }
                    is CalendarDay.Day -> {
                        CalendarDayItem(
                            day = calendarDay,
                            onClick = { onDateClick(calendarDay.date) },
                            // 传递固定色板
                            neutralDayBg = NeutralDayBg,
                            neutralTodayBg = NeutralTodayBg,
                            incomeBg = IncomeBg,
                            neutralText = NeutralText,
                            neutralHint = NeutralHint
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: CalendarDay.Day,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    neutralDayBg: Color = Color(0xFFE0E0E0),
    neutralTodayBg: Color = Color(0xFFBDBDBD),
    incomeBg: Color = Color(0xFFFF8A80),
    neutralText: Color = Color(0xFF212121),
    neutralHint: Color = Color(0xFF757575)
) {
    val backgroundColor = when {
        day.income != null && day.income.dailyIncome > 0 -> incomeBg // 固定浅红色表示有收入
        day.isToday -> neutralTodayBg
        else -> neutralDayBg
    }

    val textColor = when {
        day.income != null && day.income.dailyIncome > 0 -> Color.White
        else -> neutralText
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (day.income != null) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                fontSize = 16.sp,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )

            if (day.income != null && day.income.dailyIncome > 0) {
                Text(
                    text = "+${String.format("%.2f", day.income.dailyIncome)}",
                    fontSize = 10.sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            } else if (day.date.isBefore(LocalDate.now()) || day.date.isEqual(LocalDate.now())) {
                Text(
                    text = "0.00",
                    fontSize = 10.sp,
                    color = neutralHint,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

sealed class CalendarDay {
    object Empty : CalendarDay()
    data class Day(
        val date: LocalDate,
        val income: DailyIncome?,
        val isToday: Boolean = false
    ) : CalendarDay()
}