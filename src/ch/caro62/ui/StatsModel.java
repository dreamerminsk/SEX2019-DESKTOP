package ch.caro62.ui;

import javax.swing.table.DefaultTableModel;

public class StatsModel extends DefaultTableModel {

    private int newBoards = 0;
    private int newPins = 0;
    private int newFollowers = 0;

    private int totalBoards = 0;
    private int totalPins = 0;
    private int totalFollowers = 0;

    public StatsModel() {
        this.addColumn("name");
        this.addColumn("boards");
        this.addColumn("pins");
        this.addColumn("followers");
        this.addRow(new Object[]{"new", newBoards, newPins, newFollowers});
        this.addRow(new Object[]{"total", totalBoards, totalPins, totalFollowers});
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

    public void addNewBoard(int pins, int followers) {
        newBoards++;
        newPins += pins;
        newFollowers += followers;
        this.setValueAt(newBoards, 0, 1);
        this.setValueAt(newPins, 0, 2);
        this.setValueAt(newFollowers, 0, 3);

        totalBoards++;
        totalPins += pins;
        totalFollowers += followers;
        this.setValueAt(totalBoards, 1, 1);
        this.setValueAt(totalPins, 1, 2);
        this.setValueAt(totalFollowers, 1, 3);
    }

    public void addBoard(int pins, int followers) {
        totalBoards++;
        totalPins += pins;
        totalFollowers += followers;
        this.setValueAt(totalBoards, 1, 1);
        this.setValueAt(totalPins, 1, 2);
        this.setValueAt(totalFollowers, 1, 3);
    }

}
