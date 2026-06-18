package org.protege.editor.core.ui.view;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class CloseIcon extends ViewBarIcon {

    private static final CloseIcon ICON = new CloseIcon();

    private CloseIcon() {
    }

    /**
     * Gets the Close View icon.
     * @return The Close View icon.
     */
    @Nonnull
    public static CloseIcon get() {
        return ICON;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = ModernProtegeTheme.iconGraphics(g);
        g2.translate(x, y);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawLine(4, 4, width - 4, height - 4);
        g2.drawLine(4, height - 4, width - 4, 4);
        g2.dispose();
    }

}
