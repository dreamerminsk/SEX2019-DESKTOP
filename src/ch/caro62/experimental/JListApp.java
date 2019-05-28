package ch.caro62.experimental;

import ch.caro62.model.Board;
import ch.caro62.model.ModelSource;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    public static void main(String... args) throws SQLException {
        try {
            //UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
            }
        }
        JListApp app = new JListApp();
    }

    private void init() {
        JToolBar toolbar = new JToolBar();
        JButton reload = new JButton("reload");
        reload.addActionListener((e) -> {
            Disposable d = Flowable.fromCallable(this::load)
                    .subscribeOn(Schedulers.io())
                    //.observeOn(Schedulers.single())
                    .subscribe();
        });
        toolbar.add(reload);
        add(toolbar, BorderLayout.NORTH);

        list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
        add(scrollPane, BorderLayout.CENTER);
        Disposable d = Flowable.fromCallable(this::load)
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

}
