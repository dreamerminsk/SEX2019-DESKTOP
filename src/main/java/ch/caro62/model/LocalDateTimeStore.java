/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.caro62.model;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.field.types.DateTimeType;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author User
 */
public class LocalDateTimeStore extends BaseDataType {
    
    private static final LocalDateTimeStore singleTon = new LocalDateTimeStore();

    public LocalDateTimeStore(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public LocalDateTimeStore(SqlType sqlType) {
        super(sqlType);
    }
    
    private LocalDateTimeStore() {
		super(SqlType.STRING);
	}
    
    public static LocalDateTimeStore getSingleton() {
		return singleTon;
	}

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return defaultStr;
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        String time = results.getString(columnPos);
        return time;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        LocalDateTime dt = LocalDateTime.parse((String) sqlArg, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dt;
    }

}
