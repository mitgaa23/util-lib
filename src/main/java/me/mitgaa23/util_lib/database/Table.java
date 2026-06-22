package me.mitgaa23.util_lib.database;

import me.mitgaa23.util_lib.database.annotations.Column;
import me.mitgaa23.util_lib.database.proxy.SQLTypeHandler;
import me.mitgaa23.util_lib.database.proxy.TableProxy;
import me.mitgaa23.util_lib.logging.Log;
import me.mitgaa23.util_lib.logging.spinner.Spinner;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/// TODO: Make less Postgres specific
public class Table<T> {
	private static final Logger LOGGER = Log.get(Table.class);

	private final String name;
	private final Connection connection;

	private final Class<T> clazz;

	private final List<Column> columns;

	private final HashMap<String, Field> fields;
	private final HashMap<String, SQLTypeHandler> handlers;
	private final List<Column> nonPulledColumns;
	private final List<Column> pulledColumns;
	private final List<Column> primaryKeyColumns;

	private final PreparedStatement updateStatement;
	private final PreparedStatement dropStatement;
	private final PreparedStatement createStatement;
	private final PreparedStatement deleteStatement;
	private final PreparedStatement insertStatement;
	private final PreparedStatement pullStatement;

	public Table(String name, Class<T> clazz, Connection connection) throws SQLException {
		this.name = name;
		this.clazz = clazz;
		this.connection = connection;

		this.fields = new HashMap<>();
		this.columns = new ArrayList<>();
		this.handlers = new HashMap<>();

		for (Field field : clazz.getFields()) {
			Column column = field.getAnnotation(Column.class);

			columns.add(column);
			fields.put(column.value(), field);

			SQLTypeHandler proxy = TableProxy.getProxy(column.handler());
			handlers.put(column.value(), proxy);
		}

		columns.sort(Comparator.comparingInt(Column::index));

		List<Column> nonPulledColumns = new ArrayList<>();
		List<Column> pulledColumns = new ArrayList<>();
		List<Column> primaryKeyColumns = new ArrayList<>();

		initColumnList(primaryKeyColumns, pulledColumns, nonPulledColumns);

		this.pulledColumns = Collections.unmodifiableList(pulledColumns);
		this.nonPulledColumns = Collections.unmodifiableList(nonPulledColumns);
		this.primaryKeyColumns = Collections.unmodifiableList(primaryKeyColumns);

		this.dropStatement = connection.prepareStatement(getDropStatement());
		this.updateStatement = connection.prepareStatement(getUpdateStatement());
		this.createStatement = connection.prepareStatement(getCreateStatement());
		this.deleteStatement = connection.prepareStatement(getDeleteStatement());
		this.insertStatement = connection.prepareStatement(getInsertStatement());
		this.pullStatement = connection.prepareStatement(getPullStatement());
	}

	protected void initColumnList(List<Column> primaryKeyColumns, List<Column> pulledColumns, List<Column> nonPulledColumns) {
		for (Column column : columns) {
			SQLTypeHandler handler = handlers.get(column.value());

			if (column.primary()) {
				primaryKeyColumns.add(column);
			}

			if (handler.isPulledFromDB()) {
				pulledColumns.add(column);

			} else {
				nonPulledColumns.add(column);
			}
		}
	}

	private String getDropStatement() {
		return "DROP TABLE IF EXISTS %s CASCADE;".formatted(name);
	}

