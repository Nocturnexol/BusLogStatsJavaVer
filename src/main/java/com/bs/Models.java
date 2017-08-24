package com.bs;

import net.servicestack.func.Function;
import net.servicestack.func.Group;
import net.servicestack.func.Tuple;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
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
    private String[] titleName;

    public Vector getContent() {
        return content;
    }

    public void setContent(Vector content) {
        this.content = content;
    }

    public void setTitleName(String[] titleName) {
        this.titleName = titleName;
    }

    public String getColumnName(int col) {
        return titleName[col];
    }

    public int getColumnCount() {
        return titleName != null ? titleName.length : 0;
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
        this.setTitleName(new String[]{"TimeDiffMin", "Count"});
        this.setContent(new Vector(list.size()));
        List statsList = orderBy(map(groupBy(list, BusDataStats::getTimeDiffMin), (Function<Group<Long,
                        BusDataStats>, Tuple<Long, Group<Long, BusDataStats>>>) g -> new Tuple<>(g.key, g)),
                (Function<Tuple<Long, Group<Long, BusDataStats>>, Comparable>) t -> t.A);
        for (Object o : statsList) {
            Tuple tuple = (Tuple<Long, Group<Long, BusDataStats>>) o;
            addRow((long) tuple.A, ((Group<Long, BusDataStats>) tuple.B).items.size());
        }
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

    }

    private void addRow(long diffMin, int count) {
        Vector v = new Vector(2);
        v.add(0, diffMin);
        v.add(1, count);
        this.getContent().add(v);
    }

}