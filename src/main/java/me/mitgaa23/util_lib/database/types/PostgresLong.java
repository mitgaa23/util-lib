package me.mitgaa23.util_lib.database.types;

import me.mitgaa23.util_lib.database.StatementPreparer;
import me.mitgaa23.util_lib.database.proxy.SQLTypeHandler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PostgresLong extends SQLTypeHandler {
	@Override
	default String getName() {
		return "LONG";
	}

	@Override
	default void prepareStatement(StatementPreparer preparer, Object obj) throws SQLException {
		if (obj instanceof Long value) {
			preparer.setLong(value);
		}
	}

	@Override
	default void copyToField(String columnName, ResultSet resultSet, Field field, Object obj) throws IllegalAccessException, SQLException {
		long value = resultSet.getLong(columnName);

		if (field.getType() == long.class || field.getType() == Long.class) {
			field.set(obj, value);
		}
	}
}
