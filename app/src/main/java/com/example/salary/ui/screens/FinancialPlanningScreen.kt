package com.example.salary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.salary.data.TransactionType
import com.example.salary.ui.components.WorkInfoInputScreen
import com.example.salary.ui.components.ExtraTransactionDialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialPlanningScreen(
    workInfo: WorkInfo?,
    onWorkInfoUpdated: (WorkInfo) -> Unit,
    extraTransactions: List<ExtraTransaction>,
    onTransactionAdded: (ExtraTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showWorkInfoInput by remember { mutableStateOf(false) }
    var showExtraIncomeDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "财政规划",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 三个功能按钮
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 正经收入按钮
            Card(
                onClick = { showWorkInfoInput = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column {
                        Text(
                            text = "正经收入",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "设置工作信息和基础收入",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // 意外之财按钮
            Card(
                onClick = { showExtraIncomeDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Column {
                        Text(
                            text = "意外之财",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "记录额外收入",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // 遭遇打劫按钮
            Card(
                onClick = { showExpenseDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Column {
                        Text(
                            text = "遭遇打劫",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "记录意外支出",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        
        // 当前工作信息显示
        if (workInfo != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "当前工作信息",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text("月薪: ¥${String.format("%.2f", workInfo.monthlySalary)}")
                    Text("每月工作天数: ${workInfo.workDaysPerMonth}天")
                    Text("每天工作小时数: ${workInfo.workHoursPerDay}小时")
                    Text("开始工作日期: ${workInfo.startDate}")
                    Text("每日收入: ¥${String.format("%.2f", workInfo.getDailyIncome())}")
                    if (workInfo.workHoursPerDay > 0) {
                        Text("每小时收入: ¥${String.format("%.2f", workInfo.getHourlyIncome())}")
                    }
                }
            }
        }
    }
    
    // 工作信息输入对话框
    if (showWorkInfoInput) {
        AlertDialog(
            onDismissRequest = { showWorkInfoInput = false },
            title = { Text("设置工作信息") },
            text = {
                WorkInfoInputScreen(
                     onWorkInfoSubmit = { newWorkInfo ->
                         onWorkInfoUpdated(newWorkInfo)
                         showWorkInfoInput = false
                     }
                 )
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showWorkInfoInput = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 意外之财对话框
    if (showExtraIncomeDialog) {
        ExtraTransactionDialog(
            title = "意外之财",
            onDismiss = { showExtraIncomeDialog = false },
            onConfirm = { amount, description ->
                val transaction = ExtraTransaction(
                    date = LocalDate.now(),
                    amount = amount,
                    type = TransactionType.EXTRA_INCOME,
                    description = description.ifBlank { "意外收入" }
                )
                onTransactionAdded(transaction)
                showExtraIncomeDialog = false
            }
        )
    }
    
    // 遭遇打劫对话框
    if (showExpenseDialog) {
        ExtraTransactionDialog(
            title = "遭遇打劫",
            onDismiss = { showExpenseDialog = false },
            onConfirm = { amount, description ->
                val transaction = ExtraTransaction(
                    date = LocalDate.now(),
                    amount = amount,
                    type = TransactionType.EXPENSE,
                    description = description.ifBlank { "意外支出" }
                )
                onTransactionAdded(transaction)
                showExpenseDialog = false
            }
        )
    }
}