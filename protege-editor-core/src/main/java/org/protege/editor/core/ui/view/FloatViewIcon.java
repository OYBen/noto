package org.protege.editor.core.ui.view;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class FloatViewIcon extends ViewIcon {

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = ModernProtegeTheme.iconGraphics(g);
        g2.translate(x, y);
        g2.drawRoundRect(3, 4, getIconWidth() - 6, getIconHeight() - 6, 3, 3);
        g2.drawLine(5, 2, getIconWidth() - 3, 2);
        g2.drawLine(getIconWidth() - 3, 2, getIconWidth() - 3, getIconHeight() - 5);
        g2.dispose();
    }
}
