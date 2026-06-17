package me.mitgaa23.util_lib.database.types;

import tableGenerator.StatementPreparer;
import tableGenerator.proxy.SQLTypeHandler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PostgresString extends SQLTypeHandler {
	@Override
	default String getName() {
		return "VARCHAR";
	}

	@Override
	default void prepareStatement(StatementPreparer preparer, Object obj) throws SQLException {
		if (obj instanceof String string) {
			preparer.setString(string);
		}
	}

	@Override
	default void copyToField(String columnName, ResultSet set, Field field, Object obj) throws IllegalAccessException, SQLException {
		String value = set.getString(columnName);

		if (field.getType() == String.class) {
			field.set(obj, value);
		}
	}
}
