package io.github.piscescup.mc.clear_f3.client.debug;

//#if MC>=12109
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
//#endif
//#if MC>=12109
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The bridge between vanilla debug entries and the {@code Clear F3} groups.
 * <p>
 * Vanilla entries provide structured rows through {@code VanillaDebugEntries}.
 * Other entries use {@link DebugTextParser} to align recognizable named values.
 * Unnamed text following a named value continues in its value column until a
 * blank line. Leading text retains the full width, and original styles are kept.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DebugAdapter
    //#if MC>=12109
    implements DebugScreenDisplayer
    //#endif
{
    /**
     * The cached definitions for groups not provided by {@link DebugGroups}.
     */
    private final Map<Identifier, DebugGroup> definitions = new LinkedHashMap<>();

    /**
     * The content collected for the current frame in insertion order.
     */
    private final Map<Identifier, DebugGroupContents> contents = new LinkedHashMap<>();

    /**
     * The fallback group for an entry that outputs an ungrouped line.
     */
    private Identifier currentEntry = DebugGroups.MISC.id();

    /**
     * Whether the active entry is an original vanilla text provider.
     * Third-party entry implementations retain their own names and formatting.
     */
    private boolean translateVanillaText;

    /**
     * The data providers that convert exact vanilla entry classes to rows.
     */
    //#if MC>=260300
    private final VanillaDebugEntries vanillaEntries = new VanillaDebugEntries(
        (group, row) -> content(group).addRow(row)
    );
    //#endif

    /**
     * Create the vanilla debug adapter with an empty content collection.
     */
    public DebugAdapter() {
    }

    /**
     * Clear the content collected for the previous frame.
     */
    public void beginFrame() {
        contents.clear();
        currentEntry = DebugGroups.MISC.id();
        translateVanillaText = false;
        content(DebugGroups.LEFT_PRIORITY);
        content(DebugGroups.RIGHT_PRIORITY);
    }

    /**
     * Adapt an exact vanilla entry into structured rows.
     * <p>
     * Replaced entries and subclasses retain their own implementation.
     * The caller must invoke the original entry when this method returns {@code false}.
     *
     * @param id the identifier of the currently enabled entry
     * @param entry the entry selected by vanilla
     * @param level the level supplied by vanilla, or {@code null} outside a world
     * @param clientChunk the cached client chunk, or {@code null} when unavailable
     * @param serverChunk the cached server chunk, or {@code null} when unavailable
     * @return {@code true} if the entry has been fully displayed by this adapter
     */
    //#if MC>=12109
    public boolean displayStructured(Identifier id, DebugScreenEntry entry, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
        currentEntry = id;
        translateVanillaText = entry.getClass().getPackageName().equals(DebugScreenEntry.class.getPackageName());
        //#if MC>=260300
        return vanillaEntries.display(entry, level, clientChunk, serverChunk);
        //#else
        //$$ return false;
        //#endif
    }
    //#endif

    /**
     * Get the groups collected for the current frame.
     *
     * @return an immutable list in the shared display order
     */
    public Collection<DebugGroupContents> groups() {
        return contents.values().stream()
            .sorted(java.util.Comparator.comparingInt(value -> DebugGroups.displayOrder(value.group())))
            .toList();
    }

    /**
     * Collect a vanilla text column on versions preceding the debug-entry API.
     * <p>
     * Values remain vanilla output, including reduced-debug restrictions and
     * additions made by other mods. Recognized headings only choose a group.
     * Consecutive target tags share a collapsible row so long tag lists do not
     * prevent the target's position, registry name and properties from fitting.
     *
     * @param lines the original vanilla column, including third-party additions
     * @param leftSide whether the text belongs to the game-information column
     */
    public void addLegacyLines(Collection<String> lines, boolean leftSide) {
        DebugGroup group = leftSide ? DebugGroups.CHUNK_RENDERING : DebugGroups.SYSTEM_SPECS;
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            String plain = ChatFormatting.stripFormatting(line);
            if (plain == null) {
                plain = line;
            }
            group = legacyGroup(plain.stripLeading(), group, leftSide);
            DebugGroupContents destination = content(group);
            if (!VanillaDebugText.append(destination, line)) {
                appendText(destination, line);
            }
        }
    }

    /**
     * Resolve a legacy heading while leaving all displayed values untouched.
     *
     * @param line the line without formatting codes or leading whitespace
     * @param previous the destination used by the previous line
     * @param leftSide whether the text belongs to the game-information column
     * @return the destination of the current line
     */
    private DebugGroup legacyGroup(String line, DebugGroup previous, boolean leftSide) {
        if (line.startsWith("Minecraft ")) {
            return DebugGroups.RIGHT_PRIORITY;
        }
        if (line.contains(" fps ") || line.endsWith(" fps")) {
            return DebugGroups.LEFT_PRIORITY;
        }
        if (line.startsWith("\"") && line.contains("\" server")) {
            return DebugGroups.SERVER;
        }
        return DebugGroups.findLegacyGroup(line).orElse(previous);
    }

    /**
     * Add vanilla shortcut descriptions without their outer separator lines.
     *
     * @param lines the shortcut descriptions produced by vanilla
     */
    public void addHelp(Collection<String> lines) {
        List<String> descriptions = List.copyOf(lines);
        int first = 0;
        int last = descriptions.size();
        while (first < last && descriptions.get(first).isBlank()) {
            first++;
        }
        while (last > first && descriptions.get(last - 1).isBlank()) {
            last--;
        }
        if (first < last) {
            DebugGroupContents destination = content(DebugGroups.HELP);
            for (String line : descriptions.subList(first, last)) {
                if (!VanillaDebugText.append(destination, line)) {
                    destination.addLine(line);
                }
            }
        }
    }

    /**
     * Route known priority entries consistently, balancing unknown priority output.
     *
     * @param line the priority line produced by vanilla
     */
    //#if MC>=12109
    @Override
    //#endif
    public void addPriorityLine(String line) {
        DebugGroup known = DebugGroups.find(currentEntry).orElse(null);
        if (known == DebugGroups.LEFT_PRIORITY || known == DebugGroups.RIGHT_PRIORITY) {
            appendText(content(known), line);
            return;
        }
        DebugGroupContents left = content(DebugGroups.LEFT_PRIORITY);
        DebugGroupContents right = content(DebugGroups.RIGHT_PRIORITY);
        int leftSize = left.lines().size() + left.rows().size();
        int rightSize = right.lines().size() + right.rows().size();
        appendText(leftSize <= rightSize ? left : right, line);
    }

    /**
     * Append an ungrouped line to the current entry's fallback group.
     *
     * @param line the original line produced by the entry
     */
    //#if MC>=12109
    @Override
    //#endif
    public void addLine(String line) {
        appendText(content(groupFor(currentEntry)), line);
    }

    /**
     * Append original lines to the given vanilla or third-party group.
     *
     * @param group the identifier of the original group
     * @param lines the original lines produced by the entry
     */
    //#if MC>=12109
    @Override
    //#endif
    public void addToGroup(Identifier group, Collection<String> lines) {
        DebugGroupContents destination = content(groupFor(group));
        lines.forEach(line -> appendText(destination, line));
    }

    /**
     * Append an original line to the given vanilla or third-party group.
     *
     * @param group the identifier of the original group
     * @param line the original line produced by the entry
     */
    //#if MC>=12109
    @Override
    //#endif
    public void addToGroup(Identifier group, String line) {
        appendText(content(groupFor(group)), line);
    }

    /**
     * Align named values and combine valid target tags across all collection APIs.
     * Other text retains its original formatting and continuation behavior.
     *
     * @param destination the group receiving the output
     * @param line the original debug line
     */
    private void appendText(DebugGroupContents destination, String line) {
        if (translateVanillaText && VanillaDebugText.append(destination, line)) {
            return;
        }
        DebugRow row = DebugTextParser.parse(line);
        DebugGroup group = destination.group();
        String plain = row.value().getString().stripLeading();
        if (!row.hasLabel() && DebugGroups.isTargetGroup(group) && plain.startsWith("#")
            && Identifier.tryParse(plain.substring(1)) != null) {
            destination.addCollapsibleLine(Component.translatable("clear_f3.row.tags"), row.value());
        } else if (row.hasLabel()) {
            destination.addRow(row);
        } else {
            destination.addLine(line, row.value());
        }
    }

    /**
     * Get or create the content container for the given group.
     *
     * @param group the display definition of the group
     * @return the content container for the current frame
     */
    private DebugGroupContents content(DebugGroup group) {
        DebugGroups.remember(group);
        return contents.computeIfAbsent(group.id(), ignored -> new DebugGroupContents(group));
    }

    /**
     * Resolve a group definition without interpreting any rendered text.
     *
     * @param id the identifier of the original group or entry
     * @return the cached display definition of the group
     */
    private DebugGroup groupFor(Identifier id) {
        return DebugGroups.find(id).orElseGet(() -> definitions.computeIfAbsent(id, this::defineGroup));
    }

    /**
     * Create the display definition of a vanilla or third-party group.
     *
     * @param id the identifier of the group
     * @return the new display definition
     */
    private DebugGroup defineGroup(Identifier id) {
        if (!id.getNamespace().equals("minecraft")) {
            return DebugGroup.Builder
                .titled(
                    id,
                    Component.translatableWithFallback("clear_f3." + id.toString().replace(':', '.'), id.toString())
                )
                .withAccentColor(DebugGroups.DEFAULT_THIRD_MOD_DEBUG_COLOR)
                .build();
        }
        String fallback = id.getPath().replace('_', ' ');
        fallback = fallback.isEmpty()
            ? id.toString()
            : Character.toUpperCase(fallback.charAt(0)) + fallback.substring(1);
        return DebugGroup.Builder
            .titled(
                id,
                Component.translatableWithFallback("clear_f3.group." + id.getPath(), fallback)
            )
            .withAccentColor(0xA0A0A0)
            .build();
    }

}
