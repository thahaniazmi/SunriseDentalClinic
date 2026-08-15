package com.dental.util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class ReceiptPrinter {
    public static void print(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Monospaced", Font.PLAIN, 11));
        area.setEditable(false);
        area.setSize(420, 700);
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((Graphics g, PageFormat pf, int page) -> {
                if (page > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                g.translate((int) pf.getImageableX(), (int) pf.getImageableY());
                area.print(g);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not print the receipt:\n" + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}