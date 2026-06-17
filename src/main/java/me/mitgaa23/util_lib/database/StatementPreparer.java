package me.mitgaa23.util_lib.database;

import java.math.BigDecimal;
import java.sql.*;

public class StatementPreparer {
	private final PreparedStatement statement;
	private int index = 1;

	public StatementPreparer(PreparedStatement statement) {
		this.statement = statement;
	}

	public void setBoolean(boolean value) throws SQLException {
		statement.setBoolean(advanceIndex(), value);
	}

	protected int advanceIndex() {
		return index++;
	}

	public void setByte(byte value) throws SQLException {
		statement.setByte(advanceIndex(), value);
	}

	public void setShort(short value) throws SQLException {
		statement.setShort(advanceIndex(), value);
	}

	public void setInt(int value) throws SQLException {
		statement.setInt(advanceIndex(), value);
	}

	public void setLong(long value) throws SQLException {
		statement.setLong(advanceIndex(), value);
	}

	public void setFloat(float value) throws SQLException {
		statement.setFloat(advanceIndex(), value);
	}

	public void setDouble(double value) throws SQLException {
		statement.setDouble(advanceIndex(), value);
	}

	public void setBigDecimal(BigDecimal value) throws SQLException {
		statement.setBigDecimal(advanceIndex(), value);
	}

	public void setString(String value) throws SQLException {
		statement.setString(advanceIndex(), value);
	}

	public void setBytes(byte[] value) throws SQLException {
		statement.setBytes(advanceIndex(), value);
	}

	public void setDate(Date value) throws SQLException {
		statement.setDate(advanceIndex(), value);
	}

	public void setTime(Time value) throws SQLException {
		statement.setTime(advanceIndex(), value);
	}

	public void setTimestamp(Timestamp value) throws SQLException {
		statement.setTimestamp(advanceIndex(), value);
	}
}
