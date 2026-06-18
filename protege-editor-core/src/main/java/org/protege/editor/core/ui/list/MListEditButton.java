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
public class MListEditButton extends MListButton {

    public MListEditButton(ActionListener actionListener) {
        super("Edit", ModernProtegeTheme.INFO, actionListener);
    }

    @Override
	public void paintButtonContent(Graphics2D g) {
        Rectangle bounds = getBounds();
        int x = bounds.x;
        int y = bounds.y;
        int size = bounds.width;
        int left = x + size / 4;
        int bottom = y + size * 3 / 4;
        int right = x + size * 3 / 4;
        int top = y + size / 4;
        g.drawLine(left, bottom, right, top);
        g.drawLine(left - 1, bottom + 1, left + 3, bottom);
    }

    @Override
    protected int getSizeMultiple() {
        return 4;
    }
}
