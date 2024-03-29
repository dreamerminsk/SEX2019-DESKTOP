package ch.caro62.view;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.System.out;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

import ch.caro62.model.Board;
import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.parser.BoardListParser;
import ch.caro62.parser.UserParser;
import ch.caro62.service.ImageLoader;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;

public class UserView extends JPanel {

  private User currentUser;

  private JButton buttonPrev;
  private JLabel userName;
  private JLabel userAvatar;
  private JPanel menuPanel;
  private JLabel userLabel;
  private JPanel itemsPanel;

  public UserView(User user) {
    super(new BorderLayout());
    this.currentUser = user;
    init(user);
  }

  private void init(User user) {
    JToolBar toolbar = new JToolBar();

    buttonPrev = new JButton("<");
    toolbar.add(buttonPrev);

    userLabel = new JLabel(user.getName());
    toolbar.addSeparator(new Dimension(12, 12));
    toolbar.add(userLabel);

    JPanel userDetails = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.PAGE_START;
    c.weightx = 0.0;
    c.weighty = 1.0;
    c.insets = new Insets(5, 5, 5, 5);
    c.fill = GridBagConstraints.NONE;
    c.gridheight = 2;
    userAvatar = new JLabel();
    userAvatar.setBorder(BorderFactory.createTitledBorder(user.getName()));
    userDetails.add(userAvatar, c);
    getBufferedImage(user).subscribe(pic -> userAvatar.setIcon(new ImageIcon(pic)));

    // c.gridx = 0;
    // c.gridy = 1;
    // c.anchor = GridBagConstraints.LINE_START;
    // c.weightx = 0.0;
    // c.weighty = 1.0;
    // c.fill = GridBagConstraints.HORIZONTAL;
    // userDetails.add(new JLabel(" "), c);

    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    c.weightx = 1.0;
    c.weighty = 0.0;
    c.insets = new Insets(12, 5, 5, 5);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridheight = 1;
    menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    menuPanel.setBorder(BorderFactory.createEtchedBorder());
    menuPanel.add(getCatLabel(user.getBoardCount(), " boards"));
    menuPanel.add(getCatLabel(user.getFollowerCount(), " following"));
    menuPanel.add(getCatLabel(user.getPinCount(), " pins"));
    menuPanel.add(getCatLabel(user.getRepinCount(), " repins"));
    menuPanel.add(getCatLabel(user.getLikeCount(), " likes"));
    userDetails.add(menuPanel, c);

    c.gridx = 1;
    c.gridy = 1;
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    c.weightx = 1.0;
    c.weighty = 1.0;
    c.insets = new Insets(5, 5, 5, 5);
    c.fill = GridBagConstraints.BOTH;
    itemsPanel = new JPanel(new ModifiedFlowLayout(FlowLayout.LEFT));
    //        loadBoards(1)
    //                .subscribe(board -> {
    //                    SwingUtilities.invokeLater(() -> {
    //                        JPanel itemPanel = new JPanel(new BorderLayout());
    //                        itemPanel.add(new JButton(board.getTitle()), BorderLayout.CENTER);
    //
    // itemPanel.setBorder(BorderFactory.createTitledBorder(board.getTitle()));
    //                        itemsPanel.add(itemPanel);
    //                    });
    //                });
    JScrollPane scroll =
        new JScrollPane(itemsPanel, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
    userDetails.add(scroll, c);

    add(toolbar, BorderLayout.NORTH);
    add(userDetails, BorderLayout.CENTER);
    reload(user)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .subscribe(this::updateUI);
  }

  private JLabel getCatLabel(int boardCount, String s) {
    JLabel l = new JLabel(boardCount + s);

    Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
    fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    l.setFont(l.getFont().deriveFont(fontAttributes));

    l.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return l;
  }

  private Flowable<User> reload(User user) {
    return Flowable.just(user)
        .map(User::getAbsRef)
        .flatMap(ImageLoader::getString)
        .map(Jsoup::parse)
        .flatMap(UserParser::parse)
        .doOnNext(this::saveUser)
        .doOnError((e) -> out.println(e.getLocalizedMessage()));
  }

  private void updateUI(User user) {
    SwingUtilities.invokeLater(
        () -> {
          userLabel.setText(user.getName());
          menuPanel.removeAll();
          menuPanel.add(getCatLabel(user.getBoardCount(), " boards"));
          menuPanel.add(getCatLabel(user.getFollowerCount(), " following"));
          menuPanel.add(getCatLabel(user.getPinCount(), " pins"));
          menuPanel.add(getCatLabel(user.getRepinCount(), " repins"));
          menuPanel.add(getCatLabel(user.getLikeCount(), " likes"));
          menuPanel.invalidate();
          menuPanel.revalidate();
          menuPanel.repaint();

          itemsPanel.removeAll();
          loadBoards(1)
              .subscribe(
                  board ->
                      SwingUtilities.invokeLater(
                          () -> {
                            JPanel itemPanel = new JPanel(new BorderLayout());
                            itemPanel.setMinimumSize(new Dimension(100, 30));
                            JLabel imgLabel = new JLabel(new ImageIcon(createPic()));
                            itemPanel.add(imgLabel, BorderLayout.CENTER);
                            itemPanel.setBorder(BorderFactory.createTitledBorder(board.getTitle()));
                            itemsPanel.add(itemPanel);
                            ImageLoader.getBytes(board.getPins().get(0))
                                .map(ImageIO::read)
                                .map(Thumbnails::of)
                                .map(i -> i.size(100, 100))
                                .map(Thumbnails.Builder::asBufferedImage)
                                .onErrorReturnItem(createPic())
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.single())
                                .subscribe(
                                    (image) ->
                                        SwingUtilities.invokeLater(
                                            () -> imgLabel.setIcon(new ImageIcon(image))));
                          }),
                  throwable -> System.out.println(throwable.getMessage()),
                  () -> {
                    itemsPanel.revalidate();
                    itemsPanel.repaint();
                  });

          getBufferedImage(user)
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.single())
              .subscribe(
                  pic -> {
                    SwingUtilities.invokeLater(
                        () -> {
                          userAvatar.setIcon(new ImageIcon(pic));
                          userAvatar.setBorder(BorderFactory.createTitledBorder(user.getName()));
                        });
                  });
        });
  }

  private BufferedImage createPic() {
    BufferedImage img = new BufferedImage(100, 100, TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setBackground(Color.black);
    return img;
  }

  private Flowable<Board> loadBoards(int page) {
    return Flowable.just(currentUser.getAbsRef())
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .flatMap(ImageLoader::getString)
        .doOnNext((str) -> System.out.println(str.length()))
        .map(Jsoup::parse)
        .flatMap(BoardListParser::parse)
        .doOnNext(blp -> System.out.println(blp.getBoards().size()))
        .flatMap(blp -> Flowable.fromIterable(blp.getBoards()))
        .doOnError(System.out::println);
  }

  private void saveUser(User u) throws SQLException {
    Dao<User, String> userDao = ModelSource.getUserDAO();
    System.out.println(u.getRef());
    Dao.CreateOrUpdateStatus r = userDao.createOrUpdate(u);
    out.println(r.isCreated() + ", " + r.isUpdated() + ", " + r.getNumLinesChanged());
  }

  private Maybe<BufferedImage> getBufferedImage(User user) {
    String imgRef;
    if (user.getAvatar() == null || user.getAvatar().length() < 1)
      imgRef = "https://www.sex.com/images/default_profile_picture.png";
    else imgRef = user.getAvatar();
    return ImageLoader.getBytes(imgRef)
        .map(ImageIO::read)
        .map(Thumbnails::of)
        .map(i -> i.size(200, 200))
        .map(Thumbnails.Builder::asBufferedImage)
        .firstElement();
  }

  public void addBackListener(ActionListener listener) {
    this.buttonPrev.addActionListener(listener);
  }
}
