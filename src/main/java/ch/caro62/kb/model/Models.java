package ch.caro62.kb.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

public class Models {

  private static JdbcPooledConnectionSource getConnectionSource() throws SQLException {
    return new JdbcPooledConnectionSource("jdbc:sqlite:kinobusiness");
  }

  public static void createTables() throws SQLException {
    TableUtils.createTableIfNotExists(getConnectionSource(), Movie.class);
    TableUtils.createTableIfNotExists(getConnectionSource(), Years.class);
  }

  private static Dao<Movie, Integer> getMovieDAO() throws SQLException {
    return DaoManager.createDao(getConnectionSource(), Movie.class);
  }

  private static Dao<Years, Integer> getYearsDAO() throws SQLException {
    return DaoManager.createDao(getConnectionSource(), Years.class);
  }

  public static void saveYears(Years y) throws SQLException {
    Dao<Movie, Integer> m = getMovieDAO();
    m.createOrUpdate(y.getMovie());
    Dao<Years, Integer> yd = getYearsDAO();
    yd.createOrUpdate(y);
  }
}
