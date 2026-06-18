package org.protege.editor.core.ui.view;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class FloatIcon extends ViewBarIcon {

    public static final FloatIcon ICON = new FloatIcon();

    private FloatIcon() {
    }

    /**
     * Gets the Float View Icon.
     * @return The Float View Icon.
     */
    @Nonnull
    public static FloatIcon get() {
        return ICON;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = ModernProtegeTheme.iconGraphics(g);
        g2.translate(x, y);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawRoundRect(3, 4, width - 6, height - 6, 3, 3);
        g2.drawLine(5, 2, width - 3, 2);
        g2.drawLine(width - 3, 2, width - 3, height - 5);
        g2.dispose();
    }
}
