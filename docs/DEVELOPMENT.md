# 开发与架构

[English README](../README.md) · [中文 README](README_ZHCN.md) · [更新记录](../CHANGELOG.md)

本文保留共享源码、布局模型、扩展分组和多版本构建的开发说明。安装与日常使用见 README。

## 调试数据与绘制

原版文字条目已接入 `DebugRow`，包括位置、内存、系统、帧率、服务器、光照、高度图、渲染、生物生成、目标状态、声音和后处理效果。第三方条目继续通过原版接口采集。保留原版条目开关、简化调试信息限制、区块缓存及性能图表。

Clear F3 文字固定使用相当于 GUI Scale 2 的大小，布局和背景使用同一比例，不随游戏 GUI Scale 或 Auto 放大。缩放入口集中在 `DebugOverlayRenderer.extractAtFixedScale()`，比例由 `TEXT_SCALE` 定义。物品栏、菜单及原版图表继续使用各自的缩放设置。

注视方块、流体和实体的连续标签通过共用接口合并为可折叠行。空间不足时，仅缩短标签并显示省略号；空间恢复后重新显示完整内容。旧版标题前缀集中在 `DebugGroups` 的映射表中维护，新版优先使用原版分组 ID。

区块生成器、生物群系来源及部分渲染器只提供复合文本统计。这些内容完整保存在具名行的值中，不解析其中的缩写或命名空间。远程服务器未提供的测量数据不会以本地估算代替。

## 模块

| 模块 | 内容 | Java 包 |
| --- | --- | --- |
| `common` | 共用初始化、调试数据、分组、布局、渲染、Mixin 和资源 | `io.github.piscescup.mc.clear_f3` |
| `fabric` | Fabric 多版本聚合、客户端入口、数据生成和元数据 | `io.github.piscescup.fabricmc.clear_f3` |
| `neoforge` | NeoForge 26.3 客户端入口、元数据和访问转换配置 | `io.github.piscescup.neoforgemc.clear_f3` |

