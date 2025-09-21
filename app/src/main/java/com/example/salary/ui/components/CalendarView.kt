package com.example.salary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    selectedDate: LocalDate? = null,
    onDateClick: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    canGoPrev: Boolean = true,
    canGoNext: Boolean = true,
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
    val IncomeBg = Color(0xFFFF8A80)           // 红收入未选中）
    val SelectedBg = Color(0xFFE53935)         // 深红（选中）
    val NeutralText = Color(0xFF212121)        // 深灰文字
    val NeutralHint = Color(0xFF757575)        // 次级文字
    val WeekdayText = Color(0xFF9E9E9E)        // 星期标题文字

    Column(modifier = modifier) {
        // 顶部：月份切换（居中）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevMonth, enabled = canGoPrev) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "上个月"
                )
            }

            Text(
                text = "${currentMonth.year}年 ${currentMonth.monthValue}月",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            IconButton(onClick = onNextMonth, enabled = canGoNext) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "下个月"
                )
            }
        }

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

        // 日历网格（根据屏幕宽度动态计算单元尺寸）
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columns = 7
            val gridHorizontalPadding = 16.dp // contentPadding 水平合计 8dp*2
            val columnSpacing = 4.dp
            val totalSpacing = columnSpacing * (columns - 1)
            val cellSize = (maxWidth - gridHorizontalPadding - totalSpacing) / columns

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(columnSpacing),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(calendarDays) { calendarDay ->
                    when (calendarDay) {
                        is CalendarDay.Empty -> {
                            Box(
                                modifier = Modifier
                                    .requiredSize(cellSize)
                            ) {}
                        }
                        is CalendarDay.Day -> {
                            CalendarDayItem(
                                day = calendarDay,
                                selected = selectedDate != null && calendarDay.date == selectedDate,
                                onClick = { onDateClick(calendarDay.date) },
                                cellSize = cellSize,
                                neutralDayBg = NeutralDayBg,
                                neutralTodayBg = NeutralTodayBg,
                                incomeBg = IncomeBg,
                                selectedBg = SelectedBg,
                                neutralText = NeutralText,
                                neutralHint = NeutralHint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: CalendarDay.Day,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cellSize: Dp? = null,
    neutralDayBg: Color = Color(0xFFE0E0E0),
    neutralTodayBg: Color = Color(0xFFBDBDBD),
    incomeBg: Color = Color(0xFFFFCDD2),
    selectedBg: Color = Color(0xFFE53935),
    neutralText: Color = Color(0xFF212121),
    neutralHint: Color = Color(0xFF757575)
) {
    val backgroundColor = when {
        selected -> selectedBg // 选中优先深红
        day.income != null && day.income.dailyIncome > 0 -> incomeBg // 未选中但有收入浅红
        day.isToday -> neutralTodayBg
        else -> neutralDayBg
    }

    val textColor = when {
        selected -> Color.White
        else -> neutralText
    }

    val baseModifier = if (cellSize != null) {
        modifier.requiredSize(cellSize)
    } else {
        modifier.aspectRatio(1f)
    }

    // 动态字体：随单元尺寸略缩放，兼顾小屏
    val dayFont = if (cellSize != null) (cellSize.value * 0.25f).sp else 16.sp
    val incomeFont = if (cellSize != null) (cellSize.value * 0.20f).sp else 10.sp

    Card(
        modifier = baseModifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (day.income != null) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                fontSize = dayFont,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                maxLines = 1
            )

            val incomeValue = day.income?.dailyIncome
            if (incomeValue != null) {
                val incomeText = when {
                    incomeValue > 0 -> "+" + String.format("%.2f", incomeValue)
                    incomeValue < 0 -> String.format("%.2f", incomeValue)
                    else -> "0.00"
                }
                Text(
                    text = incomeText,
                    fontSize = incomeFont,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            } else if (day.date.isBefore(LocalDate.now()) || day.date.isEqual(LocalDate.now())) {
                Text(
                    text = "0.00",
                    fontSize = incomeFont,
                    color = neutralText,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            } else {
                // 未来日期，无收入则不显示
                Spacer(modifier = Modifier.height(0.dp))
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