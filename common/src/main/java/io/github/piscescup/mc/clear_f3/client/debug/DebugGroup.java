package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.OptionalInt;

/**
 * The display definition of a group in the {@code Clear F3} overlay.
 *
 * @param id the unique identifier of the group
 * @param title the title of the group, or an empty component for a titleless group
 * @param accentColor the optional RGB color of the outer accent line
 * @param preferredColumn the preferred column of the group
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public record DebugGroup(
    Identifier id,
    Component title,
    OptionalInt accentColor,
    Column preferredColumn
) {
    /**
     * Create a new {@link DebugGroup} with the given display definition.
     *
     * @param id the unique identifier of the group
     * @param title the title of the group
     * @param accentColor the optional RGB color of the outer accent line
     * @param preferredColumn the preferred column of the group
     */
    public DebugGroup {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(accentColor, "accentColor");
        Objects.requireNonNull(preferredColumn, "preferredColumn");
    }

    /**
     * The builder for a {@link DebugGroup} with a component title and optional styling.
     *
     * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
     * Name: <b>Yuantong Ren</b>
     * @version 1.0.0
     * @since 1.0.0
     */
    public static final class Builder {
        /**
         * The stable identifier of the group being built.
         */
        private final Identifier id;

        /**
         * The component title, preserving translations and text styles.
         */
        private final Component title;

        /**
         * The RGB accent color when a color has been selected.
         */
        private int accentColor;

        /**
         * Whether the group has an accent color, including a valid black color.
         */
        private boolean hasAccentColor;

        /**
         * The preferred column, or {@link Column#AUTO} when none is selected.
         */
        private Column preferredColumn = Column.AUTO;

        /**
         * Create a builder with the given identifier and component title.
         *
         * @param id the stable identifier of the group
         * @param title the title, including a translatable or styled component
         */
        private Builder(Identifier id, Component title) {
            this.id = Objects.requireNonNull(id, "id");
            this.title = Objects.requireNonNull(title, "title");
        }

        /**
         * Create a builder for a titled group without resolving its component to text.
         *
         * @param id the stable identifier of the group
         * @param title the component title of the group
         * @return a new builder retaining the supplied component
         */
        public static Builder titled(Identifier id, Component title) {
            return new Builder(id, title);
        }

        /**
         * Create a builder for a group without a visible title.
         *
         * @param id the stable identifier of the group
         * @return a new builder with an empty title
         */
        public static Builder titleless(Identifier id) {
            return new Builder(id, Component.empty());
        }

        /**
         * Select the RGB accent color of the group.
         *
         * @param color the RGB color of the outer accent line
         * @return {@code this} builder
         */
        public Builder withAccentColor(int color) {
            accentColor = color;
            hasAccentColor = true;
            return this;
        }

        /**
         * Select the preferred column of the group.
         *
         * @param column the preferred column
         * @return {@code this} builder
         */
        public Builder withPreferredColumn(Column column) {
            preferredColumn = Objects.requireNonNull(column, "column");
            return this;
        }

        /**
         * Build the group using the selected title, color and column preference.
         *
         * @return the new group definition
         */
        public DebugGroup build() {
            return new DebugGroup(id, title, hasAccentColor ? OptionalInt.of(accentColor) : OptionalInt.empty(), preferredColumn);
        }
    }

    /**
     * The column preferences available to a {@link DebugGroup}.
     *
     * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
     * Name: <b>Yuantong Ren</b>
     * @version 1.0.0
     * @since 1.0.0
     */
    public enum Column {
        /**
         * Place the group in an available column according to its current height.
         */
        AUTO,

        /**
         * Prefer the left column when there is enough space.
         */
        LEFT,

        /**
         * Prefer the right column when there is enough space.
         */
        RIGHT
    }
}
