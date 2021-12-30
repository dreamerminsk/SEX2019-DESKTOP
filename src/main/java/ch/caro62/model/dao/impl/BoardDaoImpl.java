package ch.caro62.model.dao.impl;

import ch.caro62.model.Board;
import ch.caro62.model.dao.BoardDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class BoardDaoImpl extends BaseDaoImpl<Board, String> implements BoardDao {

    public BoardDaoImpl(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, Board.class);
    }

}
