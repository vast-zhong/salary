package com.example.salary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

@Composable
fun WeeklyView(
    dailyIncomes: List<DailyIncome>,
    currentDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val weekFields = WeekFields.of(Locale.getDefault())
    val currentWeek = currentDate.get(weekFields.weekOfYear())
    val currentYear = currentDate.year
    
    // 按周分组收入数据
    val weeklyData = dailyIncomes
        .filter { it.date.year == currentYear }
        .groupBy { it.date.get(weekFields.weekOfYear()) }
        .map { (week, incomes) ->
            val weekStart = incomes.minOf { it.date }
            val weekEnd = incomes.maxOf { it.date }
            val weekTotal = incomes.sumOf { it.dailyIncome }
            
            WeeklyData(
                weekNumber = week,
                weekStart = weekStart,
                weekEnd = weekEnd,
                totalIncome = weekTotal,
                isCurrentWeek = week == currentWeek
            )
        }
        .sortedByDescending { it.weekNumber }
    
    Column(modifier = modifier) {
        Text(
            text = "${currentYear}年 周视图",
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
            items(weeklyData) { weekData ->
                WeeklyCard(weekData = weekData)
            }
        }
    }
}

@Composable
private fun WeeklyCard(
    weekData: WeeklyData,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (weekData.isCurrentWeek) {
        Color(0xFFE53935)
    } else {
        Color(0xFFFFCDD2)
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
                        text = "${weekData.weekStart.format(DateTimeFormatter.ofPattern("MM.dd"))}-${weekData.weekEnd.format(DateTimeFormatter.ofPattern("MM.dd"))}",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    if (weekData.isCurrentWeek) {
                        Text(
                            text = "本周",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Text(
                    text = "+${String.format("%.2f", weekData.totalIncome)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

data class WeeklyData(
    val weekNumber: Int,
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val totalIncome: Double,
    val isCurrentWeek: Boolean = false
)