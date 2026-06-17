package me.mitgaa23.util_lib.database.types;

import me.mitgaa23.util_lib.database.StatementPreparer;
import me.mitgaa23.util_lib.database.proxy.SQLTypeHandler;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public interface PostgresDate extends SQLTypeHandler {
	@Override
	default String getName() {
		return "DATE";
	}

	@Override
	default void prepareStatement(StatementPreparer preparer, Object obj) throws SQLException {
		if (obj instanceof Date date) {
			preparer.setDate(date);
		}

		if (obj instanceof LocalDate date) {
			preparer.setDate(Date.valueOf(date));
		}
	}

	@Override
	default void copyToField(String columnName, ResultSet set, Field field, Object obj) throws IllegalAccessException, SQLException {
		Date date = set.getDate(columnName);

		if (field.getType() == Date.class) {
			field.set(obj, date);
		}

		if (field.getType() == LocalDate.class) {
			field.set(obj, date.toLocalDate());
		}
	}
}
