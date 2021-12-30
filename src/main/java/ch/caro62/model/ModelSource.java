package ch.caro62.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

public class ModelSource {

  private static JdbcPooledConnectionSource getConnectionSource() throws SQLException {
    return new JdbcPooledConnectionSource("jdbc:sqlite:sex.com.stats");
  }

  public static void createTables() throws SQLException {
    TableUtils.createTableIfNotExists(getConnectionSource(), Board.class);
  }

  public static Dao<Board, String> getBoardDAO() throws SQLException {
    return DaoManager.createDao(getConnectionSource(), Board.class);
  }

  public static Dao<User, String> getUserDAO() throws SQLException {
    return DaoManager.createDao(getConnectionSource(), User.class);
  }
}
