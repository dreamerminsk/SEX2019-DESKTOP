package ch.caro62.view;

import ch.caro62.ui.BoardModel;
import java.awt.*;
import javax.swing.*;

public class UpdatedBoardsView extends JPanel {

  private JTable table;

  public UpdatedBoardsView(BoardModel model) {
    super(new BorderLayout());
    init(model);
  }

  private void init(BoardModel model) {
    table = new JTable(model);
    add(new JScrollPane(table), BorderLayout.CENTER);
  }
}
