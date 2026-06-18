package org.protege.editor.core.ui.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.util.HashMap;
import java.util.Map;

public final class SharpTextRendering {

    public static final Object TEXT_ANTIALIASING = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;

    public static final Object FRACTIONAL_METRICS = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;

    public static final Integer LCD_CONTRAST = 250;

    private SharpTextRendering() {
    }

    public static Map<RenderingHints.Key, Object> desktopHints() {
        Map<RenderingHints.Key, Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, TEXT_ANTIALIASING);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS, FRACTIONAL_METRICS);
        hints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, LCD_CONTRAST);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return hints;
    }

    public static void apply(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, TEXT_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, FRACTIONAL_METRICS);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, LCD_CONTRAST);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static FontRenderContext fontRenderContext() {
        return new FontRenderContext(null, TEXT_ANTIALIASING, FRACTIONAL_METRICS);
    }
}
