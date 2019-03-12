package com.bs;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

class StatsFrame extends JFrame implements ActionListener {
    private TableModelMatrix tableModelMatrix;
    private JScrollPane spStats = new JScrollPane();
    private JScrollPane spMatrix = new JScrollPane();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JMenuItem chooseMenu = new JMenuItem("选择文件...");
    private JMenuItem chooseDirMenu = new JMenuItem("选择文件夹...");
    private JMenuItem exportMenu = new JMenuItem("导出EXCEL");
    private JMenuItem exitMenu = new JMenuItem("退出");

    StatsFrame(String title) {
        super(title);
        tabbedPane.add(spStats, "统计表");
        tabbedPane.add(spMatrix, "矩阵表");
        chooseMenu.addActionListener(this);
        chooseDirMenu.addActionListener(this);
        exportMenu.addActionListener(this);
        exitMenu.addActionListener(this);
        JMenu jm = new JMenu("File");
        jm.add(chooseMenu);
        jm.add(chooseDirMenu);
        jm.add(exportMenu);
        jm.addSeparator();
        jm.add(exitMenu);
        jm.setMnemonic(KeyEvent.VK_F);
        JMenuBar jmb = new JMenuBar();
        jmb.add(jm);
        this.setJMenuBar(jmb);
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(450, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chooseMenu) {
            JFileChooser jfc = new JFileChooser(".");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter filter = new TxtFileFilter();
            jfc.addChoosableFileFilter(filter);
            jfc.setFileFilter(filter);
            jfc.setAcceptAllFileFilterUsed(false);
            int returnVal = jfc.showOpenDialog(StatsFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                String fileName = file.getName();
                System.out.println(fileName);
                try {
                    long startTime = System.currentTimeMillis();
                    List list = BusDataProcessor.getSrcList(file);
                    spStats = new JScrollPane(new DecoratedTable(new TableModelStats(list)));
                    tableModelMatrix = new TableModelMatrix(list);
                    tableModelMatrix.setFileName(fileName);
                    spMatrix = new JScrollPane(new DecoratedTable(tableModelMatrix));
                    tabbedPane.removeAll();
                    tabbedPane.add(spStats, "统计表");
                    tabbedPane.add(spMatrix, "矩阵表");
                    this.pack();
                    long endTime = System.currentTimeMillis();
                    float excTime = (float) (endTime - startTime) / 1000;
                    JOptionPane.showMessageDialog(this, "统计完成，耗时" + excTime + "秒");
                    System.out.println("耗时" + excTime + "秒");
                } catch (IOException | ParseException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == chooseDirMenu) {
            JFileChooser jfc = new JFileChooser(".");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = jfc.showOpenDialog(StatsFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    ExcelHelper.bulkExport(jfc.getSelectedFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } else if (e.getSource() == exportMenu) {
            if (tableModelMatrix != null) {
                try {
                    ExcelHelper.export(tableModelMatrix);
                    JOptionPane.showMessageDialog(this, "导出成功！");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择文件！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == exitMenu) {
            System.exit(0);
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