package ch.caro62.ui;

import ch.caro62.model.Board;

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

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 1) {
            return String.class;
        }
        return Integer.class;
    }

    public void addBoard(Board board) {
        this.addRow(new Object[]{board.getTitle(), board.getUser(),
                board.getPinCount(), board.getFollowerCount()});
    }

}
