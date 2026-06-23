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
public abstract class Table<T> {
	private static final Logger LOGGER = Log.get(Table.class);

	protected final String name;
	protected final Connection connection;

	protected final Class<T> clazz;

	protected final List<Column> columns;
	protected final HashMap<String, Field> fields;
	protected final HashMap<String, SQLTypeHandler> handlers;
	protected final List<Column> nonPulledColumns;
	protected final List<Column> pulledColumns;
	protected final List<Column> primaryKeyColumns;

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

		initFields();

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

	private void initFields() {
		for (Field field : clazz.getFields()) {
			Column column = field.getAnnotation(Column.class);

			columns.add(column);
			fields.put(column.value(), field);

			SQLTypeHandler proxy = TableProxy.getProxy(column.handler());
			handlers.put(column.value(), proxy);
		}

		columns.sort(Comparator.comparingInt(Column::index));
	}

	private void initColumnList(List<Column> primaryKeyColumns, List<Column> pulledColumns, List<Column> nonPulledColumns) {
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

	protected abstract String getDropStatement();

	protected abstract String getUpdateStatement();

	protected abstract String getCreateStatement();

	protected abstract String getDeleteStatement();

	protected abstract String getInsertStatement();

	protected abstract String getPullStatement();

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
			fillUpdateStatement(updateStatement, obj);

			updateStatement.execute();
			saveToObject(obj, pulledColumns, updateStatement.getResultSet());

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	protected abstract void fillUpdateStatement(PreparedStatement updateStatement, T obj) throws SQLException, IllegalAccessException;

	protected void saveToObject(T obj, List<Column> pulledColumns, ResultSet result) throws SQLException, IllegalAccessException {
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
			fillDeleteStatement(deleteStatement, obj);

			deleteStatement.executeUpdate();

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	protected abstract void fillDeleteStatement(PreparedStatement deleteStatement, T obj) throws SQLException, IllegalAccessException;

	public T insert(T obj) throws SQLException {
		try {
			fillInsertStatement(insertStatement, obj);

			insertStatement.execute();
			saveToObject(obj, pulledColumns, insertStatement.getResultSet());

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	protected abstract void fillInsertStatement(PreparedStatement insertStatement, T obj) throws SQLException, IllegalAccessException;

	public void insertMultiple(List<T> list) throws SQLException {
		int max = ((1 << 16) - 1) / columns.size();
		int count = list.size() - 1;
		int current = 0;

		Spinner.start();

		long start = System.currentTimeMillis();

		try {
			while (current < count) {
				int batchSize = Math.min(count - current, max);
				long startBatch = System.currentTimeMillis();

				Spinner.setMessage("Building statement for %d rows (%d / %d)".formatted(batchSize, current, count));

				try (PreparedStatement statement = fillInsertMultipleStatement(list, batchSize, current, count)) {
					Spinner.setMessage("Running statement for %d rows (%d / %d)".formatted(batchSize, current, count));

					statement.executeUpdate();
				}

				long endBatch = System.currentTimeMillis();
				long durationBatch = endBatch - startBatch;

				LOGGER.finer("Inserted %d rows in %dms (%d / %d)".formatted(batchSize, durationBatch, current + batchSize, count));
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

	protected abstract PreparedStatement fillInsertMultipleStatement(List<T> list, int batchSize, int current, int count) throws SQLException, IllegalAccessException;

	protected abstract String getInsertMultipleStatement(int count);

	/// pull the data from the row that corresponds to the given object and modifies the content of the object
	public T pull(T obj) throws SQLException {
		try {
			fillInPullStatement(pullStatement, obj);

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

	protected abstract void fillInPullStatement(PreparedStatement pullStatement, T obj) throws SQLException, IllegalAccessException;
}
