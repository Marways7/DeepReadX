# DeepReadX 变更记录

## [2025-05-20]

### 修复 (Fixed)
- 修复DeepSeek API调用422错误问题 - [api] 优化请求参数结构，确保符合最新API规范 (@DeepReadX团队)
- 增强API诊断能力 - [api] 添加OkHttp日志拦截器和详细错误捕获 (@DeepReadX团队)
- 改进用户错误反馈 - [ui] 用Snackbar替换Toast，提供错误重试功能 (@DeepReadX团队)

### 新增 (Added)
- 添加DeepSeekClient单元测试 - [test] 验证API响应处理和错误恢复 (@DeepReadX团队)

## [2025-05-18-4]

### 修改 (Changed)
- 将PDF查看器Activity设置为应用主入口点 - [config] (@DeepReadX团队)
- 修改MainActivity以直接启动PDF查看器 - [ui] (@DeepReadX团队)
- 改进PDF查看器启动逻辑，支持直接选择PDF文件 - [pdf-viewer] (@DeepReadX团队)

## [2025-05-18-3]

### 修复 (Fixed)
- 修复DeepSeekClient.requestExplanation方法调用参数不匹配问题 - [api] (@DeepReadX团队)
- 更新API调用以适配新的方法签名，添加风格参数 - [pdf-viewer] (@DeepReadX团队)

## [2025-05-18-2]

### 修复 (Fixed)
- 修复布局文件中的资源链接错误，更新主题样式 - [ui] (@DeepReadX团队)
- 修改主题从基本Android主题到MaterialComponents主题 - [ui] (@DeepReadX团队)
- 添加缺失的布局依赖库，解决构建错误 - [config] (@DeepReadX团队)
- 优化布局文件结构，添加必要的属性和描述 - [ui] (@DeepReadX团队)

## [2025-05-16]

### 新增 (Added)
- 创建解释历史记录模块，记录用户查询过的所有AI解释内容 - [history] (@DeepReadX团队)
- 实现ExplanationHistory模型类，用于存储历史记录数据 - [model] (@DeepReadX团队)
- 创建HistoryDbHelper类，管理历史记录数据库表 - [database] (@DeepReadX团队)
- 实现ExplanationHistoryDao接口和相关实现类，提供历史数据访问接口 - [dao] (@DeepReadX团队)
- 创建HistoryActivity和相关布局，实现历史记录UI - [ui] (@DeepReadX团队)
- 创建HistoryAdapter，用于RecyclerView中展示历史记录列表 - [ui] (@DeepReadX团队)

### 修改 (Changed)
- 更新PdfViewerActivity，集成历史记录保存功能 - [pdf-viewer] (@DeepReadX团队)
- 更新menu_pdf_viewer.xml，添加历史记录菜单项 - [ui] (@DeepReadX团队)
- 更新AndroidManifest.xml，添加HistoryActivity注册 - [config] (@DeepReadX团队)

## [2025-05-16]

### 新增 (Added)
- 创建侧边滑出式解释面板，提供更好的AI解释展示体验 - [ui] (@DeepReadX团队)
- 创建ExplanationFragment，支持HTML格式化的AI解释展示 - [ui] (@DeepReadX团队)
- 支持从侧边栏关闭AI解释面板 - [ui] (@DeepReadX团队)

### 修改 (Changed)
- 更新PdfViewerActivity，集成侧边解释面板 - [pdf-viewer] (@DeepReadX团队)
- 优化AI解释文本格式化，添加HTML标记支持 - [ui] (@DeepReadX团队)
- 更新app/build.gradle.kts，添加DrawerLayout依赖 - [config] (@DeepReadX团队)

## [2025-05-16]

### 新增 (Added)
- 创建讲解风格管理模块，支持风格的增删改查和默认风格设置 - [style-management] (@DeepReadX团队)
- 创建StyleManagerActivity和相关布局文件，实现风格管理UI - [ui] (@DeepReadX团队)
- 创建StyleAdapter，用于RecyclerView中展示风格列表 - [ui] (@DeepReadX团队)
- 在PdfViewerActivity中集成风格管理功能，支持选择和管理讲解风格 - [pdf-viewer] (@DeepReadX团队)

### 修改 (Changed)
- 更新PdfViewerActivity，优化AI解释流程，支持使用不同风格模板 - [pdf-viewer] (@DeepReadX团队)
- 更新AndroidManifest.xml，添加StyleManagerActivity注册 - [config] (@DeepReadX团队)

## [2025-05-15]

### 新增 (Added)
- 创建ExplainStyle模型类，定义讲解风格数据结构 - [model] (@DeepReadX团队)
- 创建StyleDbHelper类，管理风格数据库表 - [database] (@DeepReadX团队)
- 创建StyleDao接口和StyleDaoImpl实现类，提供风格数据访问接口 - [dao] (@DeepReadX团队)

## [2025-05-10]

### 新增 (Added)
- 实现AI解释功能，包含OCR文本识别和DeepSeek API集成 - [ai-explanation] (@DeepReadX团队)
- 创建OcrProcessor类，封装Google ML Kit Text Recognition功能 - [ocr] (@DeepReadX团队)
- 创建DeepSeekClient类，负责与DeepSeek API交互 - [api] (@DeepReadX团队)
- 在Toolbar右侧添加解释按钮，支持单击触发OCR识别和AI解释流程 - [ui] (@DeepReadX团队)

### 修改 (Changed)
- 更新PdfViewerActivity，集成OCR和AI解释功能 - [pdf-viewer] (@DeepReadX团队)
- 更新build.gradle，添加ML Kit和OkHttp相关依赖 - [config] (@DeepReadX团队)
- 在AndroidManifest中添加网络和存储权限声明 - [config] (@DeepReadX团队)

## [2025-05-05]

### 新增 (Added)
- 创建PdfViewerActivity.java实现PDF查看基本功能 - [pdf-viewer] (@DeepReadX团队)
- 创建activity_pdf_viewer.xml布局文件 - [ui] (@DeepReadX团队)
- 实现PdfRenderer类用于PDF文件渲染 - [pdf-viewer] (@DeepReadX团队)

### 修改 (Changed)
- 添加必要的字符串资源和图标文件 - [resources] (@DeepReadX团队)

## [2025-05-01]

### 新增 (Added)
- 项目初始化 - [core] (@DeepReadX团队)
- 创建Cursor规则文件 - [documentation] (@DeepReadX团队) 