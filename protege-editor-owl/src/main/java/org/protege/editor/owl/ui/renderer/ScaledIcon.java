package org.protege.editor.owl.ui.renderer;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

/**
 * Paints an icon at a smaller visual scale while preserving its layout slot.
 */
public final class ScaledIcon implements Icon {

    private static final double ENTITY_MARKER_SCALE = 1.0 / 3.0;

    @Nonnull
    private final Icon delegate;

    private final double scale;

    private ScaledIcon(@Nonnull Icon delegate, double scale) {
        this.delegate = delegate;
        this.scale = scale;
    }

    @Nonnull
    public static Icon entityMarker(@Nonnull Icon delegate) {
        if (delegate instanceof ScaledIcon) {
            return delegate;
        }
        return new ScaledIcon(delegate, ENTITY_MARKER_SCALE);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            int visualWidth = Math.max(1, (int) Math.round(delegate.getIconWidth() * scale));
            int visualHeight = Math.max(1, (int) Math.round(delegate.getIconHeight() * scale));
            int dx = x + (getIconWidth() - visualWidth) / 2;
            int dy = y + (getIconHeight() - visualHeight) / 2;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(dx, dy);
            g2.scale(scale, scale);
            delegate.paintIcon(c, g2, 0, 0);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public int getIconWidth() {
        return delegate.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return delegate.getIconHeight();
    }
}
