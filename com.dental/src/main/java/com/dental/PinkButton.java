package com.dental;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JButton;

// a normal button, but we paint the pink color ourselves because the
// windows look and feel ignores setBackground() on buttons
public class PinkButton extends JButton {

    public PinkButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(UIStyles.FONT_BOLD);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        ButtonModel model = getModel();
        if (model.isPressed()) {
            g.setColor(UIStyles.PINK_PRESSED);
        } else if (model.isRollover()) {
            g.setColor(UIStyles.PINK_HOVER);
        } else {
            g.setColor(UIStyles.PINK);
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}
