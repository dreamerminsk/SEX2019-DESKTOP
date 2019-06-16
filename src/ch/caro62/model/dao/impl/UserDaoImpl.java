package ch.caro62.model.dao.impl;

import ch.caro62.model.User;
import ch.caro62.model.dao.UserDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import io.reactivex.Flowable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserDaoImpl extends BaseDaoImpl<User, String> implements UserDao {

    public UserDaoImpl(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, User.class);
    }

    @Override
    public Flowable<User> getRandom() {
        return Flowable.fromCallable(() -> queryBuilder().orderByRaw("RANDOM()").queryForFirst());
    }

    @Override
    public Flowable<User> getRandom(int count) {
        return Flowable.fromIterable(() -> {
            return getIterator(count);
        });
    }

    private Iterator<User> getIterator(long count) {
        List<User> users = new ArrayList<>();
        try {
            users.addAll(queryBuilder()
                    .orderByRaw("RANDOM()")
                    .limit(count).query());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users.iterator();
    }
}
