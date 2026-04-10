package com.pocketledger.app.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardBackspace
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketledger.app.utils.availableCategories
import com.pocketledger.app.viewmodel.AddRecordViewModel
import com.pocketledger.app.viewmodel.RecordType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val keypadRows = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(".", "0", "del"),
)

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRecordScreen(
    viewModel: AddRecordViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showCategoryDialog by remember { mutableStateOf(false) }

    if (showCategoryDialog) {
        CategoryPickerDialog(
            selectedCategory = uiState.category,
            onCategorySelected = { category ->
                viewModel.selectCategory(category)
                showCategoryDialog = false
            },
            onResetToAuto = {
                viewModel.resetCategoryToAuto()
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.title, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "返回",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Button(
                    onClick = {
                        viewModel.saveRecord { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            onSaved()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(text = uiState.saveButtonLabel, style = MaterialTheme.typography.titleMedium)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AmountHeroCard(
                amount = uiState.amount,
                modifier = Modifier.fillMaxWidth(),
            )

            CompactInfoPanel(
                content = uiState.content,
                selectedType = uiState.selectedType,
                date = uiState.date,
                time = uiState.time,
                category = uiState.category,
                isCategoryManual = uiState.isCategoryManual,
                onContentChange = viewModel::updateContent,
                onTypeSelected = viewModel::selectType,
                onDateClick = {
                    showDatePickerDialog(
                        context = context,
                        currentDate = uiState.date,
                        onDateSelected = viewModel::updateDate,
                    )
                },
                onTimeClick = {
                    showTimePickerDialog(
                        context = context,
                        currentTime = uiState.time,
                        onTimeSelected = viewModel::updateTime,
                    )
                },
                onCategoryClick = { showCategoryDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            KeypadCard(
                onKeyClick = { key ->
                    if (key == "del") viewModel.deleteLast() else viewModel.appendNumber(key)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryPickerDialog(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onResetToAuto: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择分类") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "可以保留自动识别，也可以手动改成更准确的分类。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    availableCategories.forEach { category ->
                        val selected = selectedCategory == category
                        AssistChip(
                            onClick = { onCategorySelected(category) },
                            label = { Text(category) },
                            colors = if (selected) {
                                AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                AssistChipDefaults.assistChipColors()
                            },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onResetToAuto) {
                Text("恢复自动")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        },
    )
}

@Composable
private fun AmountHeroCard(
    amount: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E1B3D),
                            Color(0xFF4E40D6),
                        ),
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "本次金额",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.78f),
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactInfoPanel(
    content: String,
    selectedType: RecordType,
    date: String,
    time: String,
    category: String,
    isCategoryManual: Boolean,
    onContentChange: (String) -> Unit,
    onTypeSelected: (RecordType) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("内容") },
                placeholder = { Text("早饭 / 地铁 / 打印资料") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            )

            RecordTypeSegmentedRow(
                selectedType = selectedType,
                onTypeSelected = onTypeSelected,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                InfoChipCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
                    title = "日期",
                    value = date,
                    onClick = onDateClick,
                )
                InfoChipCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Rounded.AccessTime, contentDescription = null) },
                    title = "时间",
                    value = time,
                    onClick = onTimeClick,
                )
            }

            CategoryCompactCard(
                category = category,
                isManual = isCategoryManual,
                onClick = onCategoryClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordTypeSegmentedRow(
    selectedType: RecordType,
    onTypeSelected: (RecordType) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        RecordType.entries.forEachIndexed { index, type ->
            SegmentedButton(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = RecordType.entries.size,
                ),
                label = { Text(type.label) },
            )
        }
    }
}

@Composable
private fun InfoChipCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    title: String,
    value: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon()
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun CategoryCompactCard(
    category: String,
    isManual: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isManual) "分类（手动）" else "自动分类",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "修改",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "修改分类",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun KeypadCard(
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            keypadRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { key ->
                        KeypadButton(
                            modifier = Modifier.weight(1f),
                            label = key,
                            onClick = { onKeyClick(key) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
) {
    val isDelete = label == "del"
    Card(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (isDelete) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardBackspace,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

private fun showDatePickerDialog(
    context: android.content.Context,
    currentDate: String,
    onDateSelected: (String) -> Unit,
) {
    val parsedDate = runCatching { LocalDate.parse(currentDate, dateFormatter) }.getOrDefault(LocalDate.now())
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(selectedDate.format(dateFormatter))
        },
        parsedDate.year,
        parsedDate.monthValue - 1,
        parsedDate.dayOfMonth,
    ).show()
}

private fun showTimePickerDialog(
    context: android.content.Context,
    currentTime: String,
    onTimeSelected: (String) -> Unit,
) {
    val parsedTime = runCatching { LocalTime.parse(currentTime, timeFormatter) }.getOrDefault(LocalTime.now())
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val selectedTime = LocalTime.of(hourOfDay, minute)
            onTimeSelected(selectedTime.format(timeFormatter))
        },
        parsedTime.hour,
        parsedTime.minute,
        true,
    ).show()
}


