package com.example.salary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        // 标题和视图切换
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "财政日历",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // 视图类型切换按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                viewTypes.forEachIndexed { index, type ->
                    FilterChip(
                        onClick = { selectedViewType = index },
                        label = { Text(type) },
                        selected = selectedViewType == index,
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 根据选择的视图类型显示不同内容
        when (selectedViewType) {
            0 -> {
                // 日历视图
                val dailyIncomes = remember(workInfo, extraTransactions) {
                    IncomeCalculator.calculateDailyIncomes(workInfo, extraTransactions)
                }
                
                CalendarView(
                    dailyIncomes = dailyIncomes,
                    currentMonth = currentMonth,
                    onDateClick = { date ->
                        selectedDate = date
                    }
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