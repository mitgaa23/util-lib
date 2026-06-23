package me.mitgaa23.util_lib.database;

import me.mitgaa23.util_lib.database.annotations.Column;
import me.mitgaa23.util_lib.database.proxy.SQLTypeHandler;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PostgresTable<T> extends Table<T> {
	public PostgresTable(String name, Class<T> clazz, Connection connection) throws SQLException {
		super(name, clazz, connection);
	}

	@Override
	protected String getDropStatement() {
		return "DROP TABLE IF EXISTS %s CASCADE;".formatted(name);
	}

	@Override
	protected String getUpdateStatement() {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE ");
		sb.append(name);
		sb.append(" SET ");

		for (int i = 0; i < nonPulledColumns.size(); i++) {
			Column column = nonPulledColumns.get(i);

			sb.append(column.value());
			sb.append(" = ?");

			if (i < nonPulledColumns.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(" WHERE ");

		for (int i = 0; i < primaryKeyColumns.size(); i++) {
			Column column = primaryKeyColumns.get(i);
			sb.append(column.value());
			sb.append(" = ?");

			if (i < primaryKeyColumns.size() - 1) {
				sb.append(" AND");
			}
		}

		sb.append(" RETURNING *;");

		return sb.toString();
	}

	@Override
	protected String getCreateStatement() {
		StringBuilder sb = new StringBuilder();

		sb.append("CREATE TABLE IF NOT EXISTS ");

		sb.append(name);
		sb.append(" (");

		for (int i = 0; i < columns.size(); i++) {
			Column column = columns.get(i);
			sb.append(column.value());
			sb.append(" ");

			SQLTypeHandler handler = handlers.get(column.value());
			sb.append(handler.getName());

			int[] params = column.params();
			if (params.length != 0) {
				sb.append("(");

				for (int j = 0; j < params.length; j++) {
					int param = params[j];
					sb.append(param);

					if (j < params.length - 1) {
						sb.append(", ");
					}
				}

				sb.append(")");
			}

			if (i < columns.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(", PRIMARY KEY (");

		int primaryKeys = 0;
		for (Column column : columns) {
			if (column.primary()) {
				if (primaryKeys > 0) {
					sb.append(", ");
				}

				sb.append(column.value());
				primaryKeys++;
			}
		}

		sb.append("));");
		return sb.toString();
	}

	@Override
	protected String getDeleteStatement() {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append(name);
		sb.append(" WHERE ");
		appendWhereClause(sb);
		return sb.toString();
	}

	@Override
	protected String getInsertStatement() {
		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		sb.append(name);
		sb.append(" (");

		for (int i = 0; i < nonPulledColumns.size(); i++) {
			Column column = nonPulledColumns.get(i);
			sb.append(column.value());

			if (i < nonPulledColumns.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(") VALUES (");

		for (int i = 0; i < nonPulledColumns.size(); i++) {
			sb.append('?');

			if (i < nonPulledColumns.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(") RETURNING *;");

		return sb.toString();
	}

	@Override
	protected String getPullStatement() {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");

		for (int i = 0; i < columns.size(); i++) {
			Column column = columns.get(i);
			sb.append(column.value());

			if (i < columns.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(" FROM ");
		sb.append(name);
		sb.append(" WHERE ");

		appendWhereClause(sb);
		sb.append(";");

		return sb.toString();
	}

	protected void fillUpdateStatement(PreparedStatement updateStatement, T obj) throws SQLException, IllegalAccessException {
		StatementPreparer preparer = new StatementPreparer(updateStatement);

		prepareColumnNames(obj, nonPulledColumns, preparer);
		prepareColumnNames(obj, primaryKeyColumns, preparer);
	}

	@Override
	protected void fillDeleteStatement(PreparedStatement deleteStatement, T obj) throws SQLException, IllegalAccessException {
		StatementPreparer preparer = new StatementPreparer(deleteStatement);

		for (Column column : primaryKeyColumns) {
			SQLTypeHandler handler = handlers.get(column.value());
			Field field = fields.get(column.value());

			handler.prepareStatement(preparer, field.get(obj));
		}
	}

	@Override
	protected void fillInsertStatement(PreparedStatement insertStatement, T obj) throws SQLException, IllegalAccessException {
		StatementPreparer preparer = new StatementPreparer(insertStatement);

		for (Column column : nonPulledColumns) {
			Field field = fields.get(column.value());
			SQLTypeHandler handler = handlers.get(column.value());
			handler.prepareStatement(preparer, field.get(obj));
		}
	}

	@Override
	protected PreparedStatement fillInsertMultipleStatement(List<T> list, int batchSize, int current, int count) throws SQLException, IllegalAccessException {
		String statementString = getInsertMultipleStatement(batchSize);
		PreparedStatement statement = connection.prepareStatement(statementString);
		StatementPreparer preparer = new StatementPreparer(statement);

		for (int i = current; i < current + batchSize && i < count; i++) {
			T obj = list.get(i);

			for (Column column : nonPulledColumns) {
				Field field = fields.get(column.value());
				SQLTypeHandler handler = handlers.get(column.value());

				handler.prepareStatement(preparer, field.get(obj));
			}
		}

		return statement;
	}

	@Override
	protected String getInsertMultipleStatement(int count) {
		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		sb.append(name);
		sb.append(" (");

		for (int i = 0; i < nonPulledColumns.size(); i++) {
			Column column = nonPulledColumns.get(i);
			sb.append(column.value());

			if (i < nonPulledColumns.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(") VALUES ");

		for (int i = 0; i < count; i++) {
			sb.append('(');

			for (int j = 0; j < nonPulledColumns.size(); j++) {
				sb.append('?');

				if (j < nonPulledColumns.size() - 1) {
					sb.append(", ");
				}
			}

			sb.append(')');

			if (i < count - 1) {
				sb.append(", ");
			}
		}

		sb.append(";");

		return sb.toString();
	}

	@Override
	protected void fillInPullStatement(PreparedStatement pullStatement, T obj) throws SQLException, IllegalAccessException {
		StatementPreparer preparer = new StatementPreparer(pullStatement);

		for (Column column : primaryKeyColumns) {
			SQLTypeHandler handler = handlers.get(column.value());
			Field field = fields.get(column.value());
			handler.prepareStatement(preparer, field.get(obj));
		}
	}

	protected void prepareColumnNames(T obj, List<Column> affectedColumns, StatementPreparer preparer) throws SQLException, IllegalAccessException {
		for (Column column : affectedColumns) {
			Field field = fields.get(column.value());
			SQLTypeHandler handler = handlers.get(column.value());

			handler.prepareStatement(preparer, field.get(obj));
		}
	}

	protected void appendWhereClause(StringBuilder sb) {
		for (int i = 0; i < primaryKeyColumns.size(); i++) {
			Column column = primaryKeyColumns.get(i);
			sb.append(column.value());
			sb.append(" = ?");

			if (i < primaryKeyColumns.size() - 1) {
				sb.append(" AND ");
			}
		}
	}
}
