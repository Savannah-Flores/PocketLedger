package com.pocketledger.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    SpendingDistributionItem(summary = summary)
                }
            }
        }
    }
}

@Composable
private fun SpendingDistributionItem(
    summary: SpendingCategorySummary,
) {
    val style = categoryVisualStyle(summary.category)

    Row(
        modifier = Modifier.fillMaxWidth(),
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
            }

            SpendingProgressBar(
                progress = summary.ratio,
                color = style.color,
            )
        }
    }
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
        "餐饮" -> CategoryVisualStyle(Icons.Outlined.Restaurant, Color(0xFF5C48C8))
        "学习" -> CategoryVisualStyle(Icons.Outlined.MenuBook, Color(0xFF5C9EC7))
        "交通" -> CategoryVisualStyle(Icons.Outlined.DirectionsBus, Color(0xFF3AA5A5))
        "娱乐" -> CategoryVisualStyle(Icons.Outlined.SportsEsports, Color(0xFF8A78C8))
        "医疗" -> CategoryVisualStyle(Icons.Outlined.LocalHospital, Color(0xFF6FA6B8))
        "衣服" -> CategoryVisualStyle(Icons.Outlined.Checkroom, Color(0xFF9E8FB4))
        "日用品" -> CategoryVisualStyle(Icons.Outlined.LocalMall, Color(0xFF7C9A87))
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
