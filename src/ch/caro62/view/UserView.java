package ch.caro62.view;

import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.parser.UserParser;
import ch.caro62.service.ImageLoader;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import static java.lang.System.out;

public class UserView extends JPanel {

    private JButton buttonPrev;
    private JLabel userName;
    private JLabel userAvatar;
    private JPanel menuPanel;

    public UserView(User user) {
        super(new BorderLayout());
        init(user);
    }

    private void init(User user) {
        JToolBar toolbar = new JToolBar();

        buttonPrev = new JButton("<");
        toolbar.add(buttonPrev);

        JLabel userLabel = new JLabel(user.getName());
        toolbar.addSeparator(new Dimension(12, 12));
        toolbar.add(userLabel);

        JPanel userDetails = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        userName = new JLabel(user.getName());
        userDetails.add(userName, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        userAvatar = new JLabel();
        userDetails.add(userAvatar, c);
        getBufferedImage(user).subscribe(pic -> userAvatar.setIcon(new ImageIcon(pic)));

        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        userDetails.add(new JLabel(" "), c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBorder(BorderFactory.createEtchedBorder());
        menuPanel.add(new JLabel(user.getBoardCount() + " boards"));
        userDetails.add(menuPanel, c);


        add(toolbar, BorderLayout.NORTH);
        add(userDetails, BorderLayout.CENTER);
        reload(user)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(this::updateUI);
    }

    private Flowable<User> reload(User user) {
        return Flowable.just(user)
                .map(u -> String.format("https://sex.com/user/%s/", u.getRef()))
                .flatMap(ImageLoader::getString)
                .map(Jsoup::parse)
                .flatMap(UserParser::parse)
                .doOnNext(this::saveUser)
                .doOnError((e) -> out.println(e.getLocalizedMessage()));

    }

    private void updateUI(User user) {
        SwingUtilities.invokeLater(() -> {
            userName.setText(user.getName());
            menuPanel.removeAll();
            menuPanel.add(new JLabel(user.getBoardCount() + " boards"));
            menuPanel.add(new JLabel(user.getFollowerCount() + " following"));
            menuPanel.add(new JLabel(user.getPinCount() + " pins"));
            menuPanel.add(new JLabel(user.getRepinCount() + " repins"));
            menuPanel.add(new JLabel(user.getLikeCount() + " likes"));
            menuPanel.invalidate();
            menuPanel.revalidate();
            menuPanel.repaint();
            getBufferedImage(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(pic -> {
                        userAvatar.setIcon(new ImageIcon(pic));
                        userAvatar.setBorder(BorderFactory.createTitledBorder(user.getName()));
                    });
        });
    }

    private void saveUser(User u) throws SQLException {
        Dao<User, String> userDao = ModelSource.getUserDAO();
        userDao.createOrUpdate(u);
    }

    private Maybe<BufferedImage> getBufferedImage(User user) {
        String imgRef;
        if (user.getAvatar() == null || user.getAvatar().length() < 1)
            imgRef = "https://www.sex.com/images/default_profile_picture.png";
        else imgRef = user.getAvatar();
        return ImageLoader
                .getBytes(imgRef)
                .map(ImageIO::read)
                .map(Thumbnails::of)
                .map(i -> i.size(160, 160))
                .map(Thumbnails.Builder::asBufferedImage).firstElement();
    }

    public void addBackListener(ActionListener listener) {
        this.buttonPrev.addActionListener(listener);
    }

}
