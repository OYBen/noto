package org.protege.editor.core.ui.view;

import java.awt.*;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class HelpIcon extends ViewBarIcon {


    private static final String HELP_STRING = "?";

    private static final HelpIcon ICON = new HelpIcon();

    private static final Font font = new Font("Verdana", Font.BOLD, 10);

    private HelpIcon() {
    }

    public static HelpIcon get() {
        return ICON;
    }


    @Override
    public void paintIcon(Component c, Graphics graphics, int x, int y) {
        Graphics2D g2 = ModernProtegeTheme.iconGraphics(graphics);
        g2.translate(x, y);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawOval(2, 2, width - 4, height - 4);

        g2.setFont(font);
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        java.awt.geom.Rectangle2D bounds = fm.getStringBounds(HELP_STRING, g2);

        float xPos = (float) ((width - bounds.getWidth()) / 2);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        float yPos = ((height + 1) / 2 - (ascent + descent) / 2 + ascent);
        g2.drawString(HELP_STRING,
                     xPos,
                     yPos);
        g2.dispose();
    }
}
