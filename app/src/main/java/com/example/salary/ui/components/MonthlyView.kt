package com.example.salary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.salary.data.DailyIncome
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthlyView(
    dailyIncomes: List<DailyIncome>,
    currentYear: Int,
    modifier: Modifier = Modifier
) {
    val currentMonth = YearMonth.now()
    
    // 按月分组收入数据
    val monthlyData = dailyIncomes
        .filter { it.date.year == currentYear }
        .groupBy { YearMonth.from(it.date) }
        .map { (month, incomes) ->
            MonthlyData(
                month = month,
                totalIncome = incomes.sumOf { it.dailyIncome },
                isCurrentMonth = month == currentMonth
            )
        }
        .sortedByDescending { it.month }
    
    Column(modifier = modifier) {
        Text(
            text = "${currentYear}年",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(monthlyData) { monthData ->
                MonthlyCard(monthData = monthData)
            }
        }
    }
}

@Composable
private fun MonthlyCard(
    monthData: MonthlyData,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (monthData.isCurrentMonth) {
        Color(0xFFE53935)
    } else {
        Color(0xFFFF8A80)
    }
    
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${monthData.month.monthValue}月",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor
                    )
                    if (monthData.isCurrentMonth) {
                        Text(
                            text = "本月",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Text(
                    text = "+${String.format("%.2f", monthData.totalIncome)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

data class MonthlyData(
    val month: YearMonth,
    val totalIncome: Double,
    val isCurrentMonth: Boolean = false
)