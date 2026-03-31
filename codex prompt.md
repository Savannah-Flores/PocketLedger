# 项目骨架
```
你现在是一个资深 Android 开发工程师，请帮我从零创建一个 Android App 项目骨架，使用 Kotlin + Jetpack Compose + Material3。

项目名称：PocketLedger（中文名：“口袋账本”）

【技术要求】
- Kotlin
- Jetpack Compose UI
- Material3 设计体系
- 使用 MVVM 架构（ViewModel）
- 使用 Room 作为本地数据库（后续用）
- Navigation Compose 做页面导航

【整体目标】
先搭建完整项目结构 + 页面骨架，不需要实现具体业务逻辑。

【需要完成的内容】

1. 项目结构设计：
- data（数据层）
- ui（界面层）
- viewmodel（状态管理）
- navigation（导航）
- theme（主题）

2. 底部导航栏（Bottom Navigation）包含5个页面：
- HomeScreen（首页）
- RecordScreen（记录页）
- CalendarScreen（日历页）
- SavingsScreen（存钱小荷包）
- SettingsScreen（设置页）

3. 每个页面先用占位UI：
- 显示页面标题（居中）
- 简单卡片结构（Material3 Card）

4. 首页（HomeScreen）特殊要求：
- 顶部一个大卡片（深色渐变风格）
- 显示：
  - 当前结余（大字体）
  - 今日已花
  - 本月总支出
（先写死假数据）

5. 主题设计（非常重要）：
- 支持浅色 + 深色模式
- 主色：紫色（偏蓝紫）
- 使用 Material3 动态配色结构
- 卡片圆角：16dp以上
- 整体风格偏现代、简洁

6. Navigation：
- 使用 Navigation Compose
- 底部导航切换页面

7. 提供：
- 完整代码结构
- 关键文件代码
- 每个模块说明

【注意】
先不要实现数据库和业务逻辑，只搭UI骨架和结构。
代码要规范、可运行。
```