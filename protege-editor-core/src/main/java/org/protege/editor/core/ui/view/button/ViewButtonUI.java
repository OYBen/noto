package org.protege.editor.core.ui.view.button;

import org.protege.editor.core.ui.view.ModernProtegeTheme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 24, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewButtonUI extends BasicButtonUI {

    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();
        if (model.isRollover() || model.isPressed()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ModernProtegeTheme.VIEW_ICON_ROLLOVER_BACKGROUND);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 7, 7);
            g2.dispose();
        }
        super.paint(g, c);
    }


    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        g.translate(1, 1);
        paintIcon(g, b, b.getBounds());
    }
}
