package ch.caro62.model.dao.impl;

import ch.caro62.model.User;
import ch.caro62.model.dao.UserDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class UserDaoImpl extends BaseDaoImpl<User, String> implements UserDao {

    public UserDaoImpl(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, User.class);
    }

}
