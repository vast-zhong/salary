package com.example.salary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salary.data.WorkInfo
import com.example.salary.data.ExtraTransaction
import com.example.salary.ui.components.CalendarView
import com.example.salary.ui.components.WeeklyView
import com.example.salary.ui.components.MonthlyView
import com.example.salary.utils.IncomeCalculator
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialCalendarScreen(
    workInfo: WorkInfo?,
    extraTransactions: List<ExtraTransaction>,
    modifier: Modifier = Modifier
) {
    var selectedViewType by remember { mutableStateOf(0) } // 0=日, 1=周, 2=月, 3=年
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val viewTypes = listOf("日", "周", "月", "年")

    if (workInfo == null) {
        // 如果没有工作信息，显示提示
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "请先在财政规划中设置工作信息",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部切换条放置在中间上方（参考示例）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val types = viewTypes
            SegmentedButtons(
                options = types,
                selectedIndex = selectedViewType,
                onSelected = { selectedViewType = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 根据选择的视图类型显示不同内容
        when (selectedViewType) {
            0 -> {
                // 日历视图（支持切换月份 + 选中态高亮）
                val dailyIncomes = remember(workInfo, extraTransactions) {
                    IncomeCalculator.calculateDailyIncomes(workInfo, extraTransactions)
                }

                // 限制最早不能早于工作开始月份，最晚不超过当前月份+12（可按需调整）
                val minMonth = YearMonth.from(workInfo.startDate)
                val maxMonth = YearMonth.from(LocalDate.now()).plusMonths(12)

                CalendarView(
                    dailyIncomes = dailyIncomes,
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    onDateClick = { date ->
                        selectedDate = date
                    },
                    onPrevMonth = {
                        val prev = currentMonth.minusMonths(1)
                        currentMonth = if (!prev.isBefore(minMonth)) prev else minMonth
                    },
                    onNextMonth = {
                        val next = currentMonth.plusMonths(1)
                        currentMonth = if (!next.isAfter(maxMonth)) next else maxMonth
                    },
                    canGoPrev = currentMonth.isAfter(minMonth),
                    canGoNext = currentMonth.isBefore(maxMonth)
                )
            }
            1 -> {
                // 周视图
                val dailyIncomes = remember(workInfo, extraTransactions) {
                    IncomeCalculator.calculateDailyIncomes(workInfo, extraTransactions)
                }

                WeeklyView(
                    dailyIncomes = dailyIncomes,
                    currentDate = selectedDate
                )
            }
            2 -> {
                // 月视图
                val dailyIncomes = remember(workInfo, extraTransactions) {
                    IncomeCalculator.calculateDailyIncomes(workInfo, extraTransactions)
                }

                MonthlyView(
                    dailyIncomes = dailyIncomes,
                    currentYear = selectedDate.year
                )
            }
            3 -> {
                // 年视图（暂时显示占位符）
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "年视图功能开发中...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedButtons(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = selectedIndex == index,
                onClick = { onSelected(index) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), textAlign = TextAlign.Center)
            }
        }
    }
}