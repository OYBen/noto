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

    public static final Color VIEW_HEADER_FOREGROUND = new Color(250, 250, 247);

    public static final Color VIEW_ICON_COLOR = new Color(252, 252, 249);

    public static final Color VIEW_ICON_ROLLOVER_BACKGROUND = new Color(255, 255, 255, 42);

    public static final Color APP_BACKGROUND = new Color(243, 245, 246);

    public static final Color SURFACE = new Color(255, 255, 253);

    public static final Color SURFACE_ALT = new Color(235, 239, 240);

    public static final Color BORDER = new Color(184, 194, 198);

    public static final Color TEXT = new Color(14, 18, 20);

    public static final Color MUTED_TEXT = new Color(47, 58, 62);

    public static final Color SELECTION = new Color(36, 99, 169);

    public static final Color SELECTION_SOFT = new Color(214, 229, 247);

    public static final Color CONTROL_HOVER = new Color(222, 229, 232);

    public static final Color MENU_BAR_BACKGROUND = new Color(43, 45, 48);

    public static final Color MENU_BAR_FOREGROUND = new Color(238, 240, 242);

    public static final Color MENU_BAR_SELECTION = new Color(58, 62, 66);

    public static final Color MENU_POPUP_BACKGROUND = new Color(250, 251, 251);

    public static final Color STATUS_BACKGROUND = new Color(232, 237, 239);

    public static final Color STATUS_BORDER = new Color(170, 182, 187);

    public static final Color STATUS_CHIP_BACKGROUND = new Color(248, 249, 248);

    public static final Color DANGER = new Color(168, 100, 95);

    public static final Color SUCCESS = new Color(110, 138, 112);

    public static final Color INFO = new Color(103, 126, 151);

    private static final Color MORANDI_STONE = new Color(93, 107, 112);

    private static final Color MORANDI_MAUVE = new Color(125, 82, 117);

    private static final Color MORANDI_OLIVE = new Color(124, 114, 57);

    private static final Color MORANDI_SAGE = new Color(84, 125, 91);

    private static final Color MORANDI_BLUE = new Color(72, 103, 137);

    private static final Color MORANDI_TEAL = new Color(62, 124, 130);

    private static final Color MORANDI_CLAY = new Color(138, 82, 68);

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
        return blend(color, MORANDI_STONE, 0.18f);
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
        g2.setColor(color);
        int width = component.getWidth();
        int height = component.getHeight();
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height + 10, 10, 10));
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
            g2.setColor(c.hasFocus() ? SELECTION : BORDER);
            g2.drawRoundRect(x, y, width - 1, height - 1, 14, 14);
            g2.dispose();
        }
    }
}
