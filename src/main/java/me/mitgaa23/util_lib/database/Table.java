package me.mitgaa23.util_lib.database;

import me.mitgaa23.util_lib.database.annotations.Column;
import me.mitgaa23.util_lib.database.proxy.SQLTypeHandler;
import me.mitgaa23.util_lib.database.proxy.TableProxy;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class Table {
	private final String name;
	private final Connection connection;

	private final Class<?> clazz;

	private final List<Column> markers;

	private final HashMap<String, Field> fields;
	private final HashMap<String, SQLTypeHandler> handlers;

	public Table(String name, Class<?> clazz, Connection connection) {
		this.name = name;
		this.clazz = clazz;
		this.connection = connection;

		this.fields = new HashMap<>();
		this.markers = new ArrayList<>();
		this.handlers = new HashMap<>();

		for (Field field : clazz.getFields()) {
			Column column = field.getAnnotation(Column.class);

			markers.add(column);
			fields.put(column.value(), field);
		}

		for (Column marker : markers) {
			SQLTypeHandler proxy = TableProxy.getProxy(marker.handler());
			handlers.put(marker.value(), proxy);
		}

		markers.sort(Comparator.comparingInt(Column::index));
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public void drop() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE IF EXISTS ");
		sb.append(name);
		sb.append(" CASCADE;");

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(sb.toString());
		}
	}

	public void createTable(boolean ignoreIfExisting) throws SQLException {
		Objects.requireNonNull(connection, "connection cannot be null");

		StringBuilder sb = new StringBuilder();

		sb.append("CREATE TABLE ");

		if (ignoreIfExisting) {
			sb.append("IF NOT EXISTS ");
		}

		sb.append(name);
		sb.append(" (");

		for (int i = 0; i < markers.size(); i++) {
			Column marker = markers.get(i);
			sb.append(marker.value());
			sb.append(" ");

			SQLTypeHandler handler = handlers.get(marker.value());
			sb.append(handler.getName());

			int[] params = marker.params();
			if (params.length != 0) {
				sb.append("(");
				appendParams(params, sb);
				sb.append(")");
			}

			if (i < markers.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(", PRIMARY KEY (");

		int primaryKeys = 0;
		for (Column marker : markers) {
			if (marker.primary()) {
				if (primaryKeys > 0) {
					sb.append(", ");
				}

				sb.append(marker.value());
				primaryKeys++;
			}
		}

		sb.append("));");

		try (Statement statement = connection.createStatement()) {
			String string = sb.toString();
			statement.executeUpdate(string);
		}
	}

	protected static void appendParams(int[] params, StringBuilder sb) {
		for (int j = 0; j < params.length; j++) {
			int param = params[j];
			sb.append(param);

			if (j < params.length - 1) {
				sb.append(", ");
			}
		}
	}

	/// update the row that corresponds to the given object
	public <T> T push(T obj) throws SQLException {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE ");
		sb.append(name);
		sb.append(" SET ");

		List<Column> affectedColumns = new ArrayList<>();
		List<Column> pulledColumns = new ArrayList<>();

		for (Column marker : markers) {
			SQLTypeHandler handler = handlers.get(marker.value());

			if (handler.isPulledFromDB()) {
				pulledColumns.add(marker);
				continue;
			}

			if (!affectedColumns.isEmpty()) {
				sb.append(", ");
			}

			affectedColumns.add(marker);

			sb.append(marker.value());
			sb.append(" = ?");
		}

		sb.append(" WHERE ");

		List<Column> primaryColumns = new ArrayList<>();
		for (Column marker : markers) {
			if (!marker.primary()) {
				continue;
			}

			if (!primaryColumns.isEmpty()) {
				sb.append(" AND");
			}

			sb.append(marker.value());
			sb.append(" = ?");

			primaryColumns.add(marker);
		}

		sb.append(" RETURNING *;");

		try (PreparedStatement statement = connection.prepareStatement(sb.toString())) {
			StatementPreparer preparer = new StatementPreparer(statement);

			prepareColumnNames(obj, affectedColumns, preparer);
			prepareColumnNames(obj, primaryColumns, preparer);

			statement.execute();
			saveToObject(obj, pulledColumns, statement);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	protected <T> void prepareColumnNames(T obj, List<Column> affectedColumns, StatementPreparer preparer) throws SQLException, IllegalAccessException {
		for (Column marker : affectedColumns) {
			Field field = fields.get(marker.value());
			SQLTypeHandler handler = handlers.get(marker.value());

			handler.prepareStatement(preparer, field.get(obj));
		}
	}

	protected <T> void saveToObject(T obj, List<Column> pulledColumns, PreparedStatement statement) throws SQLException, IllegalAccessException {
		ResultSet result = statement.getResultSet();

		if (!result.next()) {
			throw new IllegalStateException("No result was returned");
		}

		for (Column marker : pulledColumns) {
			Field field = fields.get(marker.value());

			if (field != null) {
				SQLTypeHandler handler = handlers.get(marker.value());

				handler.copyToField(marker.value(), result, field, obj);
			}
		}
	}

	public <T> T delete(T obj) throws SQLException {
		StringBuilder sb = new StringBuilder();

		sb.append("DELETE FROM ");
		sb.append(name);
		sb.append(" WHERE ");

		List<Column> affectedColumns = appendWhereClause(sb);

		try (PreparedStatement statement = connection.prepareStatement(sb.toString())) {
			StatementPreparer preparer = new StatementPreparer(statement);

			for (Column marker : affectedColumns) {
				SQLTypeHandler handler = handlers.get(marker.value());
				Field field = fields.get(marker.value());

				handler.prepareStatement(preparer, field.get(obj));
			}

			statement.executeUpdate();

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	protected List<Column> appendWhereClause(StringBuilder sb) {
		List<Column> affectedColumns = new ArrayList<>();

		for (Column marker : markers) {
			if (!marker.primary()) {
				continue;
			}

			if (!affectedColumns.isEmpty()) {
				sb.append(" AND ");
			}

			sb.append(marker.value());
			sb.append(" = ?");

			affectedColumns.add(marker);
		}

		sb.append(';');

		return affectedColumns;
	}

	public <T> T insert(T obj) throws SQLException {
		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		sb.append(name);
		sb.append(" (");

		List<Column> affectedColumns = new ArrayList<>();
		List<Column> pulledColumns = new ArrayList<>();

		for (Column marker : markers) {
			SQLTypeHandler handler = handlers.get(marker.value());

			if (handler.isPulledFromDB()) {
				pulledColumns.add(marker);
				continue;
			}

			if (!affectedColumns.isEmpty()) {
				sb.append(", ");
			}

			sb.append(marker.value());
			affectedColumns.add(marker);
		}

		sb.append(") VALUES (");

		for (int i = 0; i < affectedColumns.size(); i++) {
			sb.append('?');

			if (i < affectedColumns.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(") RETURNING *;");

		try (PreparedStatement statement = connection.prepareStatement(sb.toString())) {
			StatementPreparer preparer = new StatementPreparer(statement);

			for (Column marker : affectedColumns) {
				Field field = fields.get(marker.value());
				SQLTypeHandler handler = handlers.get(marker.value());

				handler.prepareStatement(preparer, field.get(obj));
			}

			statement.execute();

			saveToObject(obj, pulledColumns, statement);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}

	/// pull the data from the row that corresponds to the given object and modifies the content of the object
	public <T> T pull(T obj) throws SQLException {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");

		for (int i = 0; i < markers.size(); i++) {
			Column marker = markers.get(i);
			sb.append(marker.value());

			if (i < markers.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(" FROM ");
		sb.append(name);
		sb.append(" WHERE ");

		List<Column> affectedColumns = appendWhereClause(sb);

		try (PreparedStatement statement = connection.prepareStatement(sb.toString())) {
			StatementPreparer preparer = new StatementPreparer(statement);

			for (Column marker : affectedColumns) {
				SQLTypeHandler handler = handlers.get(marker.value());
				Field field = fields.get(marker.value());

				handler.prepareStatement(preparer, field.get(obj));
			}

			ResultSet result = statement.executeQuery();

			if (!result.next()) {
				throw new IllegalStateException("No result was returned");
			}

			for (Column marker : markers) {
				SQLTypeHandler handler = handlers.get(marker.value());
				Field field = fields.get(marker.value());

				handler.copyToField(marker.value(), result, field, obj);
			}

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return obj;
	}
}
