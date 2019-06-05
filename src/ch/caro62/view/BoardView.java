package ch.caro62.view;

import javax.swing.*;
import java.awt.*;

public class BoardView extends JPanel {

    private JToolBar toolbar;

    public BoardView() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        toolbar = new JToolBar();
        JButton buttonPrev = new JButton("<");
        toolbar.add(buttonPrev);

        add(toolbar, BorderLayout.NORTH);
    }


}
