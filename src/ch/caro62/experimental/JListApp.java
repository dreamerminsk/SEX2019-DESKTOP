package ch.caro62.experimental;

import javax.swing.*;
import java.awt.*;

public class JListApp extends JFrame {

    public JListApp() {
        super("JListApp");
        init();
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String... args) {
        JListApp app = new JListApp();
    }

    private void init() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
        for (int i = 0; i < 8; i++) {
            JPanel inner = new JPanel();
            SpringLayout layout = new SpringLayout();
            inner.setLayout(layout);
            JLabel title = new JLabel("title " + i);
            title.setBorder(BorderFactory.createLineBorder(Color.RED));
            JLabel user = new JLabel("user " + i);
            user.setBorder(BorderFactory.createLineBorder(Color.RED));
            JButton del = new JButton("delete");
            del.setBorder(BorderFactory.createLineBorder(Color.RED));
            inner.add(title);
            inner.add(user);
            inner.add(del);

            layout.putConstraint(SpringLayout.WEST, title,
                    5,
                    SpringLayout.WEST, inner);

            layout.putConstraint(SpringLayout.WEST, user,
                    20,
                    SpringLayout.WEST, inner);

            layout.putConstraint(SpringLayout.EAST, del,
                    15,
                    SpringLayout.EAST, inner);

            layout.putConstraint(SpringLayout.NORTH, title,
                    5,
                    SpringLayout.NORTH, inner);

            layout.putConstraint(SpringLayout.NORTH, user,
                    2,
                    SpringLayout.SOUTH, title);

            layout.putConstraint(SpringLayout.NORTH, del,
                    5,
                    SpringLayout.NORTH, inner);

            list.add(inner);
        }
        add(new JScrollPane(list));

    }

}
