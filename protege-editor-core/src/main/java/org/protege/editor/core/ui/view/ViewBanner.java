package org.protege.editor.core.ui.view;

import org.protege.editor.core.ui.view.button.ViewButtonUI;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A component that displays a "banner" with
 * white text and a coloured  background.
 */
public class ViewBanner extends JPanel {


    private final JPanel toolBarPanel;

    private final JPanel labelPanel;

    private final JLabel label = new JLabel();

    private final JToolBar toolBar = new JToolBar();

    private final Color foregroundColor = ModernProtegeTheme.VIEW_HEADER_FOREGROUND;

    private Color backgroundColor;

    private String labelText;

    private Color defaultBackgroundColor;

    /**
     * Constructs a {@link ViewBanner} with the specified label text and the specified background color.
     * @param labelText The label text.
     * @param bannerColor The background color.
     */
    public ViewBanner(@Nonnull String labelText,
                      @Nonnull Color bannerColor) {
        this.labelText = checkNotNull(labelText);
        this.defaultBackgroundColor = ModernProtegeTheme.subtleHeaderColor(checkNotNull(bannerColor));
        this.backgroundColor = defaultBackgroundColor;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBackground(backgroundColor);
        labelPanel = new JPanel(new BorderLayout());
        add(labelPanel, BorderLayout.NORTH);
        labelPanel.setPreferredSize(new Dimension(0, ModernProtegeTheme.VIEW_HEADER_HEIGHT));
        labelPanel.setBackground(backgroundColor);
        labelPanel.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        label.setForeground(foregroundColor);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 14f));
        setText("");
        toolBar.setBorderPainted(false);
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);
        ModernProtegeTheme.tuneToolbar(toolBar);
        toolBarPanel = new JPanel(new BorderLayout());
        toolBarPanel.add(toolBar, BorderLayout.EAST);
        toolBarPanel.setOpaque(false);
        toolBarPanel.setBackground(backgroundColor);
        labelPanel.add(toolBarPanel, BorderLayout.EAST);
        labelPanel.add(label, BorderLayout.WEST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        ModernProtegeTheme.paintRoundedHeader(g, this, backgroundColor);
        super.paintComponent(g);
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        label.setEnabled(enabled);
    }


    /**
     * Sets the header sub header.
     * @param text The text that should be set as
     *             the sub header text.
     */
    public void setText(@Nonnull String text) {
        label.setForeground(foregroundColor);
        label.setText(labelText + ": " + text);
    }


    /**
     * Sets the background color of the header.
     * @param color The color to be set.
     */
    public void setBannerColor(Color color) {
        backgroundColor = ModernProtegeTheme.subtleHeaderColor(color);
        setBackground(backgroundColor);
        labelPanel.setBackground(backgroundColor);
        toolBarPanel.setBackground(backgroundColor);
        repaint();
        revalidate();
    }


    /**
     * Sets this view as being pinned.  If a view is pinned then it will not synchronise with the global selection.
     * @param pinned true if the view should be pinned, otherwise false.
     */
    public void setPinned(boolean pinned) {
        if (pinned) {
            setBannerColor(Color.GRAY);
        }
        else {
            setBannerColor(defaultBackgroundColor);
        }
    }


    /**
     * Removes all actions from the header.
     */
    public void removeAllActions() {
        toolBar.removeAll();
    }

    /**
     * Adds an action to the view header.
     * @param name The name of the action.
     * @param icon An icon to display in the view header for the action.
     * @param runnable The code that should be called when the action is executed.
     */
    public void addAction(@Nonnull String name,
                          @Nonnull Icon icon,
                          @Nonnull final Runnable runnable) {
        addAction(new AbstractAction(name, icon) {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        });
    }

    /**
     * Adds an action to the view header.
     * @param action The action to be added.
     */
    public void addAction(@Nonnull Action action) {
        String name = (String) action.getValue(Action.NAME);
        action.putValue(Action.NAME, "");
        action.putValue(Action.SHORT_DESCRIPTION, name);
        JButton button = new JButton(action) {
            public void updateUI() {
            }
        };
        button.setFocusable(false);
        toolBar.add(button);
        Icon icon = (Icon) action.getValue(Action.SMALL_ICON);
        if (icon != null) {
            button.setPreferredSize(ModernProtegeTheme.iconButtonSize());
            button.setMinimumSize(ModernProtegeTheme.iconButtonSize());
            button.setOpaque(false);
            button.setUI(new ViewButtonUI());
            ModernProtegeTheme.tuneToolbar(button);
        }
    }
}
