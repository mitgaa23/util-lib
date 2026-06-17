package me.mitgaa23.util_lib.database.types;

import tableGenerator.StatementPreparer;
import tableGenerator.proxy.SQLTypeHandler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PostgresInteger extends SQLTypeHandler {
	@Override
	default String getName() {
		return "INT";
	}

	@Override
	default void prepareStatement(StatementPreparer preparer, Object obj) throws SQLException {
		if (obj instanceof Integer value) {
			preparer.setInt(value);
		}
	}

	@Override
	default void copyToField(String columnName, ResultSet set, Field field, Object obj) throws IllegalAccessException, SQLException {
		int value = set.getInt(columnName);

		if (field.getType() == int.class || field.getType() == Integer.class) {
			field.set(obj, value);
		}
	}
}
