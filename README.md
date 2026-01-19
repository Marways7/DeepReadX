# DeepReadX

<p align="center">
  <strong>AI 辅助理解的 PDF 文档阅读 Android 应用</strong>
</p>

<p align="center">
  <em>当你遇到晦涩难懂的内容，让 AI 用你习惯的风格重新讲清楚。</em>
</p>

<p align="center">
  <img alt="status" src="https://img.shields.io/badge/status-MVP%20demo-blue" />
  <img alt="course" src="https://img.shields.io/badge/course-JAVA%20%E8%AF%BE%E7%A8%8B%E8%AE%BE%E8%AE%A1-orange" />
  <img alt="timeline" src="https://img.shields.io/badge/timeline-2025%20%E5%B9%B45%E6%9C%88-green" />
  <img alt="license" src="https://img.shields.io/badge/license-MIT-black" />
</p>

> **DeepReadX** 是基于 AI 辅助理解的 PDF 文档阅读 Android 应用：当用户遇到晦涩难懂的地方，可以通过**自定义讲解风格**，将当前页面内容转化为通俗易懂的语言。
>
> 本项目为 **JAVA 课程设计**，当前仅完成核心功能，是一个 **MVP demo**，整体实现偏简陋但链路完整，便于继续迭代与扩展。

## ✨ 项目亮点

- **完整阅读链路**：PDF 选取 → 页面渲染 → OCR 识别 → AI 解释 → 历史记录沉淀。
- **可扩展的解释风格**：支持多种解释模板，便于拓展为学习模式、学术模式或口语化模式。
- **移动端工程实践**：涵盖网络请求、数据库/缓存、权限与 UI 架构等课程关键点。

## 🧭 功能概览

- **PDF 阅读与分页浏览**：支持从本地选择 PDF 并进行页面渲染与翻页。
- **OCR 文字识别**：使用 ML Kit 对页面内容进行识别。
- **AI 智能解释**：基于 DeepSeek API 对识别文本进行风格化解释。
- **风格管理与历史记录**：支持风格管理、解释历史回溯。

## 🧱 技术栈

- **语言**：Java
- **平台**：Android
- **OCR**：Google ML Kit
- **网络**：OkHttp
- **UI 组件**：AppCompat / RecyclerView / Material Components

## 🗂️ 项目结构（概览）

```
app/src/main/java/com/deepreadx/
├── api/            # DeepSeek API 客户端
├── dao/            # 数据访问与风格管理
├── model/          # 业务模型
├── ocr/            # OCR 处理逻辑
├── ui/             # UI 页面与列表
└── viewer/         # PDF 视图与解释入口
```

## 🧩 运行流程（数据流）

1. 用户选择本地 PDF。
2. 页面渲染为可视内容。
3. OCR 识别页面文字。
4. 识别文本通过 DeepSeek API 生成解释。
5. 解释结果可按风格展示，并写入历史记录。

## 🚀 快速开始

### 1) 环境准备

- Android Studio（推荐最新稳定版）
- Android SDK 24+
- JDK 11+

### 2) 配置 DeepSeek API

> ⚠️ 出于安全考虑，**请勿将真实 Key 写入源码**，推荐通过环境变量注入。

复制并配置 `.env`：

```bash
cp .env.example .env
```

在终端中加载环境变量（示例）：

```bash
set -a
source .env
set +a
```

如需在 Android Studio 中构建，可在启动 Studio 的终端中先执行上述命令，确保环境变量传递给 Gradle。

### 3) 构建与运行

使用 Android Studio 打开项目后直接运行；或使用 Gradle：

```bash
./gradlew :app:assembleDebug
```

## 🔐 安全说明

- API Key 通过环境变量注入到 BuildConfig，避免硬编码。
- 建议将 `.env` 添加到 `.gitignore`，不要提交到仓库。

## 📌 课程背景

本项目为 **JAVA 课程设计**，旨在综合展示 Android 工程能力、网络通信、OCR 识别与 AI 服务集成能力，满足课程对完整系统设计与实现的要求。

## 🛣️ 未来规划

- 更稳定的 PDF 渲染与缩放体验
- 解释风格的在线分享与导入
- 离线 OCR 与缓存策略
- 多语言识别与翻译联动

## 🤝 参与贡献

欢迎提交 Issue 和 PR，建议包含：

- 清晰的问题复现路径或功能描述
- 关联的截图或日志
- 变更动机与影响评估

## 📄 许可证

本项目采用 **MIT License** 开源，详情见 [LICENSE](./LICENSE)。
