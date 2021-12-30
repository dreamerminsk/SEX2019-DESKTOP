package ch.caro62.experimental;

import ch.caro62.model.Board;
import ch.caro62.model.ModelSource;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.util.List;

import static java.awt.GridBagConstraints.HORIZONTAL;

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
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
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
            Disposable d = Flowable.fromCallable(this::load)
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
        Disposable d = Flowable.fromCallable(this::load)
                .subscribeOn(Schedulers.io())
                //.observeOn(Schedulers.single())
                .subscribe();
    }

    private int load() throws SQLException {
        list.removeAll();
        Dao<Board, String> boardDao = ModelSource.getBoardDAO();
        List<Board> boards = boardDao.queryBuilder().orderByRaw("RANDOM()").limit((long) 32).query();
        for (int i = 0; i < 32; i++) {
            JPanel inner = new JPanel();
            inner.setLayout(new GridBagLayout());
            inner.setBorder(BorderFactory.createTitledBorder(boards.get(i).getTitle()));
            GridBagConstraints c = new GridBagConstraints();

            JLabel title = new JLabel(boards.get(i).getTitle());
            title.setFont(title.getFont().deriveFont(15.0f));
            //title.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1.0;
            c.weighty = 0.5;
            c.fill = HORIZONTAL;
            c.anchor = GridBagConstraints.PAGE_START;
            inner.add(title, c);

            String userName = boards.get(i).getRef().split("/")[2];
            JLabel user = new JLabel(userName);
            //user.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
            user.setFont(user.getFont().deriveFont(11.0f));
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 1.0;
            c.weighty = 0.5;
            c.fill = HORIZONTAL;
            c.anchor = GridBagConstraints.LINE_START;
            inner.add(user, c);

            JButton del = new JButton("delete");
            del.setFont(del.getFont().deriveFont(12.0f));
            c.gridx = 1;
            c.gridy = 0;
            c.weightx = 0.0;
            c.weighty = 1.0;
            c.anchor = GridBagConstraints.LINE_END;
            inner.add(del, c);

            //inner.setMinimumSize(new Dimension(100, 50));
            //inner.setPreferredSize(new Dimension(100, title.getPreferredSize().height * 3));
            inner.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {

                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    System.out.println(userName);
                    Component comp = e.getComponent();
                    SubstanceColorScheme s = SubstanceLookAndFeel
                            .getCurrentSkin(comp)
                            .getEnabledColorScheme(
                                    SubstanceLookAndFeel.getDecorationType(comp)
                            );
                    //SubstanceColorUtilities.
                    TitledBorder b = BorderFactory.createTitledBorder("");
                    //b.
                }
            });
            SwingUtilities.invokeLater(() -> list.add(inner));
            SwingUtilities.invokeLater(JListApp.this::revalidate);
        }
        return 0;
    }

}
