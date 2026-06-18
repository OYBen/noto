package org.protege.editor.core.ui.list;

import org.protege.editor.core.ui.view.ModernProtegeTheme;

import java.awt.*;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class MListDeleteButton extends MListButton {

    public static final Color ROLL_OVER_COLOR = ModernProtegeTheme.DANGER;


    public MListDeleteButton(ActionListener actionListener) {
        super("Remove", ROLL_OVER_COLOR, actionListener);
    }


    public void paintButtonContent(Graphics2D gIn) {
        Graphics2D g = (Graphics2D) gIn.create();
        int size = getBounds().height;
        int x = getBounds().x;
        int y = getBounds().y;

        int insetX = size / 4;
        int insetY = size / 4;
        int insetHeight = size / 2;
        int insetWidth = size / 2;
        g.drawLine(x + insetX, y + insetY, x + insetX + insetWidth, y + insetY + insetHeight);
        g.drawLine(x + insetX, y + insetY + insetHeight, x + insetX + insetWidth, y + insetY);
        g.dispose();
    }

    @Override
    protected int getSizeMultiple() {
        return 4;
    }
}

