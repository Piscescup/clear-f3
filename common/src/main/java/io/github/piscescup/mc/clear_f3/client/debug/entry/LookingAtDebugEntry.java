//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.TypedInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The structured data provider for {@code LookingAtDebugEntry}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LookingAtDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Whether the target is a fluid.
     */
    private final boolean fluid;

    /**
     * Whether to display only tags.
     */
    private final boolean tagsOnly;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output   the destination of the collected rows
     * @param fluid    whether the target is a fluid
     * @param tagsOnly whether to display only tags
     */
    public LookingAtDebugEntry(DebugEntryOutput output, boolean fluid, boolean tagsOnly) {
        this.output = Objects.requireNonNull(output, "output");
        this.fluid = fluid;
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
        displayTarget((DebugEntryLookingAt) entry, level, fluid, tagsOnly);
    }

    /**
     * Display block or fluid state and tags using the original entry's ray cast.
     *
     * @param entry    the original target entry
     * @param level    the level supplied by vanilla
     * @param fluid    whether the target is a fluid
     * @param tagsOnly whether to display only tags
     */
    private void displayTarget(DebugEntryLookingAt entry, Level level, boolean fluid, boolean tagsOnly) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();
        Level targetLevel = SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES ? level : minecraft.level;
        if (camera == null || targetLevel == null) {
            return;
        }
        HitResult hit = entry.getHitResult(camera);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos position = blockHit.getBlockPos();
        DebugGroup group = fluid ? DebugGroups.LOOKING_AT_FLUID : DebugGroups.LOOKING_AT_BLOCK;
        if (fluid) {
            var state = targetLevel.getFluidState(position);
            if (tagsOnly) {
                displayTags(group, state);
            } else {
                displayState(group, position, state, state.getValues());
            }
        } else {
            var state = targetLevel.getBlockState(position);
            if (tagsOnly) {
                displayTags(group, state);
            } else {
                displayState(group, position, state, state.getValues());
            }
        }
    }

    /**
     * Display the position, registry name and typed properties of a targeted state.
     *
     * @param group      the target group definition
     * @param position   the targeted block position
     * @param state      the targeted typed state
     * @param properties the state property values in vanilla order
     */
    private void displayState(DebugGroup group, BlockPos position, TypedInstance<?> state, Stream<Property.Value<?>> properties) {
        output.row(group, "target_position", "%d %d %d", position.getX(), position.getY(), position.getZ());
        output.row(group, "registry_id", "%s", state.typeHolder().getRegisteredName());
        properties.forEach(property -> output.namedRow(group, Component.literal(property.property().getName()),
            Component.literal(property.valueName()).withStyle(Boolean.TRUE.equals(property.value()) ? ChatFormatting.GREEN
                : Boolean.FALSE.equals(property.value()) ? ChatFormatting.RED : ChatFormatting.WHITE)));
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
