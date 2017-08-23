package com.bs;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import static net.servicestack.func.Func.*;

class Main {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private JTable table;

    private TableModel model;

    private Main() {
        JFrame frame = new JFrame("JTableTest");
        JPanel pane = new JPanel();
        JButton button_1 = new JButton("清除数据");
        button_1.addActionListener(e -> removeData());
        JButton button_2 = new JButton("添加数据");
//        button_2.addActionListener(e -> addData());
        JButton button_3 = new JButton("保存数据");
        button_3.addActionListener(e -> saveData());
        pane.add(button_1);
        pane.add(button_2);
        pane.add(button_3);
        model = new TableModel(20);
        table = new JTable(model);
        table.setBackground(Color.white);
//        String[] age = {"16", "17", "18", "19", "20", "21", "22"};
//        JComboBox com = new JComboBox(age);
        TableColumnModel tcm = table.getColumnModel();
//        tcm.getColumn(3).setCellEditor(new DefaultCellEditor(com));
        tcm.getColumn(0).setPreferredWidth(50);
        tcm.getColumn(1).setPreferredWidth(100);
//        tcm.getColumn(2).setPreferredWidth(50);

        JScrollPane s_pan = new JScrollPane(table);

        frame.getContentPane().add(s_pan, BorderLayout.CENTER);
//        frame.getContentPane().add(pane, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);

    }

//    private void addData() {
//        model.addRow("李逵", true, "19");
//        table.updateUI();
//    }

    private void removeData() {
        model.removeRows(0, model.getRowCount());
        table.updateUI();
    }

    // 保存数据，暂时是将数据从控制台显示出来
    private void saveData() {
        int col = model.getColumnCount();
        int row = model.getRowCount();
        for (int i = 0; i < col; i++) {
            System.out.print(model.getColumnName(i) + "\t");
        }
        System.out.print("\r\n");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.print(model.getValueAt(i, j) + "\t");
            }
            System.out.print("\r\n");
        }
        System.out.println("------------------------------------");
    }

    public static void main(String[] args) throws IOException, ParseException {
        // write your code here
        new Main();
        List srcList = getSrcList();
        System.out.println(srcList.size());
    }

    private static List getSrcList() throws IOException, ParseException {
        List res = fileToList();
//        orderBy();
        return res;
    }

    private static List fileToList() throws IOException, ParseException {
        List res = new ArrayList<BusData>();
//        BufferedReader br = new BufferedReader(new FileReader(Main.class.getResource("").getPath() + "\\data.txt"));
//        BufferedReader br=new BufferedReader(new InputStreamReader(Main.class.getClass().getResourceAsStream("data
// .txt")));
        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\data.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] arr = line.split("\\|");
            if (arr.length == 0 || arr.length < 8 || arr[6].isEmpty() ||
                    arr[7].isEmpty()) continue;
            BusData data = new BusData();
            data.LineCode = arr[1];
            data.Direction = Direction.values()[Integer.parseInt(arr[2])];
            data.StationNum = Integer.parseInt(arr[3]);
            data.StationCode = arr[4];
            data.VehCode = arr[5];
            data.EstimatedTime = simpleDateFormat.parse(arr[6]);
            data.ProcessTime = simpleDateFormat.parse(arr[7]);
//            data.ProcessTime.setSeconds(0);
//            Calendar cal=Calendar.getInstance(Locale.CHINA);
//            cal.setTime(data.ProcessTime);
//            cal.set(Calendar.SECOND,0);
//            data.ProcessTime=cal.getTime();
            res.add(data);
        }
        br.close();
        return res;
    }
}

class TableModel extends AbstractTableModel {

    private static final long serialVersionUID = -7495940408592595397L;

    private Vector content = null;

    private String[] title_name = {"TimeDiffMin", "Count"};

    TableModel() {
        content = new Vector();
    }

    TableModel(int count) {
        content = new Vector(count);
    }

    void addRow(int diffMin, int count) {
        Vector v = new Vector(4);
        v.add(0, diffMin);
        v.add(1, count);
        content.add(v);
    }

    public void removeRow(int row) {
        content.remove(row);
    }

    void removeRows(int row, int count) {
        for (int i = 0; i < count; i++) {
            if (content.size() > row) {
                content.remove(row);
            }
        }
    }

    /**
     * 让表格中某些值可修改，但需要setValueAt(Object value, int row, int col)方法配合才能使修改生效
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    /**
     * 使修改的内容生效
     */
    public void setValueAt(Object value, int row, int col) {
        ((Vector) content.get(row)).remove(col);
        ((Vector) content.get(row)).add(col, value);
        this.fireTableCellUpdated(row, col);
    }

    public String getColumnName(int col) {
        return title_name[col];
    }

    public int getColumnCount() {
        return title_name.length;
    }

    public int getRowCount() {
        return content.size();
    }

    public Object getValueAt(int row, int col) {
        return ((Vector) content.get(row)).get(col);
    }

    /**
     * 返回数据类型
     */
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
}


