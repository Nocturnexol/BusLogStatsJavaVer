package com.bs;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

class StatsFrame extends JFrame implements ActionListener {
    private TableModelMatrix tableModelMatrix;
    private String fileName;
    private ResultBean res = new ResultBean();
    private JScrollPane spStats = new JScrollPane();
    private JScrollPane spMatrix = new JScrollPane();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JButton chooseBtn = new JButton("选择文件");
    private JButton exportBtn = new JButton("导出EXCEL");

    StatsFrame(String title) {
        super(title);
        tabbedPane.add(spStats, "统计表");
        tabbedPane.add(spMatrix, "矩阵表");
        chooseBtn.addActionListener(this);
        exportBtn.addActionListener(this);
        JPanel pnl = new JPanel();
        pnl.add(exportBtn);
        pnl.add(chooseBtn);
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        this.getContentPane().add(pnl, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(450, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chooseBtn) {
            JFileChooser jfc = new JFileChooser(".");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter filter = new TxtFileFilter();
            jfc.addChoosableFileFilter(filter);
            jfc.setFileFilter(filter);
            jfc.setAcceptAllFileFilterUsed(false);
            int returnVal = jfc.showOpenDialog(StatsFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                fileName = file.getName();
                System.out.println(fileName);
                try {
                    long startTime = System.currentTimeMillis();
                    List list = Main.getSrcList(file);
                    spStats = new JScrollPane(new DecoratedTable(new TableModelStats(list, res)));
                    tableModelMatrix = new TableModelMatrix(list);
                    spMatrix = new JScrollPane(new DecoratedTable(tableModelMatrix));
                    tabbedPane.removeAll();
                    tabbedPane.add(spStats, "统计表");
                    tabbedPane.add(spMatrix, "矩阵表");
                    this.pack();
                    long endTime = System.currentTimeMillis();
                    float excTime = (float) (endTime - startTime) / 1000;
                    JOptionPane.showMessageDialog(this, "统计完成，耗时" + excTime + "秒");
                } catch (IOException | ParseException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == exportBtn) {
            if (tableModelMatrix != null) {
                try {
                    ExcelHelper.export(tableModelMatrix, fileName, res);
                    JOptionPane.showMessageDialog(this, "导出成功！");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择文件！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        }
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