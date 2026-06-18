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
public class MListAddButton extends MListButton {

    public MListAddButton(ActionListener actionListener) {
        super("Add", ModernProtegeTheme.SELECTION, actionListener);
    }


    public void paintButtonContent(Graphics2D g) {
        int size = getBounds().height;
        int thickness = (Math.round(size / 8.0f) / 2) * 2;
        
        int x = getBounds().x;
        int y = getBounds().y;

        int insetX = size / 4;
        int insetY = size / 4;
        int insetHeight = size / 2;
        int insetWidth = size / 2;
        g.drawLine(x + size / 2, y + insetY, x + size / 2, y + insetY + insetHeight);
        g.drawLine(x + insetX, y + size / 2, x + insetX + insetWidth, y + size / 2);
    }

    @Override
    protected int getSizeMultiple() {
        return 4;
    }
}
