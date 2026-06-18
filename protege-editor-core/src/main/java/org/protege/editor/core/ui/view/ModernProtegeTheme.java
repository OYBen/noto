package org.protege.editor.core.ui.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Small shared palette and drawing helpers for the FlatLaf based Protege skin.
 */
public final class ModernProtegeTheme {

    public static final int ROW_HEIGHT = 28;

    public static final int CONTROL_HEIGHT = 30;

    public static final int TOOLBAR_HEIGHT = 34;

    public static final int VIEW_HEADER_HEIGHT = 28;

    public static final int TAB_HEIGHT = 30;

    public static final int ICON_SIZE = 16;

    public static final int ICON_BUTTON_SIZE = 24;

    public static final int CORNER_RADIUS = 12;

    public static final Color VIEW_HEADER_FOREGROUND = new Color(29, 36, 46);

    public static final Color VIEW_ICON_COLOR = new Color(93, 105, 120);

    public static final Color VIEW_ICON_ROLLOVER_BACKGROUND = new Color(238, 244, 255);

    public static final Color APP_BACKGROUND = new Color(248, 250, 252);

    public static final Color SURFACE = new Color(255, 255, 255);

    public static final Color SURFACE_ALT = new Color(244, 246, 249);

    public static final Color BORDER = new Color(219, 224, 230);

    public static final Color TEXT = new Color(29, 36, 46);

    public static final Color MUTED_TEXT = new Color(93, 105, 120);

    public static final Color SELECTION = new Color(47, 100, 190);

    public static final Color SELECTION_SOFT = new Color(238, 244, 255);

    public static final Color SELECTION_BORDER = new Color(121, 167, 248);

    public static final Color CONTROL_HOVER = new Color(242, 246, 251);

    public static final Color TITLE_BAR_BACKGROUND = new Color(23, 24, 26);

    public static final Color MENU_BAR_BACKGROUND = TITLE_BAR_BACKGROUND;

    public static final Color MENU_BAR_FOREGROUND = new Color(244, 246, 245);

    public static final Color MENU_BAR_SELECTION = new Color(43, 47, 54);

    public static final Color MENU_POPUP_BACKGROUND = new Color(252, 253, 252);

    public static final Color STATUS_BACKGROUND = new Color(247, 249, 252);

    public static final Color STATUS_BORDER = new Color(206, 214, 222);

    public static final Color STATUS_CHIP_BACKGROUND = new Color(255, 255, 255);

    public static final Color DANGER = new Color(154, 83, 76);

    public static final Color SUCCESS = new Color(80, 145, 104);

    public static final Color INFO = SELECTION;

    private static final Color MORANDI_STONE = new Color(102, 113, 128);

    private static final Color MORANDI_MAUVE = new Color(98, 113, 148);

    private static final Color MORANDI_OLIVE = new Color(120, 128, 108);

    private static final Color MORANDI_SAGE = new Color(101, 128, 123);

    private static final Color MORANDI_BLUE = new Color(69, 103, 151);

    private static final Color MORANDI_TEAL = new Color(80, 123, 143);

    private static final Color MORANDI_CLAY = new Color(135, 105, 105);

    private ModernProtegeTheme() {
    }

    public static Color viewHeaderColor(Color source) {
        float[] hsb = Color.RGBtoHSB(source.getRed(), source.getGreen(), source.getBlue(), null);
        if (hsb[1] < 0.12f) {
            return MORANDI_STONE;
        }
        float hue = hsb[0];
        if (hue < 0.06f || hue > 0.93f) {
            return MORANDI_CLAY;
        }
        if (hue < 0.18f) {
            return MORANDI_OLIVE;
        }
        if (hue < 0.42f) {
            return MORANDI_SAGE;
        }
        if (hue < 0.55f) {
            return MORANDI_TEAL;
        }
        if (hue < 0.72f) {
            return MORANDI_BLUE;
        }
        return MORANDI_MAUVE;
    }

    public static Color subtleHeaderColor(Color source) {
        Color color = viewHeaderColor(source);
        return blend(color, SELECTION, 0.12f);
    }

    public static Color blend(Color a, Color b, float bWeight) {
        float aWeight = 1.0f - bWeight;
        return new Color(
                Math.round(a.getRed() * aWeight + b.getRed() * bWeight),
                Math.round(a.getGreen() * aWeight + b.getGreen() * bWeight),
                Math.round(a.getBlue() * aWeight + b.getBlue() * bWeight),
                Math.round(a.getAlpha() * aWeight + b.getAlpha() * bWeight)
        );
    }

    public static Graphics2D iconGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(VIEW_ICON_COLOR);
        return g2;
    }

    public static void paintRoundedHeader(Graphics g, Component component, Color color) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = component.getWidth();
        int height = component.getHeight();
        g2.setColor(SURFACE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(SELECTION);
        g2.fillRoundRect(8, 7, 3, Math.max(0, height - 14), 3, 3);
        g2.setColor(BORDER);
        g2.drawLine(0, height - 1, width, height - 1);
        g2.dispose();
    }

    public static Dimension iconButtonSize() {
        return new Dimension(ICON_BUTTON_SIZE, ICON_BUTTON_SIZE);
    }

    public static void tuneToolbar(AbstractButton button) {
        button.setFocusable(false);
        button.setRequestFocusEnabled(false);
        button.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        button.setPreferredSize(iconButtonSize());
        button.setMinimumSize(iconButtonSize());
    }

    public static void tuneToolbar(JToolBar toolBar) {
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        toolBar.setPreferredSize(new Dimension(toolBar.getPreferredSize().width, TOOLBAR_HEIGHT));
    }

    public static javax.swing.border.Border roundedTextBorder() {
        return new RoundedControlBorder();
    }

    private static final class RoundedControlBorder extends AbstractBorder implements UIResource {

        private static final Insets INSETS = new Insets(5, 9, 5, 9);

        @Override
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = INSETS.top;
            insets.left = INSETS.left;
            insets.bottom = INSETS.bottom;
            insets.right = INSETS.right;
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.hasFocus() ? SELECTION_BORDER : BORDER);
            g2.drawRoundRect(x, y, width - 1, height - 1, 14, 14);
            g2.dispose();
        }
    }
}
