package org.protege.editor.core.ui.view;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class SplitVerticallyIcon extends ViewBarIcon {

    private static final SplitVerticallyIcon ICON = new SplitVerticallyIcon();

    private SplitVerticallyIcon() {
    }

    /**
     * Gets the Split Vertically Icon.
     * @return The Split Vertically Icon.
     */
    @Nonnull
    public static SplitVerticallyIcon get() {
        return ICON;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = ModernProtegeTheme.iconGraphics(g);
        g2.translate(x, y);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawRoundRect(2, 2, width - 4, height - 4, 3, 3);
        g2.drawLine(width / 2, 3, width / 2, height - 3);
        g2.dispose();
    }


}
