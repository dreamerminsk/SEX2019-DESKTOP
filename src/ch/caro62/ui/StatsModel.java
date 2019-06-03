package ch.caro62.ui;

import javax.swing.table.DefaultTableModel;

public class StatsModel extends DefaultTableModel {

    private int newBoards = 0;
    private int newPins = 0;
    private int newFollowers = 0;

    public StatsModel() {
        this.addColumn("name");
        this.addColumn("boards");
        this.addColumn("pins");
        this.addColumn("followers");
        this.addRow(new Object[]{"new", newBoards, newPins, newFollowers});
    }

    public void addNewBoard(int pins, int followers) {
        newBoards++;
        newPins += pins;
        newFollowers += followers;
        this.setValueAt(newBoards, 0, 1);
        this.setValueAt(newPins, 0, 2);
        this.setValueAt(newFollowers, 0, 3);
    }

}
