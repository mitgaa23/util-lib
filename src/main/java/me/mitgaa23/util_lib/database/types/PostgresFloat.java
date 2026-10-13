package me.mitgaa23.util_lib.database.types;

import me.mitgaa23.util_lib.database.StatementPreparer;
import me.mitgaa23.util_lib.database.proxy.SQLTypeHandler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PostgresFloat extends SQLTypeHandler {
	@Override
	default String getName() {
		return "REAL";
	}

	@Override
	default void prepareStatement(StatementPreparer preparer, Object obj) throws SQLException {
		if (obj instanceof Float value) {
			preparer.setFloat(value);
		}
	}

	@Override
	default void copyToField(String columnName, ResultSet resultSet, Field field, Object obj) throws IllegalAccessException, SQLException {
		float value = resultSet.getFloat(columnName);

		if (field.getType() == float.class || field.getType() == Float.class) {
			field.set(obj, value);
		}
	}
}
