package com.pocketledger.app.utils

fun inferCategory(content: String): String {
    val text = content.trim()
    if (text.isEmpty()) return "其他"

    return when {
        text.containsAny("饭", "早餐", "午饭", "晚饭", "奶茶", "咖啡", "瑞幸") -> "餐饮"
        text.containsAny("地铁", "公交", "打车", "电动车") -> "交通"
        text.containsAny("教材", "打印", "文具", "GPT") -> "学习"
        text.containsAny("电影", "游戏", "攀岩", "旅游", "火车", "高铁", "飞机", "门票") -> "娱乐"
        text.containsAny("药", "医院") -> "医疗"
        text.containsAny("衣", "裤", "鞋") -> "衣服"
        text.containsAny("纸", "牙膏", "洗发水", "卫生巾") -> "日用品"
        else -> "其他"
    }
}

private fun String.containsAny(vararg keywords: String): Boolean {
    return keywords.any { keyword -> contains(keyword, ignoreCase = true) }
}
