package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.ui.view.ModernProtegeTheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Searchable command palette backed by the current workspace menu bar.
 */
final class ModernCommandPalette {

    private static final String ACTION_KEY = "modern-command-palette";

    private ModernCommandPalette() {
    }

    static void install(JComponent component, JMenuBar menuBar) {
        int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        KeyStroke shortcut = KeyStroke.getKeyStroke(KeyEvent.VK_P, menuMask | InputEvent.SHIFT_DOWN_MASK);
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(shortcut, ACTION_KEY);
        component.getActionMap().put(ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                show(component, menuBar);
            }
        });
        installMenuEntry(menuBar, component);
    }

    private static void installMenuEntry(JMenuBar menuBar, JComponent component) {
        JMenu windowMenu = findMenu(menuBar, Workspace.WINDOW_MENU_NAME);
        if (windowMenu == null || hasCommandPaletteItem(windowMenu)) {
            return;
        }
        windowMenu.addSeparator();
        JMenuItem item = windowMenu.add("Command Palette...");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK));
        item.addActionListener(e -> show(component, menuBar));
    }

    private static boolean hasCommandPaletteItem(JMenu menu) {
        for (Component component : menu.getMenuComponents()) {
            if (component instanceof JMenuItem && "Command Palette...".equals(((JMenuItem) component).getText())) {
                return true;
            }
        }
        return false;
    }

    private static JMenu findMenu(JMenuBar menuBar, String name) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null && name.equals(menu.getText())) {
                return menu;
            }
        }
        return null;
    }

    private static void show(JComponent component, JMenuBar menuBar) {
        Window owner = SwingUtilities.getWindowAncestor(component);
        JDialog dialog = new JDialog(owner, "Command Palette", Dialog.ModalityType.MODELESS);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTextField searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search commands");
        searchField.setBorder(ModernProtegeTheme.roundedTextBorder());

        DefaultListModel<CommandItem> model = new DefaultListModel<>();
        JList<CommandItem> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(10);
        list.setFixedCellHeight(ModernProtegeTheme.ROW_HEIGHT + 4);
        list.setCellRenderer(new CommandRenderer());

        List<CommandItem> commands = collectCommands(menuBar);
        Runnable refresh = () -> {
            String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
            model.clear();
            for (CommandItem command : commands) {
                if (query.isEmpty() || command.searchText.contains(query)) {
                    model.addElement(command);
                }
            }
            if (!model.isEmpty()) {
                list.setSelectedIndex(0);
            }
        };
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                refresh.run();
            }

            public void removeUpdate(DocumentEvent e) {
                refresh.run();
            }

            public void changedUpdate(DocumentEvent e) {
                refresh.run();
            }
        });

        Action execute = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                CommandItem selected = list.getSelectedValue();
                if (selected != null) {
                    dialog.dispose();
                    SwingUtilities.invokeLater(() -> selected.menuItem.doClick());
                }
            }
        };
        searchField.addActionListener(execute);
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    execute.actionPerformed(null);
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        panel.setBackground(ModernProtegeTheme.APP_BACKGROUND);
        panel.add(searchField, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        dialog.setContentPane(panel);
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setSize(560, 420);
        dialog.setLocationRelativeTo(owner);
        refresh.run();
        dialog.setVisible(true);
        searchField.requestFocusInWindow();
    }

    private static List<CommandItem> collectCommands(JMenuBar menuBar) {
        List<CommandItem> commands = new ArrayList<>();
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                collectCommands(menu, clean(menu.getText()), commands);
            }
        }
        return commands;
    }

    private static void collectCommands(JMenu menu, String path, List<CommandItem> commands) {
        for (Component component : menu.getMenuComponents()) {
            if (component instanceof JMenu) {
                JMenu childMenu = (JMenu) component;
                collectCommands(childMenu, path + " / " + clean(childMenu.getText()), commands);
            }
            else if (component instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) component;
                String text = clean(item.getText());
                if (!text.isEmpty() && item.isEnabled()) {
                    commands.add(new CommandItem(path + " / " + text, item));
                }
            }
        }
    }

    private static String clean(String text) {
        return text == null ? "" : text.replace("...", "").trim();
    }

    private static final class CommandItem {
        private final String path;
        private final String searchText;
        private final JMenuItem menuItem;

        private CommandItem(String path, JMenuItem menuItem) {
            this.path = path;
            this.menuItem = menuItem;
            this.searchText = path.toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return path;
        }
    }

    private static final class CommandRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            label.setForeground(isSelected ? Color.WHITE : ModernProtegeTheme.TEXT);
            return label;
        }
    }
}
