package org.protege.editor.core.ui.util;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-18
 */
public class SearchIcon implements Icon {

    private static final int WIDTH = 18;

    private static final int HEIGHT = 18;

    private final Color iconColor;

    public static final BasicStroke RIM_STROKE = new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public static final BasicStroke HANDLE_STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public SearchIcon(@Nonnull Color color) {
        this.iconColor = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(iconColor);
        g2.translate(x, y);
        Stroke s = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(HANDLE_STROKE);
        g2.drawLine(11, 11, 16, 16);

        g2.setStroke(RIM_STROKE);
        g2.drawOval(1, 1, 11, 11);
        g2.setStroke(s);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return WIDTH;
    }

    @Override
    public int getIconHeight() {
        return HEIGHT;
    }
}
