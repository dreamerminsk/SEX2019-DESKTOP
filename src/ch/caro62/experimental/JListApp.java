package ch.caro62.experimental;

import ch.caro62.model.Board;
import ch.caro62.model.ModelSource;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class JListApp extends JFrame {

    private JPanel list;

    public JListApp() throws SQLException {
        super("JListApp");
        init();
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                JListApp app = new JListApp();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void init() {
        JToolBar toolbar = new JToolBar();
        JButton reload = new JButton("reload");
        reload.addActionListener((e) -> {
            Disposable d = Flowable.fromCallable(this::load2)
                    .subscribeOn(Schedulers.io())
                    //.observeOn(Schedulers.single())
                    .subscribe();
        });
        toolbar.add(reload);
        add(toolbar, BorderLayout.NORTH);

        list = new JPanel();
        list.setLayout(new GridLayout(0, 1));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.getVerticalScrollBar().setBlockIncrement(48);
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        add(scrollPane, BorderLayout.CENTER);
        Disposable d = Flowable.fromCallable(this::load2)
                .subscribeOn(Schedulers.io())
                //.observeOn(Schedulers.single())
                .subscribe();
    }

    private int load() throws SQLException {
        //list.removeAll();
        Dao<Board, String> boardDao = ModelSource.getBoardDAO();
        List<Board> boards = boardDao.queryBuilder().orderByRaw("RANDOM()").limit((long) 32).query();
        for (int i = 0; i < 32; i++) {
            JPanel inner = new JPanel();

            SpringLayout layout = new SpringLayout();
            inner.setLayout(layout);
            System.out.println(boards.get(i).getTitle());
            JLabel title = new JLabel(boards.get(i).getTitle());
            JLabel user = new JLabel(boards.get(i).getRef());
            user.setFont(user.getFont().deriveFont(11.0f));
            JButton del = new JButton("delete");
            del.setFont(del.getFont().deriveFont(12.0f));
            inner.add(title);
            inner.add(user);
            inner.add(del);

            inner.setMinimumSize(new Dimension(100, 50));
            inner.setPreferredSize(new Dimension(100, title.getPreferredSize().height * 3));

            layout.putConstraint(SpringLayout.WEST, title,
                    5,
                    SpringLayout.WEST, inner);

            layout.putConstraint(SpringLayout.WEST, user,
                    20,
                    SpringLayout.WEST, inner);

            layout.putConstraint(SpringLayout.EAST, del,
                    -5,
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

            SwingUtilities.invokeLater(() -> list.add(inner));
            SwingUtilities.invokeLater(JListApp.this::revalidate);
        }
        return 0;
    }

    private int load2() throws SQLException {
        list.removeAll();
        Dao<Board, String> boardDao = ModelSource.getBoardDAO();
        List<Board> boards = boardDao.queryBuilder().orderByRaw("RANDOM()").limit((long) 32).query();
        for (int i = 0; i < 32; i++) {
            JPanel inner = new JPanel();
            inner.setAlignmentX(0.0f);
            inner.setBorder(BorderFactory.createTitledBorder(""));

            JLabel title = new JLabel(boards.get(i).getTitle());
            title.setFont(title.getFont().deriveFont(14.0f));
            String userName = boards.get(i).getRef().split("/")[2];
            JLabel user = new JLabel(userName);
            user.setFont(user.getFont().deriveFont(11.0f));
            JButton del = new JButton("delete");
            del.setFont(del.getFont().deriveFont(12.0f));
            inner.add(title);
            inner.add(user);
            inner.add(del);

            GroupLayout layout = new GroupLayout(inner);
            inner.setLayout(layout);
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                            .addComponent(title)
                            //.addGap(GroupLayout.DEFAULT_SIZE,
                            //      GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(user))
                    .addComponent(del));
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                            .addComponent(title)
                            .addComponent(del))
                    //.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                    //      GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(user));

            inner.setMinimumSize(new Dimension(100, 50));
            inner.setPreferredSize(new Dimension(100, title.getPreferredSize().height * 3));

            SwingUtilities.invokeLater(() -> list.add(inner));
            SwingUtilities.invokeLater(JListApp.this::revalidate);
        }
        return 0;
    }

}
