//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.TypedInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The structured data provider for {@code LookingAtEntityDebugEntry}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LookingAtEntityDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Whether to display only tags.
     */
    private final boolean tagsOnly;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output   the destination of the collected rows
     * @param tagsOnly whether to display only tags
     */
    public LookingAtEntityDebugEntry(DebugEntryOutput output, boolean tagsOnly) {
        this.output = Objects.requireNonNull(output, "output");
        this.tagsOnly = tagsOnly;
    }

    /**
     * Collect this entry's rows using the data already made available by vanilla.
     *
     * @param entry       the original vanilla entry
     * @param level       the server or client level, or {@code null} outside a world
     * @param clientChunk the cached client chunk, or {@code null} when unavailable
     * @param serverChunk the cached server chunk, or {@code null} when unavailable
     */
    @Override
    public void display(DebugScreenEntry entry, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
        displayTargetEntity(tagsOnly);
    }

    /**
     * Display a targeted entity's registry identifier or its tags.
     *
     * @param tagsOnly whether to display only tags
     */
    private void displayTargetEntity(boolean tagsOnly) {
        Entity target = Minecraft.getInstance().crosshairPickEntity;
        if (target != null) {
            if (tagsOnly) {
                displayTags(DebugGroups.LOOKING_AT_ENTITY, target);
            } else {
                output.row(DebugGroups.LOOKING_AT_ENTITY, "registry_id", "%s", target.typeHolder().getRegisteredName());
            }
        }
    }

    /**
     * Display registry tags as one aligned, collapsible value with explicit line breaks.
     *
     * @param group    the target group definition
     * @param instance the typed instance whose tags are displayed
     */
    private void displayTags(DebugGroup group, TypedInstance<?> instance) {
        String tags = instance.tags().map(tag -> "#" + tag.location()).collect(Collectors.joining("\n"));
        if (!tags.isEmpty()) {
            output.collapsibleRow(group, "tags", Component.literal(tags).withStyle(ChatFormatting.WHITE));
        }
    }
}
//#endif
