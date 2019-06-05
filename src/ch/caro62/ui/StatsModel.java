package ch.caro62.ui;

import javax.swing.table.DefaultTableModel;

public class StatsModel extends DefaultTableModel {

    private int newBoards = 0;
    private int newPins = 0;
    private int newFollowers = 0;

    private int existingBoards = 0;
    private int existingPins = 0;
    private int existingFollowers = 0;

    public StatsModel() {
        this.addColumn("name");
        this.addColumn("boards");
        this.addColumn("pins");
        this.addColumn("followers");
        this.addRow(new Object[]{
                "new",
                newBoards,
                newPins,
                newFollowers});
        this.addRow(new Object[]{
                "existing",
                existingBoards,
                existingPins,
                existingFollowers});
        this.addRow(new Object[]{
                "total",
                newBoards + existingBoards,
                newPins + existingPins,
                newFollowers + existingFollowers});
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

        this.setValueAt(newBoards + existingBoards, 2, 1);
        this.setValueAt(newPins + existingPins, 2, 2);
        this.setValueAt(newFollowers + existingFollowers, 2, 3);
    }

    public void addExistingBoard(int pins, int followers) {
        existingBoards++;
        existingPins += pins;
        existingFollowers += followers;
        this.setValueAt(existingBoards, 1, 1);
        this.setValueAt(existingPins, 1, 2);
        this.setValueAt(existingFollowers, 1, 3);

        this.setValueAt(newBoards + existingBoards, 2, 1);
        this.setValueAt(newPins + existingPins, 2, 2);
        this.setValueAt(newFollowers + existingFollowers, 2, 3);
    }

}
