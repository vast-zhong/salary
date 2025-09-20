package com.example.salary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.salary.data.WorkInfo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkInfoInputScreen(
    onWorkInfoSubmit: (WorkInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var monthlySalary by remember { mutableStateOf("3000") }
    var workDaysPerMonth by remember { mutableStateOf("22") }
    var workHoursPerDay by remember { mutableStateOf("8") }
    var startDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "工作信息设置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        // 月薪输入
        OutlinedTextField(
            value = monthlySalary,
            onValueChange = { monthlySalary = it },
            label = { Text("月薪 (元)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        
        // 每月工作天数
        OutlinedTextField(
            value = workDaysPerMonth,
            onValueChange = { workDaysPerMonth = it },
            label = { Text("每月工作天数") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        // 每天工作小时数
        OutlinedTextField(
            value = workHoursPerDay,
            onValueChange = { workHoursPerDay = it },
            label = { Text("每天工作小时数") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        // 开始工作日期
        OutlinedTextField(
            value = if (startDate.isEmpty()) selectedDate.format(dateFormatter) else startDate,
            onValueChange = { },
            label = { Text("开始工作日期") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text("选择日期")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 提交按钮
        Button(
            onClick = {
                val salary = monthlySalary.toDoubleOrNull()
                val workDays = workDaysPerMonth.toIntOrNull()
                val workHours = workHoursPerDay.toIntOrNull()
                
                if (salary != null && workDays != null && workHours != null) {
                    val workInfo = WorkInfo(
                        monthlySalary = salary,
                        workDaysPerMonth = workDays,
                        workHoursPerDay = workHours,
                        startDate = selectedDate
                    )
                    onWorkInfoSubmit(workInfo)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = monthlySalary.isNotEmpty() && 
                     workDaysPerMonth.isNotEmpty() && 
                     workHoursPerDay.isNotEmpty()
        ) {
            Text("开始记录收入", style = MaterialTheme.typography.titleMedium)
        }
        
        // 预览信息
        if (monthlySalary.isNotEmpty() && workDaysPerMonth.isNotEmpty()) {
            val salary = monthlySalary.toDoubleOrNull()
            val workDays = workDaysPerMonth.toIntOrNull()
            val workHours = workHoursPerDay.toIntOrNull()
            
            if (salary != null && workDays != null && workHours != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "收入预览",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("每日收入: ¥${String.format("%.2f", salary / workDays)}")
                        if (workHours > 0) {
                            Text("每小时收入: ¥${String.format("%.2f", salary / workDays / workHours)}")
                        }
                    }
                }
            }
        }
    }
    
    // 日期选择器：Material3 DatePickerDialog，可选择任意日期
    if (showDatePicker) {
        val initialMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val picked = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            selectedDate = picked
                            startDate = picked.format(dateFormatter)
                        }
                        showDatePicker = false
                    }
                ) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = true
            )
        }
    }
}