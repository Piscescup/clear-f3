package io.github.piscescup.fabricmc.clear_f3.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//#if MC>=12005
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
//#endif

/**
 * The Simplified Chinese translations generated for every supported loader and version.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ZHCNLanguageProvider extends FabricLanguageProvider {
    /**
     * Create the language provider for the shared resource pack.
     *
     * @param output the output supplied by Fabric
     * @param registries the registry lookup supplied by the data generator
     */
    //#if MC>=12005
    public ZHCNLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "zh_cn", registries);
    }
    //#else
    //$$ public ZHCNLanguageProvider(FabricDataOutput output) {
    //$$     super(output, "zh_cn");
    //$$ }
    //#endif

    /**
     * Add the translations, preserving format arguments used by the shared overlay.
     *
     * @param registries the resolved registry lookup
     * @param translations the builder receiving translation keys and values
     */
    @Override
    //#if MC>=12005
    public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder translations) {
    //#else
    //$$ public void generateTranslations(TranslationBuilder translations) {
    //#endif
        translations.add("clear_f3.gui.tab.colors", "条目颜色");
        translations.add("clear_f3.gui.colors_hint", "展开分组，分别设置每个条目的 Key Color 和 Value Color。");
        translations.add("clear_f3.gui.reset_component", "重置此分组");
        translations.add("clear_f3.gui.color_button", "%s：%s");
        translations.add("clear_f3.gui.expand_hint", "提示：点击 [+] 展开");
        translations.add("clear_f3.gui.rows_hint", "打开 F3 后，此分组的额外条目会自动出现在这里。");
        translations.add("clear_f3.gui.unnamed_row", "条目 %s");
        translations.add("clear_f3.color.key", "Key Color");
        translations.add("clear_f3.color.value", "Value Color");
        translations.add("clear_f3.gui.tab.general", "常规");
        translations.add("clear_f3.gui.shortcuts", "快捷键");
        translations.add("clear_f3.gui.reset_colors", "重置颜色");
        translations.add("clear_f3.gui.expand_all", "展开全部");
        translations.add("clear_f3.gui.collapse_all", "折叠全部");
        translations.add("clear_f3.gui.edit_color", "编辑%s");
        translations.add("clear_f3.gui.hex_color", "十六进制颜色");
        translations.add("clear_f3.gui.apply", "应用");
        translations.add("clear_f3.gui.hex_hint", "RGB：#RRGGBB  ARGB：#AARRGGBB");
        translations.add("clear_f3.gui.hex_invalid", "请输入六位 RGB 或八位 ARGB 十六进制数。");
        translations.add("clear_f3.group.priority_left", "左上角信息");
        translations.add("clear_f3.group.priority_right", "右上角信息");
        translations.add("clear_f3.group.miscellaneous", "其他信息");
        translations.add("key.category.clear_f3.main", "Clear F3");
        translations.add("key.categories.clear_f3", "Clear F3");
        translations.add("key.clear_f3.toggle", "切换 Clear F3");
        translations.add("key.clear_f3.open_settings", "打开 Clear F3 设置");
        translations.add("clear_f3.gui.title", "Clear F3 设置");
        translations.add("clear_f3.gui.enabled", "Clear F3：%s");
        translations.add("clear_f3.gui.toggle_key", "启用 Clear F3：");
        translations.add("clear_f3.gui.settings_key", "配置界面：");
        translations.add("clear_f3.gui.press_key", "请按键…");
        translations.add("clear_f3.gui.keybind_hint", "点击后按键或鼠标键，Esc 清除绑定。");
        translations.add("clear_f3.message.enabled", "Clear F3 已开启");
        translations.add("clear_f3.message.disabled", "Clear F3 已关闭（使用原版 F3）");
        translations.add("clear_f3.group.position", "位置");
        translations.add("clear_f3.group.memory", "内存");
        translations.add("clear_f3.group.light", "光照");
        translations.add("clear_f3.group.system", "系统规格");
        translations.add("clear_f3.group.looking_at_block", "指向的方块");
        translations.add("clear_f3.group.looking_at_fluid", "指向的流体");
        translations.add("clear_f3.group.looking_at_entity", "指向的实体");
        translations.add("clear_f3.group.heightmaps", "高度图");
        translations.add("clear_f3.group.chunk_generation", "区块生成");
        translations.add("clear_f3.group.chunk_render_stats", "区块渲染");
        translations.add("clear_f3.group.chunk_source_stats", "区块缓存");
        translations.add("clear_f3.group.entity_render_stats", "实体渲染");
        translations.add("clear_f3.group.particle_render_stats", "粒子渲染");
        translations.add("clear_f3.group.entity_spawn_counts", "生物生成");
        translations.add("clear_f3.group.simple_performance_impactors", "性能信息");
        translations.add("clear_f3.group.sound_mood", "环境音效");
        translations.add("clear_f3.group.sound_cache", "声音缓存");
        translations.add("clear_f3.group.post_effects", "后处理效果");
        translations.add("clear_f3.group.help", "调试组合键");
        translations.add("clear_f3.row.xyz", "坐标");
        translations.add("clear_f3.row.block", "方块坐标");
        translations.add("clear_f3.row.chunk", "区块坐标");
        translations.add("clear_f3.row.facing", "朝向");
        translations.add("clear_f3.row.dimension", "维度");
        translations.add("clear_f3.row.forced_chunks", "强制加载区块数");
        translations.add("clear_f3.row.heap_used", "已用内存");
        translations.add("clear_f3.row.allocation_rate", "内存分配速率");
        translations.add("clear_f3.row.heap_allocated", "已分配内存");
        translations.add("clear_f3.facing", "%s（%s）（%s）");
        translations.add("clear_f3.direction.north", "Z 轴负方向");
        translations.add("clear_f3.direction.south", "Z 轴正方向");
        translations.add("clear_f3.direction.west", "X 轴负方向");
        translations.add("clear_f3.direction.east", "X 轴正方向");
        translations.add("clear_f3.direction.up", "Y 轴正方向");
        translations.add("clear_f3.direction.down", "Y 轴负方向");
        translations.add("clear_f3.direction_name.north", "北");
        translations.add("clear_f3.direction_name.south", "南");
        translations.add("clear_f3.direction_name.west", "西");
        translations.add("clear_f3.direction_name.east", "东");
        translations.add("clear_f3.direction_name.up", "上");
        translations.add("clear_f3.direction_name.down", "下");
        translations.add("clear_f3.group.rendering", "渲染统计");
        translations.add("clear_f3.group.chunk_source", "区块缓存");
        translations.add("clear_f3.group.spawn_counts", "生物生成");
        translations.add("clear_f3.group.performance", "性能信息");
        translations.add("clear_f3.group.server", "服务器与网络");
        translations.add("clear_f3.group.sound", "声音");
        translations.add("clear_f3.row.section_relative", "区段内坐标");
        translations.add("clear_f3.row.speed", "移动速度");
        translations.add("clear_f3.row.biome", "生物群系");
        translations.add("clear_f3.row.server_biome", "服务端生物群系");
        translations.add("clear_f3.row.day", "天数");
        translations.add("clear_f3.row.local_difficulty", "局部难度");
        translations.add("clear_f3.row.heap_initial", "堆内存初始大小");
        translations.add("clear_f3.row.heap_used_detail", "已用堆内存（MXBean）");
        translations.add("clear_f3.row.heap_committed", "已分配堆内存");
        translations.add("clear_f3.row.heap_maximum", "堆内存上限");
        translations.add("clear_f3.row.non_heap_initial", "非堆内存初始大小");
        translations.add("clear_f3.row.non_heap_used_detail", "已用非堆内存");
        translations.add("clear_f3.row.non_heap_committed", "已分配非堆内存");
        translations.add("clear_f3.row.non_heap_maximum", "非堆内存上限");
        translations.add("clear_f3.row.java", "Java");
        translations.add("clear_f3.row.cpu", "CPU");
        translations.add("clear_f3.row.display", "显示分辨率");
        translations.add("clear_f3.row.window", "窗口尺寸");
        translations.add("clear_f3.row.pixel_density", "像素密度");
        translations.add("clear_f3.row.gpu_device", "GPU");
        translations.add("clear_f3.row.graphics_backend", "图形接口");
        translations.add("clear_f3.row.graphics_driver", "显卡驱动");
        translations.add("clear_f3.row.fps", "帧率");
        translations.add("clear_f3.row.frame_limit", "帧率上限");
        translations.add("clear_f3.row.presentation", "呈现模式");
        translations.add("clear_f3.row.refresh_rate", "刷新率");
        translations.add("clear_f3.row.game_version", "Minecraft");
        translations.add("clear_f3.row.client_brand", "客户端");
        translations.add("clear_f3.row.server", "服务器");
        translations.add("clear_f3.row.tick_state", "游戏刻状态");
        translations.add("clear_f3.row.tick_time", "每游戏刻耗时");
        translations.add("clear_f3.row.target_tick_time", "目标每游戏刻耗时");
        translations.add("clear_f3.row.packets_sent", "发送数据包");
        translations.add("clear_f3.row.packets_received", "接收数据包");
        translations.add("clear_f3.row.gpu_utilization", "GPU 占用率");
        translations.add("clear_f3.row.transparency", "改进透明显示");
        translations.add("clear_f3.row.clouds", "云");
        translations.add("clear_f3.row.biome_blend", "生物群系混合半径");
        translations.add("clear_f3.row.filtering", "纹理过滤");
        translations.add("clear_f3.row.terrain_rendering", "地形渲染");
        translations.add("clear_f3.row.client_light", "客户端光照");
        translations.add("clear_f3.row.client_sky_light", "客户端天空光照");
        translations.add("clear_f3.row.client_block_light", "客户端方块光照");
        translations.add("clear_f3.row.server_sky_light", "服务端天空光照");
        translations.add("clear_f3.row.server_block_light", "服务端方块光照");
        translations.add("clear_f3.row.client_heightmap", "客户端%s");
        translations.add("clear_f3.row.server_heightmap", "服务端%s");
        translations.add("clear_f3.row.section_statistics", "子区块渲染");
        translations.add("clear_f3.row.entity_statistics", "实体统计");
        translations.add("clear_f3.row.particles", "粒子数");
        translations.add("clear_f3.row.client_chunks", "客户端区块缓存");
        translations.add("clear_f3.row.server_chunks", "服务端区块缓存");
        translations.add("clear_f3.row.generator", "生成器");
        translations.add("clear_f3.row.biome_source", "生物群系源");
        translations.add("clear_f3.row.blending", "混合生成");
        translations.add("clear_f3.row.spawnable_chunks", "可生成生物的区块数");
        translations.add("clear_f3.row.target_position", "位置");
        translations.add("clear_f3.row.registry_id", "ID");
        translations.add("clear_f3.row.tags", "标签");
        translations.add("clear_f3.row.sound_channels", "声音通道");
        translations.add("clear_f3.row.sound_mood", "环境音效触发进度");
        translations.add("clear_f3.row.sound_buffers", "音频缓冲区数");
        translations.add("clear_f3.row.sound_cache_size", "缓存大小");
        translations.add("clear_f3.row.post_effect", "后处理效果");
        translations.add("clear_f3.value.unavailable", "不可用");
        translations.add("clear_f3.value.unlimited", "无限制");
        translations.add("clear_f3.value.integrated_server", "内置服务器");
        translations.add("clear_f3.value.running", "运行中");
        translations.add("clear_f3.value.frozen", "已冻结");
        translations.add("clear_f3.value.stepping", "冻结中单步运行");
        translations.add("clear_f3.value.sprinting", "加速运行");
        translations.add("clear_f3.value.enabled", "已开启");
        translations.add("clear_f3.value.disabled", "已关闭");
        translations.add("clear_f3.value.old_noise", "旧版生成");
        translations.add("clear_f3.heightmap.world_surface_wg", "WORLD_SURFACE_WG");
        translations.add("clear_f3.heightmap.world_surface", "WORLD_SURFACE");
        translations.add("clear_f3.heightmap.ocean_floor_wg", "OCEAN_FLOOR_WG");
        translations.add("clear_f3.heightmap.ocean_floor", "OCEAN_FLOOR");
        translations.add("clear_f3.heightmap.motion_blocking", "MOTION_BLOCKING");
        translations.add("clear_f3.heightmap.motion_blocking_no_leaves", "MOTION_BLOCKING_NO_LEAVES");
        translations.add("clear_f3.mob_category.monster", "敌对生物");
        translations.add("clear_f3.mob_category.creature", "动物");
        translations.add("clear_f3.mob_category.ambient", "环境生物");
        translations.add("clear_f3.mob_category.axolotls", "美西螈");
        translations.add("clear_f3.mob_category.underground_water_creature", "地底水生动物");
        translations.add("clear_f3.mob_category.water_creature", "水生动物");
        translations.add("clear_f3.mob_category.water_ambient", "水生环境动物");
        translations.add("clear_f3.mob_category.misc", "其他生物");
        translations.add("clear_f3.row.server_light", "服务端光照");
        translations.add("clear_f3.row.client_heightmap_values", "客户端高度图");
        translations.add("clear_f3.row.server_heightmap_values", "服务端高度图");
        translations.add("clear_f3.row.spawn_statistics", "生物生成统计");
        translations.add("clear_f3.row.help", "帮助");
        translations.add("clear_f3.row.edit_debug", "编辑调试条目");
        translations.add("clear_f3.value.fast", "快速");
        translations.add("clear_f3.value.fancy", "精细");
        translations.add("clear_f3.value.waiting_for_chunk", "正在等待区块……");
        translations.add("clear_f3.value.facing_angles", "%s（%s）%s");
        translations.add("clear_f3.value.chart_shortcut", "%s %s");
        translations.add("clear_f3.value.visible", "已显示");
        translations.add("clear_f3.value.hidden", "已隐藏");
        translations.add("clear_f3.chart.profiler", "性能分析");
        translations.add("clear_f3.chart.fps_tps", "帧率与游戏刻");
        translations.add("clear_f3.chart.fps", "帧率");
        translations.add("clear_f3.chart.network", "带宽与延迟");
        translations.add("clear_f3.chart.ping", "网络延迟");
        translations.add("clear_f3.chart.lightmap", "光照贴图");
    }
}

