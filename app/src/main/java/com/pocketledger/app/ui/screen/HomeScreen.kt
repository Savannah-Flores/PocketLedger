package com.pocketledger.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketledger.app.viewmodel.HomeViewModel
import com.pocketledger.app.viewmodel.SpendingCategorySummary
import com.pocketledger.app.viewmodel.SpendingDetailItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<SpendingCategorySummary?>(null) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "有数",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        MonthlyOverviewCard(
            balance = uiState.balance,
            budgetBaseline = uiState.budgetBaseline,
            todaySpent = uiState.todaySpent,
            monthExpense = uiState.monthExpense,
        )

        MonthlySpendingDistributionCard(
            summaries = uiState.spendingDistribution,
            onCategoryClick = { summary ->
                selectedCategory = summary
            },
        )
    }

    selectedCategory?.let { summary ->
        CategorySpendingDetailSheet(
            summary = summary,
            details = uiState.spendingDetailsByCategory[summary.category].orEmpty(),
            onDismissRequest = { selectedCategory = null },
        )
    }
}

@Composable
private fun MonthlyOverviewCard(
    balance: String,
    budgetBaseline: String,
    todaySpent: String,
    monthExpense: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF5B4DDE),
                            Color(0xFF2C226E),
                        ),
                    ),
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "本月结余",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.92f),
                )
                Text(
                    text = balance,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = "预算基线 $budgetBaseline，每月 1 号自动重置，不结转上月超支。",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.76f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SummaryItem(label = "今日已花", value = todaySpent)
                SummaryItem(label = "本月总支出", value = monthExpense)
            }
        }
    }
}

@Composable
private fun MonthlySpendingDistributionCard(
    summaries: List<SpendingCategorySummary>,
    onCategoryClick: (SpendingCategorySummary) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFF0F0EA))
                .padding(horizontal = 22.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = "本月花销分布",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF242424),
            )

            if (summaries.isEmpty()) {
                Text(
                    text = "本月还没有支出记录。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6E6E68),
                )
            } else {
                summaries.forEach { summary ->
                    SpendingDistributionItem(
                        summary = summary,
                        onClick = { onCategoryClick(summary) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingDistributionItem(
    summary: SpendingCategorySummary,
    onClick: () -> Unit,
) {
    val style = categoryVisualStyle(summary.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .clip(CircleShape)
                .background(style.color.copy(alpha = 0.12f))
                .padding(7.dp),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = style.icon,
                contentDescription = summary.category,
                tint = Color(0xFF5B5B55),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = summary.category,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D2D2A),
                )
                Text(
                    text = summary.formattedAmount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D2D2A),
                )
                Text(
                    text = summary.formattedRatio,
                    modifier = Modifier.padding(start = 18.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D2D2A),
                )
                androidx.compose.material3.Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = "查看${summary.category}明细",
                    modifier = Modifier.padding(start = 6.dp),
                    tint = Color(0xFF8B8B86),
                )
            }

            SpendingProgressBar(
                progress = summary.ratio,
                color = style.color,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySpendingDetailSheet(
    summary: SpendingCategorySummary,
    details: List<SpendingDetailItem>,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xFFFCFCF8),
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFD5D5CF))
                    .padding(horizontal = 24.dp, vertical = 3.dp),
            )

            Text(
                text = "${summary.category}明细",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232321),
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "本月截至当前总支出",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A7A74),
                )
                Text(
                    text = summary.formattedAmount,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C226E),
                )
            }

            if (details.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "本月暂无该分类支出记录",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF7A7A74),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(
                        items = details,
                        key = { it.id },
                    ) { detail ->
                        SpendingDetailRow(detail = detail)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpendingDetailRow(
    detail: SpendingDetailItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = detail.content,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF252523),
            )
            Text(
                text = "${detail.date} ${detail.time}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A7A74),
            )
        }
        Text(
            text = detail.formattedAmount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C226E),
        )
    }

    HorizontalDivider(color = Color(0xFFE4E4DE))
}

@Composable
private fun SpendingProgressBar(
    progress: Float,
    color: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFC9C9C1)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(color),
        )
    }
}

private data class CategoryVisualStyle(
    val icon: ImageVector,
    val color: Color,
)

private fun categoryVisualStyle(category: String): CategoryVisualStyle {
    return when (category) {
        "\u9910\u996E" -> CategoryVisualStyle(Icons.Outlined.Restaurant, Color(0xFF5C48C8))
        "\u5B66\u4E60" -> CategoryVisualStyle(Icons.AutoMirrored.Outlined.MenuBook, Color(0xFF5C9EC7))
        "\u4EA4\u901A" -> CategoryVisualStyle(Icons.Outlined.DirectionsBus, Color(0xFF3AA5A5))
        "\u5A31\u4E50" -> CategoryVisualStyle(Icons.Outlined.SportsEsports, Color(0xFF8A78C8))
        "\u533B\u7597" -> CategoryVisualStyle(Icons.Outlined.LocalHospital, Color(0xFF6FA6B8))
        "\u8863\u670D" -> CategoryVisualStyle(Icons.Outlined.Checkroom, Color(0xFF9E8FB4))
        "\u65E5\u7528\u54C1" -> CategoryVisualStyle(Icons.Outlined.LocalMall, Color(0xFF7C9A87))
        else -> CategoryVisualStyle(Icons.Outlined.MoreHoriz, Color(0xFF6F7168))
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.78f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}
