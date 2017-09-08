package com.bs;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

class StatsFrame extends JFrame {
    private JScrollPane spStats;
    private JScrollPane spMatrix;
    private JTabbedPane tabbedPane = new JTabbedPane();

    StatsFrame() {
        super("BusDataStats");
        spStats = new JScrollPane();
        spMatrix = new JScrollPane();
        tabbedPane.add(spStats, "统计表");
        tabbedPane.add(spMatrix, "矩阵表");
        JButton btn = new JButton("选择文件");
        btn.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser(".");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter filter = new TxtFileFilter();
            jfc.addChoosableFileFilter(filter);
            jfc.setFileFilter(filter);
            int returnVal = jfc.showOpenDialog(StatsFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                String fileName = file.getName();
                System.out.println(fileName);
                if (fileName.toLowerCase().endsWith(".txt")) {
                    try {
                        List list = Main.getSrcList(file);
                        spStats = new JScrollPane(new DecoratedTable(new TableModelStats(list)));
                        spMatrix = new JScrollPane(new DecoratedTable(new TableModelMatrix(list)));
                        tabbedPane.removeAll();
                        tabbedPane.add(spStats, "统计表");
                        tabbedPane.add(spMatrix, "矩阵表");
                        this.pack();
                    } catch (IOException | ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        this.getContentPane().add(btn, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(450, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}

class TxtFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
    }

    @Override
    public String getDescription() {
        return ".txt";
    }
}