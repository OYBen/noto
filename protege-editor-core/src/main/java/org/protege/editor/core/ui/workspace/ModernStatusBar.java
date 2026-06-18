package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.ui.view.ModernProtegeTheme;

import javax.swing.*;
import java.awt.*;

/**
 * A modern shell for workspace status components.
 */
final class ModernStatusBar extends JPanel {

    private ModernStatusBar(JComponent content) {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(ModernProtegeTheme.STATUS_BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ModernProtegeTheme.STATUS_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        content.setOpaque(false);
        tuneChildren(content);
        add(content, BorderLayout.CENTER);
    }

    static JComponent wrap(JComponent content) {
        if (content instanceof ModernStatusBar) {
            return content;
        }
        return new ModernStatusBar(content);
    }

    private static void tuneChildren(Component component) {
        if (component instanceof JComponent) {
            ((JComponent) component).setForeground(ModernProtegeTheme.MUTED_TEXT);
        }
        if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            button.setOpaque(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        }
        if (component instanceof JLabel) {
            ((JLabel) component).setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                tuneChildren(child);
            }
        }
    }
}
