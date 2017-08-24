package com.bs;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatsFrame extends JFrame {
    public StatsFrame(List list) {
        super("BusDataStats");
        JTabbedPane tabbedPane = new JTabbedPane();
        JScrollPane spStats = new JScrollPane(new DecoratedTable(new TableModelStats(list)));
        JScrollPane spMatrix = new JScrollPane(new DecoratedTable(new TableModelMatrix(list)));
        tabbedPane.add(spStats, "统计表");
        tabbedPane.add(spMatrix, "矩阵表");
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
