package com.bs;

import net.servicestack.func.Function;
import net.servicestack.func.Group;
import net.servicestack.func.Predicate;
import net.servicestack.func.Tuple;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Vector;

import static net.servicestack.func.Func.*;

class DecoratedTable extends JTable {
    DecoratedTable(AbstractTableModel model) {
        super(model);
        this.setBackground(Color.white);
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        tcr.setBackground(new Color(202, 234, 206));
        this.setFont(new Font("Tahoma", Font.PLAIN, 15));
        this.setDefaultRenderer(Object.class, tcr);
        this.getTableHeader().setReorderingAllowed(false);
    }
}

class TableModel extends AbstractTableModel {
    private Vector content;
    private Vector titleName;

    public Vector getContent() {
        return content;
    }

    public void setContent(Vector content) {
        this.content = content;
    }

    public List getTitleName() {
        return titleName;
    }

    public void setTitleName(Vector titleName) {
        this.titleName = titleName;
    }

    public String getColumnName(int col) {
        return (String) titleName.get(col);
    }

    public int getColumnCount() {
        return titleName != null ? titleName.size() : 0;
    }

    public int getRowCount() {
        return content != null ? content.size() : 0;
    }

    public Object getValueAt(int row, int col) {
        return ((Vector) content.get(row)).get(col);
    }
}

class TableModelStats extends TableModel {
    TableModelStats(List list) {
        this.setTitleName(new Vector(toList("TimeDiffMin", "Count")));
        this.setContent(new Vector(list.size()));
        List statsList = orderBy(map(groupBy(list, BusDataStats::getTimeDiffMin), (Function<Group<Long,
                        BusDataStats>, Tuple<Long, Group<Long, BusDataStats>>>) g -> new Tuple<>(g.key, g)),
                (Function<Tuple<Long, Group<Long, BusDataStats>>, Comparable>) t -> t.A);
        int total = 0;
        int rangeCount = 0;
        for (Object o : statsList) {
            Tuple tuple = (Tuple<Long, Group<Long, BusDataStats>>) o;
            long diffMin = (long) tuple.A;
            int count = ((Group<Long, BusDataStats>) tuple.B).items.size();
            addRow(diffMin, count);
            if (diffMin >= -1 && diffMin <= 1)
                rangeCount += count;
            total += count;
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        System.out.println(String.format("-1 ~ 1 所占百分比：%s", nf.format((double) rangeCount / total)));
    }

    private void addRow(long diffMin, int count) {
        Vector v = new Vector(2);
        v.add(0, diffMin);
        v.add(1, count);
        this.getContent().add(v);
    }
}

class TableModelMatrix extends TableModel {
    TableModelMatrix(List list) {
        int minDiff = (int) Math.floor(min(list, (Function<BusDataStats, Integer>) t -> new Long(t.getTimeDiffMin())
                .intValue()));
        minDiff = minDiff % 2 == 0 ? minDiff : minDiff - 1;
        int maxDiff = (int) Math.ceil(max(list, (Function<BusDataStats, Integer>) t -> new Long(t.getTimeDiffMin())
                .intValue()));
        maxDiff = maxDiff % 2 == 0 ? maxDiff + 2 : maxDiff + 1;
        int maxTotal = (int) Math.ceil(max(list, (Function<BusDataStats, Integer>) t -> new Long(t.getActualTotalMin
                ()).intValue()));
        maxTotal = maxTotal % 2 == 0 ? maxTotal + 2 : maxTotal + 1;
        int colCount = maxTotal / 2;
        int rowCount = (maxDiff - minDiff) / 2;
        this.setTitleName(new Vector(toList(new String[]{""})));
        this.setContent(new Vector(rowCount));
        for (int i = 0; i < colCount; i++) {
            this.getTitleName().add(String.format("%d ~ %d", 2 * i, 2 * (i + 1)));
        }
        for (int j = 0; j < rowCount; j++) {
            Vector v = new Vector(colCount + 1);
            v.add(0, String.format("%d ~ %d", 2 * j + minDiff, 2 * (j + 1) + minDiff));
            for (int i = 0; i < colCount; i++) {
                int finalI = i;
                int finalJ = j;
                int finalMinDiff = minDiff;
                v.add(i + 1, count(list, (Predicate<BusDataStats>) t -> t.getActualTotalMin() >= 2 * finalI && t
                        .getActualTotalMin() < 2 * (finalI + 1) && t.getTimeDiffMin() >= finalMinDiff + 2 * finalJ &&
                        t.getTimeDiffMin() < finalMinDiff + 2 * (finalJ + 1)));
            }
            this.getContent().add(v);
        }

    }

}