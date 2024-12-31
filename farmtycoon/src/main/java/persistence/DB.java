package persistence;

import java.sql.SQLException;

import exceptions.*;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;

public class DB {
	private java.sql.Connection connection;
	private @RUntainted String name;

	/**
	 *
	 * @throws DBConnectException
	 * @throws SystemDBException
	 */
	// constructor connects to database
	public DB() throws DBConnectException, SystemDBException {
		this("farmsave");
	}

	/**
	 *
	 * @param name
	 * @throws DBConnectException
	 * @throws SystemDBException
	 */
	public DB(@RUntainted String name) throws DBConnectException, SystemDBException {
		this.name=name;
		try {
			Class.forName("org.sqlite.JDBC");
//			connection = java.sql.DriverManager.getConnection(String.format(
//			"jdbc:sqlite:%s.sav", name));
			connection = java.sql.DriverManager.getConnection("jdbc:sqlite:");
			connection.createStatement().executeUpdate(String.format("restore from %s.sav",name));
		} catch (SQLException sqlException) {
			DBConnectException e = (DBConnectException) sqlException;
			throw e;
		}
		// detect problems loading database driver'
		catch (ClassNotFoundException classNotFound) {
			throw new SystemDBException();
		}
	}

	public void sync() throws SQLException{
		connection.createStatement().executeUpdate(String.format("backup to %s.sav",name));
	}

	public void close() throws DBCloseException {
		try {
			sync();
			connection.close();
		} catch (SQLException e) {
			throw (DBCloseException) e;
		}
	}

	public java.sql.Connection getConnection() {
		return connection;
	}

	/**
	 * @see Object#finalize()
	 */
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

}
