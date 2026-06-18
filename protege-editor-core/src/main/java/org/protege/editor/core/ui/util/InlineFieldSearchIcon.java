package org.protege.editor.core.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 4 Aug 16
 */
public class InlineFieldSearchIcon implements Icon {

    private static final int WIDTH = 12;

    private static final int HEIGHT = 12;

    private final Color iconColor;

    private double scaleFactor = 1.0;

    public static final BasicStroke RIM_STROKE = new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public static final BasicStroke HANDLE_STROKE = new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public InlineFieldSearchIcon(Color color) {
        this.iconColor = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(iconColor);
        g2.translate(x, y);
        Stroke s = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.scale(scaleFactor, scaleFactor);

        g2.setStroke(HANDLE_STROKE);
        g2.drawLine(7, 7, 11, 11);


        g2.setStroke(RIM_STROKE);
        g2.setColor(iconColor);
        g2.drawOval(1, 1, 8, 8);
        g2.setStroke(s);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return (int) (WIDTH * scaleFactor + 0.5);
    }

    @Override
    public int getIconHeight() {
        return (int) (HEIGHT * scaleFactor + 0.5);
    }

    /**
     * Sets the scale factor that can be used to scale the icon up or down.  The default value is 1.0.
     * @param scaleFactor The scale factor.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
