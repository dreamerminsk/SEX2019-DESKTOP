package ch.caro62.model.dao;

import ch.caro62.model.User;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;

public interface UserDao extends Dao<User, String> {

    Flowable<User> getRandom();
}