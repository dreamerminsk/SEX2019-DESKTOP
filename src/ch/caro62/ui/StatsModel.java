package ch.caro62.ui;

import javax.swing.table.DefaultTableModel;

public class StatsModel extends DefaultTableModel {

    public StatsModel() {
        this.addColumn("name");
        this.addColumn("boards");
        this.addColumn("pins");
        this.addColumn("followers");
    }

}
