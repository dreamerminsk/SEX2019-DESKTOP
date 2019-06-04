package ch.caro62.ui;

import javax.swing.table.DefaultTableModel;

public class BoardModel extends DefaultTableModel {

    public BoardModel() {
        this.addColumn("title");
        this.addColumn("user");
        this.addColumn("pins");
        this.addColumn("followers");
        //this.addRow(new Object[]{"new", newBoards, newPins, newFollowers});
        //this.addRow(new Object[]{"total", totalBoards, totalPins, totalFollowers});
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        }
        return Integer.class;
    }

}
