package ch.caro62.view;

import ch.caro62.model.User;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class UserView extends JPanel {

    private JButton buttonPrev;

    public UserView(User user) {
        super(new BorderLayout());
        init(user);
    }

    private void init(User user) {
        JToolBar toolbar = new JToolBar();

        buttonPrev = new JButton("<");
        toolbar.add(buttonPrev);

        JLabel userLabel = new JLabel(user.getRef());
        toolbar.addSeparator(new Dimension(12, 12));
        toolbar.add(userLabel);

        JPanel userDetails = new JPanel(new GridBagLayout());
        userDetails.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        userDetails.add(new JLabel(user.getRef()), c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        BufferedImage img = null;
        try {
            img = ImageIO.read(new URL(user.getAvatar()));
            img = Thumbnails.of(img).size(100, 100).asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userDetails.add(new JLabel(new ImageIcon(img)), c);

        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        userDetails.add(new JLabel(" "), c);


        add(toolbar, BorderLayout.NORTH);
        add(userDetails, BorderLayout.CENTER);
    }

    public void addBackListener(ActionListener listener) {
        this.buttonPrev.addActionListener(listener);
    }

}
