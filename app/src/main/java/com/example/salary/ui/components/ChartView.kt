package com.example.salary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salary.data.DailyIncome
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Composable
fun ChartView(
    dailyIncomes: List<DailyIncome>,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    // 获取选择日期前后一周的数据
    val startDate = selectedDate.minusDays(3)
    val endDate = selectedDate.plusDays(3)
    
    val chartData = dailyIncomes
        .filter { it.date >= startDate && it.date <= endDate }
        .sortedBy { it.date }
    
    val maxIncome = chartData.maxOfOrNull { it.dailyIncome } ?: 0.0
    val selectedIncome = chartData.find { it.date == selectedDate }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题和日期
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日")),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        
        // 当日收入显示
        if (selectedIncome != null) {
            Text(
                text = "+${String.format("%.2f", selectedIncome.dailyIncome)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // 柱状图
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (chartData.isNotEmpty()) {
                    BarChart(
                        data = chartData,
                        maxValue = maxIncome,
                        selectedDate = selectedDate,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "暂无数据",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // 日期范围显示
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = startDate.format(DateTimeFormatter.ofPattern("MM-dd")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = endDate.format(DateTimeFormatter.ofPattern("MM-dd")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BarChart(
    data: List<DailyIncome>,
    maxValue: Double,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    Canvas(modifier = modifier) {
        if (data.isEmpty() || maxValue <= 0) return@Canvas
        
        val barWidth = size.width / data.size * 0.6f
        val barSpacing = size.width / data.size * 0.4f
        val maxBarHeight = size.height * 0.8f
        
        data.forEachIndexed { index, income ->
            val barHeight = if (maxValue > 0) {
                (income.dailyIncome / maxValue * maxBarHeight).toFloat()
            } else 0f
            
            val x = index * (barWidth + barSpacing) + barSpacing / 2
            val y = size.height - barHeight
            
            val color = if (income.date == selectedDate) primaryColor else surfaceVariant
            
            // 绘制柱子
            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
        
        // 绘制基准线
        drawLine(
            color = surfaceVariant,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2.dp.toPx()
        )
    }
}