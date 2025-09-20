package com.example.salary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.salary.ui.theme.SalaryTheme
import com.example.salary.ui.screens.*
import com.example.salary.data.WorkInfo
import com.example.salary.data.ExtraTransaction
import com.example.salary.data.WorkInfoStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalaryTheme {
                SalaryApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryApp() {
    val context = androidx.compose.ui.platform.LocalContext.current

    var workInfo by remember { mutableStateOf<WorkInfo?>(null) }
    var extraTransactions by remember { mutableStateOf<List<ExtraTransaction>>(emptyList()) }
    var selectedBottomTab by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }

    // 启动时加载保存的工作信息
    LaunchedEffect(Unit) {
        workInfo = WorkInfoStorage.load(context)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部应用栏
        TopAppBar(
            title = { Text("财政管理") },
            actions = {
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "设置")
                }
            }
        )

        // 主要内容区域
        Box(modifier = Modifier.weight(1f)) {
            when (selectedBottomTab) {
                0 -> FinancialPlanningScreen(
                    workInfo = workInfo,
                    onWorkInfoUpdated = { newWorkInfo ->
                        workInfo = newWorkInfo
                        // 同步保存
                        WorkInfoStorage.save(context, newWorkInfo)
                    },
                    extraTransactions = extraTransactions,
                    onTransactionAdded = { transaction ->
                        extraTransactions = extraTransactions + transaction
                    }
                )
                1 -> FinancialCalendarScreen(
                    workInfo = workInfo,
                    extraTransactions = extraTransactions
                )
                2 -> FinancialStatusScreen(
                    workInfo = workInfo,
                    extraTransactions = extraTransactions
                )
            }
        }

        // 底部导航栏
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                label = { Text("财政规划") },
                selected = selectedBottomTab == 0,
                onClick = { selectedBottomTab = 0 }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                label = { Text("财政日历") },
                selected = selectedBottomTab == 1,
                onClick = { selectedBottomTab = 1 }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                label = { Text("财政情况") },
                selected = selectedBottomTab == 2,
                onClick = { selectedBottomTab = 2 }
            )
        }

        // 设置对话框
        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("") },
                text = {
                    SettingsScreen()
                },
                confirmButton = {
                    TextButton(onClick = { showSettings = false }) {
                        Text("关闭")
                    }
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}

data class TabItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Preview(showBackground = true)
@Composable
fun SalaryAppPreview() {
    SalaryTheme {
        SalaryApp()
    }
}