模块职责参考 [BetterF3](https://github.com/TreyRuffy/BetterF3)，Fabric 多版本构建参考 [Carpet-Igny-Addition](https://github.com/liuyuexiaoyu1/Carpet-Igny-Addition)。Fabric 使用 Fabric Loom，NeoForge 使用 ModDevGradle。`common` 保持 26.3 的开发环境，两端分别编译和打包共享内容，安装时只需要对应平台和版本的一个模组 JAR。

两端的 mod ID、资源命名空间及翻译键前缀统一为 `clear_f3`。

| 路径 | 用途 |
| --- | --- |
| `fabric/versions.json` | 启用的 Fabric 构建节点 |
| `fabric/version.gradle` | 各版本共用的构建脚本 |
| `fabric/versions/<版本>/gradle.properties` | 游戏版本、兼容范围、Fabric API 和 Java 字节码版本 |
| `fabric/src/main` | 共用的 Fabric 入口和元数据模板 |
| `common/src/main` | 共用的功能代码和资源 |
| `fabric/src/main/java/io/github/piscescup/fabricmc/clear_f3/datagen/lang` | 中英文翻译的唯一维护入口 |
| `common/src/generated/resources` | 数据生成器输出的共用语言资源，由两端打包 |

多版本构建沿用 [TeamCraft](https://github.com/Piscescup/TeamCraft) 的版本节点与相邻版本映射结构。`fabric/build.gradle` 为 `fabric/versions.json` 中的版本建立预处理图，`fabric/versions/mainProject` 指定 26.3 为公共源码入口。各相邻版本的补充映射位于 `fabric/versions/mappings/`；类型和方法改名由预处理器结合 Minecraft 映射处理，不再逐行替换 Java 文本。API 签名或行为差异在公共源码中使用 `//#if MC>=...`、`//$$` 维护。旧版生成的源码位于各版本的 `build/preprocessed/main/java`。

旧版源码预处理任务因上游预处理器持有 Gradle 项目引用，单独标记为不兼容配置缓存；这些构建会跳过配置缓存，任务的增量构建仍然生效。26.3 直接使用核心源码，不经过此任务。

旧版从原版的左右文字列采集数据；1.21.9 起使用原版调试条目采集接口，保留条目开关。26.3 使用结构化提供者直接采集，其他版本通过 `VanillaDebugText` 将已知原版输出转换为带翻译键的 `DebugRow`。位置、内存、系统、光照、朝向、目标信息和图表快捷键等使用同一套语言资源。无法识别的格式保留原文，复杂统计值保留原版缩写和精度。

`common/src/main/resources/clear_f3.accesswidener` 开放 26.3 渲染所需的两个原版私有成员；NeoForge 的 `META-INF/accesstransformer.cfg` 提供对应配置。新增此类访问时，需要同步维护两端声明。

## 代码结构

| 类 | 职责 |
| --- | --- |
| `ClearF3References` | 共用初始化、日志及资源标识符 |
| `ClearF3Fabric` | Fabric 客户端入口 |
| `ClearF3NeoForge` | NeoForge 客户端入口 |
| `ClearF3Config` | 保存总开关，以及每条 DebugRow 独立的 Key/Value 颜色，兼容原有配置 |
| `ClearF3KeyMappings` | 可改键的 F8 开关与 F9 设置界面入口 |
| `ClearF3ConfigScreen` | 常规与条目颜色页签，保留各 Group 的折叠和页面滚动状态 |
| `ClearF3ContentList` | 可折叠、可滚动的内容区域与原生控件布局 |
| `ClearF3ColorScreen` | 预设色板、RGB/ARGB 输入和应用前预览 |
| `DebugColorRole` | 单条 DebugRow 的 Key Color 与 Value Color 定义 |
| `DebugRowCatalog` | 原版条目元数据，以及运行中发现的第三方或动态条目 |
| `ClearF3GuiTheme` / `ClearF3Button` | 半透明背景、灰色立体按钮、折叠标题和颜色色块 |
| `ClearF3ModMenu` | Fabric 可选的 Mod Menu 配置界面入口 |
| `DebugGroup` | 保存稳定 ID、可翻译标题、强调色和偏好列位 |
| `DebugRow` | 分开保存行名与带样式的值 |
| `DebugGroupContents` | 收集当前帧的结构化行和原始文本 |
| `DebugAdapter` | 组织分组、转换原版数据并接收第三方原始输出 |
| `VanillaDebugText` | 将已知原版文本格式转换为可翻译的结构化行 |
| `VanillaDebugEntries` | 按原版条目的精确类型分派到提供者实现 |
| `DebugEntryProvider` | 调试条目的统一采集入口 |
| `DebugEntryOutput` | 提供条目输出的具名行接口 |
| `DebugOverlay` | 新老版本共用的采集与绘制入口，切换世界时清理布局状态 |
| `DebugGroupLayout` | 测量组件、准备名称和值的换行、按空间折叠标签 |
| `DebugLayout` | 计算左右列位置、恢复已有列位、输出最终面板坐标 |
| `DebugPanelSize` | 宽度扩大时立即更新、缩小时短暂保留，高度随当前内容立即更新 |
| `DebugOverlayRenderer` | 按布局绘制文字、背景和色条，处理 GUI 缩放及版本间绘制接口差异 |
| `DebugScreenOverlayMixin` | 接入新版原版条目采集并交给统一入口 |
| `LegacyDebugScreenOverlayMixin` | 接入旧版左右文本列并交给同一套展示流程 |

展示模型与适配器位于 `common` 的 `client/debug` 包，原版条目提供者位于 `client/debug/entry` 包，Mixin 位于 `mixin/client` 包。公开成员使用英文 Javadoc。

## 扩展分组

分组通过 `Identifier` 保持身份稳定，每帧创建新的输出容器，再添加结构化行或普通文字：

```java
DebugGroup group = new DebugGroup(
    ClearF3References.id("example"),
    Component.translatable("clear_f3.group.example"),
    OptionalInt.of(0x55FFFF),
    DebugGroup.Column.LEFT
);
DebugGroupContents contents = new DebugGroupContents(group);
contents.addRow(new DebugRow(
    Component.translatable("clear_f3.row.example"),
    Component.literal("42").withStyle(ChatFormatting.WHITE)
));
contents.addLine("Additional information");
```

展示结构参考 [Angelica PR #2301](https://github.com/GTNewHorizons/Angelica/pull/2301) 的职责划分：`DebugOverlay` 调度采集和绘制，`DebugGroupLayout` 测量组件与换行，`DebugLayout` 输出面板坐标，`DebugOverlayRenderer` 完成实际绘制。布局结果先完整计算，再提交绘制。

新老版本共用 `DebugGroups` 的显示顺序和默认列位。游戏版本位于右上角，帧率位于左上角；未知第三方分组保留彼此的输入顺序。分组集合变化时恢复统一顺序，普通数值变化时优先保留已有列位。面板宽度扩大时立即更新，缩小时最多保留两秒；高度始终随当前内容更新，切换指向目标后不会保留上一目标的空白。空间不足时折叠长标签。窗口大小、世界或原版条目配置变化时清理布局历史。

名称与数值使用统一起始位置，手动换行和自动换行遵循同一套布局。组件继续保留翻译和样式；测量每帧使用当前字体，因此切换语言或资源包后不会复用旧的文字换行结果。旧版仍从原版文本采集，新版仍使用原版条目接口，各版本实际提供的数据和菜单能力以对应游戏版本为准。

每个原版提供者实现 `DebugEntryProvider`，通过 `DebugEntryOutput` 输出行；`VanillaDebugEntries` 只维护原版精确类型到提供者的映射。新增原版适配时，添加独立实现并登记对应类型。共享采集流程的条目可以配置同一个提供者实现。

条目的显示开关相互独立：位置、区块坐标、区段坐标、速度和生物群系虽然可显示在同一分组，仍分别遵循原版设置与简化调试信息限制。第三方条目通过 `DebugScreenDisplayer` 接口输出，自定义分组使用原有 ID 保持稳定。

## 开发与构建

使用 JDK 25 运行 Gradle，并安装 JDK 21 以编译较早版本。1.20.1、1.20.2 和 1.20.4 使用 JDK 21 编译为 Java 17 字节码；其余 1.20/1.21 节点使用 Java 21 字节码，26.x 使用 Java 25 字节码。CI 已配置 JDK 21 和 25。

补丁版本按兼容范围共用一个构建节点：

| 构建节点 | 声明支持的游戏版本 | Java 字节码 |
| --- | --- | --- |
| `1.20.1` | 1.20–1.20.1 | 17 |
| `1.20.2` | 1.20.2 | 17 |
| `1.20.4` | 1.20.3–1.20.4 | 17 |
| `1.20.6` | 1.20.5–1.20.6 | 21 |
| `1.21.1` | 1.21–1.21.1 | 21 |
| `1.21.3` | 1.21.2–1.21.3 | 21 |
| `1.21.4` | 1.21.4 | 21 |
| `1.21.5` | 1.21.5 | 21 |
| `1.21.8` | 1.21.6–1.21.8 | 21 |
| `1.21.10` | 1.21.9–1.21.10 | 21 |
| `1.21.11` | 1.21.11 | 21 |
| `26.1.2` | 26.1–26.1.2 | 25 |
| `26.2` | 26.2 系列 | 25 |
| `26.3` | 26.3 系列 | 25 |

在项目根目录运行：

| 操作 | Windows 命令 |
| --- | --- |
| 构建全部模块 | `.\gradlew.bat buildAndGather` |
| 构建并汇总所有 Fabric 版本 | `.\gradlew.bat :fabric:build` |
| 仅构建 Fabric 1.20.1 | `.\gradlew.bat :fabric:1.20.1:build` |
| 仅构建 Fabric 26.3 | `.\gradlew.bat :fabric:26.3:build` |
| 启动 Fabric 1.21.1 客户端 | `.\gradlew.bat :fabric:1.21.1:runClient` |
| 启动 Fabric 26.3 客户端 | `.\gradlew.bat :fabric:26.3:runClient` |
| Fabric 26.3 数据生成 | `.\gradlew.bat :fabric:26.3:runDatagen` |
| 仅构建 NeoForge 26.3 | `.\gradlew.bat :neoforge:build` |
| 启动 NeoForge 客户端 | `.\gradlew.bat :neoforge:runClient` |

`:fabric:runClient` 和 `:fabric:runDatagen` 是 26.3 对应任务的快捷入口。Linux/macOS 将 `.\gradlew.bat` 换为 `./gradlew`。

语言翻译维护在 Fabric 模块的 `datagen.lang.ENUSLanguageProvider` 和 `datagen.lang.ZHCNLanguageProvider`。修改 Provider 后运行 `.\gradlew.bat :fabric:runDatagen`，生成 `common/src/generated/resources/assets/clear_f3/lang/en_us.json` 和 `zh_cn.json`。这些 JSON 是生成结果，普通构建直接打包它们，无需每次启动游戏或执行数据生成；提交代码时应同时保留 Provider 与生成结果。

Fabric 所有版本和 NeoForge 都读取这个共用目录。各个旧版的 `runDatagen` 仍可用于兼容性验证，但输出到自己的 `build/generated/datagen`，不会覆盖共用结果或重复打包语言文件。语言资源由 Fabric 数据生成器维护，NeoForge 不需要加载 Fabric 的 Provider。

Fabric 各版本产物位于 `fabric/versions/<版本>/build/libs`。安装包按 `clean-f3-v<mod_version>-fabric-<fabric_api_version>mc.jar` 命名，例如 `clean-f3-v1.0.0-fabric-0.92.6+1.20.1mc.jar`。NeoForge 安装包按 `clean-f3-v<mod_version>-mc-<mc_version>-neoforge-<neoforge_version>.jar` 命名，例如 `clean-f3-v1.0.0-mc-26.3-neoforge-26.3.0.26-beta.jar`。

根目录运行 `.\gradlew.bat buildAndGather` 后，可安装的 Fabric 和 NeoForge JAR 分别汇总到根目录的 `build/releases/fabric/` 和 `build/releases/neoforge/`。单独运行 `:fabric:build` 或 `:neoforge:build` 也会更新对应的汇总目录。源码包与开发包不参与汇总；`common` 的产物用于开发，安装时使用对应版本的主 JAR。

Fabric 运行目录为 `fabric/run/<版本>`，NeoForge 运行目录为 `neoforge/run`。进入游戏后按 F3 查看界面；具有调试条目菜单的游戏版本可通过原版选项选择条目。

新增构建版本时，将节点加入 `fabric/versions.json`，添加该节点的 `gradle.properties` 和相邻版本映射文件，并在公共源码中补充必要的条件编译。版本数字示例：1.20.1 为 `12001`，1.21.11 为 `12111`，26.3 为 `260300`。随后单独构建该节点，并核对 Mixin 注入点及游戏内表现。
