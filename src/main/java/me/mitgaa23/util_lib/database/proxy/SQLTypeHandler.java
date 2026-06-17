package me.mitgaa23.util_lib.database.proxy;


import me.mitgaa23.util_lib.database.StatementPreparer;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLTypeHandler {
	default String getString(Object... params) {
		StringBuilder sb = new StringBuilder(getName());

		if (params.length > 0) {
			sb.append('(');

			for (int i = 0; i < params.length; i++) {
				Object param = params[i];
				sb.append(param);

				if (i < params.length - 1) {
					sb.append(", ");
				}
			}

			sb.append(')');
		}

		return sb.toString();
	}

	String getName();

	default boolean isPulledFromDB() {
		return false;
	}

	void prepareStatement(StatementPreparer preparer, Object obj) throws SQLException;

	void copyToField(String columnName, ResultSet set, Field field, Object obj) throws IllegalAccessException, SQLException;
}