	private String getUpdateStatement() {
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

	private String getDeleteStatement() {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append(name);
		sb.append(" WHERE ");
		appendWhereClause(sb);
		return sb.toString();
	}

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

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public void dropTable() throws SQLException {
		dropStatement.executeUpdate();
	}

	public void createTable(boolean ignoreIfExisting) throws SQLException {
		Objects.requireNonNull(connection, "connection cannot be null");

		createStatement.executeUpdate();
	}

	/// update the row that corresponds to the given object
	public T push(T obj) throws SQLException {
		try {
			StatementPreparer preparer = new StatementPreparer(updateStatement);

			prepareColumnNames(obj, nonPulledColumns, preparer);
			prepareColumnNames(obj, primaryKeyColumns, preparer);

			updateStatement.execute();
			saveToObject(obj, pulledColumns, updateStatement);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	protected void prepareColumnNames(T obj, List<Column> affectedColumns, StatementPreparer preparer) throws SQLException, IllegalAccessException {
		for (Column column : affectedColumns) {
			Field field = fields.get(column.value());
			SQLTypeHandler handler = handlers.get(column.value());

			handler.prepareStatement(preparer, field.get(obj));
		}
	}

	protected void saveToObject(T obj, List<Column> pulledColumns, PreparedStatement statement) throws SQLException, IllegalAccessException {
		ResultSet result = statement.getResultSet();

		if (!result.next()) {
			throw new IllegalStateException("No result was returned");
		}

		for (Column column : pulledColumns) {
			Field field = fields.get(column.value());

			if (field != null) {
				SQLTypeHandler handler = handlers.get(column.value());

				handler.copyToField(column.value(), result, field, obj);
			}
		}
	}

	public T delete(T obj) throws SQLException {
		try {
			StatementPreparer preparer = new StatementPreparer(deleteStatement);

			for (Column column : primaryKeyColumns) {
				SQLTypeHandler handler = handlers.get(column.value());
				Field field = fields.get(column.value());

				handler.prepareStatement(preparer, field.get(obj));
			}

			deleteStatement.executeUpdate();

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	public T insert(T obj) throws SQLException {
		try {
			StatementPreparer preparer = new StatementPreparer(insertStatement);

			for (Column column : nonPulledColumns) {
				Field field = fields.get(column.value());
				SQLTypeHandler handler = handlers.get(column.value());
				handler.prepareStatement(preparer, field.get(obj));
			}

			insertStatement.execute();
			saveToObject(obj, pulledColumns, insertStatement);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	public void insertMultiple(List<T> list) throws SQLException {
		int max = ((1 << 16) - 1) / columns.size();
		int count = list.size() - 1;
		int current = 0;

		Spinner.start();

		long start = System.currentTimeMillis();

		try {
			while (current < count) {
				int batchSize = Math.min(count - current, max);

				Spinner.setMessage("Building statement for %d rows (%d / %d)".formatted(batchSize, current, count));

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

				long startBatch = System.currentTimeMillis();

				Spinner.setMessage("Running statement for %d rows (%d / %d)".formatted(batchSize, current, count));
				statement.executeUpdate();

				long endBatch = System.currentTimeMillis();
				long durationBatch = endBatch - startBatch;

				LOGGER.finer("Inserted %d rows in %dms (%d / %d)".formatted(batchSize, durationBatch, current, count));
				current += batchSize;
			}

		} catch (IllegalAccessException e) {
			Spinner.stop();

			throw new RuntimeException(e);
		}

		Spinner.stop();

		long end = System.currentTimeMillis();
		long duration = end - start;
		LOGGER.fine("Inserted %d rows in %dms".formatted(count, duration));
	}

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

	/// pull the data from the row that corresponds to the given object and modifies the content of the object
	public T pull(T obj) throws SQLException {
		try {
			StatementPreparer preparer = new StatementPreparer(pullStatement);

			for (Column column : primaryKeyColumns) {
				SQLTypeHandler handler = handlers.get(column.value());
				Field field = fields.get(column.value());
				handler.prepareStatement(preparer, field.get(obj));
			}

			ResultSet result = pullStatement.executeQuery();

			if (!result.next()) {
				throw new IllegalStateException("No result was returned");
			}

			for (Column column : columns) {
				SQLTypeHandler handler = handlers.get(column.value());
				Field field = fields.get(column.value());

				handler.copyToField(column.value(), result, field, obj);
			}

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}
}
