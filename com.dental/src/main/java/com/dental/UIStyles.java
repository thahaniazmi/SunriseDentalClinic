package com.dental;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class UIStyles {

    // our colour palette
    public static final Color BACKGROUND = new Color(0xFDFDFC);
    public static final Color TEXT_PRIMARY = new Color(0x7A7A7A);
    public static final Color TEXT_SECONDARY = new Color(0x8A8A8A);
    public static final Color PLACEHOLDER = new Color(0x9A9A9A);
    public static final Color BORDER_COLOR = new Color(0xB9B9B9);
    public static final Color PINK = new Color(0xE8899B);
    public static final Color PINK_HOVER = new Color(0xE0768B);
    public static final Color PINK_PRESSED = new Color(0xD4637A);
    public static final Color ALTERNATE_ROW = new Color(0xF4F4F2);
    public static final Color SELECTION = new Color(0xF6DCE1);

    // the fonts we reuse
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 15);

    // same text color on all tables
    public static DefaultTableCellRenderer cellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    cell.setBackground((row % 2 == 0) ? BACKGROUND : ALTERNATE_ROW);
                    cell.setForeground(TEXT_PRIMARY);
                }
                return cell;
            }
        };
    }

    // basic table setup, used by most tables
    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setGridColor(BORDER_COLOR);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer());
        }
    }

    public static void styleField(JComponent field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(4, 8, 4, 8)));
        field.setFont(FONT_NORMAL);
        field.setForeground(TEXT_PRIMARY);
    }

    public static void styleFieldLabel(JLabel label) {
        label.setForeground(TEXT_SECONDARY);
        label.setFont(FONT_BOLD);
    }

    public static void showHelpDialog(Component parent, String bodyHtml) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Help",
                Dialog.ModalityType.APPLICATION_MODAL);

        JEditorPane editor = new JEditorPane();
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setBackground(BACKGROUND);
        editor.setText(bodyHtml);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(new JScrollPane(editor), BorderLayout.CENTER);

        JButton okButton = new PinkButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        okButton.setPreferredSize(new Dimension(100, 36));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        buttonPanel.add(okButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setSize(520, 480);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
