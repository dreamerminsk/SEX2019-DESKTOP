package ch.caro62.view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class BoardView extends JPanel {

  private JToolBar toolbar;
  private JButton buttonPrev;

  public BoardView() {
    super(new BorderLayout());
    init();
  }

  private void init() {
    toolbar = new JToolBar();
    buttonPrev = new JButton("<");
    toolbar.add(buttonPrev);

    add(toolbar, BorderLayout.NORTH);
  }

  public void addBackListener(ActionListener listener) {
    this.buttonPrev.addActionListener(listener);
  }
}
