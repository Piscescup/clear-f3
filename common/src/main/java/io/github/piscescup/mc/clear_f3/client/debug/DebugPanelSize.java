package io.github.piscescup.mc.clear_f3.client.debug;

/**
 * Panel dimensions with a brief hold before the width shrinks.
 * Height follows the current content so changing targets leaves no empty space.
 * Equal width measurements do not restart the hold, so a quiet panel can shrink.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @since 1.0.0
 */
final class DebugPanelSize {
    private static final long HOLD_NANOS = 2_000_000_000L;
    private int width;
    private int height;
    private long widthChanged;

    /**
     * Update height immediately and stabilize width using a monotonic clock.
     *
     * @param measuredWidth  the current text width
     * @param measuredHeight the current text height
     * @param now            the current monotonic time in nanoseconds
     */
    void update(int measuredWidth, int measuredHeight, long now) {
        if (measuredWidth != width && (measuredWidth > width || now - widthChanged >= HOLD_NANOS)) {
            width = measuredWidth;
            widthChanged = now;
        }
        height = measuredHeight;
    }

    /**
     * @return the held panel width
     */
    int width() {
        return width;
    }

    /**
     * @return the current content height
     */
    int height() {
        return height;
    }
}
