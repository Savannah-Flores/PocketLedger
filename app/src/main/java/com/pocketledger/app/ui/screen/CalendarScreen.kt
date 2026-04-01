package com.pocketledger.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketledger.app.ui.calendar.CalendarDayCellUiModel
import com.pocketledger.app.ui.model.RecordItemUiModel
import com.pocketledger.app.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val weekdayLabels = listOf("一", "二", "三", "四", "五", "六", "日")
private val selectedDateFormatter = DateTimeFormatter.ofPattern("M月d日")

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            CalendarHeader(
                title = uiState.currentMonthLabel,
                onPreviousMonth = viewModel::goToPreviousMonth,
                onNextMonth = viewModel::goToNextMonth,
            )
        }
        item {
            CalendarGrid(
                dates = uiState.currentMonthDates,
                selectedDate = uiState.selectedDate,
                onDateSelected = viewModel::selectDate,
            )
        }
        item {
            SelectedDateSummary(
                selectedDate = uiState.selectedDate,
                expense = uiState.selectedDateExpense,
            )
        }
        if (uiState.selectedDateRecords.isEmpty()) {
            item {
                EmptyDayCard()
            }
        } else {
            items(uiState.selectedDateRecords, key = { it.id }) { record ->
                DayRecordCard(record = record)
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    title: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = "上一月")
        }
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = "下一月")
        }
    }
}

@Composable
private fun CalendarGrid(
    dates: List<CalendarDayCellUiModel>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    val maxExpense = dates.maxOfOrNull { it.expenseTotal } ?: 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdayLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            dates.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    week.forEach { day ->
                        CalendarDayCell(
                            modifier = Modifier.weight(1f),
                            day = day,
                            isSelected = day.date == selectedDate,
                            maxExpense = maxExpense,
                            onClick = { onDateSelected(day.date) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    modifier: Modifier = Modifier,
    day: CalendarDayCellUiModel,
    isSelected: Boolean,
    maxExpense: Double,
    onClick: () -> Unit,
) {
    val heatRatio = if (maxExpense == 0.0) 0f else (day.expenseTotal / maxExpense).toFloat().coerceIn(0f, 1f)
    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (day.isInCurrentMonth) 0.8f else 0.35f)
    val heatColor = lerp(baseColor, MaterialTheme.colorScheme.primary.copy(alpha = 0.95f), heatRatio)
    val background = if (isSelected) MaterialTheme.colorScheme.primary else heatColor
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (day.isInCurrentMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)

    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(top = 10.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
        )
        if (day.expenseTotal > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = if (isSelected) 0.18f else 0.55f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = day.expenseTotal.toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                )
            }
        } else {
            Box(modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SelectedDateSummary(
    selectedDate: LocalDate,
    expense: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${selectedDate.format(selectedDateFormatter)} 总支出",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = expense,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "热力深浅只统计当天支出，不计算收入与退款。下方展示该日全部记录。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptyDayCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = "这一天还没有记录。",
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DayRecordCard(record: RecordItemUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = record.content,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${record.type.label} · ${record.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = record.amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = record.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
