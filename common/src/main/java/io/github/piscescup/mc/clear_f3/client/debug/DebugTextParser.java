package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;

import java.util.Objects;
import java.util.Optional;

/**
 * The conservative conversion of original debug text into {@link DebugRow} values.
 * <p>
 * An ASCII colon must be followed by spacing or a line break. A full-width colon
 * may be followed directly by a value. Only the first line supplies the name;
 * explicit line breaks in its value retain the shared value alignment.
 * Identifiers, headings and ambiguous lines retain their text and formatting.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
final class DebugTextParser {
    /**
     * Prevent the creation of this utility class.
     */
    private DebugTextParser() {
    }

    /**
     * Convert an original line into a named row when its separator is unambiguous.
     *
     * @param line the original debug line, including its formatting codes
     * @return a named row, or a full-width row with an empty label
     */
    static DebugRow parse(String line) {
        Component formatted = formattedText(line);
        String plain = formatted.getString();
        DebugRow original = new DebugRow(Component.empty(), formatted);
        for (int index = 0; index < plain.length(); index++) {
            char character = plain.charAt(index);
            if (character == '\n') {
                return original;
            }
            if (character != '：' && (character != ':' || index + 1 >= plain.length()
                || !(isSpacing(plain.charAt(index + 1)) || plain.charAt(index + 1) == '\n'))) {
                continue;
            }
            int nameStart = 0;
            int nameEnd = index;
            while (nameStart < nameEnd && isSpacing(plain.charAt(nameStart))) {
                nameStart++;
            }
            while (nameEnd > nameStart && isSpacing(plain.charAt(nameEnd - 1))) {
                nameEnd--;
            }
            int valueStart = index + 1;
            while (valueStart < plain.length() && isSpacing(plain.charAt(valueStart))) {
                valueStart++;
            }
            String name = plain.substring(nameStart, nameEnd);
            if (valueStart == plain.length() || name.indexOf(':') >= 0 || name.indexOf('：') >= 0
                || name.startsWith("#") || name.codePoints().noneMatch(Character::isLetter)) {
                return original;
            }
            return new DebugRow(slice(formatted, nameStart, nameEnd), slice(formatted, valueStart, plain.length()));
        }
        return original;
    }

    /**
     * Convert vanilla formatting codes into styled component segments.
     * <p>
     * Windows and legacy carriage-return line endings become newline characters
     * understood by the font splitter without changing the original stored text.
     *
     * @param text the original text with optional formatting codes
     * @return the visible text with its original colors and decorations
     */
    static Component formattedText(String text) {
        Objects.requireNonNull(text, "text");
        text = text.replace("\r\n", "\n").replace('\r', '\n');
        MutableComponent result = Component.empty();
        StringBuilder run = new StringBuilder();
        Style[] currentStyle = {Style.EMPTY};
        StringDecomposer.iterateFormatted(text, Style.EMPTY, (index, style, codePoint) -> {
            if (!style.equals(currentStyle[0])) {
                if (!run.isEmpty()) {
                    result.append(Component.literal(run.toString()).setStyle(currentStyle[0]));
                    run.setLength(0);
                }
                currentStyle[0] = style;
            }
            run.appendCodePoint(codePoint);
            return true;
        });
        if (!run.isEmpty()) {
            result.append(Component.literal(run.toString()).setStyle(currentStyle[0]));
        }
        return result;
    }

    /**
     * Copy a visible text range without losing styles inherited before its start.
     *
     * @param text  the styled text to copy
     * @param start the inclusive offset in its visible string
     * @param end   the exclusive offset in its visible string
     * @return the styled component for the selected range
     */
    private static Component slice(Component text, int start, int end) {
        MutableComponent result = Component.empty();
        int[] offset = {0};
        text.visit((style, part) -> {
            int from = Math.max(0, start - offset[0]);
            int to = Math.min(part.length(), end - offset[0]);
            if (from < to) {
                result.append(Component.literal(part.substring(from, to)).setStyle(style));
            }
            offset[0] += part.length();
            return offset[0] >= end ? Optional.of(Boolean.TRUE) : Optional.empty();
        }, Style.EMPTY);
        return result;
    }

    /**
     * Check for horizontal spacing without treating line breaks as separators.
     *
     * @param character the character to inspect
     * @return {@code true} for a space, a tab or a Unicode space character
     */
    private static boolean isSpacing(char character) {
        return character == ' ' || character == '\t' || Character.isSpaceChar(character);
    }
}
