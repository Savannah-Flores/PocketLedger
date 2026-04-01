package com.pocketledger.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketledger.app.ui.model.SavingsEntryUiModel
import com.pocketledger.app.viewmodel.SavingsViewModel

private data class SavingsDialogState(
    val title: String,
    val amount: String,
    val editingId: Long? = null,
)

@Composable
fun SavingsScreen(
    viewModel: SavingsViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    var dialogState by remember { mutableStateOf<SavingsDialogState?>(null) }
    var pendingDelete by remember { mutableStateOf<SavingsEntryUiModel?>(null) }

    dialogState?.let { state ->
        var depositTitle by remember(state) { mutableStateOf(state.title) }
        var depositAmount by remember(state) { mutableStateOf(state.amount) }
        val isEditing = state.editingId != null

        AlertDialog(
            onDismissRequest = { dialogState = null },
            title = { Text(if (isEditing) "修改手动存入" else "手动存入") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = depositTitle,
                        onValueChange = { depositTitle = it },
                        singleLine = true,
                        label = { Text("来源") },
                        placeholder = { Text("例如 红包 / 奖金") },
                    )
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it },
                        singleLine = true,
                        label = { Text("金额") },
                        placeholder = { Text("例如 200 / 3000") },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val success = if (isEditing) {
                            viewModel.updateManualDeposit(state.editingId!!, depositTitle, depositAmount)
                        } else {
                            viewModel.addManualDeposit(depositTitle, depositAmount)
                        }
                        if (success) {
                            Toast.makeText(
                                context,
                                if (isEditing) "已更新手动存入" else "已存入小荷包",
                                Toast.LENGTH_SHORT,
                            ).show()
                            dialogState = null
                        } else {
                            Toast.makeText(context, "请输入有效的正数金额", Toast.LENGTH_SHORT).show()
                        }
                    },
                ) {
                    Text(if (isEditing) "更新" else "确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogState = null }) {
                    Text("取消")
                }
            },
        )
    }

    pendingDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("确认删除") },
            text = { Text("将删除“${entry.title}”这条手动存入记录，删除后无法恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        entry.manualDepositId?.let(viewModel::deleteManualDeposit)
                        pendingDelete = null
                    },
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("取消")
                }
            },
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF183A73),
                                    Color(0xFF0E6BA8),
                                ),
                            ),
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "存钱小荷包余额",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.86f),
                    )
                    Text(
                        text = uiState.totalBalance,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "这里只允许入账，不允许转出。系统会自动把已结束月份的正向结余转入小荷包。",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f),
                    )
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "入账来源",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "1. 系统自动转入：已结束月份如果有结余，会在次月 1 号自动记入。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "2. 手动存入：例如红包、奖金，直接进入小荷包，不进入日常流水，也不影响本月支出统计。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(onClick = { dialogState = SavingsDialogState(title = "", amount = "") }) {
                        Text("手动存入")
                    }
                }
            }
        }
        item {
            Text(
                text = "入账明细",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        if (uiState.entries.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "还没有小荷包入账记录。",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(uiState.entries, key = { it.id }) { entry ->
                SavingsEntryCard(
                    entry = entry,
                    onEdit = {
                        entry.manualDepositId?.let {
                            dialogState = SavingsDialogState(
                                title = entry.title,
                                amount = entry.amount.replace("¥", "").replace(",", ""),
                                editingId = it,
                            )
                        }
                    },
                    onDelete = { pendingDelete = entry },
                )
            }
        }
    }
}

@Composable
private fun SavingsEntryCard(
    entry: SavingsEntryUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = entry.source,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "+${entry.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = entry.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (entry.editable) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Outlined.Edit, contentDescription = "编辑")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = "删除")
                        }
                    }
                }
            }
        }
    }
}
